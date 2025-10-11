package de.hhn.it.devtools.apis.examples.fourconnect;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;

public interface GameBoard {
    Field[][] getFields();                       // Gibt alle Felder zurück
    Field getField(int row, int column);        // Gibt ein bestimmtes Feld zurück
    boolean dropChip(int column, Player player) throws IllegalParameterException; // Spieler wirft einen Stein in eine Spalte
    void resetBoard();                           // Setzt das Spielfeld zurück
    boolean isFull();                            // Prüft, ob das Board voll ist
    void placeRandomToxicFields();               // zwei zufällige ToxicFelder festlegen
}

