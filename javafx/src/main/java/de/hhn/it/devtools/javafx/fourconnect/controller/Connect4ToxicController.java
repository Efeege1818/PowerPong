package de.hhn.it.devtools.javafx.fourconnect.controller;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.*;
import de.hhn.it.devtools.javafx.fourconnect.ServiceProvider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Connect4ToxicController implements GameListener {

	@FXML private Label statusLabel;
	@FXML private Label messageLabel;

	@FXML private Spinner<Integer> toxicCountSpinner;
	@FXML private Spinner<Integer> decayTurnsSpinner;

	@FXML private Button startButton;
	@FXML private HBox dropButtonsBox;
	@FXML private GridPane boardGrid;

	private ConnectFourService service;
	private boolean gameRunning = false;
	private boolean gameOver = false;

	@FXML
	private void initialize() {
		service = ServiceProvider.getConnectFourService();

		toxicCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 4));
		decayTurnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));

		statusLabel.setText("Noch nicht gestartet");
		messageLabel.setText("Wähle Einstellungen und klicke Start.");
		dropButtonsBox.getChildren().clear();
		boardGrid.getChildren().clear();
	}

	@FXML
	private void onStartGame() {
		int toxicCount = toxicCountSpinner.getValue();
		int decayTurns = decayTurnsSpinner.getValue();

		service.startGame(new GameConfiguration(toxicCount, decayTurns));

		// Listener registrieren (Winckler will das!)
		try {
			service.registerListener(this);
		} catch (Exception ignored) {
			service.addGameListener(this);
		}

		gameRunning = true;
		gameOver = false;

		buildDropButtons();
		redrawBoard(service.getBoard());
		statusLabel.setText("Am Zug: " + safeCurrentPlayerName());
		messageLabel.setText("Spiel läuft. Klick oben auf eine Spalte.");
	}

	private void buildDropButtons() {
		dropButtonsBox.getChildren().clear();
		int cols = service.getBoard().getColumns();

		for (int c = 0; c < cols; c++) {
			int col = c;
			Button b = new Button("Drop " + (col + 1));
			b.setOnAction(e -> onDrop(col));
			dropButtonsBox.getChildren().add(b);
		}
	}

	private void onDrop(int column) {
		if (!gameRunning || gameOver) return;

		try {
			service.dropChip(column);

			// Optional: falls ihr Toxic-Decay pro Zug wollt
			try { service.applyToxicDecay(); } catch (Exception ignored) {}

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

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				Field f = fields[r][c];

				StackPane cell = new StackPane();
				cell.setPrefSize(45, 45);

				String bg = "white";
				String text = "";

				if (f.isToxicZone()) {
					bg = "lightgray"; // Toxic Zone
				}

				if (f.isOccupied() && f.getOccupyingPlayer() != null) {
					PlayerColor color = f.getOccupyingPlayer().color();
					bg = (color == PlayerColor.RED) ? "tomato" : "gold";

					int decay = f.getDecayTime();
					if (decay > 0) text = String.valueOf(decay);
				}

				cell.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: " + bg + ";");
				cell.getChildren().add(new Label(text));

				boardGrid.add(cell, c, r);
			}
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

	// ---------------- GameListener ----------------

	@Override
	public void onTurnChanged(Player currentPlayer) {
		Platform.runLater(() -> statusLabel.setText("Am Zug: " + (currentPlayer == null ? "-" : currentPlayer.name())));
	}

	@Override
	public void onBoardChanged(GameBoard board) {
		Platform.runLater(() -> redrawBoard(board));
	}

	@Override
	public void onGameEnded(Player winner, boolean isDraw) {
		Platform.runLater(() -> {
			gameOver = true;
			if (isDraw) {
				messageLabel.setText("Unentschieden!");
			} else if (winner != null) {
				messageLabel.setText("Gewonnen: " + winner.name());
			} else {
				messageLabel.setText("Spiel beendet.");
			}
		});
	}
}