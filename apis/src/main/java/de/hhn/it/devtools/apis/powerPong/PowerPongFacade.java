package de.hhn.it.devtools.apis.powerPong;

import java.util.ArrayList;
import java.util.List;

public class PowerPongFacade {

    // Player-Paddles
    private PaddleParams player1;
    private PaddleParams player2;

    // Ball
    private BallParams ball;

    // PowerUps
    private List<PowerUpParams> powerUps;

    // Gamemode
    private GameMode currentMode;

    // Optional: AI Controller for Spieler2
    private AIController aiController;

    // Scores
    private int scorePlayer1;
    private int scorePlayer2;

    // Needed Score to win
    private int winningScore;

    public PowerPongFacade() {
        this.player1 = new PaddleParams(10, 100, 5);  // x, y, speed
        this.player2 = new PaddleParams(580, 100, 5);
        this.ball = new BallParams(5, 10, 1, 1);    // speed, radius, directionX, directionY
        this.powerUps = new ArrayList<>();
        this.currentMode = GameMode.CLASSIC;
        this.scorePlayer1 = 0;
        this.scorePlayer2 = 0;
        this.winningScore = 10;
        this.aiController = null; // optional
    }

    // -----------------------
    // Getter & Setter
    // -----------------------
    public PaddleParams getPlayer1() { return player1; }
    public PaddleParams getPlayer2() { return player2; }
    public BallParams getBall() { return ball; }
    public List<PowerUpParams> getPowerUps() { return powerUps; }
    public GameMode getCurrentMode() { return currentMode; }
    public void setCurrentMode(GameMode mode) { this.currentMode = mode; }
    public AIController getAiController() { return aiController; }
    public void setAiController(AIController aiController) { this.aiController = aiController; }
    public int getScorePlayer1() { return scorePlayer1; }
    public int getScorePlayer2() { return scorePlayer2; }
    public void setWinningScore(int score) { this.winningScore = score; }
    public int getWinningScore() { return winningScore; }

    // -----------------------
    // Punkte-Methoden
    // -----------------------
    public void addPointPlayer1() {
        scorePlayer1++;
        resetBall();
    }
    public void addPointPlayer2() {
        scorePlayer2++;
        resetBall();
    }
    public boolean isGameOver() {
        return scorePlayer1 >= winningScore || scorePlayer2 >= winningScore;
    }

    // -----------------------
    // Ball & PowerUp Methoden
    // -----------------------
    public void resetBall() {

        ball.setGeschwindigkeit(5);
        // Richtung kann man nur ändern, wenn Setter hinzugefügt werden
    }

    public void spawnPowerUp(PowerUpType type, double x, double y, double dauer) {
        powerUps.add(new PowerUpParams(type, dauer, x, y));
    }

    public void removePowerUp(PowerUpParams powerUp) {
        powerUps.remove(powerUp);
    }

    // -----------------------
    // Utility-Methoden
    // -----------------------
    public void resetGame() {
        scorePlayer1 = 0;
        scorePlayer2 = 0;
        resetBall();
        powerUps.clear();
    }

}
