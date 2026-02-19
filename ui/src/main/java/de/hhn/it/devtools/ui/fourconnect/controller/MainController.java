package de.hhn.it.devtools.ui.fourconnect.controller;

import de.hhn.it.devtools.ui.fourconnect.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class MainController {

  private final SceneManager sceneManager;

  @FXML private GridPane boardGrid;
  @FXML private Label turnLabel;
  @FXML private Label statusLabel;

  private final int rows = 6;
  private final int cols = 7;

  private final int[][] board = new int[rows][cols];
  private boolean playerOneTurn = true;

  public MainController(SceneManager sceneManager) {
    this.sceneManager = sceneManager;
  }

  @FXML
  public void initialize() {
    buildBoard();
    updateTurnUI();
  }

  private void buildBoard() {
    boardGrid.getChildren().clear();

    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        StackPane cell = new StackPane();
        cell.setMinSize(62, 62);
        cell.setPrefSize(62, 62);

        StackPane chip = new StackPane();
        cell.getChildren().add(chip);

        boardGrid.add(cell, c, r);
      }
    }
  }

  private void updateTurnUI() {
    if (playerOneTurn) {
      turnLabel.setText("Am Zug: Spieler 1 (Rot)");
    } else {
      turnLabel.setText("Am Zug: Spieler 2 (Gelb)");
    }
  }

  private void dropInColumn(int c) {
    for (int r = rows - 1; r >= 0; r--) {
      if (board[r][c] == 0) {
        int player = playerOneTurn ? 1 : 2;
        board[r][c] = player;

        statusLabel.setText("Chip gesetzt in Spalte " + (c + 1));
        playerOneTurn = !playerOneTurn;
        updateTurnUI();
        return;
      }
    }
    statusLabel.setText("Spalte " + (c + 1) + " ist voll!");
  }

  @FXML private void dropCol0() { dropInColumn(0); }
  @FXML private void dropCol1() { dropInColumn(1); }
  @FXML private void dropCol2() { dropInColumn(2); }
  @FXML private void dropCol3() { dropInColumn(3); }
  @FXML private void dropCol4() { dropInColumn(4); }
  @FXML private void dropCol5() { dropInColumn(5); }
  @FXML private void dropCol6() { dropInColumn(6); }

  @FXML
  private void onNewGame() {
    clearBoard();
    playerOneTurn = true;
    statusLabel.setText("Neues Spiel gestartet.");
    updateTurnUI();
  }

  private void clearBoard() {
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        board[r][c] = 0;
      }
    }
  }

  @FXML
  private void onBack() {
    sceneManager.showSelect();
  }
}
