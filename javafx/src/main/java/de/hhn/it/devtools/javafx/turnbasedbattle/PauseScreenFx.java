package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.Map;

import static javafx.application.Application.launch;

public class PauseScreenFx extends VBox {
		private static final org.slf4j.Logger logger =
						org.slf4j.LoggerFactory.getLogger(PauseScreenFx.class);
		private final PauseScreenViewModel viewModel;

		public PauseScreenFx(PauseScreenViewModel viewModel) {
				this.viewModel = viewModel;

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

				// RIGHT COLUMN (Monster image + status lists)
				VBox rightCol = new VBox();
				rightCol.setAlignment(Pos.TOP_CENTER);
				rightCol.setPadding(new Insets(12));
				rightCol.setPrefWidth(300);

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

				// STATUS BOX below the image: active buffs, debuffs and DOTs
				VBox statusBox = new VBox(6);
				statusBox.setPadding(new Insets(10, 0, 0, 0));
				statusBox.setPrefWidth(280);
				statusBox.setStyle("-fx-border-color: transparent;");

				// Active positive buffs
				Label buffsTitle = new Label("Active Buffs:");
				buffsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
				statusBox.getChildren().add(buffsTitle);

				Map<Integer, Move> positiveBuffs = viewModel.getActivePositiveBuffs();
				if (positiveBuffs.isEmpty()) {
						Label none = new Label("  (none)");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : positiveBuffs.values()) {
								Label l = new Label("  • " + m.name() + " — " + m.description());
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}

				// Active debuffs
				Label debuffsTitle = new Label("Active Debuffs:");
				debuffsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 0 0 0;");
				statusBox.getChildren().add(debuffsTitle);

				Map<Integer, Move> debuffs = viewModel.getActiveDebuffs();
				if (debuffs.isEmpty()) {
						Label none = new Label("  (none)");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : debuffs.values()) {
								Label l = new Label("  • " + m.name() + " — " + m.description());
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}

				// Active DOTs
				Label dotsTitle = new Label("Active DOTs:");
				dotsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 0 0 0;");
				statusBox.getChildren().add(dotsTitle);

				Map<Integer, Move> dots = viewModel.getActiveDots();
				if (dots.isEmpty()) {
						Label none = new Label("  (none)");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : dots.values()) {
								Label l = new Label("  • " + m.name() + " — " + m.description());
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}

				// Add status box below whatever was added above (image or fallback)
				rightCol.getChildren().add(statusBox);


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
				SimpleMonster monster2 = SimpleMonster.create(data.getMonsters()[3]);
				PauseScreenViewModel viewModel = new PauseScreenViewModel(runtimeMonster, monster2);

				// Pass the viewModel into the TestApp via a static field
				PauseScreenFx.TestApp.viewModelForTest = viewModel;

				// Now launch the JavaFX application (this will call TestApp.start)
				launch(PauseScreenFx.TestApp.class, args);

				// Code after launch runs only after the JavaFX window is closed.
		}

		public static class TestApp extends Application {
				// Static holder used only for quick preview/testing
				public static PauseScreenViewModel viewModelForTest;
				Data data = new Data();

				@Override
				public void start(Stage stage) {
						// Defensive check — create a fallback view model if the static holder is null
						PauseScreenViewModel vm = viewModelForTest != null
										? viewModelForTest
										: new PauseScreenViewModel(
										SimpleMonster.create(data.getMonsters()[0]),
										SimpleMonster.create(data.getMonsters()[1])
						);

						PauseScreenFx screen = new PauseScreenFx(vm);
						Scene scene = new Scene(screen, 880, 560);
						stage.setTitle("Pause Screen Preview");
						stage.setScene(scene);
						stage.show();
				}

		}
}