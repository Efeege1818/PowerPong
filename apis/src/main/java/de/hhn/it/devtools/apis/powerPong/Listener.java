package de.hhn.it.devtools.apis.powerPong;

public interface Listener {
    void onBallCollision();
    void playerScored();
    void onGameEnd();
    void onPowerUpCollected();
}
