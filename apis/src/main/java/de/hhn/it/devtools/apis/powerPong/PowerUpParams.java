package de.hhn.it.devtools.apis.powerPong;

public class PowerUpParams {
    private PowerUpType typ;
    private double dauerInSekunden;
    private double xPosition;
    private double yPosition;

    public PowerUpParams(PowerUpType typ, double dauerInSekunden, double xPosition, double yPosition) {
        this.typ = typ;
        this.dauerInSekunden = dauerInSekunden;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    public PowerUpType getTyp() { return typ; }
    public double getDauerInSekunden() { return dauerInSekunden; }
    public double getXPosition() { return xPosition; }
    public double getYPosition() { return yPosition; }
}
