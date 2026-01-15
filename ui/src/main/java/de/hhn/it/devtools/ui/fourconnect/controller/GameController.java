package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GameController {

  @FXML
  private GridPane boardGrid;
  @FXML
  private Label statusLabel;

  private ConnectFourService service;

  private static final int ROWS = 6;
  private static final int COLS = 7;

  private Button[][] cells;

  @FXML
  public void initialize() {
    service = new ConnectFourServiceImpl();
    buildBoardUI();
    onNewGame(); // direkt starten
  }

  @FXML
  private void onNewGame() {
    try {
      // WICHTIG: Falls GameConfiguration bei euch anders konstruiert wird,
      // sag mir kurz den Konstruktor, dann passe ich das an.
      GameConfiguration config = new GameConfiguration(5, 3); // z.B. 5 toxic fields

      service.startGame(config);
      renderBoard();
      statusLabel.setText("Neues Spiel ✅ Spieler am Zug: " + currentPlayerText());
    } catch (Exception e) {
      statusLabel.setText("Start-Fehler: " + e.getMessage());
    }
  }

  private void buildBoardUI() {
    boardGrid.getChildren().clear();
    boardGrid.setHgap(8);
    boardGrid.setVgap(8);

    cells = new Button[ROWS][COLS];

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        Button cell = new Button();
        cell.setPrefSize(70, 70);
        cell.setFocusTraversable(false);

        final int col = c;
        cell.setOnAction(e -> onColumnClicked(col));

        cells[r][c] = cell;
        boardGrid.add(cell, c, r);
      }
    }
  }

  private void onColumnClicked(int col) {
    try {
      service.dropChip(col);
      renderBoard();
      statusLabel.setText("Zug ✅ Spieler am Zug: " + currentPlayerText());
    } catch (IllegalParameterException | OperationNotSupportedException ex) {
      statusLabel.setText("Nicht möglich: " + ex.getMessage());
    } catch (Exception ex) {
      statusLabel.setText("Fehler: " + ex.getMessage());
    }
  }

  private void renderBoard() {
    GameBoard board = service.getBoard();

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        Field f = board.getField(r, c);

        // Text: R / Y / leer
        Player p = f.getOccupyingPlayer();
        String txt = (p == null) ? "" : p.color().name().substring(0, 1);
        cells[r][c].setText(txt);

        // Kein CSS: wir setzen inline style nur für Toxic-Felder (minimal)
        if (f.isToxicZone()) {
          cells[r][c].setStyle("-fx-border-color: black; -fx-border-width: 2;");
        } else {
          cells[r][c].setStyle("");
        }
      }
    }
  }

  private String currentPlayerText() {
    try {
      Player p = service.getCurrentPlayer();
      return p.name() + " (" + p.color().name() + ")";
    } catch (Exception e) {
      return "?";
    }
  }
}
