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
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameController {

  @FXML
  private GridPane boardGrid;

  @FXML
  private Label statusLabel;

  private ConnectFourService service;

  private static final int ROWS = 6;
  private static final int COLS = 7;

  private StackPane[][] cells;
  private Circle[][] discs;
  private Label[][] decayLabels;

  // ✅ verhindert Klicks bevor New Game gestartet wurde
  private boolean gameStarted = false;

  @FXML
  public void initialize() {
    service = new ConnectFourServiceImpl();
    buildBoardUI();

    // Startzustand: leeres Board + Hinweis
    renderBoard();
    statusLabel.setText("Klicke auf 'New Game' ✅");
  }

  @FXML
  private void onNewGame() {
    try {
      // passt zu eurem Konstruktor: (toxicFieldCount, decayAfterTurns)
      GameConfiguration config = new GameConfiguration(5, 3);

      service.startGame(config);
      gameStarted = true;

      renderBoard();
      statusLabel.setText("Your Turn: " + currentPlayerText());
    } catch (Exception e) {
      statusLabel.setText("Start-Fehler: " + e.getMessage());
    }
  }

  private void buildBoardUI() {
    boardGrid.getChildren().clear();
    boardGrid.setHgap(10);
    boardGrid.setVgap(10);

    cells = new StackPane[ROWS][COLS];
    discs = new Circle[ROWS][COLS];
    decayLabels = new Label[ROWS][COLS];

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {

        StackPane cell = new StackPane();
        cell.setPrefSize(70, 70);
        cell.setMinSize(70, 70);
        cell.setMaxSize(70, 70);
        cell.setAlignment(Pos.CENTER);

        Circle disc = new Circle(26);
        disc.setStroke(Color.GRAY);
        disc.setFill(Color.LIGHTBLUE); // leer

        Label decay = new Label("");
        decay.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: black;");

        cell.getChildren().addAll(disc, decay);

        final int col = c;
        cell.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> onColumnClicked(col));

        cells[r][c] = cell;
        discs[r][c] = disc;
        decayLabels[r][c] = decay;

        boardGrid.add(cell, c, r);
      }
    }
  }

  private void onColumnClicked(int col) {
    // ✅ Keine Moves bevor New Game gestartet wurde
    if (!gameStarted) {
      statusLabel.setText("Bitte zuerst 'New Game' klicken ✅");
      return;
    }

    try {
      service.dropChip(col);
      renderBoard();
      statusLabel.setText("Your Turn: " + currentPlayerText());
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

        // Default: leer
        discs[r][c].setFill(Color.LIGHTBLUE);
        decayLabels[r][c].setText("");

        Player p = f.getOccupyingPlayer();
        if (p != null) {
          switch (p.color()) {
            case RED -> discs[r][c].setFill(Color.RED);
            case YELLOW -> discs[r][c].setFill(Color.GOLD);
            default -> discs[r][c].setFill(Color.GRAY);
          }
        }

        // Toxic: sichtbar grün + Zahl (wie Mockup)
        if (f.isToxicZone()) {
          // Wenn Feld leer ist, trotzdem grün anzeigen
          if (p == null) {
            discs[r][c].setFill(Color.LIGHTGREEN);
          }

          int decay = f.getDecayTime();
          if (decay > 0) {
            decayLabels[r][c].setText(String.valueOf(decay));
          }
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
