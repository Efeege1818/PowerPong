package de.hhn.it.devtools.apis.examples.fourconnect;

/**
 * Diese Klasse ist für die reine Spielregellogik zuständig, insbesondere für die Siegprüfung.
 * Sie dient als Logik-Assistent für den ConnectFourService.
 * Die Klasse ist zustandslos (statisch) in Bezug auf das Spielfeld, da sie die Daten
 * (das Board) als Parameter erhält.
 */
public class GameRules {

    // Speichert die Spielfeldgröße (Höhe/Breite).
    private final int rows;
    private final int columns;

    /**
     * Konstruktor: Legt die Größe des Spielfelds fest, auf das die Regeln angewendet werden.
     * * @param rows Die Anzahl der Reihen (Höhe) des Spielfelds.
     * @param columns Die Anzahl der Spalten (Breite) des Spielfelds.
     */
    public GameRules(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Prüft, ob der angegebene Spieler gewonnen hat.
     * Sie kombiniert die Ergebnisse der horizontalen, vertikalen und diagonalen Prüfungen.
     * Diese Methode wird von der Fassade (ConnectFourService) aufgerufen.
     * * @param board Der aktuelle Zustand des Spielfelds (z.B. Player[][]-Array).
     * @param player Der Spieler, dessen Gewinnchancen geprüft werden.
     * @return Wahr (true), wenn der Spieler vier gleiche Chips in einer Reihe hat.
     */
    public boolean checkWinner(Player[][] board, Player player) {
        return checkHorizontal(board, player)
            || checkVertical(board, player)
            || checkDiagonal(board, player);
    }

    /**
     *  Prüft alle möglichen Vierer-Reihen in horizontaler Richtung.
     * (Hier muss später der Algorithmus für die horizontale Prüfung implementiert werden.)
     */
    private boolean checkHorizontal(Player[][] board, Player player) {

        return false;
    }

    /**
     *  Prüft alle möglichen Vierer-Reihen in vertikaler Richtung.
     * (Hier muss später der Algorithmus für die vertikale Prüfung implementiert werden.)
     */
    private boolean checkVertical(Player[][] board, Player player) {

        return false;
    }

    /**
     *  Prüft alle möglichen Vierer-Reihen in beiden diagonalen Richtungen.
     * (Hier muss später der Algorithmus für die diagonale Prüfung implementiert werden.)
     */
    private boolean checkDiagonal(Player[][] board, Player player) {

        return false;
    }
}