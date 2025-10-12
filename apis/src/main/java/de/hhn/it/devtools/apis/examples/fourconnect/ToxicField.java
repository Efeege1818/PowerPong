package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Definiert den Zustand eines Chips, der von der Toxizität betroffen ist.
 * Dieses Interface ist strikt Read-Only (nur lesbar).
 * Die aktive Steuerung des Turns muss in der Service-Implementierung liegen.
 */
public interface ToxicField {

    boolean isExpired();

    int getRow();

    int getColumn();

    int getTurn();
}