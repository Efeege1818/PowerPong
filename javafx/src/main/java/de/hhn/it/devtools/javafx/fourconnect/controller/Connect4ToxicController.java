package de.hhn.it.devtools.javafx.fourconnect.controller;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.GameListener;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.javafx.fourconnect.ServiceProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class Connect4ToxicController implements GameListener {

	private static final double CELL_SIZE = 58;
	private static final double GAP = 4;

	@FXML
	private Label statusLabel;

	@FXML
	private Label messageLabel;

	@FXML
	private Spinner<Integer> toxicCountSpinner;

	@FXML
	private Spinner<Integer> decayTurnsSpinner;

	@FXML
	private Button startButton;

	@FXML
	private GridPane dropButtonsGrid;

	@FXML
	private GridPane boardGrid;

	private ConnectFourService service;
	private boolean gameRunning = false;
	private boolean gameOver = false;
	private boolean winnerAlertShown = false;

	@FXML
	private void initialize() {
		service = ServiceProvider.getConnectFourService();

		toxicCountSpinner.setValueFactory(
				new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 4));
		decayTurnsSpinner.setValueFactory(
				new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

		statusLabel.setText("Noch nicht gestartet");
		messageLabel.setText("Wähle Einstellungen und klicke Start.");

		dropButtonsGrid.getChildren().clear();
		boardGrid.getChildren().clear();
	}

	@FXML
	private void onStartGame() {
		int toxicCount = toxicCountSpinner.getValue();
		int decayTurns = decayTurnsSpinner.getValue();

		service.startGame(new GameConfiguration(toxicCount, decayTurns));

		try {
			service.registerListener(this);
		} catch (Exception ignored) {
			try {
				service.addGameListener(this);
			} catch (Exception ignoredAgain) {
				// absichtlich leer
			}
		}

		gameRunning = true;
		gameOver = false;
		winnerAlertShown = false;

		GameBoard board = service.getBoard();

		buildDropButtons(board.getColumns());
		redrawBoard(board);

		statusLabel.setText("Am Zug: " + safeCurrentPlayerName());
		messageLabel.setText(
				"Spiel läuft. Klick oben auf eine Spalte. Toxic Fields: "
						+ countVisibleToxicFields(board)
						+ "/"
						+ toxicCount);
	}

	private void buildDropButtons(int cols) {
		dropButtonsGrid.getChildren().clear();

		double totalWidth = cols * CELL_SIZE + (cols - 1) * GAP;
		dropButtonsGrid.setPrefWidth(totalWidth);
		dropButtonsGrid.setMinWidth(totalWidth);
		dropButtonsGrid.setMaxWidth(totalWidth);

		for (int c = 0; c < cols; c++) {
			int col = c;
			Button button = new Button("Drop " + (col + 1));
			button.setPrefWidth(CELL_SIZE);
			button.setMinWidth(CELL_SIZE);
			button.setMaxWidth(CELL_SIZE);
			button.setPrefHeight(30);
			button.setOnAction(e -> onDrop(col));
			dropButtonsGrid.add(button, c, 0);
		}
	}

	private void onDrop(int column) {
		if (!gameRunning || gameOver) {
			return;
		}

		try {
			service.dropChip(column);

			try {
				service.applyToxicDecay();
			} catch (Exception ignored) {
				// optional
			}

		} catch (IllegalParameterException e) {
			messageLabel.setText("Ungültige Spalte oder Spalte voll.");
		} catch (OperationNotSupportedException e) {
			messageLabel.setText("Operation nicht möglich (Spiel nicht gestartet / beendet).");
		}
	}

	private void redrawBoard(GameBoard board) {
		boardGrid.getChildren().clear();

		int rows = board.getRows();
		int cols = board.getColumns();
		Field[][] fields = board.getFields();

		double totalWidth = cols * CELL_SIZE + (cols - 1) * GAP;
		boardGrid.setPrefWidth(totalWidth);
		boardGrid.setMinWidth(totalWidth);
		boardGrid.setMaxWidth(totalWidth);

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Field field = fields[r][c];

				StackPane cell = new StackPane();
				cell.setPrefSize(CELL_SIZE, CELL_SIZE);
				cell.setMinSize(CELL_SIZE, CELL_SIZE);
				cell.setMaxSize(CELL_SIZE, CELL_SIZE);

				String bg = "white";
				String text = "";

				if (field.isToxicZone()) {
					bg = "lightgray";
				}

				if (field.isOccupied() && field.getOccupyingPlayer() != null) {
					PlayerColor color = field.getOccupyingPlayer().color();
					bg = (color == PlayerColor.RED) ? "tomato" : "gold";

					int decay = field.getDecayTime();
					if (decay > 0) {
						text = String.valueOf(decay);
					}
				}

				cell.setStyle(
						"-fx-border-color: black; "
								+ "-fx-border-width: 1; "
								+ "-fx-background-color: " + bg + ";");

				Label label = new Label(text);
				cell.getChildren().add(label);

				boardGrid.add(cell, c, r);
			}
		}

		int visibleToxic = countVisibleToxicFields(board);
		int requestedToxic = toxicCountSpinner.getValue();

		if (!gameOver) {
			if (visibleToxic < requestedToxic) {
				messageLabel.setText(
						"Achtung: angezeigt werden "
								+ visibleToxic
								+ " Toxic Fields, angefordert waren "
								+ requestedToxic
								+ ".");
			} else {
				messageLabel.setText("Spiel läuft. Klick oben auf eine Spalte.");
			}
		}
	}

	private int countVisibleToxicFields(GameBoard board) {
		int count = 0;
		Field[][] fields = board.getFields();

		for (Field[] row : fields) {
			for (Field field : row) {
				if (field.isToxicZone()) {
					count++;
				}
			}
		}
		return count;
	}

	private void disableDropButtons() {
		for (var node : dropButtonsGrid.getChildren()) {
			node.setDisable(true);
		}
	}

	private String safeCurrentPlayerName() {
		try {
			Player p = service.getCurrentPlayer();
			return (p == null) ? "-" : p.name();
		} catch (Exception e) {
			return "-";
		}
	}

	private void showWinnerAlert(String text) {
		if (winnerAlertShown) {
			return;
		}
		winnerAlertShown = true;

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Spiel beendet");
		alert.setHeaderText("Connect4Toxic");
		alert.setContentText(text);
		alert.showAndWait();
	}

	@Override
	public void onTurnChanged(Player currentPlayer) {
		Platform.runLater(() ->
				statusLabel.setText("Am Zug: " + (currentPlayer == null ? "-" : currentPlayer.name())));
	}

	@Override
	public void onBoardChanged(GameBoard board) {
		Platform.runLater(() -> redrawBoard(board));
	}

	@Override
	public void onGameEnded(Player winner, boolean isDraw) {
		Platform.runLater(() -> {
			gameOver = true;
			disableDropButtons();

			if (isDraw) {
				messageLabel.setText("Unentschieden!");
				showWinnerAlert("Das Spiel ist unentschieden.");
			} else if (winner != null) {
				messageLabel.setText("Gewonnen: " + winner.name());
				showWinnerAlert(winner.name() + " hat gewonnen!");
			} else {
				messageLabel.setText("Spiel beendet.");
				showWinnerAlert("Das Spiel wurde beendet.");
			}
		});
	}
}