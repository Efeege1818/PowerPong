package de.hhn.it.devtools.apis.towerDefenseApis;

public interface GameLoop extends Runnable{

    @Override
    void run();

    void endGame();
}
