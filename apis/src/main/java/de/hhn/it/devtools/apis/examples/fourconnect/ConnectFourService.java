package de.hhn.it.devtools.apis.examples.fourconnect;


public interface ConnectFourService {

  

  Player[][] getBoardState();


  Player getCurrentPlayer();


  boolean checkForWin();


  void resetGame();


  boolean checkForDraw();


  void applyToxicDecay();
}
