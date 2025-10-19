package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Definiert den Zustand eines Chips, der von der Toxizität betroffen ist.
 * Es liefert der UI die Informationen, die sie zur Darstellung des Zerfall-Timers benötigt.
 */
public interface ToxicField {

    /**
     * Prüft, ob der Chip zerfallen ist (der Timer ist abgelaufen).
     * @return Wahr, wenn die Zerfallzeit erreicht ist.
     */
    boolean isExpired();

    /**
     * Liefert die Zeile (Reihe) im Spielfeld, auf der sich das toxische Feld befindet.
     * @return Die Reihennummer.
     */
    int getRow();

    /**
     * Liefert die Spalte im Spielfeld, auf der sich das toxische Feld befindet.
     * @return Die Spaltennummer.
     */
    int getColumn();

    /**
     * Liefert den aktuellen Countdown-Wert (die verbleibende Zeit bis zum Zerfall).
     * @return Der Countdown-Wert (z.B. 3, 2, 1, oder 0, wenn der Chip normal ist).
     */
    int getTurn();
}