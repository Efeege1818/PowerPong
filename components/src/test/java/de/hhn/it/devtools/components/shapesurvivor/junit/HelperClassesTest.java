package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import de.hhn.it.devtools.components.shapesurvivor.helper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Helper Classes Test")
class HelperClassesTest {

  // === PlayerState Tests ===

  @Test
  @DisplayName("PlayerState can be created and converted to Player")
  void testPlayerStateConversion() {
    PlayerState state = new PlayerState();
    state.setPosition(new Position(100, 200));
    state.setCurrentHealth(80);
    state.setMaxHealth(100);
    state.setMovementSpeed(5.0);
    state.setBaseDamage(10);
    state.setAttackSpeed(1.0);
    state.setDamageResistance(0.2);
    state.setLevel(5);
    state.setExperience(250);
    state.setExperienceToNextLevel(500);
    state.setWeapons(new Weapon[]{
        new Weapon(WeaponType.SWORD, "desc", 1, 10, 1.0, 100, true)
    });

    Player player = state.toPlayer();

    assertEquals(100, player.position().x());
    assertEquals(200, player.position().y());
    assertEquals(80, player.currentHealth());
    assertEquals(100, player.maxHealth());
    assertEquals(5.0, player.movementSpeed());
    assertEquals(10, player.baseDamage());
    assertEquals(1.0, player.attackSpeed());
    assertEquals(0.2, player.damageResistance());
    assertEquals(5, player.level());
    assertEquals(250, player.experience());
    assertEquals(500, player.experienceToNextLevel());
    assertEquals(1, player.equippedWeapons().length);
  }

  @Test
  @DisplayName("PlayerState getters work correctly")
  void testPlayerStateGetters() {
    PlayerState state = new PlayerState();
    Position pos = new Position(50, 60);
    Weapon[] weapons = new Weapon[]{
        new Weapon(WeaponType.AURA, "test", 2, 15, 1.5, 120, true)
    };

    state.setPosition(pos);
    state.setCurrentHealth(90);
    state.setMaxHealth(100);
    state.setMovementSpeed(6.5);
    state.setBaseDamage(12);
    state.setAttackSpeed(1.2);
    state.setDamageResistance(0.3);
    state.setLevel(3);
    state.setExperience(150);
    state.setExperienceToNextLevel(300);
    state.setWeapons(weapons);

    assertEquals(pos, state.getPosition());
    assertEquals(90, state.getCurrentHealth());
    assertEquals(100, state.getMaxHealth());
    assertEquals(6.5, state.getMovementSpeed());
    assertEquals(12, state.getBaseDamage());
    assertEquals(1.2, state.getAttackSpeed());
    assertEquals(0.3, state.getDamageResistance());
    assertEquals(3, state.getLevel());
    assertEquals(150, state.getExperience());
    assertEquals(300, state.getExperienceToNextLevel());
    assertArrayEquals(weapons, state.getWeapons());
  }

  // === EnemyState Tests ===

  @Test
  @DisplayName("EnemyState can be created from Enemy")
  void testEnemyStateCreation() {
    Enemy enemy = new Enemy(1, new Position(100, 200), 50, 100, 3.0, 10, 25);
    EnemyState state = new EnemyState(enemy);

    assertEquals(1, state.getId());
    assertEquals(100, state.getXpos());
    assertEquals(200, state.getYpos());
    assertEquals(50, state.getCurrentHealth());
    assertEquals(100, state.getMaxHealth());
    assertEquals(3.0, state.getSpeed());
    assertEquals(10, state.getContactDamage());
    assertEquals(25, state.getExperience());
  }

  @Test
  @DisplayName("EnemyState can be converted back to Enemy")
  void testEnemyStateToEnemy() {
    Enemy original = new Enemy(2, new Position(150, 250), 60, 100, 2.5, 15, 30);
    EnemyState state = new EnemyState(original);

    state.setXpos(160);
    state.setYpos(260);
    state.setCurrentHealth(40);

    Enemy converted = state.toEnemy();

    assertEquals(2, converted.id());
    assertEquals(160, converted.position().x());
    assertEquals(260, converted.position().y());
    assertEquals(40, converted.currentHealth());
    assertEquals(100, converted.maxHealth());
    assertEquals(2.5, converted.movementSpeed());
    assertEquals(15, converted.contactDamage());
    assertEquals(30, converted.experienceValue());
  }

  @Test
  @DisplayName("EnemyState setters work correctly")
  void testEnemyStateSetters() {
    Enemy enemy = new Enemy(3, new Position(0, 0), 100, 100, 2.0, 5, 10);
    EnemyState state = new EnemyState(enemy);

    state.setXpos(50);
    state.setYpos(75);
    state.setCurrentHealth(80);

    assertEquals(50, state.getXpos());
    assertEquals(75, state.getYpos());
    assertEquals(80, state.getCurrentHealth());
  }

