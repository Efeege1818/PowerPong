package de.hhn.it.devtools.apis.powerPong;
/**
 * Defines the different types of power-ups (based on your nice-to-haves).
 */
public enum PowerUpType {
    BIGGER_PADDLE,
    SMALLER_ENEMY_PADDLE,
    SLOW_ENEMY_PADDLE,
    BARRIERLESS, // Ball re-enters from the opposite side
    DOUBLE_BALL,
    FASTER_BALL_ENEMY_SIDE,
    SHIELD
}