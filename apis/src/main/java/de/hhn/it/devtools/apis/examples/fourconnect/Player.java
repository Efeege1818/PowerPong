package de.hhn.it.devtools.apis.examples.fourconnect;

public interface Player {

    String getName();          // Name des Spielers
    void setName(String name);

    String getColor();         // Farbe des Spielers
    void setColor(PlayerColor color);

    int getScore();            // Punktestand des Spielers
    void setScore(int score);
    void incrementScore();     // Punktestand erhöhen
    void resetScore();         // Punktestand zurücksetzen
}