  // === GameConfigurationBuilder Tests ===

  @Test
  @DisplayName("GameConfigurationBuilder creates Easy difficulty")
  void testEasyDifficulty() {
    GameConfiguration config = GameConfigurationBuilder.fromDifficulty(
        "Easy", WeaponType.SWORD, 800, 600);

    assertEquals(0.75, config.difficultyMultiplier());
    assertEquals(WeaponType.SWORD, config.initialWeapons()[0]);
  }

  @Test
  @DisplayName("GameConfigurationBuilder creates Normal difficulty")
  void testNormalDifficulty() {
    GameConfiguration config = GameConfigurationBuilder.fromDifficulty(
        "Normal", WeaponType.AURA, 800, 600);

    assertEquals(1.0, config.difficultyMultiplier());
  }

  @Test
  @DisplayName("GameConfigurationBuilder creates Hard difficulty")
  void testHardDifficulty() {
    GameConfiguration config = GameConfigurationBuilder.fromDifficulty(
        "Hard", WeaponType.WHIP, 800, 600);

    assertEquals(1.5, config.difficultyMultiplier());
  }

  @Test
  @DisplayName("GameConfigurationBuilder creates Nightmare difficulty")
  void testNightmareDifficulty() {
    GameConfiguration config = GameConfigurationBuilder.fromDifficulty(
        "Nightmare", WeaponType.SWORD, 800, 600);

    assertEquals(2.0, config.difficultyMultiplier());
  }

  @Test
  @DisplayName("GameConfigurationBuilder creates custom configuration")
  void testCustomConfiguration() {
    GameConfiguration config = GameConfigurationBuilder.custom(
        600, 1000, 800, 150, 6.0, 20, 1.5,
        WeaponType.SWORD, WeaponType.AURA);

    assertEquals(600, config.gameDurationSeconds());
    assertEquals(1000, config.fieldWidth());
    assertEquals(800, config.fieldHeight());
    assertEquals(150, config.startingPlayerHealth());
    assertEquals(6.0, config.startingPlayerSpeed());
    assertEquals(20, config.startingPlayerDamage());
    assertEquals(1.5, config.difficultyMultiplier());
    assertEquals(2, config.initialWeapons().length);
  }

  @Test
  @DisplayName("GameConfigurationBuilder creates default configuration")
  void testDefaultConfiguration() {
    GameConfiguration config = GameConfigurationBuilder.defaultConfiguration(800, 600);

    assertEquals(1.0, config.difficultyMultiplier());
    assertEquals(WeaponType.SWORD, config.initialWeapons()[0]);
  }

  // === UpgradeOptionFactory Tests ===

