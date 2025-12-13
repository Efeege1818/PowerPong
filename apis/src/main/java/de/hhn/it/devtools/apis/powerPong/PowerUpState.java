package de.hhn.it.devtools.apis.powerPong;

import java.util.Objects;

import static java.lang.Double.isFinite;

/**
 * State of a power-up visible on the field.
 *
 * @param xPosition The X-coordinate of the power-up.
 * @param yPosition The Y-coordinate of the power-up.
 * @param type      The type of the power-up (so the UI can draw it correctly).
 */
public record PowerUpState(double xPosition, double yPosition, double radius, PowerUpType type) {
    public PowerUpState{
        if(!isFinite(xPosition) || !isFinite(yPosition)){
            throw new IllegalStateException("xPosition/yPosition must be finite numbers");
        }
        if(!isFinite(radius) || radius <= 0.0){
            throw new IllegalStateException("radius must be postive and finite");
        }
        Objects.requireNonNull(type, "type must not be null");
    }
}
