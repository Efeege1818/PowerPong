package de.hhn.it.devtools.apis.towerDefenseApis;

public interface GameLoop extends Runnable {

    //TODO Kommentare adden
    @Override
    void run();

    void endGame();

    void retry();

    void pauseGame();
}
