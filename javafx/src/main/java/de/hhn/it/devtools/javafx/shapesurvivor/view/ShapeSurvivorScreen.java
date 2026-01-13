package de.hhn.it.devtools.javafx.shapesurvivor.view;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.SimpleGameLoopService;
import de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;
import de.hhn.it.devtools.javafx.shapesurvivor.helper.PopupProvider;
import de.hhn.it.devtools.javafx.shapesurvivor.viewmodel.ShapeSurvivorViewModel;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class ShapeSurvivorScreen extends AnchorPane implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label timeLabel;

    private final Parent root;
    private final Stage mainStage;
    private final ShapeSurvivorViewModel viewModel;
    private final WeaponRenderer weaponRenderer;
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private SimpleGameLoopService renderLoop;
    private GraphicsContext gc;
    private Runnable onExitCallback;
    private final MapRenderer mapRenderer = new MapRenderer();

    public ShapeSurvivorScreen(Stage mainStage) {
        this.mainStage = mainStage;
        SimpleShapeSurvivorService gameService = new SimpleShapeSurvivorService();
        this.viewModel = new ShapeSurvivorViewModel(gameService);
        this.weaponRenderer = new WeaponRenderer();
        gameService.addListener(viewModel);

        gameService.setInputProvider(this::getActiveDirections);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/shapesurvivor/ShapeSurvivorGame.fxml")
            );
            loader.setController(this);
            root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("FXML load failed", e);
        }
    }

    public void setOnExit(Runnable callback) {
        this.onExitCallback = callback;
    }

    public Parent getView() {
        return root;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();

        scoreLabel.textProperty().bind(viewModel.getScoreProperty().asString());
        levelLabel.textProperty().bind(viewModel.getLevelProperty().asString());
        renderLoop = new SimpleGameLoopService(this::render);

        Platform.runLater(() -> {
            setupInput();
            showStartupPopup();
            renderLoop.startLoop();
        });
    }

    private void setupInput() {
        // Handle key presses - just track the keys
        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                showPausePopup();
                e.consume();
                return;
            }
            synchronized (pressedKeys) {
                pressedKeys.add(e.getCode());
            }
            e.consume();
        });

        // Handle key releases
        root.setOnKeyReleased(e -> {
            synchronized (pressedKeys) {
                pressedKeys.remove(e.getCode());
            }
            e.consume();
        });

        root.setFocusTraversable(true);

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        showPausePopup();
                        e.consume();
                        return;
                    }
                    synchronized (pressedKeys) {
                        pressedKeys.add(e.getCode());
                    }
                    e.consume();
                });

                newScene.setOnKeyReleased(e -> {
                    synchronized (pressedKeys) {
                        pressedKeys.remove(e.getCode());
                    }
                    e.consume();
                });
            }
        });
        root.requestFocus();

        viewModel.gameOverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                synchronized (pressedKeys) {
                    pressedKeys.clear();
                }
                Platform.runLater(this::showDeathPopup);
            }
        });

        viewModel.levelUpAvailableProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(this::showUpgradePopup);
            }
        });

        timeLabel.textProperty().bind(
                Bindings.createStringBinding(() -> {
                    int totalSeconds = viewModel.remainingTimeProperty().get();
                    int minutes = totalSeconds / 60;
                    int seconds = totalSeconds % 60;
                    return String.format("%02d:%02d", minutes, seconds);
                }, viewModel.remainingTimeProperty())
        );

    }

    private Direction[] getActiveDirections() {
        Set<Direction> directions = new HashSet<>();

        synchronized (pressedKeys) {
            if (pressedKeys.contains(KeyCode.UP) || pressedKeys.contains(KeyCode.W)) {
                directions.add(Direction.UP);
            }
            if (pressedKeys.contains(KeyCode.DOWN) || pressedKeys.contains(KeyCode.S)) {
                directions.add(Direction.DOWN);
            }

            if (pressedKeys.contains(KeyCode.LEFT) || pressedKeys.contains(KeyCode.A)) {
                directions.add(Direction.LEFT);
            }
            if (pressedKeys.contains(KeyCode.RIGHT) || pressedKeys.contains(KeyCode.D)) {
                directions.add(Direction.RIGHT);
            }
        }

        return directions.toArray(new Direction[0]);
    }

    private void showStartupPopup() {
        PopupProvider provider = new PopupProvider(mainStage)
                .setTitle("ShapeSurvivor - Game Setup")
                .addLabel("Configure Your Game")
                .addSelector("Difficulty:", new String[]{"Easy", "Normal", "Hard", "Nightmare"}, 1)
                .addSelector("Starting Weapon:", WeaponType.values(), 0);

        provider.addButton(e -> {
            ComboBox<String> difficultyBox = provider.getSelector(0);
            ComboBox<WeaponType> weaponBox = provider.getSelector(1);

            String difficulty = difficultyBox.getSelectionModel().getSelectedItem();
            WeaponType weapon = weaponBox.getSelectionModel().getSelectedItem();

            viewModel.startGame(difficulty, weapon, (int) canvas.getWidth(), (int) canvas.getHeight());

            weaponRenderer.initializeWeapon(weapon);

            Platform.runLater(root::requestFocus);
        }, "Start Game");

        Stage startPopup = provider.build();
        startPopup.setOnHidden(e -> Platform.runLater(root::requestFocus));
        startPopup.show();
    }

    private void render() {
        Platform.runLater(() -> {
            Player player = viewModel.getPlayerProperty().get();
            if (player == null) {
                gc.setFill(Color.rgb(20, 20, 30));
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                return;
            }

            Position cameraPos = player.position();

            if (viewModel.getGameMap() != null) {
                mapRenderer.renderMap(gc, viewModel.getGameMap(), cameraPos);
            }

            // Update weapon animation states
            weaponRenderer.setAnimationStates(viewModel.getWeaponStates());

            viewModel.getEnemiesMap().values().forEach(enemy ->
                    renderEnemyWithCamera(enemy, cameraPos)
            );

            renderPlayerCentered(player);

            // Render weapons
            weaponRenderer.renderWeapons(
                    gc,
                    player,
                    cameraPos,
                    canvas.getWidth(),
                    canvas.getHeight()
            );

            // Draw health bar
            drawHealthBarCentered(player);
        });
    }

    private void renderPlayerCentered(Player player) {
        int screenCenterX = (int) (canvas.getWidth() / 2);
        int screenCenterY = (int) (canvas.getHeight() / 2);

        gc.setFill(Color.BLUE);
        gc.fillOval(screenCenterX - 15, screenCenterY - 15, 30, 30);
    }

    private void renderEnemyWithCamera(Enemy enemy, Position cameraPos) {
        int canvasWidth = (int) canvas.getWidth();
        int canvasHeight = (int) canvas.getHeight();

        // Convert world position to screen position
        int screenX = enemy.position().x() - cameraPos.x() + canvasWidth / 2;
        int screenY = enemy.position().y() - cameraPos.y() + canvasHeight / 2;

        double size = 20;

        // Calculate angle to player (at screen center)
        double dx = (double) canvasWidth / 2 - screenX;
        double dy = (double) canvasHeight / 2 - screenY;
        double angle = Math.atan2(dy, dx);

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];
        xPoints[0] = screenX + Math.cos(angle) * size;
        yPoints[0] = screenY + Math.sin(angle) * size;

        xPoints[1] = screenX + Math.cos(angle + 2.5) * (size * 0.7);
        yPoints[1] = screenY + Math.sin(angle + 2.5) * (size * 0.7);

        xPoints[2] = screenX + Math.cos(angle - 2.5) * (size * 0.7);
        yPoints[2] = screenY + Math.sin(angle - 2.5) * (size * 0.7);

        gc.setFill(Color.RED);
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Health bar
        double ratio = (double) enemy.currentHealth() / enemy.maxHealth();
        gc.setFill(Color.DARKRED);
        gc.fillRect(screenX - 12, screenY - 25, 24, 4);
        gc.setFill(Color.RED);
        gc.fillRect(screenX - 12, screenY - 25, 24 * ratio, 4);
    }

    private void drawHealthBarCentered(Player player) {
        int screenCenterX = (int) (canvas.getWidth() / 2);
        int screenCenterY = (int) (canvas.getHeight() / 2);

        double barWidth = 40;
        double barHeight = 6;
        double healthRatio = (double) player.currentHealth() / player.maxHealth();

        int x = screenCenterX - 20;
        int y = screenCenterY - 30;

        // Background
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, barWidth, barHeight);

        // Foreground
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, y, barWidth * healthRatio, barHeight);

        // Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, barWidth, barHeight);
    }

    private void renderPlayer(Player player) {
        gc.setFill(Color.BLUE);
        gc.fillOval(player.position().x() - 15, player.position().y() - 15, 30, 30);
    }

    private void renderEnemy(Enemy enemy) {
        Player player = viewModel.getPlayerProperty().get();

        double size = 20; // Triangle size
        int cx = enemy.position().x();
        int cy = enemy.position().y();

        double angle = 0;
        if (player != null) {
            double dx = player.position().x() - cx;
            double dy = player.position().y() - cy;
            angle = Math.atan2(dy, dx);
        }

        double[] xPoints = new double[3];
        double[] yPoints = new double[3];
        xPoints[0] = cx + Math.cos(angle) * size;
        yPoints[0] = cy + Math.sin(angle) * size;

        xPoints[1] = cx + Math.cos(angle + 2.5) * (size * 0.7);
        yPoints[1] = cy + Math.sin(angle + 2.5) * (size * 0.7);

        xPoints[2] = cx + Math.cos(angle - 2.5) * (size * 0.7);
        yPoints[2] = cy + Math.sin(angle - 2.5) * (size * 0.7);

        gc.setFill(Color.RED);
        gc.fillPolygon(xPoints, yPoints, 3);

        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokePolygon(xPoints, yPoints, 3);

        // Health bar
        double ratio = (double) enemy.currentHealth() / enemy.maxHealth();
        gc.setFill(Color.DARKRED);
        gc.fillRect(cx - 12, cy - 25, 24, 4);
        gc.setFill(Color.RED);
        gc.fillRect(cx - 12, cy - 25, 24 * ratio, 4);
    }

    private void drawHealthBar(Player player) {
        double barWidth = 40;
        double barHeight = 6;

        double healthRatio = (double) player.currentHealth() / player.maxHealth();

        int x = player.position().x() - 20;
        int y = player.position().y() - 30;

        // Background
        gc.setFill(Color.DARKRED);
        gc.fillRect(x, y, barWidth, barHeight);

        // Foreground
        gc.setFill(Color.LIMEGREEN);
        gc.fillRect(x, y, barWidth * healthRatio, barHeight);

        // Border
        gc.setStroke(Color.BLACK);
        gc.strokeRect(x, y, barWidth, barHeight);
    }

    private void showPausePopup() {
        viewModel.pauseGame();
        synchronized (pressedKeys) {
            pressedKeys.clear();
        }

        PopupProvider provider = new PopupProvider(mainStage)
                .setTitle("Game Paused")
                .addLabel("What do you want to do?");

        provider.addButton(e -> {
            viewModel.resumeGame();
            closePopup(e);
        }, "Continue");

        provider.addButton(e -> {
            weaponRenderer.reset();
            synchronized (pressedKeys) {
                pressedKeys.clear();
            }
            viewModel.resetAndStartDefault();
            closePopup(e);
        }, "Restart");

        provider.addButton(e -> {
            weaponRenderer.reset();
            synchronized (pressedKeys) {
                pressedKeys.clear();
            }
            viewModel.exitGame();
            closePopup(e);
            if (onExitCallback != null) {
                onExitCallback.run();
            }
        }, "Exit Game");

        provider.build().show();
    }

    private void closePopup(javafx.event.ActionEvent e) {
        ((Stage) ((javafx.scene.Node) e.getSource())
                .getScene().getWindow()).close();
        Platform.runLater(root::requestFocus);
    }

    private void showDeathPopup() {
        PopupProvider provider = new PopupProvider(mainStage)
                .setTitle("You Died!")
                .addLabel("Game Over");

        provider.addButton(e -> {
            viewModel.resetGameOver();
            viewModel.exitGame();
            viewModel.resetGame();
            weaponRenderer.reset();
            closePopup(e);

            Platform.runLater(() -> {
                showStartupPopup();
                root.requestFocus();
            });
        }, "New Game");

        provider.addButton(e -> {
            weaponRenderer.reset();
            viewModel.exitGame();
            closePopup(e);

            if (onExitCallback != null) {
                onExitCallback.run();
            }
        }, "Exit");

        provider.build().show();
    }

    private void showUpgradePopup() {
        UpgradeOption[] upgrades = viewModel.availableUpgradesProperty().get();
        if (upgrades == null || upgrades.length == 0) return;

        viewModel.pauseGame();
        synchronized (pressedKeys) {
            pressedKeys.clear();
        }

        Stage popup = new Stage();
        popup.initOwner(mainStage);
        popup.initModality(javafx.stage.Modality.WINDOW_MODAL);
        popup.setTitle("Level Up!");
        popup.setResizable(false);

        VBox container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(30));

        Label titleLabel = new Label("LEVEL UP!");
        titleLabel.setStyle(
                "-fx-text-fill: #FFD700; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 28px; "
                        + "-fx-effect: dropshadow(one-pass-box, black, 4, 0.0, 2, 2);"
        );

        Label subtitleLabel = new Label("Choose Your Upgrade");
        subtitleLabel.setStyle(
                "-fx-text-fill: #FFFFFF; "
                        + "-fx-font-size: 16px; "
                        + "-fx-effect: dropshadow(one-pass-box, black, 2, 0.0, 1, 1);"
        );

        container.getChildren().addAll(titleLabel, subtitleLabel);

        // Create upgrade buttons
        for (UpgradeOption option : upgrades) {
            VBox upgradeBox = createUpgradeButton(option, popup);
            container.getChildren().add(upgradeBox);
        }

        container.setStyle(
                "-fx-background-color: linear-gradient(to bottom, "
                        + "rgba(20,20,40,0.95), rgba(40,40,70,0.95)); "
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 25, 0.8, 0, 10);"
        );

        Scene scene = new Scene(container, 500, 550);
        popup.setScene(scene);
        popup.setOnCloseRequest(Event::consume);
        popup.show();
    }

    private VBox createUpgradeButton(UpgradeOption option, Stage popup) {
        VBox upgradeBox = new VBox(8);
        upgradeBox.setAlignment(Pos.CENTER);
        upgradeBox.setPadding(new Insets(15));
        upgradeBox.setStyle(
                "-fx-background-color: rgba(50,50,80,0.7); "
                        + "-fx-border-color: #4a90e2; "
                        + "-fx-border-width: 2; "
                        + "-fx-border-radius: 8; "
                        + "-fx-background-radius: 8; "
                        + "-fx-cursor: hand;"
        );
        upgradeBox.setPrefWidth(450);

        Label nameLabel = new Label(option.name());
        nameLabel.setStyle(
                "-fx-text-fill: #FFD700; "
                        + "-fx-font-weight: bold; "
                        + "-fx-font-size: 18px; "
                        + "-fx-effect: dropshadow(one-pass-box, black, 2, 0.0, 1, 1);"
        );

        Label descLabel = new Label(option.description());
        descLabel.setStyle(
                "-fx-text-fill: #CCCCCC; "
                        + "-fx-font-size: 14px; "
                        + "-fx-wrap-text: true;"
        );
        descLabel.setMaxWidth(420);
        descLabel.setWrapText(true);

        upgradeBox.getChildren().addAll(nameLabel, descLabel);

        // Hover effect
        upgradeBox.setOnMouseEntered(e -> upgradeBox.setStyle(
                "-fx-background-color: rgba(70,70,110,0.9); "
                        + "-fx-border-color: #FFD700; "
                        + "-fx-border-width: 3; "
                        + "-fx-border-radius: 8; "
                        + "-fx-background-radius: 8; "
                        + "-fx-cursor: hand; "
                        + "-fx-scale-x: 1.02; "
                        + "-fx-scale-y: 1.02;"
        ));

        upgradeBox.setOnMouseExited(e -> upgradeBox.setStyle(
                "-fx-background-color: rgba(50,50,80,0.7); "
                        + "-fx-border-color: #4a90e2; "
                        + "-fx-border-width: 2; "
                        + "-fx-border-radius: 8; "
                        + "-fx-background-radius: 8; "
                        + "-fx-cursor: hand;"
        ));

        upgradeBox.setOnMouseClicked(e -> {
            if (option.type() == UpgradeType.NEW_WEAPON) {
                weaponRenderer.initializeWeapon(option.weaponType());
            }

            viewModel.selectUpgrade(option);

            synchronized (pressedKeys) {
                pressedKeys.clear();
            }

            viewModel.resumeGame();
            popup.close();
            Platform.runLater(root::requestFocus);
        });

        return upgradeBox;
    }

    public void cleanup() {
        if (renderLoop != null && renderLoop.isRunning()) {
            renderLoop.stopLoop();
        }
        if (viewModel != null) {
            viewModel.exitGame();
        }
        synchronized (pressedKeys) {
            pressedKeys.clear();
        }
    }
}