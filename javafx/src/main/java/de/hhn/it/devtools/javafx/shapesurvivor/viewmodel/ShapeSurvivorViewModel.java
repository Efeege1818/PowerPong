package de.hhn.it.devtools.javafx.shapesurvivor.viewmodel;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.GameMap;
import de.hhn.it.devtools.components.shapesurvivor.helper.GameConfigurationBuilder;
import de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ViewModel for ShapeSurvivor game.
 */
public class ShapeSurvivorViewModel implements ShapeSurvivorListener {

    private final ShapeSurvivorService gameService;
    private final ObjectProperty<Player> playerProperty;
    private final Map<Integer, Enemy> enemiesMap;
    private final IntegerProperty scoreProperty;
    private final IntegerProperty levelProperty;
    private final BooleanProperty gameOverProperty;
    private final BooleanProperty levelUpAvailableProperty;
    private final ObjectProperty<UpgradeOption[]> availableUpgradesProperty;
    private final IntegerProperty remainingTimeProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty experienceProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty experienceToNextLevelProperty = new SimpleIntegerProperty(100);
    private final DoubleProperty experienceProgressProperty = new SimpleDoubleProperty(0);
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(ShapeSurvivorViewModel.class);

    public ShapeSurvivorViewModel(ShapeSurvivorService gameService) {
        this.gameService = gameService;
        this.playerProperty = new SimpleObjectProperty<>();
        this.enemiesMap = Collections.synchronizedMap(new HashMap<>());
        this.scoreProperty = new SimpleIntegerProperty(0);
        this.levelProperty = new SimpleIntegerProperty(1);
        this.gameOverProperty = new SimpleBooleanProperty(false);
        this.levelUpAvailableProperty = new SimpleBooleanProperty(false);
        this.availableUpgradesProperty = new SimpleObjectProperty<>(new UpgradeOption[0]);
    }

    /**
     * Starts game with given configuration.
     */
    public void startGame(String difficulty, WeaponType weapon, int fieldWidth, int fieldHeight) {
        try {
            GameConfiguration config = GameConfigurationBuilder.fromDifficulty(
                    difficulty,
                    weapon,
                    fieldWidth,
                    fieldHeight
            );

            gameService.configure(config);
            updatePlayer(gameService.getPlayer());
            gameService.start();
        } catch (IllegalParameterException | IllegalStateException e) {
            logger.error(e.getMessage());
        }
    }

    public void movePlayer(Direction direction) {
        gameService.movePlayer(direction);
    }

    public ObjectProperty<Player> getPlayerProperty() {
        return playerProperty;
    }

    public Map<Integer, Enemy> getEnemiesMap() {
        return enemiesMap;
    }

    public IntegerProperty getScoreProperty() {
        return scoreProperty;
    }

    public IntegerProperty getLevelProperty() {
        return levelProperty;
    }

    public BooleanProperty gameOverProperty() {
        return gameOverProperty;
    }

    public BooleanProperty levelUpAvailableProperty() {
        return levelUpAvailableProperty;
    }

    public ObjectProperty<UpgradeOption[]> availableUpgradesProperty() {
        return availableUpgradesProperty;
    }

    public IntegerProperty remainingTimeProperty() {
        return remainingTimeProperty;
    }

    @Override
    public void updatePlayer(Player player) {
        Platform.runLater(() -> {
            playerProperty.set(player);
            levelProperty.set(player.level());
        });
    }

    @Override
    public void updateEnemies(Enemy[] enemies) {
        Platform.runLater(() -> {
            enemiesMap.clear();
            for (Enemy enemy : enemies) {
                enemiesMap.put(enemy.id(), enemy);
            }
        });
    }

    @Override
    public void updateWeapon(Weapon weapon) {
        // Optional: implement if needed for weapon UI updates
    }

    @Override
    public void playerDamaged(int damage) {
        // Optional: could trigger damage animation via property
    }

    @Override
    public void enemyDamaged(Enemy enemy, int damage) {
        Platform.runLater(() -> enemiesMap.put(enemy.id(), enemy));
    }

    @Override
    public void enemyKilled(Enemy enemy, int experience) {
        Platform.runLater(() -> {
            enemiesMap.remove(enemy.id());
            scoreProperty.set(scoreProperty.get() + experience);
        });
    }

    @Override
    public void playerLeveledUp() {
        Platform.runLater(() -> {
            try {
                UpgradeOption[] upgrades = gameService.getAvailableUpgrades();
                availableUpgradesProperty.set(upgrades);
                levelUpAvailableProperty.set(true);
            } catch (IllegalStateException e) {
                logger.error(e.getMessage());
            }
        });
    }

    @Override
    public void changedGameState(GameState state) {
        // Optional: handle state changes
    }

    @Override
    public void updateRemainingTime(int remainingTime) {
        Platform.runLater(() -> remainingTimeProperty.set(remainingTime));
    }

    @Override
    public void enemyWaveSpawned(int wave, int count) {
        // Optional: show wave info
    }

    @Override
    public void gameEnded(boolean victory) {
        if (!victory) {
            Platform.runLater(() -> gameOverProperty.set(true));
        }
    }

    @Override
    public void updateExperience(int experience, int experienceToNextLevel) {
        Platform.runLater(() -> {
            experienceProperty.set(experience);
            experienceToNextLevelProperty.set(experienceToNextLevel);

            double progress = experienceToNextLevel == 0
                ? 0
                : (double) experience / experienceToNextLevel;

            experienceProgressProperty.set(progress);
            scoreProperty.set(experience);
        });
    }

    @Override
    public void updateGameConfiguration(GameConfiguration configuration) {
        // Optional: handle config updates
    }

    public void pauseGame() {
        try {
            gameService.pause();
        } catch (IllegalStateException ignored) {
        }
    }

    public void resumeGame() {
        try {
            gameService.resume();
        } catch (IllegalStateException ignored) {
        }
    }

    public void resetGame() {
        gameService.reset();
        Platform.runLater(() -> {
            playerProperty.set(gameService.getPlayer());
            enemiesMap.clear();
            scoreProperty.set(0);
            levelProperty.set(1);
        });
    }

    public IntegerProperty experienceProperty() {
        return experienceProperty;
    }

    public IntegerProperty experienceToNextLevelProperty() {
        return experienceToNextLevelProperty;
    }

    public DoubleProperty experienceProgressProperty() {
        return experienceProgressProperty;
    }

    public void resetAndStartDefault() {
        gameService.reset();
        gameService.start();
    }

    public void resetGameOver() {
        gameOverProperty.set(false);
    }

    public GameMap getGameMap() {
        if (gameService instanceof SimpleShapeSurvivorService service) {
            return service.getGameMap();
        }
        return null;
    }

    public void exitGame() {
        try {
            if (gameService.getGameState() != GameState.ABORTED &&
                    gameService.getGameState() != GameState.PREPARED) {
                gameService.abort();
            }
        } catch (IllegalStateException ignored) {
        }
    }

    public void selectUpgrade(UpgradeOption option) {
        try {
            gameService.applyUpgrade(option);
            Platform.runLater(() -> levelUpAvailableProperty.set(false));
        } catch (IllegalStateException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
        if (gameService instanceof SimpleShapeSurvivorService service) {
            return service.getWeaponStates();
        }
        return Map.of();
    }
}