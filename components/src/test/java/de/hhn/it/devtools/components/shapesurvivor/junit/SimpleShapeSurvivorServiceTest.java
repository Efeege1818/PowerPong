package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SimpleShapeSurvivorService test")
class SimpleShapeSurvivorServiceTest {

    private SimpleShapeSurvivorService service;
    private TestListener listener;

    @BeforeEach
    void setUp() {
        service = new SimpleShapeSurvivorService();
        listener = new TestListener();
        service.addListener(listener);
    }

    @AfterEach
    void tearDown() {
        try {
            if (service.getGameState() == GameState.RUNNING ||
                service.getGameState() == GameState.PAUSED) {
                service.abort();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    // === Initialization Tests ===

    @Test
    @DisplayName("Service initializes in PREPARED state")
    void testInitialState() {
        assertEquals(GameState.PREPARED, service.getGameState());
        assertNotNull(service.getPlayer());
        assertNotNull(service.getConfiguration());
        assertEquals(0, service.getEnemies().length);
        assertFalse(service.isLevelUpPending());
    }

    @Test
    @DisplayName("Player starts with correct initial values")
    void testPlayerInitialization() {
        Player player = service.getPlayer();

        assertEquals(100, player.currentHealth());
        assertEquals(100, player.maxHealth());
        assertEquals(5.0, player.movementSpeed());
        assertEquals(10, player.baseDamage());
        assertEquals(1, player.level());
        assertEquals(0, player.experience());
        assertEquals(100, player.experienceToNextLevel());
        assertEquals(1, player.equippedWeapons().length);
        assertEquals(WeaponType.SWORD, player.equippedWeapons()[0].type());
    }

    @Test
    @DisplayName("Statistics are initialized correctly")
    void testStatisticsInitialization() {
        GameStatistics stats = service.getStatistics();

        assertNotNull(stats);
        assertEquals(0, stats.enemiesKilled());
        assertEquals(0, stats.damageDealt());
        assertEquals(0, stats.damageTaken());
        assertEquals(0, stats.wavesCompleted());
        assertEquals(1, stats.highestLevel());
        assertEquals(0, stats.totalExperienceGained());
    }

    // === Game State Management Tests ===

    @Test
    @DisplayName("Game can be started from PREPARED state")
    void testStartGame() {
        service.start();

        assertEquals(GameState.RUNNING, service.getGameState());
        assertTrue(listener.gameStateChanged);
        assertEquals(GameState.RUNNING, listener.lastGameState);
    }

    @Test
    @DisplayName("Cannot start game from non-PREPARED state")
    void testStartGameInvalidState() {
        service.start();
        assertThrows(IllegalStateException.class, () -> service.start());
    }

    @Test
    @DisplayName("Game can be paused and resumed")
    void testPauseAndResume() {
        service.start();

        service.pause();
        assertEquals(GameState.PAUSED, service.getGameState());

        service.resume();
        assertEquals(GameState.RUNNING, service.getGameState());
    }

    @Test
    @DisplayName("Cannot pause game that is not running")
    void testPauseInvalidState() {
        assertThrows(IllegalStateException.class, () -> service.pause());
    }

    @Test
    @DisplayName("Cannot resume game that is not paused")
    void testResumeInvalidState() {
        service.start();
        assertThrows(IllegalStateException.class, () -> service.resume());
    }

    @Test
    @DisplayName("Game can be aborted")
    void testAbortGame() {
        service.start();
        service.abort();

        assertEquals(GameState.ABORTED, service.getGameState());
    }

    @Test
    @DisplayName("Cannot abort game in PREPARED state")
    void testAbortInPreparedState() {
        assertThrows(IllegalStateException.class, () -> service.abort());
    }

    @Test
    @DisplayName("Cannot abort game in ABORTED state")
    void testAbortInAbortedState() {
        service.start();
        service.abort();
        assertThrows(IllegalStateException.class, () -> service.abort());
    }

    @Test
    @DisplayName("Game can be reset")
    void testReset() {
        service.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        service.reset();

        assertEquals(GameState.PREPARED, service.getGameState());
        assertEquals(0, service.getEnemies().length);
        assertFalse(service.isLevelUpPending());
    }

    // === Player Movement Tests ===

    @Test
    @DisplayName("Player can move UP")
    void testPlayerMoveUp() {
        Player before = service.getPlayer();
        service.movePlayer(Direction.UP);
        Player after = service.getPlayer();

        assertTrue(after.position().y() < before.position().y());
    }

    @Test
    @DisplayName("Player can move DOWN")
    void testPlayerMoveDown() {
        Player before = service.getPlayer();
        service.movePlayer(Direction.DOWN);
        Player after = service.getPlayer();

        assertTrue(after.position().y() > before.position().y());
    }

    @Test
    @DisplayName("Player can move LEFT")
    void testPlayerMoveLeft() {
        Player before = service.getPlayer();
        service.movePlayer(Direction.LEFT);
        Player after = service.getPlayer();

        assertTrue(after.position().x() < before.position().x());
    }

    @Test
    @DisplayName("Player can move RIGHT")
    void testPlayerMoveRight() {
        Player before = service.getPlayer();
        service.movePlayer(Direction.RIGHT);
        Player after = service.getPlayer();

        assertTrue(after.position().x() > before.position().x());
    }

    @Test
    @DisplayName("Player can move in multiple directions")
    void testPlayerMoveMultiple() {
        Player before = service.getPlayer();
        service.movePlayerMultiple(new Direction[]{Direction.UP, Direction.RIGHT});
        Player after = service.getPlayer();

        assertTrue(after.position().y() < before.position().y());
        assertTrue(after.position().x() > before.position().x());
    }

    @Test
    @DisplayName("Player can move diagonally")
    void testPlayerDiagonalMovement() {
        Player before = service.getPlayer();

        // Move diagonally up-right
        for (int i = 0; i < 5; i++) {
            service.movePlayerMultiple(new Direction[]{Direction.UP, Direction.RIGHT});
        }

        Player after = service.getPlayer();
        assertTrue(after.position().x() > before.position().x());
        assertTrue(after.position().y() < before.position().y());
    }

    // === Configuration Tests ===

    @Test
    @DisplayName("Configuration can be set in PREPARED state")
    void testConfigureInPreparedState() throws IllegalParameterException {
        GameConfiguration newConfig = new GameConfiguration(
            600, 1000, 800, 150, 6.0, 15, 2, 1.5, 1.2,
            new WeaponType[]{WeaponType.SWORD, WeaponType.AURA}
        );

        service.configure(newConfig);

        assertEquals(newConfig, service.getConfiguration());
        Player player = service.getPlayer();
        assertEquals(150, player.maxHealth());
        assertEquals(6.0, player.movementSpeed());
        assertEquals(2, player.equippedWeapons().length);
    }

    @Test
    @DisplayName("Configuration can be set in ABORTED state")
    void testConfigureInAbortedState() throws IllegalParameterException {
        service.start();
        service.abort();

        GameConfiguration newConfig = new GameConfiguration(
            600, 1000, 800, 150, 6.0, 15, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.WHIP}
        );

        service.configure(newConfig);
        assertEquals(newConfig, service.getConfiguration());
    }

    @Test
    @DisplayName("Cannot configure while game is running")
    void testConfigureInRunningState() {
        service.start();

        GameConfiguration newConfig = new GameConfiguration(
            600, 1000, 800, 150, 6.0, 15, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalStateException.class, () -> service.configure(newConfig));
    }

    @Test
    @DisplayName("Cannot configure while game is paused")
    void testConfigureInPausedState() {
        service.start();
        service.pause();

        GameConfiguration newConfig = new GameConfiguration(
            600, 1000, 800, 150, 6.0, 15, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalStateException.class, () -> service.configure(newConfig));
    }

    @Test
    @DisplayName("Cannot configure with null configuration")
    void testConfigureNull() {
        assertThrows(IllegalArgumentException.class, () -> service.configure(null));
    }

    @Test
    @DisplayName("Configuration validates game duration")
    void testConfigurationValidatesDuration() {
        GameConfiguration invalid = new GameConfiguration(
            -10, 800, 600, 100, 5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    @Test
    @DisplayName("Configuration validates field width")
    void testConfigurationValidatesFieldWidth() {
        GameConfiguration invalid = new GameConfiguration(
            600, -800, 600, 100, 5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    @Test
    @DisplayName("Configuration validates field height")
    void testConfigurationValidatesFieldHeight() {
        GameConfiguration invalid = new GameConfiguration(
            600, 800, -600, 100, 5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    @Test
    @DisplayName("Configuration validates starting health")
    void testConfigurationValidatesHealth() {
        GameConfiguration invalid = new GameConfiguration(
            600, 800, 600, -100, 5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    @Test
    @DisplayName("Configuration validates starting speed")
    void testConfigurationValidatesSpeed() {
        GameConfiguration invalid = new GameConfiguration(
            600, 800, 600, 100, -5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    @Test
    @DisplayName("Configuration validates starting damage")
    void testConfigurationValidatesDamage() {
        GameConfiguration invalid = new GameConfiguration(
            600, 800, 600, 100, 5.0, -10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalParameterException.class, () -> service.configure(invalid));
    }

    // === Time Management Tests ===

    @Test
    @DisplayName("Cannot get elapsed time in PREPARED state")
    void testElapsedTimeInPreparedState() {
        assertThrows(IllegalStateException.class, () -> service.getElapsedTime());
    }

    @Test
    @DisplayName("Cannot get elapsed time in ABORTED state")
    void testElapsedTimeInAbortedState() {
        service.start();
        service.abort();
        assertThrows(IllegalStateException.class, () -> service.getElapsedTime());
    }

    @Test
    @DisplayName("Can get elapsed time while game is running")
    void testElapsedTimeWhileRunning() throws InterruptedException {
        service.start();
        Thread.sleep(100);

        int elapsed = service.getElapsedTime();
        assertTrue(elapsed >= 0);
    }

    @Test
    @DisplayName("Can get remaining time while game is running")
    void testRemainingTimeWhileRunning() {
        service.start();
        int remaining = service.getRemainingTime();
        assertTrue(remaining >= 899 && remaining <= 900);
    }

    @Test
    @DisplayName("Cannot get remaining time in PREPARED state")
    void testRemainingTimeInPreparedState() {
        assertThrows(IllegalStateException.class, () -> service.getRemainingTime());
    }

    @Test
    @DisplayName("Cannot get remaining time in ABORTED state")
    void testRemainingTimeInAbortedState() {
        service.start();
        service.abort();
        assertThrows(IllegalStateException.class, () -> service.getRemainingTime());
    }

    // === Listener Management Tests ===

    @Test
    @DisplayName("Listeners can be added")
    void testAddListener() {
        TestListener newListener = new TestListener();
        assertTrue(service.addListener(newListener));
    }

    @Test
    @DisplayName("Listeners can be removed")
    void testRemoveListener() {
        TestListener newListener = new TestListener();
        service.addListener(newListener);
        assertTrue(service.removeListener(newListener));
    }

    @Test
    @DisplayName("Removing non-existent listener returns false")
    void testRemoveNonExistentListener() {
        TestListener newListener = new TestListener();
        assertFalse(service.removeListener(newListener));
    }

    @Test
    @DisplayName("Cannot add null listener")
    void testAddNullListener() {
        assertThrows(IllegalArgumentException.class, () -> service.addListener(null));
    }

    @Test
    @DisplayName("Cannot remove null listener")
    void testRemoveNullListener() {
        assertThrows(IllegalArgumentException.class, () -> service.removeListener(null));
    }

    @Test
    @DisplayName("Multiple listeners receive updates")
    void testMultipleListeners() {
        TestListener listener2 = new TestListener();
        service.addListener(listener2);

        service.movePlayer(Direction.UP);

        assertTrue(listener.playerUpdated);
        assertTrue(listener2.playerUpdated);
    }

    // === Upgrade System Tests ===

    @Test
    @DisplayName("Cannot get upgrades when no level up is pending")
    void testGetUpgradesWithoutLevelUp() {
        assertThrows(IllegalStateException.class, () -> service.getAvailableUpgrades());
    }

    @Test
    @DisplayName("Cannot apply upgrade when no level up is pending")
    void testApplyUpgradeWithoutLevelUp() {
        UpgradeOption upgrade = new UpgradeOption(
            UpgradeType.ATTRIBUTE, "Test", "Test", null,
            PlayerAttribute.MAX_HEALTH, 1.2, true
        );

        service.start();
        assertThrows(IllegalStateException.class, () -> service.applyUpgrade(upgrade));
    }

    @Test
    @DisplayName("Cannot apply null upgrade")
    void testApplyNullUpgrade() {
        assertThrows(IllegalArgumentException.class, () -> service.applyUpgrade(null));
    }

    @Test
    @DisplayName("Cannot apply upgrade in PREPARED state")
    void testApplyUpgradeInPreparedState() {
        UpgradeOption upgrade = new UpgradeOption(
            UpgradeType.ATTRIBUTE, "Test", "Test", null,
            PlayerAttribute.MAX_HEALTH, 1.2, true
        );

        assertThrows(IllegalStateException.class, () -> service.applyUpgrade(upgrade));
    }

    @Test
    @DisplayName("Cannot apply upgrade in ABORTED state")
    void testApplyUpgradeInAbortedState() {
        service.start();
        service.abort();

        UpgradeOption upgrade = new UpgradeOption(
            UpgradeType.ATTRIBUTE, "Test", "Test", null,
            PlayerAttribute.MAX_HEALTH, 1.2, true
        );

        assertThrows(IllegalStateException.class, () -> service.applyUpgrade(upgrade));
    }

    // === Game Map Tests ===

    @Test
    @DisplayName("Game map is initialized")
    void testGameMapInitialized() {
        assertNotNull(service.getGameMap());
    }

    @Test
    @DisplayName("Game map is reset on configuration change")
    void testGameMapResetOnConfigure() throws IllegalParameterException {
        var oldMap = service.getGameMap();

        GameConfiguration newConfig = new GameConfiguration(
            600, 1200, 900, 100, 5.0, 10, 1, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD}
        );
        service.configure(newConfig);

        var newMap = service.getGameMap();
        assertNotSame(oldMap, newMap);
    }

    // === Weapon State Tests ===

    @Test
    @DisplayName("Weapon states are tracked correctly")
    void testWeaponStates() {
        var weaponStates = service.getWeaponStates();

        assertNotNull(weaponStates);
        assertTrue(weaponStates.containsKey(WeaponType.SWORD));
    }

    @Test
    @DisplayName("Multiple weapons have separate states")
    void testMultipleWeaponStates() throws IllegalParameterException {
        GameConfiguration config = new GameConfiguration(
            600, 800, 600, 100, 5.0, 10, 2, 1.0, 1.0,
            new WeaponType[]{WeaponType.SWORD, WeaponType.AURA}
        );
        service.configure(config);

        var weaponStates = service.getWeaponStates();
        assertTrue(weaponStates.containsKey(WeaponType.SWORD));
        assertTrue(weaponStates.containsKey(WeaponType.AURA));
    }

    // === Enemy Tests ===

    @Test
    @DisplayName("Enemies array is initially empty")
    void testInitialEnemies() {
        assertEquals(0, service.getEnemies().length);
    }

    @Test
    @DisplayName("Enemies spawn after game starts")
    void testEnemiesSpawnAfterStart() throws InterruptedException {
        service.start();
        Thread.sleep(11000); // Wait for first wave

        assertTrue(service.getEnemies().length > 0);
    }

    // === Input Provider Tests ===

    @Test
    @DisplayName("Input provider can be set")
    void testSetInputProvider() {
        AtomicBoolean called = new AtomicBoolean(false);
        service.setInputProvider(() -> {
            called.set(true);
            return new Direction[]{Direction.UP};
        });

        service.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(called.get());
    }

    // Helper class for testing
    private static class TestListener implements ShapeSurvivorListener {
        boolean gameStateChanged = false;
        boolean playerUpdated = false;
        boolean enemiesUpdated = false;
        boolean levelUpOccurred = false;
        boolean weaponUpdated = false;
        boolean playerDamagedCalled = false;
        boolean enemyDamagedCalled = false;
        boolean enemyKilledCalled = false;
        boolean timeUpdated = false;
        boolean waveSpawned = false;
        boolean gameEnded = false;
        boolean experienceUpdated = false;
        boolean configUpdated = false;

        GameState lastGameState;

        @Override
        public void changedGameState(GameState state) {
            gameStateChanged = true;
            lastGameState = state;
        }

        @Override
        public void updatePlayer(Player player) {
            playerUpdated = true;
        }

        @Override
        public void updateEnemies(Enemy[] enemies) {
            enemiesUpdated = true;
        }

        @Override
        public void updateWeapon(Weapon weapon) {
            weaponUpdated = true;
        }

        @Override
        public void playerDamaged(int damage) {
            playerDamagedCalled = true;
        }

        @Override
        public void enemyDamaged(Enemy enemy, int damage) {
            enemyDamagedCalled = true;
        }

        @Override
        public void enemyKilled(Enemy enemy, int experience) {
            enemyKilledCalled = true;
        }

        @Override
        public void playerLeveledUp() {
            levelUpOccurred = true;
        }

        @Override
        public void updateRemainingTime(int seconds) {
            timeUpdated = true;
        }

        @Override
        public void enemyWaveSpawned(int waveNumber, int enemyCount) {
            waveSpawned = true;
        }

        @Override
        public void gameEnded(boolean victory) {
            gameEnded = true;
        }

        @Override
        public void updateExperience(int current, int toNextLevel) {
            experienceUpdated = true;
        }

        @Override
        public void updateGameConfiguration(GameConfiguration configuration) {
            configUpdated = true;
        }
    }
}