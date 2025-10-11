package de.hhn.it.devtools.apis.examples.fourconnect;

public interface GameRules {

    // Über das Spielfeld und den Spieler den Gewinner überprüfen
    boolean checkWinner(Player[][] board, Player player);

    // Überprüft, ob der Spieler 4 Steine in einer Reihe hat
    boolean checkHorizontal(Player[][] board, Player player);
    boolean checkVertical(Player[][] board, Player player);
    boolean checkDiagonal(Player[][] board, Player player);
}
