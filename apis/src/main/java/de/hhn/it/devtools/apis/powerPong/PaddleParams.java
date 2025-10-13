package de.hhn.it.devtools.apis.powerPong;
public class PaddleParams {
    private double breite;
    private double hoehe;
    private double geschwindigkeit;

    public PaddleParams(double breite, double hoehe, double geschwindigkeit) {
        this.breite = breite;
        this.hoehe = hoehe;
        this.geschwindigkeit = geschwindigkeit;
    }

    public double getBreite() { return breite; }
    public double getHoehe() { return hoehe; }
    public double getGeschwindigkeit() { return geschwindigkeit; }

    public void setBreite(double breite) { this.breite = breite; }
    public void setHoehe(double hoehe) { this.hoehe = hoehe; }
    public void setGeschwindigkeit(double geschwindigkeit) { this.geschwindigkeit = geschwindigkeit; }
}
