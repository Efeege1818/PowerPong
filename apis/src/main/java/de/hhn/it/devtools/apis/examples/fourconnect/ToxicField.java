package de.hhn.it.devtools.apis.examples.fourconnect;

public interface ToxicField {

    void incrementTurn();          // Runde erhöhen

    boolean isExpired();           // Prüfen, ob das Runde abgelaufen ist (basierend auf Turn)

    int getRow();                  // Zeile des Feldes
    int getColumn();               // Spalte des Feldes
    int getTurn();                 // Aktuelle Runde des Feldes

    int resetTurn();               // Aktuelle Runde zurücksetzen
}

