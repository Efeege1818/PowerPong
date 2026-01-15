package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static javafx.application.Application.launch;

/**
 * InfoScreenFx — builds the UI shows Monster Infos in Selectscreen.
 */
public class InfoScreenFx extends VBox {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(InfoScreenFx.class);
    public static final String SCREEN_NAME = "InfoScreen";

    private final InfoScreenViewModel viewModel;
    private final SimpleScreenManager screenManager;

    public InfoScreenFx(SimpleScreenManager screenManager, InfoScreenViewModel viewModel) {
        this.viewModel = viewModel;
        this.screenManager = screenManager;

        String monsterName = viewModel.getMonsterName();
        int maxHp = viewModel.getMaxHp();
        int atk = viewModel.getAtk();
        int def = viewModel.getDef();
        String focusInfo = viewModel.getFocus();
        String passiveInfo = viewModel.getPassiveInfo();
        Map<Integer, Move> moves = viewModel.getMoves();
        Move specialMove = moves.get(5);

        // Outer frame
        this.setStyle("-fx-border-color: black; -fx-border-width: 3; -fx-background-color: white;");
        this.setPadding(new Insets(6));

        // Main content area: left text column + right image column
        HBox content = new HBox(20);
        content.setPadding(new Insets(12));
        content.setAlignment(Pos.TOP_LEFT);

        // LEFT COLUMN (text)
        VBox leftCol = new VBox(10);
        leftCol.setPrefWidth(520);
        leftCol.setAlignment(Pos.TOP_LEFT);

        Label title = new Label(monsterName);
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label focus = new Label("Focus: "+ focusInfo);
        focus.setStyle("-fx-font-size: 12px;");

        // small gap / visual separator
        Region gap = new Region();
        gap.setPrefHeight(6);

        // Stats
        Label stats = new Label("HP: "+ maxHp+"\nATK: "+ atk+"\nDEF: "+def);
        stats.setStyle("-fx-font-size: 12px;");

        // Passive
        Label passiveTitle = new Label("Passive:");
        passiveTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Label passiveText = new Label(passiveInfo);
        passiveText.setStyle("-fx-font-size: 12px;");


        // Überlegen wie man die moves darstellt. (Hab aus Versehen den alten Kommentar gelöscht ups)
        // Attacks
        Label attacksTitle = new Label("Attacks:");
        attacksTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        TextFlow attacksList = new TextFlow();
        attacksList.setMaxWidth(420);
        int size = moves.size();
        int index = 0;

        for (Map.Entry<Integer, Move> entry : moves.entrySet()) {
            index++;
            Move move = entry.getValue();

            boolean isLast = (index == size);

            //highlights the special move
            if (isLast) {
                Text header = new Text("Special Move:\n");
                header.setFont(Font.font(header.getFont().getFamily(), FontWeight.BOLD, 14));
                attacksList.getChildren().add(header);
            }

            Text nameText = new Text("  • " + move.name() + "\n");
            Text descText = new Text("      ◦ " + move.description() + "\n");

            if (isLast) {
                nameText.setFont(Font.font(nameText.getFont().getFamily(), FontWeight.BOLD, 12));
                descText.setFont(Font.font(descText.getFont().getFamily(), FontWeight.BOLD, 12));
            }

            attacksList.getChildren().addAll(nameText, descText);
        }
        attacksList.setStyle("-fx-font-size: 12px; -fx-line-spacing: 6px;");

        leftCol.getChildren().addAll(
                title, focus, new Region(), stats,
                passiveTitle, passiveText,
                attacksTitle, attacksList
        );

        // RIGHT COLUMN

        VBox rightCol = new VBox();
        rightCol.setAlignment(Pos.TOP_CENTER);
        rightCol.setPadding(new Insets(12));
        rightCol.setPrefWidth(300);

        // close Button
        HBox forCloseButton = new HBox();
        forCloseButton.setAlignment(Pos.TOP_RIGHT);
        Button close = new Button("Close Info");
        close.setOnAction(e -> {
            screenManager.switchTo(SCREEN_NAME, SelectScreen.SCREEN_NAME);
        });
        forCloseButton.getChildren().add(close);
        rightCol.getChildren().add(0,forCloseButton);

        // Monster image
        ImageView MonsterView = viewModel.getImageView();
        if (MonsterView != null) {
            MonsterView.setFitWidth(260);
            MonsterView.setFitHeight(260);
            MonsterView.setPreserveRatio(true);
            rightCol.getChildren().add(MonsterView);
        } else {
            // Fallback: draw a circle
            Element fallbackElement = viewModel.getElement();
            String fallbackColore;
                    switch (fallbackElement){
                        case FIRE:
                            fallbackColore ="#ff0000";
                            break;
                        case GRASS:
                            fallbackColore ="#00ff00";
                            break;
                        case WATER:
                            fallbackColore ="#1e90ff";
                            break;
                        default : fallbackColore ="#000000";
                        break;
                    };


            Circle fallback = new Circle(130);
            fallback.setFill(Color.TRANSPARENT);
            fallback.setStroke(Color.web(fallbackColore));
            fallback.setStrokeWidth(3.0);
            rightCol.getChildren().add(fallback);
        }



        // Put columns into content and add to this VBox
        content.getChildren().addAll(leftCol, rightCol);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        this.getChildren().add(content);
    }


    /**
     * main — quick preview launcher.
     * Coded by AI for testing while coding
     */
    public static void main(String[] args) {
        // --- Prepare test data BEFORE launching JavaFX ---
        // Monster record requires at least 5 moves according to your record validation.
        Data data = new Data();

        // Create runtime monster and view model
        SimpleMonster runtimeMonster = SimpleMonster.create(data.getMonsters()[2]);
        InfoScreenViewModel viewModel = new InfoScreenViewModel(runtimeMonster);

        // Pass the viewModel into the TestApp via a static field
        TestApp.viewModelForTest = viewModel;

        // Now launch the JavaFX application (this will call TestApp.start)
        launch(TestApp.class, args);

        // Code after launch runs only after the JavaFX window is closed.
    }

    public static class TestApp extends Application {
        // Static holder used only for quick preview/testing
        public static InfoScreenViewModel viewModelForTest;
        public static SimpleScreenManager simpleScreenManagerForTest;
        Data data = new Data();

        @Override
        public void start(Stage stage) {
            // Defensive check — create a fallback view model if the static holder is null
            InfoScreenViewModel vm = viewModelForTest != null
                    ? viewModelForTest
                    : new InfoScreenViewModel(SimpleMonster.create(data.getMonsters()[0]));


            InfoScreenFx screen = new InfoScreenFx(simpleScreenManagerForTest,vm);
            Scene scene = new Scene(screen, 880, 560);
            stage.setTitle("Info Screen Preview");
            stage.setScene(scene);
            stage.show();
        }

    }
}