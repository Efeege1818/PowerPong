package de.hhn.it.devtools.apis.powerPong;

public class BallParams {
    //Fields
    private double speed;
    private double radius;
    private double directionX;
    private double directionY;

    //Constructor
    public BallParams(double speed, double radius, double directionX, double directionY) {
        this.speed = speed;
        this.radius = radius;
        this.directionX = directionX;
        this.directionY = directionY;
    }

    //Getter
    public double getSpeed() { return speed; }
    public double getRadius() { return radius; }
    public double getDirectionX() { return directionX; }
    public double getDirectionY() { return directionY; }

    //Setter
    public void setSpeed(double speed) { this.speed = speed; }
}
