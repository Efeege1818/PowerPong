package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.shapesurvivor.Direction;
import de.hhn.it.devtools.apis.shapesurvivor.Enemy;
import de.hhn.it.devtools.apis.shapesurvivor.GameConfiguration;
import de.hhn.it.devtools.apis.shapesurvivor.GameLoopService;
import de.hhn.it.devtools.apis.shapesurvivor.GameState;
import de.hhn.it.devtools.apis.shapesurvivor.GameStatistics;
import de.hhn.it.devtools.apis.shapesurvivor.Player;
import de.hhn.it.devtools.apis.shapesurvivor.PlayerAttribute;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.apis.shapesurvivor.ShapeSurvivorListener;
import de.hhn.it.devtools.apis.shapesurvivor.ShapeSurvivorService;
import de.hhn.it.devtools.apis.shapesurvivor.UpgradeOption;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;
import de.hhn.it.devtools.apis.shapesurvivor.WeaponType;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;
import de.hhn.it.devtools.components.shapesurvivor.helper.PlayerState;
import de.hhn.it.devtools.components.shapesurvivor.helper.UpgradeOptionFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Main implementation of the ShapeSurvivor game service.
 */
public class SimpleShapeSurvivorService implements ShapeSurvivorService {

  private static final int DEFAULT_GAME_DURATION = 900; // 15 minutes
  private static final int DEFAULT_FIELD_WIDTH = 800;
  private static final int DEFAULT_FIELD_HEIGHT = 600;
  private final List<ShapeSurvivorListener> listeners;
  private GameLoopService gameLoop;
  private final List<UpgradeOption> availableUpgrades;
  private Supplier<Direction[]> inputProvider = () -> new Direction[0];

  private final GameContext gameContext;
  private final EventDispatcher events;
  private final WeaponSystem weaponSystem;
  private final EnemySystem enemySystem;
  private final PlayerSystem playerSystem;

  /**
   * Creates a new ShapeSurvivor game service.
   */
  public SimpleShapeSurvivorService() {
    GameConfiguration configuration = new GameConfiguration(
            DEFAULT_GAME_DURATION,
            DEFAULT_FIELD_WIDTH,
            DEFAULT_FIELD_HEIGHT,
            100,
            5.0,
            10,
            1,
            1.0,
            1.0,
            new WeaponType[]{WeaponType.SWORD});

    this.gameContext = new GameContext(configuration);
    this.listeners = new CopyOnWriteArrayList<>();
    this.availableUpgrades = new ArrayList<>();
    gameContext.setNextEnemyId(0);
    gameContext.setCurrentWave(0);
    this.events = new EventDispatcher(listeners, gameContext, this);
    this.playerSystem = new PlayerSystem(gameContext, events);
    this.weaponSystem = new WeaponSystem(gameContext, events, this);
    this.enemySystem = new EnemySystem(gameContext, events, this);

    initializePlayer();
    initializeStatistics();
    initializeGameLoop();
  }

  private void initializePlayer() {
    Position startPosition = new Position(
            gameContext.getConfiguration().fieldWidth() / 2,
            gameContext.getConfiguration().fieldHeight() / 2);

    Weapon[] weapons = createInitialWeapons();

    PlayerState state = new PlayerState();
    state.setPosition(startPosition);
    state.setCurrentHealth(gameContext.getConfiguration().startingPlayerHealth());
    state.setMaxHealth(gameContext.getConfiguration().startingPlayerHealth());
    state.setMovementSpeed(gameContext.getConfiguration().startingPlayerSpeed());
    state.setBaseDamage(gameContext.getConfiguration().startingPlayerDamage());
    state.setAttackSpeed(1.0);
    state.setDamageResistance(0.2);
    state.setLevel(1);
    state.setExperience(0);
    state.setExperienceToNextLevel(100);
    state.setWeapons(weapons);

    gameContext.setPlayer(state);

    for (Weapon weapon : weapons) {
      gameContext.getWeaponStates().put(weapon.type(), new WeaponAnimationState());
    }
  }

