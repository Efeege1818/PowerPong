package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.GameState;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;
import de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener;
import de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleService;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage game logic.
 */
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

  /**
   * Simple constructor, initializes gameState and creates listeners array.
   */
  public SimpleTurnBasedBattleService() {
    logger.info("SimpleTurnBasedBattleService: initializing service");
    this.gameState = GameState.READY;
    this.listeners = new ArrayList<>();
  }

  @Override
  public void reset() {
    logger.info("reset: resetting game state");
    this.gameState = GameState.READY;
    this.player1 = null;
    this.player2 = null;
    this.player1Monster = null;
    this.player2Monster = null;
    notifyGameStateChanged(GameState.READY);
  }

  @Override
  public void start() {
    logger.info("start: starting game with player1 = {}, player2 = {}",
            player1 != null ? player1.playerId() : "null",
            player2 != null ? player2.playerId() : "null");

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
    logger.info("pause: pausing game");
    if (gameState != GameState.RUNNING) {
      throw new IllegalStateException("Game must be RUNNING to pause.");
    }
    gameState = GameState.PAUSED;
    notifyGameStateChanged(GameState.PAUSED);
  }

  @Override
  public void abort() {
    logger.info("abort: aborting game");
    if (gameState != GameState.RUNNING) {
      throw new IllegalStateException("Game must be RUNNING to abort.");
    }
    gameState = GameState.ABORTED;
    notifyGameStateChanged(GameState.ABORTED);
  }

  @Override
  public void end() {
    logger.info("end: ending game");
    if (gameState != GameState.RUNNING) {
      throw new IllegalStateException("Game must be RUNNING to end.");
    }
    gameState = GameState.END;
    notifyGameStateChanged(GameState.END);

    Player winner = battleManager.getWinner();
    if (winner != null) {
      if (winner == player1) {
        logger.info("end: player1 is the winner");
        notifyGameEnded(1);
      } else if (winner == player2) {
        logger.info("end: player2 is the winner");
        notifyGameEnded(2);
      }
    }
  }

  @Override
  public boolean addListener(TurnBasedBattleListener listener) {
    logger.info("addListener: adding listener = {}", listener);
    if (listener == null) {
      throw new IllegalArgumentException("Listener cannot be null.");
    }
    if (listeners.contains(listener)) {
      throw new IllegalStateException("Listener already exists.");
    }

    return listeners.add(listener);
  }

  @Override
  public boolean removeListener(TurnBasedBattleListener listener) {
    logger.info("removeListener: removing listener = {}", listener);
    return listeners.remove(listener);
  }

  private void notifyGameStateChanged(GameState newState) {
    logger.debug("notifyGameStateChanged: notifying listeners of new state = {}", newState);
    for (TurnBasedBattleListener l : listeners) {
      l.newGameState(newState);
    }
  }

  private void updatePlayersState() {
    logger.debug("updatePlayersState: updating players state for listeners");
    for (TurnBasedBattleListener l : listeners) {
      l.updateState(player1, player2);
    }
  }

  private void notifyGameEnded(int winnerNumber) {
    logger.debug("notifyGameEnded: notifying listeners that player {} won", winnerNumber);
    for (TurnBasedBattleListener l : listeners) {
      l.gameEnded(winnerNumber);
    }
  }

  @Override
  public void notifyListenersTurnChanged() {
    logger.debug("notifyListenersTurnChanged: turn has changed");
    updatePlayersState();
  }

  @Override
  public void notifyListenersBattleEnded() {
    logger.debug("notifyListenersBattleEnded: battle has ended");
  }

  @Override
  public GameState getGameState() {
    return gameState;
  }

  @Override
  public void setupPlayers(Player player1, Player player2, Monster monster1, Monster monster2) {
    logger.info("setupPlayers: setting up players - player1 = {}, player2 = {}, monster1 = {},"
            + " monster2 = {}",
            player1.playerId(), player2.playerId(), monster1.element(), monster2.element());

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
    logger.info("executeTurn: player {} executing turn with move index = {}",
            getCurrentPlayer().playerId(), moveIndex);

    if (gameState != GameState.RUNNING) {
      throw new IllegalStateException("Game must be RUNNING to execute turn.");
    }

    logger.debug("Player {} executing turn with move index {}",
            getCurrentPlayer().playerId(), moveIndex);

    int winner = battleManager.executeTurn(moveIndex);

    updatePlayersState();

    if (winner != 0) {
      logger.info("executeTurn: battle ended with winner = {}", winner);
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
    logger.info("nextTurn: advancing to next turn");
    if (gameState != GameState.RUNNING) {
      throw new IllegalStateException("Game must be RUNNING to change turns.");
    }
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
    logger.debug("determineStartingPlayer: determining starting player");
    return battleManager.determineStartingPlayer();
  }

  /**
   * Checks if the element of one monster is effective against the other.
   *
   * @param current currently selected Monster
   * @param opponent opponent's monster
   * @return whether element is effective or not
   */
  public boolean isElementEffective(SimpleMonster current, SimpleMonster opponent) {
    logger.debug("isElementEffective: checking if {} is effective against {}",
            current.getName(), opponent.getName());
    return battleManager.isElementEffective(current, opponent);
  }

  public SimpleMonster getCurrentMonster() {
    return battleManager.getCurrentMonster();
  }

  public SimpleMonster getOpponentMonster() {
    return battleManager.getOpponentMonster();
  }

  public SimpleMonster getPlayer1SimpleMonster() {
    return battleManager.getPlayer1Monster();
  }

  public SimpleMonster getPlayer2SimpleMonster() {
    return battleManager.getPlayer2Monster();
  }


}