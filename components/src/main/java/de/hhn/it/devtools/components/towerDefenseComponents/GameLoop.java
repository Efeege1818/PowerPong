package de.hhn.it.devtools.components.towerDefenseComponents;

public interface GameLoop extends Runnable {

    //TODO Kommentare adden
    @Override
    void run();

    void endGame();

    void retry();

    void pauseGame();
}
