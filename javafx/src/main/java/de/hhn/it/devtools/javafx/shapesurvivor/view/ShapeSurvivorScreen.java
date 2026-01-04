package de.hhn.it.devtools.javafx.shapesurvivor.view;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.SimpleGameLoopService;
import de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import de.hhn.it.devtools.javafx.shapesurvivor.helper.PopupProvider;
import de.hhn.it.devtools.javafx.shapesurvivor.viewmodel.ShapeSurvivorViewModel;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import de.hhn.it.devtools.apis.shapesurvivor.UpgradeOption;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ShapeSurvivorScreen extends AnchorPane implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label levelLabel;

    private final Parent root;
    private final Stage mainStage;
    private final ShapeSurvivorViewModel viewModel;
    private SimpleGameLoopService renderLoop;
    private GraphicsContext gc;
    private Runnable onExitCallback;

    public ShapeSurvivorScreen(Stage mainStage) {
        this.mainStage = mainStage;
        ShapeSurvivorService gameService = new SimpleShapeSurvivorService();
        this.viewModel = new ShapeSurvivorViewModel(gameService);
        gameService.addListener(viewModel);

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

        // Bind UI to ViewModel
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
        root.setOnKeyPressed(e -> {
            handleKeyPress(e);
            e.consume();
        });

        root.setFocusTraversable(true);

        root.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    handleKeyPress(e);
                    e.consume();
                });
            }
        });

        root.requestFocus();

        viewModel.gameOverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(this::showDeathPopup);
            }
        });

        viewModel.levelUpAvailableProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                Platform.runLater(this::showUpgradePopup);
            }
        });
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            showPausePopup();
            return;
        }

        Direction direction = switch (e.getCode()) {
            case UP, W -> Direction.UP;
            case DOWN, S -> Direction.DOWN;
            case LEFT, A -> Direction.LEFT;
            case RIGHT, D -> Direction.RIGHT;
            default -> null;
        };

        if (direction != null) {
            viewModel.movePlayer(direction);
        }
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
            Platform.runLater(root::requestFocus);
        }, "Start Game");

        Stage startPopup = provider.build();
        startPopup.setOnHidden(e -> Platform.runLater(root::requestFocus));
        startPopup.show();
    }

    private void render() {
        Platform.runLater(() -> {
            // Clear canvas
            gc.setFill(Color.rgb(20, 20, 30));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Draw player
            Player p = viewModel.getPlayerProperty().get();
            if (p != null) {
                gc.setFill(Color.BLUE);
                gc.fillOval(p.position().x() - 15, p.position().y() - 15, 30, 30);

                // Draw weapons
                drawWeapons(p);
                drawHealthBar(p);
            }

            // Draw enemies
            viewModel.getEnemiesMap().values().forEach(enemy -> {
                gc.setFill(Color.RED);
                gc.fillOval(enemy.position().x() - 10, enemy.position().y() - 10, 20, 20);
                double ratio = (double) enemy.currentHealth() / enemy.maxHealth();

                gc.setFill(Color.DARKRED);
                gc.fillRect(enemy.position().x() - 12, enemy.position().y() - 18, 24, 4);

                gc.setFill(Color.RED);
                gc.fillRect(enemy.position().x() - 12, enemy.position().y() - 18, 24 * ratio, 4);
            });
        });
    }

    private void drawWeapons(Player player) {
        Map<WeaponType, WeaponAnimationState> weaponStates = viewModel.getWeaponStates();

        for (Weapon weapon : player.equippedWeapons()) {
            WeaponAnimationState state = weaponStates.get(weapon.type());
            if (state == null) continue;

            switch (weapon.type()) {
                case SWORD -> drawSword(player, weapon, state);
                case AURA -> drawAura(player, weapon, state);
                case WHIP -> drawWhip(player, weapon, state);
            }
        }
    }

    private void drawSword(Player player, Weapon weapon, WeaponAnimationState state) {
        double angle = state.getAngle();

        int px = player.position().x();
        int py = player.position().y();

        double gripOffset = 50;
        double radius = weapon.range() - gripOffset;

        double sx = px + Math.cos(angle) * radius;
        double sy = py + Math.sin(angle) * radius;

        // Sword dimensions
        double bladeLength = 90;
        double bladeWidth = 6;
        double handleLength = 10;
        double handleWidth = 8;
        double guardWidth = 16;
        double guardHeight = 4;

        gc.save();
        gc.translate(sx, sy);
        gc.rotate(Math.toDegrees(angle) + 90);

        // Blade
        gc.setFill(Color.SILVER);
        gc.fillRect(-bladeWidth / 2, -bladeLength, bladeWidth, bladeLength);

        // Blade highlight
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeLine(0, -bladeLength, 0, 0);

        // Crossguard
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(-guardWidth / 2, 0, guardWidth, guardHeight);

        // Handle
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(-handleWidth / 2, guardHeight, handleWidth, handleLength);

        // Pommel
        gc.setFill(Color.GOLD);
        gc.fillOval(
                -handleWidth / 2,
                guardHeight + handleLength,
                handleWidth,
                handleWidth
        );

        gc.restore();
    }


    private void drawAura(Player player, Weapon weapon, WeaponAnimationState state) {
        // Pulsing aura effect
        double pulse = Math.sin(state.getAngle() * 3) * 10 + weapon.range();

        gc.setStroke(Color.rgb(100, 200, 255, 0.3));
        gc.setLineWidth(3);
        gc.strokeOval(
                player.position().x() - pulse,
                player.position().y() - pulse,
                pulse * 2,
                pulse * 2
        );
    }

    private void drawWhip(Player player, Weapon weapon, WeaponAnimationState state) {
        if (!state.isAttacking()) return;

        double progress = Math.min(1.0, state.getAttackProgress() / 300.0);
        boolean isLeft = state.isAttackingLeft();

        int whipLength = (int) (weapon.range() * progress);
        int whipWidth = 80;

        int startX = player.position().x();
        int startY = player.position().y();
        int endX = startX + (isLeft ? -whipLength : whipLength);

        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(6);

        for (int i = 0; i < 5; i++) {
            double t = i / 4.0;
            double curve = Math.sin(t * Math.PI) * 20 * progress;

            int x1 = (int) (startX + (endX - startX) * t);
            int y1 = (int) (startY + curve);

            int x2 = (int) (startX + (endX - startX) * (t + 0.25));
            int y2 = (int) (startY + Math.sin((t + 0.25) * Math.PI) * 20 * progress);

            gc.strokeLine(x1, y1, x2, y2);
        }

        if (progress > 0.3) {
            gc.setStroke(Color.rgb(255, 165, 0, 0.2));
            gc.setLineWidth(2);
            gc.strokeRect(
                    isLeft ? startX - whipLength : startX,
                    startY - (double) whipWidth / 2,
                    whipLength,
                    whipWidth
            );
        }
    }

    private void drawHealthBar(Player player) {
        double barWidth = 40;
        double barHeight = 6;

        double healthRatio =
                (double) player.currentHealth() / player.maxHealth();

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

        PopupProvider provider = new PopupProvider(mainStage)
                .setTitle("Game Paused")
                .addLabel("What do you want to do?");

        provider.addButton(e -> {
            viewModel.resumeGame();
            closePopup(e);
        }, "Continue");

        provider.addButton(e -> {
            viewModel.resetAndStartDefault();
            closePopup(e);
        }, "Restart");

        provider.addButton(e -> {
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
            closePopup(e);

            Platform.runLater(() -> {
                showStartupPopup();
                root.requestFocus();
            });
        }, "New Game");
        provider.addButton(e -> {
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

        // Pause the game
        viewModel.pauseGame();

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
        popup.setOnCloseRequest(Event::consume); // Prevent closing without selection
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
            viewModel.selectUpgrade(option);
            viewModel.resumeGame();
            popup.close();
            Platform.runLater(root::requestFocus);
        });

        return upgradeBox;
    }
}