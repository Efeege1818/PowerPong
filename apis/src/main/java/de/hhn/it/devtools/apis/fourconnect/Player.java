package de.hhn.it.devtools.apis.fourconnect;

/**
 * Record defining a player for read-only identification.
 */
public record Player(

    /** Liefert den Anzeigenamen des Spielers (z.B. "Spieler 1"). */
    String name,

    /** Liefert die Farbe des Spielers (z.B. ROT oder GELB). */
    PlayerColor color
) {

}