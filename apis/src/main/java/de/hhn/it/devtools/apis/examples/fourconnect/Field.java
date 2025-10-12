package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Definiert den Zustand (State) einer einzelnen Zelle auf dem Spielfeld.
 * Dies ist ein Read-Only Interface.
 */
public interface Field {

    // Basis-Zustand (Wer ist der Besitzer?)
    Player getOwner();

    // Basis-Zustand (Ist es leer?)
    boolean isEmpty();

    // ZUSTAND FÜR DIE SPEZIALITÄT:

    /**
     * Prüft, ob diese Position im Spielfeld permanent als toxische Zone markiert ist.
     * @return true, wenn das Feld toxisch ist.
     */
    boolean isToxicZone();

    /**
     * Liefert die verbleibende Zeit, bis der Chip zerfällt.
     * Wenn der Wert > 0 ist, muss die UI den Timer anzeigen (3, 2, 1).
     * @return Der Countdown-Wert (0, wenn der Chip normal ist oder das Feld leer).
     */
    int getDecayTime();
}