  @Test
  @DisplayName("UpgradeOptionFactory creates weapon upgrade")
  void testWeaponUpgrade() {
    UpgradeOption upgrade = UpgradeOptionFactory.createWeaponUpgrade(WeaponType.SWORD);

    assertEquals(UpgradeType.WEAPON, upgrade.type());
    assertEquals(WeaponType.SWORD, upgrade.weaponType());
    assertTrue(upgrade.name().contains("SWORD"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates new weapon for each type")
  void testNewWeaponForAllTypes() {
    for (WeaponType type : WeaponType.values()) {
      UpgradeOption upgrade = UpgradeOptionFactory.createNewWeapon(type);

      assertEquals(UpgradeType.NEW_WEAPON, upgrade.type());
      assertEquals(type, upgrade.weaponType());
      assertTrue(upgrade.name().contains("Unlock"));
      assertNotNull(upgrade.description());
    }
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates max health multiplier upgrade")
  void testMaxHealthMultiplierUpgrade() {
    UpgradeOption upgrade = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.MAX_HEALTH, 1.2, true);

    assertEquals(UpgradeType.ATTRIBUTE, upgrade.type());
    assertEquals(PlayerAttribute.MAX_HEALTH, upgrade.attribute());
    assertEquals(1.2, upgrade.value());
    assertTrue(upgrade.isMultiplier());
    assertTrue(upgrade.name().contains("%"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates max health flat upgrade")
  void testMaxHealthFlatUpgrade() {
    UpgradeOption upgrade = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.MAX_HEALTH, 20, false);

    assertEquals(UpgradeType.ATTRIBUTE, upgrade.type());
    assertEquals(PlayerAttribute.MAX_HEALTH, upgrade.attribute());
    assertEquals(20, upgrade.value());
    assertFalse(upgrade.isMultiplier());
    assertTrue(upgrade.name().contains("+20"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates movement speed upgrades")
  void testMovementSpeedUpgrades() {
    UpgradeOption multiplier = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.MOVEMENT_SPEED, 1.15, true);
    UpgradeOption flat = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.MOVEMENT_SPEED, 1.5, false);

    assertEquals(PlayerAttribute.MOVEMENT_SPEED, multiplier.attribute());
    assertEquals(PlayerAttribute.MOVEMENT_SPEED, flat.attribute());
    assertTrue(multiplier.name().contains("Movement Speed"));
    assertTrue(flat.name().contains("Movement Speed"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates damage upgrades")
  void testDamageUpgrades() {
    UpgradeOption upgrade = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.DAMAGE, 1.1, true);

    assertEquals(PlayerAttribute.DAMAGE, upgrade.attribute());
    assertTrue(upgrade.name().contains("Damage"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates attack speed upgrades")
  void testAttackSpeedUpgrades() {
    UpgradeOption upgrade = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.ATTACK_SPEED, 1.2, true);

    assertEquals(PlayerAttribute.ATTACK_SPEED, upgrade.attribute());
    assertTrue(upgrade.name().contains("Attack Speed"));
  }

  @Test
  @DisplayName("UpgradeOptionFactory creates damage resistance upgrade")
  void testDamageResistanceUpgrade() {
    UpgradeOption upgrade = UpgradeOptionFactory.createAttributeUpgrade(
        PlayerAttribute.DAMAGE_RESISTANCE, 0.05, false);

    assertEquals(PlayerAttribute.DAMAGE_RESISTANCE, upgrade.attribute());
    assertTrue(upgrade.name().contains("Damage Resistance"));
    assertTrue(upgrade.name().contains("5%"));
  }

  // === WeaponAnimationState Tests ===

  @Test
  @DisplayName("WeaponAnimationState initializes correctly")
  void testWeaponAnimationStateInit() {
    WeaponAnimationState state = new WeaponAnimationState();

    assertEquals(0, state.getAngle());
    assertTrue(state.isNotAttacking());
  }

  @Test
  @DisplayName("WeaponAnimationState updates angle")
  void testWeaponAnimationStateUpdate() {
    WeaponAnimationState state = new WeaponAnimationState();
    Weapon weapon = new Weapon(WeaponType.SWORD, "desc", 1, 10, 1.5, 100, true);

    double initialAngle = state.getAngle();
    state.update(weapon);
    double newAngle = state.getAngle();

    assertTrue(newAngle > initialAngle);
  }

  @Test
  @DisplayName("WeaponAnimationState angle wraps around")
  void testAngleWrapping() {
    WeaponAnimationState state = new WeaponAnimationState();
    Weapon weapon = new Weapon(WeaponType.SWORD, "desc", 1, 10, 10.0, 100, true);

    for (int i = 0; i < 100; i++) {
      state.update(weapon);
    }

    double angle = state.getAngle();
    assertTrue(angle >= 0 && angle < 2 * Math.PI);
  }

  @Test
  @DisplayName("WeaponAnimationState can attack")
  void testWeaponAttack() {
    WeaponAnimationState state = new WeaponAnimationState();

    assertTrue(state.canAttack());
    state.attack();
    assertFalse(state.isNotAttacking());
  }

  @Test
  @DisplayName("WeaponAnimationState attack cooldown works")
  void testAttackCooldown() throws InterruptedException {
    WeaponAnimationState state = new WeaponAnimationState();

    state.attack();
    assertFalse(state.canAttack());

    Thread.sleep(700);
    assertTrue(state.canAttack());
  }

  @Test
  @DisplayName("WeaponAnimationState alternates attack direction")
  void testAttackDirectionAlternates() {
    WeaponAnimationState state = new WeaponAnimationState();

    state.attack();
    boolean firstDirection = state.isAttackingLeft();

    try {
      Thread.sleep(700);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    state.attack();
    boolean secondDirection = state.isAttackingLeft();

    assertNotEquals(firstDirection, secondDirection);
  }

  @Test
  @DisplayName("WeaponAnimationState aura damage has cooldown")
  void testAuraDamageCooldown() throws InterruptedException {
    WeaponAnimationState state = new WeaponAnimationState();

    assertTrue(state.canDealAuraDamage());
    assertFalse(state.canDealAuraDamage());

    Thread.sleep(550);
    assertTrue(state.canDealAuraDamage());
  }

  @Test
  @DisplayName("WeaponAnimationState attack progress tracking")
  void testAttackProgress() throws InterruptedException {
    WeaponAnimationState state = new WeaponAnimationState();

    assertEquals(0, state.getAttackProgress());

    state.attack();
    Thread.sleep(50);

    long progress = state.getAttackProgress();
    assertTrue(progress > 0 && progress < 100);
  }
}