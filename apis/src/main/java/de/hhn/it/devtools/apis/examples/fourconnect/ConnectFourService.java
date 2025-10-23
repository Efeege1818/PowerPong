package de.hhn.it.devtools.apis.examples.fourconnect;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

/**
 * Central facade interface for Connect Four game.
 */
public interface ConnectFourService {

  void startGame(GameConfiguration configuration);

  int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException;

  Player[][] getBoardState();

  Player getCurrentPlayer();

  boolean checkForWin();

  boolean checkForDraw();

  void addGameListener(GameEventListener listener);

  void removeGameListener(GameEventListener listener);
}
