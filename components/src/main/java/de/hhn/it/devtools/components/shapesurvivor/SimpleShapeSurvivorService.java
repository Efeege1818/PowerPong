package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleShapeSurvivorService implements ShapeSurvivorService {

    private static final int DEFAULT_GAME_DURATION = 900; // 15 minutes
    private static final int DEFAULT_FIELD_WIDTH = 800;
    private static final int DEFAULT_FIELD_HEIGHT = 600;

    private GameState gameState;
    private GameConfiguration configuration;
    private Player player;
    private List<Enemy> enemies;
    private final List<ShapeSurvivorListener> listeners;
    private GameStatistics statistics;
    private GameLoopService gameLoop;

    private long gameStartTime;
    private boolean levelUpPending;
    private final List<WeaponType> availableWeaponUpgrades;
    private final List<PlayerAttribute> availableAttributeUpgrades;

    private int nextEnemyId;
    private int currentWave;
    private long lastWaveSpawnTime;

    // Weapon animation states
    private final Map<WeaponType, WeaponAnimationState> weaponStates;
    private long lastWeaponUpdateTime;
    private static final long WEAPON_UPDATE_INTERVAL_MS = 16; // ~60 FPS

    private long lastPlayerHitTime = 0;
    private static final long PLAYER_HIT_COOLDOWN_MS = 600; // 0.6 sec


    public SimpleShapeSurvivorService() {
        this.gameState = GameState.PREPARED;
        this.enemies = new ArrayList<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.availableWeaponUpgrades = new ArrayList<>();
        this.availableAttributeUpgrades = new ArrayList<>();
        this.nextEnemyId = 0;
        this.currentWave = 0;
        this.weaponStates = new HashMap<>();

        this.configuration = new GameConfiguration(
                DEFAULT_GAME_DURATION,
                DEFAULT_FIELD_WIDTH,
                DEFAULT_FIELD_HEIGHT,
                100, // starting health
                5.0, // starting speed
                10,  // starting damage
                1,   // initial weapon count
                1.0, // enemy spawn rate
                1.0, // difficulty multiplier
                new WeaponType[]{WeaponType.SWORD}
        );

        initializePlayer();
        initializeStatistics();
        initializeGameLoop();
    }

    private void initializePlayer() {
        Position startPosition = new Position(
                configuration.fieldWidth() / 2,
                configuration.fieldHeight() / 2
        );

        Weapon[] initialWeapons = createInitialWeapons();

        this.player = new Player(
                startPosition,
                configuration.startingPlayerHealth(),
                configuration.startingPlayerHealth(),
                configuration.startingPlayerSpeed(),
                configuration.startingPlayerDamage(),
                1.0, // attack speed
                0.2, // damage resistance
                1,   // level
                0,   // experience
                100, // experience to next level
                initialWeapons
        );

        // Initialize weapon states
        for (Weapon weapon : initialWeapons) {
            weaponStates.put(weapon.type(), new WeaponAnimationState());
        }
    }

    private Weapon[] createInitialWeapons() {
        WeaponType[] types = configuration.initialWeapons();
        if (types == null || types.length == 0) {
            types = new WeaponType[]{WeaponType.SWORD};
        }

        List<Weapon> weapons = new ArrayList<>();
        for (WeaponType type : types) {
            weapons.add(createWeapon(type, 1));
        }

        return weapons.toArray(new Weapon[0]);
    }

    private Weapon createWeapon(WeaponType type, int level) {
        return switch (type) {
            case SWORD -> new Weapon(
                    WeaponType.SWORD,
                    "Circles the player and damages enemies on contact",
                    level, 15 * level, 1.5 / level, 80.0 + (level * 10), true
            );
            case AURA -> new Weapon(
                    WeaponType.AURA,
                    "Deals damage in a circle around the player",
                    level, 8 * level, 2.0 / level, 100.0 + (level * 15), true
            );
            case WHIP -> new Weapon(
                    WeaponType.WHIP,
                    "Whips enemies in front and behind the player",
                    level, 25 * level, 1.2 / level, 120.0 + (level * 10), true
            );
        };
    }

    private void initializeStatistics() {
        this.statistics = new GameStatistics(0, 0, 0, 0, 1, 0, 0);
    }

    private void initializeGameLoop() {
        this.gameLoop = new SimpleGameLoopService(this::updateGame);
    }

    @Override
    public void reset() {
        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }

        gameState = GameState.PREPARED;
        enemies.clear();
        nextEnemyId = 0;
        currentWave = 0;
        levelUpPending = false;
        availableWeaponUpgrades.clear();
        availableAttributeUpgrades.clear();
        weaponStates.clear();

        initializePlayer();
        initializeStatistics();

        notifyGameStateChanged(GameState.PREPARED);
    }

    @Override
    public void start() throws IllegalStateException {
        if (gameState != GameState.PREPARED) {
            throw new IllegalStateException("Game can only be started from PREPARED state");
        }

        gameState = GameState.RUNNING;
        gameStartTime = System.currentTimeMillis();
        lastWaveSpawnTime = gameStartTime;
        lastWeaponUpdateTime = gameStartTime;

        gameLoop.startLoop();
        notifyGameStateChanged(GameState.RUNNING);
    }

    @Override
    public void abort() throws IllegalStateException {
        if (gameState == GameState.PREPARED || gameState == GameState.ABORTED) {
            throw new IllegalStateException("Cannot abort game in " + gameState + " state");
        }

        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }

        gameState = GameState.ABORTED;
        notifyGameStateChanged(GameState.ABORTED);
    }

    @Override
    public void pause() throws IllegalStateException {
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Can only pause game in RUNNING state");
        }

        gameLoop.pauseLoop();
        gameState = GameState.PAUSED;
        notifyGameStateChanged(GameState.PAUSED);
    }

    @Override
    public void resume() throws IllegalStateException {
        if (gameState != GameState.PAUSED) {
            throw new IllegalStateException("Can only resume game from PAUSED state");
        }

        gameLoop.resumeLoop();
        gameState = GameState.RUNNING;
        notifyGameStateChanged(GameState.RUNNING);
    }

    public void movePlayer(Direction direction) {
        if (player == null) return;

        Position oldPos = player.position();
        int x = oldPos.x();
        int y = oldPos.y();
        int speed = (int) player.movementSpeed();

        switch (direction) {
            case UP -> y -= speed;
            case DOWN -> y += speed;
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
        }

        // Keep player inside bounds
        x = Math.max(0, Math.min(x, configuration.fieldWidth()));
        y = Math.max(0, Math.min(y, configuration.fieldHeight()));

        // Update the player
        player = new Player(
                new Position(x, y),
                player.currentHealth(),
                player.maxHealth(),
                player.movementSpeed(),
                player.baseDamage(),
                player.attackSpeed(),
                player.damageResistance(),
                player.level(),
                player.experience(),
                player.experienceToNextLevel(),
                player.equippedWeapons()
        );

        notifyPlayerUpdated();
    }

    @Override
    public void selectWeapon(WeaponType weaponType)
            throws IllegalStateException, IllegalArgumentException {
        if (weaponType == null) {
            throw new IllegalArgumentException("WeaponType cannot be null");
        }
        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Can only select weapons when game is RUNNING");
        }
        if (!levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }

        if (!availableWeaponUpgrades.contains(weaponType)) {
            throw new IllegalArgumentException("WeaponType not available for upgrade");
        }

        // Check if player already has this weapon
        List<Weapon> weapons = new ArrayList<>(Arrays.asList(player.equippedWeapons()));
        boolean hasWeapon = false;

        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).type() == weaponType) {
                // Upgrade existing weapon
                Weapon oldWeapon = weapons.get(i);
                Weapon upgradedWeapon = createWeapon(weaponType, oldWeapon.level() + 1);
                weapons.set(i, upgradedWeapon);
                hasWeapon = true;
                notifyWeaponUpdated(upgradedWeapon);
                break;
            }
        }

        if (!hasWeapon) {
            // Add new weapon
            Weapon newWeapon = createWeapon(weaponType, 1);
            weapons.add(newWeapon);
            weaponStates.put(weaponType, new WeaponAnimationState());
            notifyWeaponUpdated(newWeapon);
        }

        player = new Player(
                player.position(),
                player.currentHealth(),
                player.maxHealth(),
                player.movementSpeed(),
                player.baseDamage(),
                player.attackSpeed(),
                player.damageResistance(),
                player.level(),
                player.experience(),
                player.experienceToNextLevel(),
                weapons.toArray(new Weapon[0])
        );

        levelUpPending = false;
        availableWeaponUpgrades.clear();
        availableAttributeUpgrades.clear();
    }

    @Override
    public void upgradeAttribute(PlayerAttribute attribute, double value, boolean isMultiplier)
            throws IllegalStateException, IllegalArgumentException {
        if (attribute == null) {
            throw new IllegalArgumentException("PlayerAttribute cannot be null");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("Value must be positive");
        }

        if (gameState != GameState.RUNNING) {
            throw new IllegalStateException("Can only upgrade attributes when game is RUNNING");
        }
        if (!levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }
        if (!availableAttributeUpgrades.contains(attribute)) {
            throw new IllegalArgumentException("PlayerAttribute not available for upgrade");
        }

        int newMaxHealth = player.maxHealth();
        int newCurrentHealth = player.currentHealth();
        double newMovementSpeed = player.movementSpeed();
        int newBaseDamage = player.baseDamage();
        double newAttackSpeed = player.attackSpeed();
        double newDamageResistance = player.damageResistance();

        switch (attribute) {
            case MAX_HEALTH -> {
                newMaxHealth = isMultiplier ?
                        (int)(player.maxHealth() * value) :
                        player.maxHealth() + (int)value;
                newCurrentHealth = newMaxHealth;
            }
            case MOVEMENT_SPEED -> newMovementSpeed = isMultiplier ?
                    player.movementSpeed() * value :
                    player.movementSpeed() + value;
            case DAMAGE -> newBaseDamage = isMultiplier ?
                    (int)(player.baseDamage() * value) :
                    player.baseDamage() + (int)value;
            case ATTACK_SPEED -> newAttackSpeed = isMultiplier ?
                    player.attackSpeed() * value :
                    player.attackSpeed() + value;
            case DAMAGE_RESISTANCE -> newDamageResistance = isMultiplier ?
                    player.damageResistance() * value :
                    Math.min(0.9, player.damageResistance() + value);
        }

        player = new Player(
                player.position(),
                newCurrentHealth,
                newMaxHealth,
                newMovementSpeed,
                newBaseDamage,
                newAttackSpeed,
                newDamageResistance,
                player.level(),
                player.experience(),
                player.experienceToNextLevel(),
                player.equippedWeapons()
        );

        levelUpPending = false;
        availableWeaponUpgrades.clear();
        availableAttributeUpgrades.clear();
        notifyPlayerUpdated();
    }

    @Override
    public boolean addListener(ShapeSurvivorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(ShapeSurvivorListener listener) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        return listeners.remove(listener);
    }

    @Override
    public void configure(GameConfiguration configuration)
            throws IllegalStateException, IllegalArgumentException, IllegalParameterException {
        if (gameState != GameState.PREPARED && gameState != GameState.ABORTED) {
            throw new IllegalStateException("Can only configure in PREPARED or ABORTED state");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        validateConfiguration(configuration);

        this.configuration = configuration;
        initializePlayer();

        notifyConfigurationUpdated(configuration);
    }

    private void validateConfiguration(GameConfiguration config)
            throws IllegalParameterException {
        if (config.gameDurationSeconds() <= 0) {
            throw new IllegalParameterException("Game duration must be positive");
        }
        if (config.fieldWidth() <= 0 || config.fieldHeight() <= 0) {
            throw new IllegalParameterException("Field dimensions must be positive");
        }
        if (config.startingPlayerHealth() <= 0) {
            throw new IllegalParameterException("Starting health must be positive");
        }
        if (config.startingPlayerSpeed() <= 0) {
            throw new IllegalParameterException("Starting speed must be positive");
        }
        if (config.startingPlayerDamage() < 0) {
            throw new IllegalParameterException("Starting damage cannot be negative");
        }
    }

    @Override
    public GameConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Enemy[] getEnemies() {
        return enemies.toArray(new Enemy[0]);
    }

    @Override
    public boolean isLevelUpPending() {
        return levelUpPending;
    }

    @Override
    public WeaponType[] getAvailableWeaponUpgrades() throws IllegalStateException {
        if (!levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }
        return availableWeaponUpgrades.toArray(new WeaponType[0]);
    }

    @Override
    public PlayerAttribute[] getAvailableAttributeUpgrades() throws IllegalStateException {
        if (!levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }
        return availableAttributeUpgrades.toArray(new PlayerAttribute[0]);
    }

    @Override
    public GameStatistics getStatistics() {
        return statistics;
    }

    @Override
    public int getElapsedTime() throws IllegalStateException {
        if (gameState == GameState.PREPARED || gameState == GameState.ABORTED) {
            throw new IllegalStateException("No time elapsed in " + gameState + " state");
        }
        return (int)((System.currentTimeMillis() - gameStartTime) / 1000);
    }

    @Override
    public int getRemainingTime() throws IllegalStateException {
        if (gameState == GameState.PREPARED || gameState == GameState.ABORTED) {
            throw new IllegalStateException("No time remaining in " + gameState + " state");
        }
        int elapsed = getElapsedTime();
        return Math.max(0, configuration.gameDurationSeconds() - elapsed);
    }

    public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
        return new HashMap<>(weaponStates);
    }

    private void updateGame() {
        if (gameState != GameState.RUNNING) {
            return;
        }

        // Check win condition
        if (getRemainingTime() <= 0) {
            endGame(true);
            return;
        }

        spawnEnemies();
        updateEnemies();
        updateWeaponAnimations();
        updateWeapons();

        if (player.currentHealth() <= 0) {
            endGame(false);
        }
        notifyTimeUpdate();
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastWave = currentTime - lastWaveSpawnTime;

        // Spawn wave every 10 seconds
        if (timeSinceLastWave >= 10000) {
            currentWave++;
            int enemyCount = (int)(5 * currentWave * configuration.enemySpawnRate());

            for (int i = 0; i < enemyCount; i++) {
                spawnEnemy();
            }

            lastWaveSpawnTime = currentTime;
            notifyEnemyWaveSpawned(currentWave, enemyCount);

            statistics = new GameStatistics(
                    statistics.enemiesKilled(),
                    statistics.damageDealt(),
                    statistics.damageTaken(),
                    currentWave,
                    statistics.highestLevel(),
                    statistics.totalExperienceGained(),
                    getElapsedTime()
            );
        }
    }

    private void spawnEnemy() {
        Random random = new Random();

        int side = random.nextInt(4);
        int x, y;

        y = switch (side) {
            case 0 -> {
                x = random.nextInt(configuration.fieldWidth());
                yield -20;
            }
            case 1 -> {
                x = configuration.fieldWidth() + 20;
                yield random.nextInt(configuration.fieldHeight());
            }
            case 2 -> {
                x = random.nextInt(configuration.fieldWidth());
                yield configuration.fieldHeight() + 20;
            }
            default -> {
                x = -20;
                yield random.nextInt(configuration.fieldHeight());
            }
        };

        int health = (int)(50 * configuration.difficultyMultiplier());

        Enemy enemy = new Enemy(
                nextEnemyId++,
                new Position(x, y),
                health,
                health,
                2.0,
                10,
                20
        );

        enemies.add(enemy);
    }

    private void updateEnemies() {
        List<Enemy> updatedEnemies = new ArrayList<>();
        Position playerPos = player.position();

        for (Enemy enemy : enemies) {
            Position enemyPos = enemy.position();
            int dx = playerPos.x() - enemyPos.x();
            int dy = playerPos.y() - enemyPos.y();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                int newX = enemyPos.x() + (int)((dx / distance) * enemy.movementSpeed());
                int newY = enemyPos.y() + (int)((dy / distance) * enemy.movementSpeed());

                Enemy movedEnemy = new Enemy(
                        enemy.id(),
                        new Position(newX, newY),
                        enemy.currentHealth(),
                        enemy.maxHealth(),
                        enemy.movementSpeed(),
                        enemy.contactDamage(),
                        enemy.experienceValue()
                );

                // Check collision with player (smaller radius for actual damage)
                if (distance < 25) {
                    long now = System.currentTimeMillis();
                    if (now - lastPlayerHitTime >= PLAYER_HIT_COOLDOWN_MS) {
                        damagePlayer(enemy.contactDamage());
                        lastPlayerHitTime = now;
                    }
                }

                updatedEnemies.add(movedEnemy);
            }
        }

        enemies = updatedEnemies;
        notifyEnemiesUpdated();
    }

    private void updateWeaponAnimations() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWeaponUpdateTime < WEAPON_UPDATE_INTERVAL_MS) {
            return;
        }
        lastWeaponUpdateTime = currentTime;

        for (Weapon weapon : player.equippedWeapons()) {
            WeaponAnimationState state = weaponStates.get(weapon.type());
            if (state != null) {
                state.update(weapon);
            }
        }
    }

    private void updateWeapons() {
        for (Weapon weapon : player.equippedWeapons()) {
            if (!weapon.isActive()) continue;

            WeaponAnimationState state = weaponStates.get(weapon.type());
            if (state == null) continue;

            // Different weapons have different attack patterns
            boolean shouldAttack = switch (weapon.type()) {
                case SWORD -> true; // Sword is always active (orbiting)
                case AURA -> true;  // Aura is always active (constant damage)
                case WHIP -> {
                    if (state.canAttack()) {
                        state.attack();
                        yield true;
                    }
                    yield false;
                }
            };

            if (!shouldAttack) continue;

            List<Enemy> toRemove = new ArrayList<>();

            for (Enemy enemy : enemies) {
                boolean hit = false;

                switch (weapon.type()) {
                    case SWORD -> hit = checkSwordHit(enemy, weapon, state);
                    case AURA -> hit = checkAuraHit(enemy, weapon);
                    case WHIP -> hit = checkWhipHit(enemy, weapon, state);
                }

                if (hit) {
                    int damage = weapon.damage() + player.baseDamage();
                    Enemy damagedEnemy = new Enemy(
                            enemy.id(),
                            enemy.position(),
                            enemy.currentHealth() - damage,
                            enemy.maxHealth(),
                            enemy.movementSpeed(),
                            enemy.contactDamage(),
                            enemy.experienceValue()
                    );

                    notifyEnemyDamaged(enemy, damage);

                    if (damagedEnemy.currentHealth() <= 0) {
                        toRemove.add(enemy);
                        gainExperience(enemy.experienceValue());
                        notifyEnemyKilled(enemy, enemy.experienceValue());

                        statistics = new GameStatistics(
                                statistics.enemiesKilled() + 1,
                                statistics.damageDealt() + damage,
                                statistics.damageTaken(),
                                statistics.wavesCompleted(),
                                Math.max(statistics.highestLevel(), player.level()),
                                statistics.totalExperienceGained(),
                                getElapsedTime()
                        );
                    } else {
                        enemies.set(enemies.indexOf(enemy), damagedEnemy);
                    }
                }
            }
            enemies.removeAll(toRemove);

        }
    }

    private boolean checkSwordHit(Enemy enemy, Weapon weapon, WeaponAnimationState state) {
        double angle = state.getAngle();
        int swordX = player.position().x() + (int)(Math.cos(angle) * weapon.range());
        int swordY = player.position().y() + (int)(Math.sin(angle) * weapon.range());

        double distance = getDistance(new Position(swordX, swordY), enemy.position());
        return distance < 30; // Sword hitbox radius
    }

    private boolean checkAuraHit(Enemy enemy, Weapon weapon) {
        double distance = getDistance(player.position(), enemy.position());
        return distance < weapon.range();
    }

    private boolean checkWhipHit(Enemy enemy, Weapon weapon, WeaponAnimationState state) {
        if (!state.isAttacking()) return false;

        Position enemyPos = enemy.position();
        Position playerPos = player.position();

        int dx = enemyPos.x() - playerPos.x();
        int dy = enemyPos.y() - playerPos.y();

        double whipWidth = 80;
        double whipLength = weapon.range();

        if (state.isAttackingLeft()) {
            return dx < 0 && dx > -whipLength && Math.abs(dy) < whipWidth;
        } else {
            return dx > 0 && dx < whipLength && Math.abs(dy) < whipWidth;
        }
    }

    private void damagePlayer(int damage) {
        int actualDamage = (int)(damage * (1 - player.damageResistance()));
        int newHealth = Math.max(0, player.currentHealth() - actualDamage);

        player = new Player(
                player.position(),
                newHealth,
                player.maxHealth(),
                player.movementSpeed(),
                player.baseDamage(),
                player.attackSpeed(),
                player.damageResistance(),
                player.level(),
                player.experience(),
                player.experienceToNextLevel(),
                player.equippedWeapons()
        );

        statistics = new GameStatistics(
                statistics.enemiesKilled(),
                statistics.damageDealt(),
                statistics.damageTaken() + actualDamage,
                statistics.wavesCompleted(),
                statistics.highestLevel(),
                statistics.totalExperienceGained(),
                getElapsedTime()
        );

        notifyPlayerDamaged(actualDamage);
        notifyPlayerUpdated();
    }

    private void gainExperience(int exp) {
        int newExp = player.experience() + exp;
        int expToNext = player.experienceToNextLevel();

        statistics = new GameStatistics(
                statistics.enemiesKilled(),
                statistics.damageDealt(),
                statistics.damageTaken(),
                statistics.wavesCompleted(),
                statistics.highestLevel(),
                statistics.totalExperienceGained() + exp,
                getElapsedTime()
        );

        if (newExp >= expToNext) {
            // Level up
            int newLevel = player.level() + 1;
            newExp -= expToNext;
            expToNext = (int)(expToNext * 1.5);

            player = new Player(
                    player.position(),
                    player.currentHealth(),
                    player.maxHealth(),
                    player.movementSpeed(),
                    player.baseDamage(),
                    player.attackSpeed(),
                    player.damageResistance(),
                    newLevel,
                    newExp,
                    expToNext,
                    player.equippedWeapons()
            );

            levelUpPending = true;
            generateUpgradeOptions();
            notifyPlayerLeveledUp();
        } else {
            player = new Player(
                    player.position(),
                    player.currentHealth(),
                    player.maxHealth(),
                    player.movementSpeed(),
                    player.baseDamage(),
                    player.attackSpeed(),
                    player.damageResistance(),
                    player.level(),
                    newExp,
                    expToNext,
                    player.equippedWeapons()
            );
        }

        notifyExperienceUpdated();
    }

    private void generateUpgradeOptions() {
        availableWeaponUpgrades.clear();
        availableAttributeUpgrades.clear();

        if (player.level() % 5 == 0) {
            availableWeaponUpgrades.addAll(Arrays.asList(WeaponType.values()));
        } else {
            availableAttributeUpgrades.addAll(Arrays.asList(PlayerAttribute.values()));
        }
    }

    private void endGame(boolean victory) {
        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }
        gameState = GameState.ABORTED;
        notifyGameEnded(victory);
        notifyGameStateChanged(GameState.ABORTED);
    }

    private double getDistance(Position p1, Position p2) {
        int dx = p1.x() - p2.x();
        int dy = p1.y() - p2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Notification methods
    private void notifyPlayerUpdated() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updatePlayer(player);
        }
    }

    private void notifyEnemiesUpdated() {
        Enemy[] enemyArray = enemies.toArray(new Enemy[0]);
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateEnemies(enemyArray);
        }
    }

    private void notifyWeaponUpdated(Weapon weapon) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateWeapon(weapon);
        }
    }

    private void notifyPlayerDamaged(int damage) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.playerDamaged(damage);
        }
    }

    private void notifyEnemyDamaged(Enemy enemy, int damage) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyDamaged(enemy, damage);
        }
    }

    private void notifyEnemyKilled(Enemy enemy, int exp) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyKilled(enemy, exp);
        }
    }

    private void notifyPlayerLeveledUp() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.playerLeveledUp();
        }
    }

    private void notifyGameStateChanged(GameState state) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.changedGameState(state);
        }
    }

    private void notifyTimeUpdate() {
        int remaining = getRemainingTime();
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateRemainingTime(remaining);
        }
    }

    private void notifyEnemyWaveSpawned(int wave, int count) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.enemyWaveSpawned(wave, count);
        }
    }

    private void notifyGameEnded(boolean victory) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.gameEnded(victory);
        }
    }

    private void notifyExperienceUpdated() {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateExperience(player.experience(), player.experienceToNextLevel());
        }
    }

    private void notifyConfigurationUpdated(GameConfiguration config) {
        for (ShapeSurvivorListener listener : listeners) {
            listener.updateGameConfiguration(config);
        }
    }
}
