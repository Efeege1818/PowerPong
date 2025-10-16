package de.hhn.it.devtools.apis.powerPong;

public class PowerUpParams {
    //Fileds
    private PowerUpType typ;
    private double durationInSeconds;
    private double xPosition;
    private double yPosition;

    //Construcktor
    public PowerUpParams(PowerUpType typ, double durationInSeconds, double xPosition, double yPosition) {
        this.typ = typ;
        this.durationInSeconds = durationInSeconds;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    //Getters
    public PowerUpType getTyp() { return typ; }
    public double getDurationInSeconds() { return durationInSeconds; }
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
}
