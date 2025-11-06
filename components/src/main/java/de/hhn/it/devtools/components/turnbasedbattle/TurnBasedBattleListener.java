package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.GameState;
import de.hhn.it.devtools.apis.turnbasedbattle.Player;

/**
 * Component-Implementierung des API-Listeners.
 *
 * Beobachtet den Zustand beider Spieler und merkt sich Lebenspunkte, Spielstatus
 * und Gewinner.
 */
public class TurnBasedBattleListener implements de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleListener {

    private final int playerNumber;
    private volatile GameState currentState = GameState.READY;

    private volatile int player1Health;
    private volatile int player2Health;

    private volatile Integer winnerPlayerNumber = null;

    /**
     * Erzeugt einen Listener für den angegebenen Spieler.
     *
     * @param playerNumber 1 oder 2 – bestimmt, welcher Spieler beobachtet wird
     * @throws IllegalArgumentException wenn playerNumber nicht 1 oder 2 ist
     */
    public TurnBasedBattleListener(final int playerNumber) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("playerNumber must be 1 or 2");
        }
        this.playerNumber = playerNumber;
        System.out.println("Listener initialized for player " + playerNumber + " (state=" + currentState + ")");
    }

    @Override
    public void newGameState(final GameState gameState) {
        this.currentState = gameState;
        System.out.println("GameState changed -> " + gameState);
    }

    /**
     * Aktualisiert den Zustand beider Spieler (z. B. Lebenspunkte).
     *
     * @param player1 Spieler 1 (nicht null)
     * @param player2 Spieler 2 (nicht null)
     * @throws IllegalArgumentException wenn einer der Player null ist
     */
    @Override
    public void updateState(final Player player1, final Player player2) throws IllegalArgumentException {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("player1 and player2 must not be null");
        }
        if (player1.monster() == null || player2.monster() == null) {
            throw new IllegalArgumentException("Each player must have an assigned monster");
        }

        player1Health = player1.monster().currentHp();
        player2Health = player2.monster().currentHp();

        System.out.printf("updateState: Player1 HP=%d, Player2 HP=%d%n", player1Health, player2Health);
    }

    /**
     * Beendet das Spiel und setzt die Gewinner-Spielernummer.
     *
     * @param winnerPlayerNumber Gewinner (1 oder 2)
     * @throws IllegalArgumentException wenn nicht 1 oder 2
     */
    @Override
    public void gameEnded(final int winnerPlayerNumber) {
        if (winnerPlayerNumber != 1 && winnerPlayerNumber != 2) {
            throw new IllegalArgumentException("winnerPlayerNumber must be 1 or 2");
        }
        this.winnerPlayerNumber = winnerPlayerNumber;
        System.out.println("Game ended. Winner = Player " + winnerPlayerNumber);
    }
}
