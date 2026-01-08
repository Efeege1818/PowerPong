package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;
import de.hhn.it.devtools.components.shapesurvivor.helper.UpgradeOptionFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleShapeSurvivorService implements ShapeSurvivorService {

    private static final int DEFAULT_GAME_DURATION = 900; // 15 minutes
    private static final int DEFAULT_FIELD_WIDTH = 800;
    private static final int DEFAULT_FIELD_HEIGHT = 600;
    private final List<ShapeSurvivorListener> listeners;
    private GameLoopService gameLoop;
    private final List<UpgradeOption> availableUpgrades;
    private static final long PLAYER_HIT_COOLDOWN_MS = 600;

    private final GameContext gameContext;
    private final EventDispatcher events;
    private final WeaponSystem weaponSystem;

    public SimpleShapeSurvivorService() {
        GameConfiguration configuration = new GameConfiguration(
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

        this.gameContext = new GameContext(configuration);
        this.listeners = new CopyOnWriteArrayList<>();
        this.availableUpgrades = new ArrayList<>();
        gameContext.nextEnemyId = 0;
        gameContext.currentWave = 0;
        this.events = new EventDispatcher(listeners, gameContext, this);
        this.weaponSystem = new WeaponSystem(gameContext, events, this);
        initializePlayer();
        initializeStatistics();
        initializeGameLoop();
    }

    private void initializePlayer() {
        Position startPosition = new Position(
                gameContext.configuration.fieldWidth() / 2,
                gameContext.configuration.fieldHeight() / 2
        );

        Weapon[] initialWeapons = createInitialWeapons();

        gameContext.player = new Player(
                startPosition,
                gameContext.configuration.startingPlayerHealth(),
                gameContext.configuration.startingPlayerHealth(),
                gameContext.configuration.startingPlayerSpeed(),
                gameContext.configuration.startingPlayerDamage(),
                1.0, // attack speed
                0.2, // damage resistance
                1,   // level
                0,   // experience
                100, // experience to next level
                initialWeapons
        );

        // Initialize weapon states
        for (Weapon weapon : initialWeapons) {
            gameContext.weaponStates.put(weapon.type(), new WeaponAnimationState());
        }
    }

    private Weapon[] createInitialWeapons() {
        WeaponType[] types = gameContext.configuration.initialWeapons();
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
                    level, 5 * level, 2.0 / level, 100.0 + (level * 15), true  // Reduced from 8 to 5
            );
            case WHIP -> new Weapon(
                    WeaponType.WHIP,
                    "Whips enemies in front and behind the player",
                    level, 25 * level, 1.2 / level, 120.0 + (level * 10), true
            );
        };
    }

    private void initializeStatistics() {
        gameContext.statistics = new GameStatistics(0, 0, 0, 0, 1, 0, 0);
    }

    private void initializeGameLoop() {
        this.gameLoop = new SimpleGameLoopService(this::updateGame);
    }

    @Override
    public void reset() {
        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }
        gameContext.reset();
        availableUpgrades.clear();
        initializePlayer();
        initializeStatistics();

        events.notifyPlayerUpdated();
        events.notifyEnemiesUpdated();
        events.notifyExperienceUpdated();
        events.notifyGameStateChanged(GameState.PREPARED);
    }

    @Override
    public void start() throws IllegalStateException {
        if (gameContext.gameState != GameState.PREPARED) {
            throw new IllegalStateException("Game can only be started from PREPARED state");
        }

        gameContext.gameState = GameState.RUNNING;
        gameContext.gameStartTime = System.currentTimeMillis();
        gameContext.lastWaveSpawnTime = gameContext.gameStartTime;
        gameContext.lastWeaponUpdateTime = gameContext.gameStartTime;

        gameLoop.startLoop();
        events.notifyGameStateChanged(GameState.RUNNING);
    }

    @Override
    public void abort() throws IllegalStateException {
        if (gameContext.gameState == GameState.PREPARED || gameContext.gameState == GameState.ABORTED) {
            throw new IllegalStateException("Cannot abort game in " + gameContext.gameState + " state");
        }

        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }

        gameContext.gameState = GameState.ABORTED;
        events.notifyGameStateChanged(GameState.ABORTED);
    }

    @Override
    public void pause() throws IllegalStateException {
        if (gameContext.gameState != GameState.RUNNING) {
            throw new IllegalStateException("Can only pause game in RUNNING state");
        }

        gameLoop.pauseLoop();
        gameContext.gameState = GameState.PAUSED;
        events.notifyGameStateChanged(GameState.PAUSED);
    }

    @Override
    public void resume() throws IllegalStateException {
        if (gameContext.gameState != GameState.PAUSED) {
            throw new IllegalStateException("Can only resume game from PAUSED state");
        }

        gameLoop.resumeLoop();
        gameContext.gameState = GameState.RUNNING;
        events.notifyGameStateChanged(GameState.RUNNING);
    }

    public void movePlayer(Direction direction) {
        if (gameContext.player == null) return;

        Position oldPos = gameContext.player.position();
        int x = oldPos.x();
        int y = oldPos.y();
        int speed = (int) gameContext.player.movementSpeed();

        switch (direction) {
            case UP -> y -= speed;
            case DOWN -> y += speed;
            case LEFT -> x -= speed;
            case RIGHT -> x += speed;
        }

        // Keep player inside bounds
        x = Math.max(0, Math.min(x, gameContext.configuration.fieldWidth()));
        y = Math.max(0, Math.min(y, gameContext.configuration.fieldHeight()));

        // Update the player
        gameContext.player = new Player(
                new Position(x, y),
                gameContext.player.currentHealth(),
                gameContext.player.maxHealth(),
                gameContext.player.movementSpeed(),
                gameContext.player.baseDamage(),
                gameContext.player.attackSpeed(),
                gameContext.player.damageResistance(),
                gameContext.player.level(),
                gameContext.player.experience(),
                gameContext.player.experienceToNextLevel(),
                gameContext.player.equippedWeapons()
        );

        events.notifyPlayerUpdated();
    }

    @Override
    public void applyUpgrade(UpgradeOption option)
            throws IllegalStateException, IllegalArgumentException {
        if (option == null) {
            throw new IllegalArgumentException("UpgradeOption cannot be null");
        }
        if (gameContext.gameState != GameState.RUNNING && gameContext.gameState != GameState.PAUSED) {
            throw new IllegalStateException("Can only apply upgrades when game is RUNNING or PAUSED");
        }
        if (!gameContext.levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }
        if (!availableUpgrades.contains(option)) {
            throw new IllegalArgumentException("UpgradeOption not available");
        }

        switch (option.type()) {
            case WEAPON -> upgradeExistingWeapon(option.weaponType());
            case NEW_WEAPON -> addNewWeapon(option.weaponType());
            case ATTRIBUTE -> upgradePlayerAttribute(option.attribute(), option.value(), option.isMultiplier());
        }

        gameContext.levelUpPending = false;
        availableUpgrades.clear();
    }

    private void upgradeExistingWeapon(WeaponType weaponType) {
        List<Weapon> weapons = new ArrayList<>(Arrays.asList(gameContext.player.equippedWeapons()));

        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).type() == weaponType) {
                Weapon oldWeapon = weapons.get(i);
                Weapon upgradedWeapon = createWeapon(weaponType, oldWeapon.level() + 1);
                weapons.set(i, upgradedWeapon);
                events.notifyWeaponUpdated(upgradedWeapon);
                break;
            }
        }

        gameContext.player = new Player(
                gameContext.player.position(),
                gameContext.player.currentHealth(),
                gameContext.player.maxHealth(),
                gameContext.player.movementSpeed(),
                gameContext.player.baseDamage(),
                gameContext.player.attackSpeed(),
                gameContext.player.damageResistance(),
                gameContext.player.level(),
                gameContext.player.experience(),
                gameContext.player.experienceToNextLevel(),
                weapons.toArray(new Weapon[0])
        );
    }

    private void addNewWeapon(WeaponType weaponType) {
        List<Weapon> weapons = new ArrayList<>(Arrays.asList(gameContext.player.equippedWeapons()));
        Weapon newWeapon = createWeapon(weaponType, 1);
        weapons.add(newWeapon);
        gameContext.weaponStates.put(weaponType, new WeaponAnimationState());
        events.notifyWeaponUpdated(newWeapon);

        gameContext.player = new Player(
                gameContext.player.position(),
                gameContext.player.currentHealth(),
                gameContext.player.maxHealth(),
                gameContext.player.movementSpeed(),
                gameContext.player.baseDamage(),
                gameContext.player.attackSpeed(),
                gameContext.player.damageResistance(),
                gameContext.player.level(),
                gameContext.player.experience(),
                gameContext.player.experienceToNextLevel(),
                weapons.toArray(new Weapon[0])
        );
    }

    private void upgradePlayerAttribute(PlayerAttribute attribute, double value, boolean isMultiplier) {
        int newMaxHealth = gameContext.player.maxHealth();
        int newCurrentHealth = gameContext.player.currentHealth();
        double newMovementSpeed = gameContext.player.movementSpeed();
        int newBaseDamage = gameContext.player.baseDamage();
        double newAttackSpeed = gameContext.player.attackSpeed();
        double newDamageResistance = gameContext.player.damageResistance();

        switch (attribute) {
            case MAX_HEALTH -> {
                newMaxHealth = isMultiplier ?
                        (int) (gameContext.player.maxHealth() * value) :
                        gameContext.player.maxHealth() + (int) value;
                newCurrentHealth = newMaxHealth;
            }
            case MOVEMENT_SPEED -> newMovementSpeed = isMultiplier ?
                    gameContext.player.movementSpeed() * value :
                    gameContext.player.movementSpeed() + value;
            case DAMAGE -> newBaseDamage = isMultiplier ?
                    (int) (gameContext.player.baseDamage() * value) :
                    gameContext.player.baseDamage() + (int) value;
            case ATTACK_SPEED -> newAttackSpeed = isMultiplier ?
                    gameContext.player.attackSpeed() * value :
                    gameContext.player.attackSpeed() + value;
            case DAMAGE_RESISTANCE -> newDamageResistance = isMultiplier ?
                    gameContext.player.damageResistance() * value :
                    Math.min(0.9, gameContext.player.damageResistance() + value);
        }

        gameContext.player = new Player(
                gameContext.player.position(),
                newCurrentHealth,
                newMaxHealth,
                newMovementSpeed,
                newBaseDamage,
                newAttackSpeed,
                newDamageResistance,
                gameContext.player.level(),
                gameContext.player.experience(),
                gameContext.player.experienceToNextLevel(),
                gameContext.player.equippedWeapons()
        );

        events.notifyPlayerUpdated();
    }

    @Override
    public UpgradeOption[] getAvailableUpgrades() throws IllegalStateException {
        if (!gameContext.levelUpPending) {
            throw new IllegalStateException("No level up is pending");
        }
        return availableUpgrades.toArray(new UpgradeOption[0]);
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
        if (gameContext.gameState != GameState.PREPARED && gameContext.gameState != GameState.ABORTED) {
            throw new IllegalStateException("Can only configure in PREPARED or ABORTED state");
        }
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        validateConfiguration(configuration);

        gameContext.configuration = configuration;
        initializePlayer();

        events.notifyConfigurationUpdated(configuration);
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
        return gameContext.configuration;
    }

    @Override
    public GameState getGameState() {
        return gameContext.gameState;
    }

    @Override
    public Player getPlayer() {
        return gameContext.player;
    }

    @Override
    public Enemy[] getEnemies() {
        return gameContext.enemies.toArray(new Enemy[0]);
    }

    @Override
    public boolean isLevelUpPending() {
        return gameContext.levelUpPending;
    }

    @Override
    public GameStatistics getStatistics() {
        return gameContext.statistics;
    }

    @Override
    public int getElapsedTime() throws IllegalStateException {
        if (gameContext.gameState == GameState.PREPARED || gameContext.gameState == GameState.ABORTED) {
            throw new IllegalStateException("No time elapsed in " + gameContext.gameState + " state");
        }
        return (int) ((System.currentTimeMillis() - gameContext.gameStartTime) / 1000);
    }

    @Override
    public int getRemainingTime() throws IllegalStateException {
        if (gameContext.gameState == GameState.PREPARED || gameContext.gameState == GameState.ABORTED) {
            throw new IllegalStateException("No time remaining in " + gameContext.gameState + " state");
        }
        int elapsed = getElapsedTime();
        return Math.max(0, gameContext.configuration.gameDurationSeconds() - elapsed);
    }

    public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
        return new HashMap<>(gameContext.weaponStates);
    }

    private void updateGame() {
        if (gameContext.gameState != GameState.RUNNING) {
            return;
        }

        // Check win condition
        if (getRemainingTime() <= 0) {
            endGame(true);
            return;
        }

        spawnEnemies();
        updateEnemies();
        weaponSystem.update(System.currentTimeMillis());

        if (gameContext.player.currentHealth() <= 0) {
            endGame(false);
        }
        events.notifyTimeUpdate();
    }

    private void spawnEnemies() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastWave = currentTime - gameContext.lastWaveSpawnTime;

        // Spawn wave every 10 seconds
        if (timeSinceLastWave >= 10000) {
            gameContext.currentWave++;
            int enemyCount = (int) (5 * gameContext.currentWave * gameContext.configuration.enemySpawnRate());

            for (int i = 0; i < enemyCount; i++) {
                spawnEnemy();
            }

            gameContext.lastWaveSpawnTime = currentTime;
            events.notifyEnemyWaveSpawned(gameContext.currentWave, enemyCount);

            gameContext.statistics = new GameStatistics(
                    gameContext.statistics.enemiesKilled(),
                    gameContext.statistics.damageDealt(),
                    gameContext.statistics.damageTaken(),
                    gameContext.currentWave,
                    gameContext.statistics.highestLevel(),
                    gameContext.statistics.totalExperienceGained(),
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
                x = random.nextInt(gameContext.configuration.fieldWidth());
                yield -20;
            }
            case 1 -> {
                x = gameContext.configuration.fieldWidth() + 20;
                yield random.nextInt(gameContext.configuration.fieldHeight());
            }
            case 2 -> {
                x = random.nextInt(gameContext.configuration.fieldWidth());
                yield gameContext.configuration.fieldHeight() + 20;
            }
            default -> {
                x = -20;
                yield random.nextInt(gameContext.configuration.fieldHeight());
            }
        };

        int health = (int) (50 * gameContext.configuration.difficultyMultiplier());

        Enemy enemy = new Enemy(
                gameContext.nextEnemyId++,
                new Position(x, y),
                health,
                health,
                2.0,
                10,
                20
        );

        gameContext.enemies.add(enemy);
    }

    private void updateEnemies() {
        List<Enemy> updatedEnemies = new ArrayList<>();
        Position playerPos = gameContext.player.position();

        for (Enemy enemy : gameContext.enemies) {
            Position enemyPos = enemy.position();
            int dx = playerPos.x() - enemyPos.x();
            int dy = playerPos.y() - enemyPos.y();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance > 0) {
                int newX = enemyPos.x() + (int) ((dx / distance) * enemy.movementSpeed());
                int newY = enemyPos.y() + (int) ((dy / distance) * enemy.movementSpeed());

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
                    if (now - gameContext.lastPlayerHitTime >= PLAYER_HIT_COOLDOWN_MS) {
                        damagePlayer(enemy.contactDamage());
                        gameContext.lastPlayerHitTime = now;
                    }
                }

                updatedEnemies.add(movedEnemy);
            }
        }

        gameContext.enemies = updatedEnemies;
        events.notifyEnemiesUpdated();
    }

    private void damagePlayer(int damage) {
        int actualDamage = (int) (damage * (1 - gameContext.player.damageResistance()));
        int newHealth = Math.max(0, gameContext.player.currentHealth() - actualDamage);

        gameContext.player = new Player(
                gameContext.player.position(),
                newHealth,
                gameContext.player.maxHealth(),
                gameContext.player.movementSpeed(),
                gameContext.player.baseDamage(),
                gameContext.player.attackSpeed(),
                gameContext.player.damageResistance(),
                gameContext.player.level(),
                gameContext.player.experience(),
                gameContext.player.experienceToNextLevel(),
                gameContext.player.equippedWeapons()
        );

        gameContext.statistics = new GameStatistics(
                gameContext.statistics.enemiesKilled(),
                gameContext.statistics.damageDealt(),
                gameContext.statistics.damageTaken() + actualDamage,
                gameContext.statistics.wavesCompleted(),
                gameContext.statistics.highestLevel(),
                gameContext.statistics.totalExperienceGained(),
                getElapsedTime()
        );

        events.notifyPlayerDamaged(actualDamage);
        events.notifyPlayerUpdated();
    }

    void gainExperience(int exp) {
        int newExp = gameContext.player.experience() + exp;
        int expToNext = gameContext.player.experienceToNextLevel();

        gameContext.statistics = new GameStatistics(
                gameContext.statistics.enemiesKilled(),
                gameContext.statistics.damageDealt(),
                gameContext.statistics.damageTaken(),
                gameContext.statistics.wavesCompleted(),
                gameContext.statistics.highestLevel(),
                gameContext.statistics.totalExperienceGained() + exp,
                getElapsedTime()
        );

        if (newExp >= expToNext) {
            // Level up
            int newLevel = gameContext.player.level() + 1;
            newExp -= expToNext;
            expToNext = (int) (expToNext * 1.5);

            gameContext.player = new Player(
                    gameContext.player.position(),
                    gameContext.player.currentHealth(),
                    gameContext.player.maxHealth(),
                    gameContext.player.movementSpeed(),
                    gameContext.player.baseDamage(),
                    gameContext.player.attackSpeed(),
                    gameContext.player.damageResistance(),
                    newLevel,
                    newExp,
                    expToNext,
                    gameContext.player.equippedWeapons()
            );

            gameContext.levelUpPending = true;
            generateUpgradeOptions();
            events.notifyPlayerLeveledUp();
        } else {
            gameContext.player = new Player(
                    gameContext.player.position(),
                    gameContext.player.currentHealth(),
                    gameContext.player.maxHealth(),
                    gameContext.player.movementSpeed(),
                    gameContext.player.baseDamage(),
                    gameContext.player.attackSpeed(),
                    gameContext.player.damageResistance(),
                    gameContext.player.level(),
                    newExp,
                    expToNext,
                    gameContext.player.equippedWeapons()
            );
        }

        events.notifyExperienceUpdated();
    }

    private void generateUpgradeOptions() {
        availableUpgrades.clear();

        if (gameContext.player.level() % 5 == 0) {
            List<WeaponType> unownedWeapons = new ArrayList<>();
            for (WeaponType type : WeaponType.values()) {
                boolean hasWeapon = false;
                for (Weapon w : gameContext.player.equippedWeapons()) {
                    if (w.type() == type) {
                        hasWeapon = true;
                        break;
                    }
                }
                if (!hasWeapon) {
                    unownedWeapons.add(type);
                }
            }

            if (unownedWeapons.isEmpty()) {
                List<Weapon> weapons = Arrays.asList(gameContext.player.equippedWeapons());
                Collections.shuffle(weapons);
                for (int i = 0; i < Math.min(3, weapons.size()); i++) {
                    availableUpgrades.add(
                            UpgradeOptionFactory.createWeaponUpgrade(weapons.get(i).type())
                    );
                }
            } else {
                // Offer up to 3 new weapons
                Collections.shuffle(unownedWeapons);
                for (int i = 0; i < Math.min(3, unownedWeapons.size()); i++) {
                    availableUpgrades.add(
                            UpgradeOptionFactory.createNewWeapon(unownedWeapons.get(i))
                    );
                }
            }
        } else {
            List<UpgradeOption> possibleUpgrades = new ArrayList<>();

            for (Weapon weapon : gameContext.player.equippedWeapons()) {
                possibleUpgrades.add(
                        UpgradeOptionFactory.createWeaponUpgrade(weapon.type())
                );
            }

            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.MAX_HEALTH, 1.2, true)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.MAX_HEALTH, 20, false)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.MOVEMENT_SPEED, 1.15, true)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.MOVEMENT_SPEED, 1.5, false)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.DAMAGE, 1.1, true)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.DAMAGE, 5, false)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.ATTACK_SPEED, 1.2, true)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.ATTACK_SPEED, 0.2, false)
            );
            possibleUpgrades.add(
                    UpgradeOptionFactory.createAttributeUpgrade(PlayerAttribute.DAMAGE_RESISTANCE, 0.05, false)
            );

            Collections.shuffle(possibleUpgrades);
            for (int i = 0; i < Math.min(3, possibleUpgrades.size()); i++) {
                availableUpgrades.add(possibleUpgrades.get(i));
            }
        }
    }

    private void endGame(boolean victory) {
        if (gameLoop.isRunning()) {
            gameLoop.stopLoop();
        }
        gameContext.gameState = GameState.ABORTED;
        events.notifyGameEnded(victory);
        events.notifyGameStateChanged(GameState.ABORTED);
    }

    private double getDistance(Position p1, Position p2) {
        int dx = p1.x() - p2.x();
        int dy = p1.y() - p2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}