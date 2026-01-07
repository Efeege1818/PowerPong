package de.hhn.it.devtools.javafx.shapesurvivor.viewmodel;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import de.hhn.it.devtools.apis.shapesurvivor.UpgradeOption;
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

    public ShapeSurvivorViewModel(ShapeSurvivorService gameService) {
        this.gameService = gameService;
        this.playerProperty = new SimpleObjectProperty<>();
        this.enemiesMap = Collections.synchronizedMap(new HashMap<>());
        this.scoreProperty = new SimpleIntegerProperty(0);
        this.levelProperty = new SimpleIntegerProperty(1);
        gameOverProperty = new SimpleBooleanProperty(false);
        this.levelUpAvailableProperty = new SimpleBooleanProperty(false);
        this.availableUpgradesProperty = new SimpleObjectProperty<>(new UpgradeOption[0]);
    }

    public void startGame(String difficulty, WeaponType weapon, int fieldWidth, int fieldHeight) {
        double difficultyMultiplier = switch (difficulty) {
            case "Easy" -> 0.75;
            case "Hard" -> 1.5;
            case "Nightmare" -> 2.0;
            default -> 1.0;
        };

        GameConfiguration config = new GameConfiguration(
                900, // 15 minutes
                fieldWidth,
                fieldHeight,
                100, // starting health
                5.0, // starting speed
                10,  // starting damage
                1,   // initial weapon count
                1.0, // enemy spawn rate
                difficultyMultiplier,
                new WeaponType[]{weapon}
        );

        try {
            gameService.configure(config);
            updatePlayer(gameService.getPlayer());
            gameService.start();
        } catch (IllegalParameterException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void movePlayer(Direction direction) {
        gameService.movePlayer(direction);
    }

    public Map<WeaponType, WeaponAnimationState> getWeaponStates() {
        if (gameService instanceof de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService) {
            return ((de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService) gameService).getWeaponStates();
        }
        return new HashMap<>();
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
        // Optional: implement if needed
    }

    @Override
    public void playerDamaged(int damage) {
        // Optional: could animate player damage
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
            if (gameService instanceof de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService) {
                try {
                    UpgradeOption[] upgrades = gameService.getAvailableUpgrades();
                    availableUpgradesProperty.set(upgrades);
                    levelUpAvailableProperty.set(true);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void changedGameState(GameState state) {
        // Optional: handle state changes
    }

    @Override
    public void updateRemainingTime(int remainingTime) {
        // Optional: bind to time property
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
        Platform.runLater(() -> scoreProperty.set(experience));
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

    public void resetAndStartDefault() {
        gameService.reset();
        gameService.start();
    }

    public void resetGameOver() {
        gameOverProperty.set(false);
    }

    public BooleanProperty gameOverProperty() {
        return gameOverProperty;
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

    public BooleanProperty levelUpAvailableProperty() {
        return levelUpAvailableProperty;
    }

    public ObjectProperty<UpgradeOption[]> availableUpgradesProperty() {
        return availableUpgradesProperty;
    }

    public void selectUpgrade(UpgradeOption option) {
            try {
                gameService.applyUpgrade(option);
                Platform.runLater(() -> levelUpAvailableProperty.set(false));
            } catch (IllegalStateException | IllegalArgumentException e) {
                e.printStackTrace();
            }
    }
}