package de.hhn.it.devtools.javafx.powerpong.viewmodel;

import de.hhn.it.devtools.apis.exceptions.GameLogicException;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.GameState;
import de.hhn.it.devtools.apis.powerPong.GameStatus;
import de.hhn.it.devtools.apis.powerPong.InputAction;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import de.hhn.it.devtools.apis.powerPong.PowerPongListener;
import de.hhn.it.devtools.apis.powerPong.PowerPongService;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import de.hhn.it.devtools.apis.powerPong.Score;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PowerPongViewModel implements PowerPongListener {
  private final PowerPongService service;
  private final PlayerInput playerInput;
  private final ObjectProperty<GameState> gameState;
  private final StringProperty scoreText;
  private final StringProperty winnerText;
  private final ObjectProperty<GameStatus> gameStatus;

  public PowerPongViewModel(final PowerPongService service) {
    this.service = Objects.requireNonNull(service);
    this.playerInput = new PlayerInput();
    this.gameState = new SimpleObjectProperty<>();
    this.scoreText = new SimpleStringProperty(formatScore(0, 0));
    this.winnerText = new SimpleStringProperty("");
    this.gameStatus = new SimpleObjectProperty<>(GameStatus.MENU);
  }

  public void startGame(final GameMode mode) throws GameLogicException {
    service.startGame(mode);
    service.removeListener(this);
    service.addListener(this);
    scoreText.set(formatScore(0, 0));
    winnerText.set("");
    gameStatus.set(GameStatus.RUNNING);
    gameState.set(service.getGameState());
  }

  public void endGame() {
    service.endGame();
    service.removeListener(this);
    gameStatus.set(GameStatus.MENU);
  }

  public void pause() {
    service.setPaused(true);
    service.removeListener(this);
    gameStatus.set(GameStatus.PAUSED);
  }

  public void keyPressed(final InputAction action) {
    playerInput.keyPressed(action);
  }

  public void keyReleased(final InputAction action) {
    playerInput.keyReleased(action);
  }

  public GameState updateGame() throws GameLogicException {
    GameState state = service.getGameState();
    if (state != null && state.status() == GameStatus.RUNNING) {
      service.updateGame(playerInput);
      state = service.getGameState();
    }
    gameState.set(state);
    return state;
  }

  public ObjectProperty<GameState> gameStateProperty() {
    return gameState;
  }

  public StringProperty scoreTextProperty() {
    return scoreText;
  }

  public StringProperty winnerTextProperty() {
    return winnerText;
  }

  public ObjectProperty<GameStatus> gameStatusProperty() {
    return gameStatus;
  }

  @Override
  public void onBallCollision(final GameState updatedState) {
  }

  @Override
  public void onPlayerScored(final int scoringPlayerIndex, final Score updatedScore) {
    Platform.runLater(() ->
        scoreText.set(formatScore(updatedScore.player1(), updatedScore.player2())));
  }

  @Override
  public void onGameEnd(final GameStatus finalStatus, final GameState finalState) {
    Platform.runLater(() -> {
      gameStatus.set(finalStatus);
      gameState.set(finalState);
      if (finalStatus == GameStatus.PLAYER_1_WINS) {
        winnerText.set("PLAYER 1 WINS!");
      } else if (finalStatus == GameStatus.PLAYER_2_WINS) {
        winnerText.set("PLAYER 2 WINS!");
      }
    });
  }

  @Override
  public void onPowerUpCollected(final int idx, final PowerUpType type) {
  }

  private String formatScore(final int p1, final int p2) {
    return "P1: " + p1 + " - P2: " + p2;
  }
}
