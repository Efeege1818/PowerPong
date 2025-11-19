package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleTurnBasedBattleService implements TurnBasedBattleService {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(SimpleTurnBasedBattleService.class);

  private GameState gameState;
  private Player player1;
  private Player player2;
  private Monster player1Monster;
  private Monster player2Monster;

  private final List<TurnBasedBattleListener> listeners;

  private final SimpleBattleManager battleManager = new SimpleBattleManager();

  public SimpleTurnBasedBattleService() {
    this.gameState = GameState.READY;
    this.listeners = new ArrayList<>();
  }

  @Override
  public void reset() {
    this.gameState = GameState.READY;
    this.player1 = null;
    this.player2 = null;
    this.player1Monster = null;
    this.player2Monster = null;
    notifyGameStateChanged(GameState.READY);
  }

  @Override
  public void start() {
    if (gameState != GameState.READY) {
      throw new IllegalStateException("Game must be READY to start.");
    }
    if (player1 == null || player2 == null) {
      throw new IllegalStateException("Players must be set before start.");
    }

    // hand off to battle manager
    battleManager.initializeBattle(player1, player2, player1Monster, player2Monster);

    gameState = GameState.RUNNING;
    notifyGameStateChanged(GameState.RUNNING);
    updatePlayersState();
  }

  @Override
  public void pause() {
    if (gameState != GameState.RUNNING)
      throw new IllegalStateException("Game must be RUNNING to pause.");
    gameState = GameState.PAUSED;
    notifyGameStateChanged(GameState.PAUSED);
  }

  @Override
  public void abort() {
    if (gameState != GameState.RUNNING)
      throw new IllegalStateException("Game must be RUNNING to abort.");
    gameState = GameState.ABORTED;
    notifyGameStateChanged(GameState.ABORTED);
  }

  @Override
  public void end() {
    if (gameState != GameState.RUNNING)
      throw new IllegalStateException("Game must be RUNNING to end.");
    gameState = GameState.END;
    notifyGameStateChanged(GameState.END);

    Player winner = battleManager.getWinner();
    if (winner != null) {
      if(winner == player1) {
        notifyGameEnded(1);
      } else  if(winner == player2) {
        notifyGameEnded(2);
      }
    }
  }

  @Override
  public boolean addListener(TurnBasedBattleListener listener) {
    if (listener == null) throw new IllegalArgumentException("Listener cannot be null.");
    if (listeners.contains(listener)) throw new IllegalStateException("Listener already exists.");
    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(TurnBasedBattleListener listener) {
    return listeners.remove(listener);
  }

  private void notifyGameStateChanged(GameState newState) {
    for (TurnBasedBattleListener l : listeners) {
      l.newGameState(newState);
    }
  }

  private void updatePlayersState() {
    for (TurnBasedBattleListener l : listeners) {
      l.updateState(player1, player2);
    }
  }

  private void notifyGameEnded(int winnerNumber) {
    for (TurnBasedBattleListener l : listeners) {
      l.gameEnded(winnerNumber);
    }
  }

  @Override
  public void notifyListenersTurnChanged() {
    updatePlayersState();
  }

  @Override
  public void notifyListenersBattleEnded() { }

  @Override
  public GameState getGameState() {
    return gameState;
  }

  @Override
  public void setupPlayers(Player player1, Player player2, Monster monster1, Monster monster2) {
    if (gameState != GameState.READY) {
      throw new IllegalStateException("Game must be READY to setup players.");
    }
    this.player1 = player1;
    this.player2 = player2;
    this.player1Monster = monster1;
    this.player2Monster = monster2;
    updatePlayersState();
  }

  @Override
  public void executeTurn(int moveIndex) {
    if (gameState != GameState.RUNNING)
      throw new IllegalStateException("Game must be RUNNING to execute turn.");

    logger.debug("Executing turn with move index {}", moveIndex);

    int winner = battleManager.executeTurn(moveIndex);

    updatePlayersState();

    if (winner != 0) {
      gameState = GameState.END;
      notifyGameStateChanged(GameState.END);
      notifyGameEnded(winner);
      return;
    }

    battleManager.nextTurn();
    notifyListenersTurnChanged();
  }

  @Override
  public void nextTurn() {
    if (gameState != GameState.RUNNING)
      throw new IllegalStateException("Game must be RUNNING to change turns.");
    battleManager.nextTurn();
    notifyListenersTurnChanged();
  }

  @Override
  public Player getCurrentPlayer() {
    return battleManager.getCurrentPlayer();
  }

  @Override
  public Player getPlayer1() {
    return player1;
  }

  @Override
  public Player getPlayer2() {
    return player2;
  }

  @Override
  public boolean isBattleOver() {
    return battleManager.isBattleOver();
  }

  @Override
  public int getTurnCount() {
    return battleManager.getTurnCount();
  }

  @Override
  public Player getWinner() {
    return battleManager.getWinner();
  }

  @Override
  public Player determineStartingPlayer() {
    return battleManager.determineStartingPlayer();
  }

  public boolean isElementEffective(SimpleMonster current, SimpleMonster opponent) {
    return battleManager.isElementEffective(current, opponent);
  }
}
