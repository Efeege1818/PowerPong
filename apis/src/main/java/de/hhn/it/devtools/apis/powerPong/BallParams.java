package de.hhn.it.devtools.apis.powerPong;

public class BallParams {
    private double geschwindigkeit;
    private double radius;
    private double richtungX;
    private double richtungY;

    public BallParams(double geschwindigkeit, double radius, double richtungX, double richtungY) {
        this.geschwindigkeit = geschwindigkeit;
        this.radius = radius;
        this.richtungX = richtungX;
        this.richtungY = richtungY;
    }

    public double getGeschwindigkeit() { return geschwindigkeit; }
    public double getRadius() { return radius; }
    public double getRichtungX() { return richtungX; }
    public double getRichtungY() { return richtungY; }

    public void setGeschwindigkeit(double geschwindigkeit) { this.geschwindigkeit = geschwindigkeit; }
}
