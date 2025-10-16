package de.hhn.it.devtools.apis.towerDefenseApis;

public record Coordinates(float x, float y) {

    @Override
    public float x() {
        return x;
    }

    @Override
    public float y() {
        return y;
    }
}
