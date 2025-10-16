package de.hhn.it.devtools.apis.powerPong;
public class PaddleParams {
    //Fields
    private double width;
    private double height;
    private double speed;

    //Constructor
    public PaddleParams(double width, double height, double speed) {
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    //Getters
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getSpeed() { return speed; }

    //Setters
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }
    public void setSpeed(double speed) { this.speed = speed; }
}
