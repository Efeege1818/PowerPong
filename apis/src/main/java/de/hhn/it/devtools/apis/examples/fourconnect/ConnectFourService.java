package de.hhn.it.devtools.apis.examples.fourconnect;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;


public interface ConnectFourService {

  int dropChip(int column) throws IllegalParameterException;

  Player[][] getBoardState();


  Player getCurrentPlayer();


  boolean checkForWin();


  void resetGame();


  boolean checkForDraw();


  void applyToxicDecay();
}
