package de.hhn.it.devtools.apis.examples.fourconnect;

public interface Field {
    Player getOwner();          // Wer besitzt das Feld
    void setOwner(Player player); // Setzt den Spieler
    boolean isEmpty();          // Prüft, ob das Feld leer ist
}

