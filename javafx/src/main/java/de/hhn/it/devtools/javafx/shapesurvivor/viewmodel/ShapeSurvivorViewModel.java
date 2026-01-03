package de.hhn.it.devtools.javafx.shapesurvivor.viewmodel;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

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

    public ShapeSurvivorViewModel(ShapeSurvivorService gameService) {
        this.gameService = gameService;
        this.playerProperty = new SimpleObjectProperty<>();
        this.enemiesMap = Collections.synchronizedMap(new HashMap<>());
        this.scoreProperty = new SimpleIntegerProperty(0);
        this.levelProperty = new SimpleIntegerProperty(1);
    }

    // ViewModel public interface
    public void startGame(String difficulty, WeaponType weapon, int fieldWidth, int fieldHeight) {
        double difficultyMultiplier = switch (difficulty) {
            case "Easy" -> 0.75;
            case "Normal" -> 1.0;
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

    // Properties
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

    // Listener implementation
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
        // Optional: trigger UI popup
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
        // Optional: show end game popup
    }

    @Override
    public void updateExperience(int experience, int experienceToNextLevel) {
        Platform.runLater(() -> scoreProperty.set(experience));
    }

    @Override
    public void updateGameConfiguration(GameConfiguration configuration) {
        // Optional: handle config updates
    }
}