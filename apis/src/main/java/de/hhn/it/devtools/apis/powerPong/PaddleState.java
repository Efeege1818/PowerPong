package de.hhn.it.devtools.apis.powerPong;
/**
 * State of a paddle (position and size).
 *
 * @param yPosition The Y-coordinate (vertical center) of the paddle.
 * @param height The current height of the paddle (can vary due to power-ups).
 */
public record PaddleState(double yPosition, double height) {
}
