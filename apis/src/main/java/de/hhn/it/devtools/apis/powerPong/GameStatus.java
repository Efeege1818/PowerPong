package de.hhn.it.devtools.apis.powerPong;

public enum GameStatus {
    /**  The game is in the main menu (or has not yet started) */
    MENU,

    /** The game is running actively. */
    RUNNING,

    /** The game is paused */
    PAUSED,

    /** Player 1 just scored a point. */
    PLAYER_1_SCORED,

    /** Player 2 just scored a point. */
    PLAYER_2_SCORED,

    /** Player 1 has won the match (end screen). */
    PLAYER_1_WINS,

    /** Player 2 has won the match (end screen). */
    PLAYER_2_WINS
}
