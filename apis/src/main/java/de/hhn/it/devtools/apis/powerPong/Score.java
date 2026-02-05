package de.hhn.it.devtools.apis.powerPong;
/**
 * The current score.
 *
 * @param player1 Points of player 1.
 * @param player2 Points of player 2.
 */
public record Score(int player1, int player2) {
    public Score{
        if(player1 < 0 || player2 < 0){
            throw new IllegalArgumentException("scores must be >= 0");
        }
    }
}