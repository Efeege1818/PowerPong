package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;
import de.hhn.it.devtools.apis.fourconnect.ConnectFourService;
import de.hhn.it.devtools.apis.fourconnect.Field;
import de.hhn.it.devtools.apis.fourconnect.GameBoard;
import de.hhn.it.devtools.apis.fourconnect.GameConfiguration;
import de.hhn.it.devtools.apis.fourconnect.Player;
import de.hhn.it.devtools.apis.fourconnect.PlayerColor;
import de.hhn.it.devtools.components.fourconnect.provider.ConnectFourServiceImpl;
import de.hhn.it.devtools.ui.fourconnect.UIState;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GameController {

  @FXML private GridPane boardGrid;
  @FXML private Label statusLabel;

  private ConnectFourService service;

  private static final int ROWS = 6;
  private static final int COLS = 7;

  private StackPane[][] cells;
  private Circle[][] discs;
  private Label[][] decayLabels;

  private boolean gameStarted = false;

  @FXML
  public void initialize() {
    service = new ConnectFourServiceImpl();
    buildBoardUI();

    renderBoard();
    statusLabel.setText(
        "Klicke auf 'New Game' | Player 1: " + UIState.getPlayer1Color()
            + " Player 2: " + UIState.getPlayer2Color());
  }

  @FXML
  private void onNewGame() {
    try {
      GameConfiguration config = new GameConfiguration(3, 3);

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
        cell.setAlignment(Pos.CENTER);

        Circle disc = new Circle(26);
        disc.setStroke(Color.GRAY);
        disc.setFill(Color.LIGHTBLUE);

        Label decay = new Label("");
        decay.setMouseTransparent(true);
        StackPane.setAlignment(decay, Pos.TOP_LEFT);
        decay.setTranslateX(12);
        decay.setTranslateY(10);

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
    if (!gameStarted) {
      statusLabel.setText("Bitte zuerst New Game klicken");
      return;
    }

    try {
      service.dropChip(col);

      // ✅ WICHTIG: Toxic decay hier — NICHT im Service dropChip
      service.applyToxicDecay();

      renderBoard();

      Player winner = findWinner();
      if (winner != null) {
        showWinnerPopup(winner);
        gameStarted = false;
        return;
      }

      if (isDraw()) {
        showDrawPopup();
        gameStarted = false;
        return;
      }

      statusLabel.setText("Your Turn: " + currentPlayerText());

    } catch (IllegalParameterException | OperationNotSupportedException ex) {
      statusLabel.setText("Nicht möglich: " + ex.getMessage());
    }
  }

  private void renderBoard() {
    GameBoard board = service.getBoard();

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        Field f = board.getField(r, c);

        discs[r][c].setFill(Color.LIGHTBLUE);
        discs[r][c].setStroke(Color.GRAY);
        discs[r][c].setStrokeWidth(1);
        decayLabels[r][c].setText("");

        Player p = f.getOccupyingPlayer();

        if (p != null) {
          if (p.color() == PlayerColor.RED) {
            discs[r][c].setFill(Color.RED);
          } else if (p.color() == PlayerColor.YELLOW) {
            discs[r][c].setFill(Color.GOLD);
          }
        }

        if (f.isToxicZone()) {
          discs[r][c].setStroke(Color.DARKGREEN);
          discs[r][c].setStrokeWidth(3);

          if (p != null && f.getDecayTime() > 0) {
            decayLabels[r][c].setText(String.valueOf(f.getDecayTime()));
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
      return "Player";
    }
  }

  private void showWinnerPopup(Player winner) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setHeaderText(winner.color() == PlayerColor.RED
        ? "Red Player won the game"
        : "Yellow Player won the game");
    a.showAndWait();
  }

  private void showDrawPopup() {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setHeaderText("Draw!");
    a.showAndWait();
  }

  private boolean isDraw() {
    GameBoard board = service.getBoard();
    for (int c = 0; c < COLS; c++) {
      if (board.getField(0, c).getOccupyingPlayer() == null) return false;
    }
    return findWinner() == null;
  }

  private Player findWinner() {
    GameBoard board = service.getBoard();

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        Player p = board.getField(r, c).getOccupyingPlayer();
        if (p == null) continue;

        if (c+3<COLS && same(board,r,c+1,p)&&same(board,r,c+2,p)&&same(board,r,c+3,p)) return p;
        if (r+3<ROWS && same(board,r+1,c,p)&&same(board,r+2,c,p)&&same(board,r+3,c,p)) return p;
        if (r+3<ROWS && c+3<COLS && same(board,r+1,c+1,p)&&same(board,r+2,c+2,p)&&same(board,r+3,c+3,p)) return p;
        if (r+3<ROWS && c-3>=0 && same(board,r+1,c-1,p)&&same(board,r+2,c-2,p)&&same(board,r+3,c-3,p)) return p;
      }
    }
    return null;
  }

  private boolean same(GameBoard b,int r,int c,Player p){
    Player o=b.getField(r,c).getOccupyingPlayer();
    return o!=null && o.color()==p.color();
  }
}