  private Weapon[] createInitialWeapons() {
    WeaponType[] types = gameContext.getConfiguration().initialWeapons();
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
      case SWORD ->
              new Weapon(
                      WeaponType.SWORD,
                      "Circles the player and damages enemies on contact",
                      level,
                      15 * level,
                      1.5 / level,
                      80.0 + (level * 10),
                      true);
      case AURA ->
              new Weapon(
                      WeaponType.AURA,
                      "Deals damage in a circle around the player",
                      level,
                      5 * level,
                      2.0 / level,
                      100.0 + (level * 15),
                      true);
      case WHIP ->
              new Weapon(
                      WeaponType.WHIP,
                      "Whips enemies in front and behind the player",
                      level,
                      25 * level,
                      1.2 / level,
                      120.0 + (level * 10),
                      true);
    };
  }

  private void initializeStatistics() {
    gameContext.setStatistics(
            new GameStatistics(0, 0, 0, 0, 1, 0, 0));
  }

  private void initializeGameLoop() {
    this.gameLoop = new SimpleGameLoopService(this::updateGame);
  }

  public void setInputProvider(Supplier<Direction[]> inputProvider) {
    this.inputProvider = inputProvider;
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
    if (gameContext.getGameState() != GameState.PREPARED) {
      throw new IllegalStateException("Game can only be started from PREPARED state");
    }

    gameContext.setGameState(GameState.RUNNING);
    gameContext.setGameStartTime(System.currentTimeMillis());
    gameContext.setLastWaveSpawnTime(gameContext.getGameStartTime());
    gameContext.setLastWeaponUpdateTime(gameContext.getGameStartTime());

    gameLoop.startLoop();
    events.notifyGameStateChanged(GameState.RUNNING);
  }

  @Override
  public void abort() throws IllegalStateException {
    if (gameContext.getGameState() == GameState.PREPARED
            || gameContext.getGameState() == GameState.ABORTED) {
      throw new IllegalStateException(
              "Cannot abort game in " + gameContext.getGameState() + " state");
    }

    if (gameLoop.isRunning()) {
      gameLoop.stopLoop();
    }

    gameContext.setGameState(GameState.ABORTED);
    events.notifyGameStateChanged(GameState.ABORTED);
  }

  @Override
  public void pause() throws IllegalStateException {
    if (gameContext.getGameState() != GameState.RUNNING) {
      throw new IllegalStateException("Can only pause game in RUNNING state");
    }

    gameLoop.pauseLoop();
    gameContext.setGameState(GameState.PAUSED);
    events.notifyGameStateChanged(GameState.PAUSED);
  }

  @Override
  public void resume() throws IllegalStateException {
    if (gameContext.getGameState() != GameState.PAUSED) {
      throw new IllegalStateException("Can only resume game from PAUSED state");
    }

    gameLoop.resumeLoop();
    gameContext.setGameState(GameState.RUNNING);
    events.notifyGameStateChanged(GameState.RUNNING);
  }

  /**
   * Moves the player in a single direction.
   *
   * @param direction the direction to move
   */
  public void movePlayer(Direction direction) {
    playerSystem.move(direction);
  }

  /**
   * Moves the player in multiple directions simultaneously (for diagonal movement).
   *
   * @param directions array of directions to move in
   */
  public void movePlayerMultiple(Direction[] directions) {
    playerSystem.moveMultiple(directions);
  }

  @Override
  public void applyUpgrade(UpgradeOption option)
          throws IllegalStateException, IllegalArgumentException {
    if (option == null) {
      throw new IllegalArgumentException("UpgradeOption cannot be null");
    }
    if (gameContext.getGameState() != GameState.RUNNING
            && gameContext.getGameState() != GameState.PAUSED) {
      throw new IllegalStateException(
              "Can only apply upgrades when game is RUNNING or PAUSED");
    }
    if (!gameContext.isLevelUpPending()) {
      throw new IllegalStateException("No level up is pending");
    }
    if (!availableUpgrades.contains(option)) {
      throw new IllegalArgumentException("UpgradeOption not available");
    }

    switch (option.type()) {
      case WEAPON -> upgradeExistingWeapon(option.weaponType());
      case NEW_WEAPON -> addNewWeapon(option.weaponType());
      case ATTRIBUTE -> upgradePlayerAttribute(
              option.attribute(), option.value(), option.isMultiplier());
      default -> {
        throw new IllegalArgumentException("Invalid upgrade option type");
      }
    }

    gameContext.setLevelUpPending(false);
    availableUpgrades.clear();
  }

  private void upgradeExistingWeapon(WeaponType weaponType) {
    List<Weapon> weapons = new ArrayList<>(
            Arrays.asList(gameContext.getPlayer().getWeapons()));

    for (int i = 0; i < weapons.size(); i++) {
      if (weapons.get(i).type() == weaponType) {
        Weapon oldWeapon = weapons.get(i);
        Weapon upgradedWeapon = createWeapon(weaponType, oldWeapon.level() + 1);
        weapons.set(i, upgradedWeapon);
        events.notifyWeaponUpdated(upgradedWeapon);
        break;
      }
    }

    gameContext.getPlayer().setWeapons(weapons.toArray(new Weapon[0]));
    events.notifyPlayerUpdated();
  }

  private void addNewWeapon(WeaponType weaponType) {
    List<Weapon> weapons = new ArrayList<>(
            Arrays.asList(gameContext.getPlayer().getWeapons()));
    Weapon newWeapon = createWeapon(weaponType, 1);
    weapons.add(newWeapon);
    gameContext.getWeaponStates().put(weaponType, new WeaponAnimationState());
    events.notifyWeaponUpdated(newWeapon);

    gameContext.getPlayer().setWeapons(weapons.toArray(new Weapon[0]));
    events.notifyPlayerUpdated();
  }

  private void upgradePlayerAttribute(PlayerAttribute attribute,
                                      double value,
                                      boolean isMultiplier) {
    PlayerState player = gameContext.getPlayer();

    switch (attribute) {
      case MAX_HEALTH -> {
        if (isMultiplier) {
          player.setMaxHealth((int) (player.getMaxHealth() * value));
        } else {
          player.setMaxHealth(player.getMaxHealth() + (int) value);
        }
        player.setCurrentHealth(player.getMaxHealth());
      }
      case MOVEMENT_SPEED -> {
        if (isMultiplier) {
          player.setMovementSpeed(player.getMovementSpeed() * value);
        } else {
          player.setMovementSpeed(player.getMovementSpeed() + value);
        }
      }
      case DAMAGE -> {
        if (isMultiplier) {
          player.setBaseDamage((int) (player.getBaseDamage() * value));
        } else {
          player.setBaseDamage(player.getBaseDamage() + (int) value);
        }
      }
      case ATTACK_SPEED -> {
        if (isMultiplier) {
          player.setAttackSpeed(player.getAttackSpeed() * value);
        } else {
          player.setAttackSpeed(player.getAttackSpeed() + value);
        }
      }
      case DAMAGE_RESISTANCE -> {
        if (isMultiplier) {
          player.setDamageResistance(player.getDamageResistance() * value);
        } else {
          player.setDamageResistance(player.getDamageResistance() + value);
        }
      }
      default -> { }
    }
    events.notifyPlayerUpdated();
  }

  @Override
  public UpgradeOption[] getAvailableUpgrades() throws IllegalStateException {
    if (!gameContext.isLevelUpPending()) {
      throw new IllegalStateException("No level up is pending");
    }
    return availableUpgrades.toArray(new UpgradeOption[0]);
  }

  @Override
  public boolean addListener(ShapeSurvivorListener listener)
          throws IllegalArgumentException {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(ShapeSurvivorListener listener)
          throws IllegalArgumentException {
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null");
    }
    return listeners.remove(listener);
  }

  @Override
  public void configure(GameConfiguration configuration)
          throws IllegalStateException, IllegalArgumentException, IllegalParameterException {
    if (gameContext.getGameState() != GameState.PREPARED
            && gameContext.getGameState() != GameState.ABORTED) {
      throw new IllegalStateException(
              "Can only configure in PREPARED or ABORTED state");
    }
    if (configuration == null) {
      throw new IllegalArgumentException("Configuration cannot be null");
    }

    validateConfiguration(configuration);

    gameContext.setConfiguration(configuration);
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
    return gameContext.getConfiguration();
  }

  @Override
  public GameState getGameState() {
    return gameContext.getGameState();
  }

  @Override
  public Player getPlayer() {
    return gameContext.getPlayer().toPlayer();
  }

  @Override
  public Enemy[] getEnemies() {
    return gameContext.getEnemiesSnapshot().toArray(new Enemy[0]);
  }

  @Override
  public boolean isLevelUpPending() {
    return gameContext.isLevelUpPending();
  }

  @Override
  public GameStatistics getStatistics() {
    return gameContext.getStatistics();
  }

  @Override
  public int getElapsedTime() throws IllegalStateException {
    if (gameContext.getGameState() == GameState.PREPARED
            || gameContext.getGameState() == GameState.ABORTED) {
      throw new IllegalStateException(
              "No time elapsed in " + gameContext.getGameState() + " state");
    }
    return (int) ((System.currentTimeMillis() - gameContext.getGameStartTime()) / 1000);
  }

  @Override
  public int getRemainingTime() throws IllegalStateException {
    if (gameContext.getGameState() == GameState.PREPARED
            || gameContext.getGameState() == GameState.ABORTED) {
      throw new IllegalStateException(
              "No time remaining in " + gameContext.getGameState() + " state");
    }
    int elapsed = getElapsedTime();
    return Math.max(0, gameContext.getConfiguration().gameDurationSeconds() - elapsed);
  }

  public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
    return new HashMap<>(gameContext.getWeaponStates());
  }

  private void updateGame() {
    if (gameContext.getGameState() != GameState.RUNNING) {
      return;
    }

    Direction[] activeDirections = inputProvider.get();
    if (activeDirections.length > 0) {
      playerSystem.moveMultiple(activeDirections);
    }
    // Check win condition
    if (getRemainingTime() <= 0) {
      endGame(true);
      return;
    }
    weaponSystem.update(System.currentTimeMillis());
    enemySystem.update(System.currentTimeMillis());

    if (gameContext.getPlayer().getCurrentHealth() <= 0) {
      endGame(false);
    }
    events.notifyTimeUpdate();
  }

  void damagePlayer(int damage) {
    playerSystem.damage(damage);
  }

  void gainExperience(int exp) {
    PlayerState player = gameContext.getPlayer();
    int newExp = player.getExperience() + exp;
    int expToNext = player.getExperienceToNextLevel();

    gameContext.setStatistics(new GameStatistics(
            gameContext.getStatistics().enemiesKilled(),
            gameContext.getStatistics().damageDealt(),
            gameContext.getStatistics().damageTaken(),
            gameContext.getStatistics().wavesCompleted(),
            gameContext.getStatistics().highestLevel(),
            gameContext.getStatistics().totalExperienceGained() + exp,
            getElapsedTime()));

    if (newExp >= expToNext) {
      player.setLevel(player.getLevel() + 1);
      player.setExperience(0);
      player.setExperienceToNextLevel((int) (expToNext * 1.5));
      gameContext.setLevelUpPending(true);
      generateUpgradeOptions();
      events.notifyPlayerLeveledUp();
    } else {
      player.setExperience(newExp);
    }

    events.notifyExperienceUpdated();
  }

  private void generateUpgradeOptions() {
    availableUpgrades.clear();

    if (gameContext.getPlayer().getLevel() % 5 == 0) {
      List<WeaponType> unownedWeapons = new ArrayList<>();
      for (WeaponType type : WeaponType.values()) {
        boolean hasWeapon = false;
        for (Weapon w : gameContext.getPlayer().getWeapons()) {
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
        List<Weapon> weapons = Arrays.asList(gameContext.getPlayer().getWeapons());
        Collections.shuffle(weapons);
        for (int i = 0; i < Math.min(3, weapons.size()); i++) {
          availableUpgrades.add(
                  UpgradeOptionFactory.createWeaponUpgrade(weapons.get(i).type()));
        }
      } else {
        Collections.shuffle(unownedWeapons);
        for (int i = 0; i < Math.min(3, unownedWeapons.size()); i++) {
          availableUpgrades.add(
                  UpgradeOptionFactory.createNewWeapon(unownedWeapons.get(i)));
        }
      }
    } else {
      List<UpgradeOption> possibleUpgrades = new ArrayList<>();

      for (Weapon weapon : gameContext.getPlayer().getWeapons()) {
        possibleUpgrades.add(
                UpgradeOptionFactory.createWeaponUpgrade(weapon.type()));
      }

      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.MAX_HEALTH, 1.2, true));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.MAX_HEALTH, 20, false));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.MOVEMENT_SPEED, 1.15, true));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.MOVEMENT_SPEED, 1.5, false));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.DAMAGE, 1.1, true));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.DAMAGE, 5, false));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.ATTACK_SPEED, 1.2, true));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.ATTACK_SPEED, 0.2, false));
      possibleUpgrades.add(
              UpgradeOptionFactory.createAttributeUpgrade(
                      PlayerAttribute.DAMAGE_RESISTANCE, 0.05, false));

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
    gameContext.setGameState(GameState.ABORTED);
    events.notifyGameEnded(victory);
    events.notifyGameStateChanged(GameState.ABORTED);
  }

  public GameMap getGameMap() {
    return gameContext.getGameMap();
  }
}