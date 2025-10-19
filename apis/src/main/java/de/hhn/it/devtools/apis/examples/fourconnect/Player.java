package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Definiert den Zustand (State) eines Spielers.
 * Dieses Interface ist strikt Read-Only (nur lesbar). Es dient dazu, der UI
 * Informationen über den Spieler zu liefern (z.B. Name, Farbe, Punktestand),
 * ohne dass die UI diese Werte direkt ändern kann. Die Änderung des Punktestands
 * muss zentral durch den ConnectFourService erfolgen.
 */
public interface Player {

    /**
     * Liefert den Anzeigenamen des Spielers (z.B. "Spieler 1").
     * @return Der Name des Spielers.
     */
    String getName();

    /**
     * Liefert die Farbe des Spielers (z.B. BLAU oder GELB).
     * @return Das PlayerColor-Objekt des Spielers.
     */
    PlayerColor getColor();

    /**
     * Liefert den aktuellen Punktestand (Score) des Spielers.
     * @return Der aktuelle Punktestand.
     */
    int getScore();
}