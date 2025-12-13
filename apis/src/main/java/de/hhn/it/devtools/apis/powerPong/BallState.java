package de.hhn.it.devtools.apis.powerPong;

import java.util.Objects;

import static java.lang.Double.isFinite;

/**
 * State of a ball (position).
 *
 * @param xPosition The X-coordinate of the ball.
 * @param yPosition The Y-coordinate of the ball.
 */
public record BallState(double xPosition, double yPosition, double radius) {
    public BallState{
        if(!isFinite(xPosition) || !isFinite(yPosition)){
            throw new IllegalStateException("xPosition/yPosition must be finite numbers");
        }
        if(!isFinite(radius) || radius <= 0.0){
            throw new IllegalStateException("radius must be postive and finite");
        }
    }
}
