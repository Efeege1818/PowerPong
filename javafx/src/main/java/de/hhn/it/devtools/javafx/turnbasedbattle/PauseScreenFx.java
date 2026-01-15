package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
		public static final String SCREEN_NAME = "PauseScreen";

		private final PauseScreenViewModel viewModel;
		private final SimpleScreenManager screenManager;

		// UI fields that need to be updated when switching monsters
		private final Label titleLabel;
		private final Label focusLabel;
		private final Label statsLabel;
		private final Label passiveTextLabel;
		private final TextFlow attacksList;
		private final VBox rightCol;
		private final VBox imageHolder; // container for the ImageView or fallback Circle
		private final VBox statusBox;

		public PauseScreenFx(SimpleScreenManager screenManager, PauseScreenViewModel viewModel) {
				this.viewModel = viewModel;
				this.screenManager = screenManager;

				// create updatable UI controls
				titleLabel = new Label();
				titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

				focusLabel = new Label();
				focusLabel.setStyle("-fx-font-size: 12px;");

				statsLabel = new Label();
				statsLabel.setStyle("-fx-font-size: 12px;");

				Label passiveTitle = new Label("Passive:");
				passiveTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
				passiveTextLabel = new Label();
				passiveTextLabel.setStyle("-fx-font-size: 12px;");

				attacksList = new TextFlow();
				attacksList.setMaxWidth(420);
				attacksList.setStyle("-fx-font-size: 12px; -fx-line-spacing: 6px;");

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

				Label attacksTitle = new Label("Attacks:");
				attacksTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

				leftCol.getChildren().addAll(
								titleLabel, focusLabel, new Region(), statsLabel,
								passiveTitle, passiveTextLabel,
								attacksTitle, attacksList
				);

				// RIGHT COLUMN (Monster image + status lists)
				rightCol = new VBox();
				rightCol.setAlignment(Pos.TOP_CENTER);
				rightCol.setPadding(new Insets(12));
				rightCol.setPrefWidth(300);

				imageHolder = new VBox();
				imageHolder.setAlignment(Pos.CENTER);

				// STATUS BOX below the image: active buffs, debuffs and DOTs
				statusBox = new VBox(6);
				statusBox.setPadding(new Insets(10, 0, 0, 0));
				statusBox.setPrefWidth(280);
				statusBox.setStyle("-fx-border-color: transparent;");

				// button for monster switch
				VBox justForTheButton = new VBox();
				Button monsterSwitch = new Button("Switch to other Monster");
				monsterSwitch.setOnAction(e -> {
						viewModel.switchShownMonster();
						refreshFromViewModel();
				});
				justForTheButton.getChildren().add(monsterSwitch);

				// assemble right column: imageHolder, statusBox, and button
				rightCol.getChildren().addAll(imageHolder, statusBox, justForTheButton);

				// Put columns into content and add to this VBox
				content.getChildren().addAll(leftCol, rightCol);
				HBox.setHgrow(leftCol, Priority.ALWAYS);
				this.getChildren().add(content);

				// initial fill from view model
				refreshFromViewModel();
		}

		/**
		 * Refresh UI controls from the current viewModel state.
		 */
		private void refreshFromViewModel() {
				// Basic fields
				String monsterName = viewModel.getMonsterName();
				int maxHp = viewModel.getMaxHp();
				int atk = viewModel.getAtk();
				int def = viewModel.getDef();
				String focusInfo = viewModel.getFocus();
				String passiveInfo = viewModel.getPassiveInfo();
				Map<Integer, Move> moves = viewModel.getMoves();

				titleLabel.setText(monsterName);
				focusLabel.setText("Focus: " + (focusInfo == null ? "" : focusInfo));
				statsLabel.setText("HP: " + maxHp + "\nATK: " + atk + "\nDEF: " + def);
				passiveTextLabel.setText(passiveInfo == null ? "" : passiveInfo);

				// Attacks list
				attacksList.getChildren().clear();
				if (moves != null && !moves.isEmpty()) {
						int size = moves.size();
						int idx = 0;
						for (Map.Entry<Integer, Move> entry : moves.entrySet()) {
								idx++;
								Move move = entry.getValue();
								boolean isLast = (idx == size);

								if (isLast) {
										Text header = new Text("Special Move:\n");
										header.setFont(Font.font(header.getFont().getFamily(), FontWeight.BOLD, 14));
										attacksList.getChildren().add(header);
								}

								Text nameText = new Text("  • " + (move.name() == null ? "" : move.name()) + "\n");
								Text descText = new Text("      ◦ " + (move.description() == null ? "" : move.description()) + "\n");

								if (isLast) {
										nameText.setFont(Font.font(nameText.getFont().getFamily(), FontWeight.BOLD, 12));
										descText.setFont(Font.font(descText.getFont().getFamily(), FontWeight.BOLD, 12));
								}

								attacksList.getChildren().addAll(nameText, descText);
						}
				}

				// Image or fallback
				imageHolder.getChildren().clear();
				ImageView iv = viewModel.getImageView();
				if (iv != null) {
						iv.setFitWidth(260);
						iv.setFitHeight(260);
						iv.setPreserveRatio(true);
						imageHolder.getChildren().add(iv);
				} else {
						Element fallbackElement = viewModel.getElement();
						String fallbackColore;
						switch (fallbackElement) {
								case FIRE:
										fallbackColore = "#ff0000";
										break;
								case GRASS:
										fallbackColore = "#00ff00";
										break;
								case WATER:
										fallbackColore = "#1e90ff";
										break;
								default:
										fallbackColore = "#000000";
										break;
						}

						Circle fallback = new Circle(130);
						fallback.setFill(Color.TRANSPARENT);
						fallback.setStroke(Color.web(fallbackColore));
						fallback.setStrokeWidth(3.0);
						imageHolder.getChildren().add(fallback);
				}

				// Rebuild status box (buffs, debuffs, dots)
				statusBox.getChildren().clear();

				Label buffsTitle = new Label("Active Buffs:");
				buffsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
				statusBox.getChildren().add(buffsTitle);

				Map<Integer, Move> positiveBuffs = viewModel.getActivePositiveBuffs();
				if (positiveBuffs == null || positiveBuffs.isEmpty()) {
						Label none = new Label(" ");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : positiveBuffs.values()) {
								Label l = new Label("  • " + (m.name() == null ? "" : m.name()) + " — " + (m.description() == null ? "" : m.description()));
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}

				Label debuffsTitle = new Label("Active Debuffs:");
				debuffsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 0 0 0;");
				statusBox.getChildren().add(debuffsTitle);

				Map<Integer, Move> debuffs = viewModel.getActiveDebuffs();
				if (debuffs == null || debuffs.isEmpty()) {
						Label none = new Label(" ");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : debuffs.values()) {
								Label l = new Label("  • " + (m.name() == null ? "" : m.name()) + " — " + (m.description() == null ? "" : m.description()));
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}

				Label dotsTitle = new Label("Active DOTs:");
				dotsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 0 0 0;");
				statusBox.getChildren().add(dotsTitle);

				Map<Integer, Move> dots = viewModel.getActiveDots();
				if (dots == null || dots.isEmpty()) {
						Label none = new Label(" ");
						none.setStyle("-fx-font-size: 11px;");
						statusBox.getChildren().add(none);
				} else {
						for (Move m : dots.values()) {
								Label l = new Label("  • " + (m.name() == null ? "" : m.name()) + " — " + (m.description() == null ? "" : m.description()));
								l.setStyle("-fx-font-size: 11px;");
								statusBox.getChildren().add(l);
						}
				}
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
				SimpleMonster monster2 = SimpleMonster.create(data.getMonsters()[1]);
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
				public static SimpleScreenManager screenManagerForTest;
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

						PauseScreenFx screen = new PauseScreenFx(screenManagerForTest, vm);
						Scene scene = new Scene(screen, 880, 560);
						stage.setTitle("Pause Screen Preview");
						stage.setScene(scene);
						stage.show();
				}

		}
}