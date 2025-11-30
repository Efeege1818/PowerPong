package de.hhn.it.devtools.apis.powerPong;

/**
 * Defines the available game modes.
 */
public enum GameMode {
    /**
     * Player vs. Player without power-ups.
     */
    CLASSIC_DUEL,

    /**
     * Player vs. Player with power-ups (must-have).
     */
    POWERUP_DUEL,

    /**
     * Player vs. AI (nice-to-have).
     */
    PLAYER_VS_AI,

    /**
     * Endless mode against AI (nice-to-have).
     */
    SURVIVAL
}
