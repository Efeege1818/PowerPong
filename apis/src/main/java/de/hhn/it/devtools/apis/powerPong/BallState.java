package de.hhn.it.devtools.apis.powerPong;

/**
 * State of a ball (position).
 *
 * @param xPosition The X-coordinate of the ball.
 * @param yPosition The Y-coordinate of the ball.
 */
public record BallState(double xPosition, double yPosition, double radius) {
}
