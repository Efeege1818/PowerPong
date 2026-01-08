package de.hhn.it.devtools.components.shapesurvivor.junit;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
    void testAbortInvalidState() {
        assertThrows(IllegalStateException.class, () -> service.abort());
    }

    @Test
    @DisplayName("Game can be reset")
    void testReset() {
        service.start();

        // Wait for some game time
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

    @Test
    @DisplayName("Player can move in all directions")
    void testPlayerMovement() {
        Player initialPlayer = service.getPlayer();
        int initialX = initialPlayer.position().x();
        int initialY = initialPlayer.position().y();

        service.movePlayer(Direction.UP);
        Player afterUp = service.getPlayer();
        assertTrue(afterUp.position().y() < initialY);

        service.movePlayer(Direction.DOWN);
        service.movePlayer(Direction.DOWN);
        Player afterDown = service.getPlayer();
        assertTrue(afterDown.position().y() > afterUp.position().y());

        service.movePlayer(Direction.LEFT);
        Player afterLeft = service.getPlayer();
        assertTrue(afterLeft.position().x() < initialX);

        service.movePlayer(Direction.RIGHT);
        service.movePlayer(Direction.RIGHT);
        Player afterRight = service.getPlayer();
        assertTrue(afterRight.position().x() > afterLeft.position().x());
    }

    @Test
    @DisplayName("Player cannot move outside field boundaries")
    void testPlayerMovementBoundaries() {
        GameConfiguration config = service.getConfiguration();

        // Move to top-left corner
        for (int i = 0; i < 100; i++) {
            service.movePlayer(Direction.UP);
            service.movePlayer(Direction.LEFT);
        }

        Player player = service.getPlayer();
        assertTrue(player.position().x() >= 0);
        assertTrue(player.position().y() >= 0);

        // Move to bottom-right corner
        for (int i = 0; i < 200; i++) {
            service.movePlayer(Direction.DOWN);
            service.movePlayer(Direction.RIGHT);
        }

        player = service.getPlayer();
        assertTrue(player.position().x() <= config.fieldWidth());
        assertTrue(player.position().y() <= config.fieldHeight());
    }

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
    void testConfigureInvalidState() {
        service.start();

        GameConfiguration newConfig = new GameConfiguration(
                600, 1000, 800, 150, 6.0, 15, 1, 1.0, 1.0,
                new WeaponType[]{WeaponType.SWORD}
        );

        assertThrows(IllegalStateException.class, () -> service.configure(newConfig));
    }

    @Test
    @DisplayName("Configuration validates parameters")
    void testConfigurationValidation() {
        assertThrows(IllegalParameterException.class, () -> {
            GameConfiguration invalidConfig = new GameConfiguration(
                    -10, 800, 600, 100, 5.0, 10, 1, 1.0, 1.0,
                    new WeaponType[]{WeaponType.SWORD}
            );
            service.configure(invalidConfig);
        });

        assertThrows(IllegalParameterException.class, () -> {
            GameConfiguration invalidConfig = new GameConfiguration(
                    600, -800, 600, 100, 5.0, 10, 1, 1.0, 1.0,
                    new WeaponType[]{WeaponType.SWORD}
            );
            service.configure(invalidConfig);
        });

        assertThrows(IllegalParameterException.class, () -> {
            GameConfiguration invalidConfig = new GameConfiguration(
                    600, 800, 600, -100, 5.0, 10, 1, 1.0, 1.0,
                    new WeaponType[]{WeaponType.SWORD}
            );
            service.configure(invalidConfig);
        });
    }

    @Test
    @DisplayName("Cannot get elapsed time in PREPARED state")
    void testElapsedTimeInPreparedState() {
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
        assertEquals(900, remaining, 1); // Within 1 second tolerance
    }

    @Test
    @DisplayName("Listeners can be added and removed")
    void testListenerManagement() {
        TestListener newListener = new TestListener();

        assertTrue(service.addListener(newListener));
        assertTrue(service.removeListener(newListener));
        assertFalse(service.removeListener(newListener));
    }

    @Test
    @DisplayName("Cannot add or remove null listener")
    void testNullListener() {
        assertThrows(IllegalArgumentException.class, () -> service.addListener(null));
        assertThrows(IllegalArgumentException.class, () -> service.removeListener(null));
    }

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

    @Test
    @DisplayName("Weapon states are tracked correctly")
    void testWeaponStates() {
        var weaponStates = service.getWeaponStates();

        assertNotNull(weaponStates);
        assertTrue(weaponStates.containsKey(WeaponType.SWORD));
    }

    // Helper class for testing
    private static class TestListener implements ShapeSurvivorListener {
        boolean gameStateChanged = false;
        boolean playerUpdated = false;
        boolean enemiesUpdated = false;
        boolean levelUpOccurred = false;
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
        public void updateWeapon(Weapon weapon) {}

        @Override
        public void playerDamaged(int damage) {}

        @Override
        public void enemyDamaged(Enemy enemy, int damage) {}

        @Override
        public void enemyKilled(Enemy enemy, int experience) {}

        @Override
        public void playerLeveledUp() {
            levelUpOccurred = true;
        }

        @Override
        public void updateRemainingTime(int seconds) {}

        @Override
        public void enemyWaveSpawned(int waveNumber, int enemyCount) {}

        @Override
        public void gameEnded(boolean victory) {}

        @Override
        public void updateExperience(int current, int toNextLevel) {}

        @Override
        public void updateGameConfiguration(GameConfiguration configuration) {}
    }
}