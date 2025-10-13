package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Definiert den Zustand (State) eines Spielers.
 * Dieses Interface ist strikt Read-Only (nur lesbar).
 * Die Änderung des Punktestands muss durch den ConnectFourService erfolgen.
 */
public interface Player {


    String getName();

    PlayerColor getColor();

    int getScore();
}