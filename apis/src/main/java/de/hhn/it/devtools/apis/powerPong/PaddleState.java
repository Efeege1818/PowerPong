package de.hhn.it.devtools.apis.powerPong;

import static java.lang.Double.isFinite;

/**
 * State of a paddle (position and size).
 *
 * @param yPosition The Y-coordinate (vertical center) of the paddle.
 * @param height    The current height of the paddle (can vary due to
 *                  power-ups).
 */
public record PaddleState(double xPosition, double yPosition, double width, double height) {
    public PaddleState {
        if (!isFinite(xPosition) || !isFinite(yPosition)) {
            throw new IllegalArgumentException("xPosition and yPosition must be finite numbers");
        }
        if (!isFinite(width) || width <= 0.0) {
            throw new IllegalArgumentException("width must be positive and finite");
        }
        if (!isFinite(height) || height <= 0.0) {
            throw new IllegalArgumentException("height must be positive and finite");
        }
    }
}
