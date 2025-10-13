package de.hhn.it.devtools.apis.powerPong;

public class Player {
    private String name;
    private PlayerType type;
    private PaddleParams paddle;
    private int score;
    private boolean hasShield;

    public Player(String name, PlayerType type, PaddleParams paddle) {
        this.name = name;
        this.type = type;
        this.paddle = paddle;
        this.score = 0;
        this.hasShield = false;
    }

    public String getName() { return name; }
    public PlayerType getType() { return type; }
    public PaddleParams getPaddle() { return paddle; }
    public int getScore() { return score; }
    public boolean hasShield() { return hasShield; }

    public void addScore(int points) { score += points; }
    public void activateShield() { hasShield = true; }
    public void deactivateShield() { hasShield = false; }
}
