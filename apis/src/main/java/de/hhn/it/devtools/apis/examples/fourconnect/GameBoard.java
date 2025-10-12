package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Dient als reiner Zustands-Abfrager (Read-Only Datenmodell) des Spielfelds.
 * Es enthält nur Methoden zum Lesen (get).
 */
public interface GameBoard {

    /**
     * Gibt ein Array aller Felder zurück. Dies ist der komplette Spielfeld-Zustand.
     * @return Ein Array von Feld-Objekten.
     */
    Field[][] getFields();

    /**
     * Gibt den Zustand eines einzelnen Feldes an der angegebenen Position zurück.
     * @param row Die Zeile (Reihe).
     * @param column Die Spalte.
     * @return Das Feld-Objekt.
     */
    Field getField(int row, int column);

    /**
     * Liefert die Anzahl der Reihen (Höhe) des Spielfelds.
     * @return Die Reihenanzahl.
     */
    int getRows();

    /**
     * Liefert die Anzahl der Spalten (Breite) des Spielfelds.
     * @return Die Spaltenanzahl.
     */
    int getColumns();
}