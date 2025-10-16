package de.hhn.it.devtools.apis.powerPong;

import java.util.ArrayList;
import java.util.List;

public class PowerPongFacade {

    // Player-Paddles
    private Player player1;
    private Player player2;

    // Ball
    private BallParams ball;

    // PowerUps
    private List<PowerUpParams> powerUps;

    // Gamemode
    private GameMode currentMode;

    // Optional: AI Controller for Spieler2
    private AIController aiController;

    // Needed Score to win
    private int winningScore;

    public PowerPongFacade() {
        this.player1 = new Player("Player1", PlayerType.HUMAN, new PaddleParams(10,100,5));
        this.player2 = new Player("Player2",PlayerType.HUMAN,new PaddleParams(10,100,5));
        this.ball = new BallParams(5, 10, 1, 1);    // speed, radius, directionX, directionY
        this.powerUps = new ArrayList<>();
        this.currentMode = GameMode.CLASSIC;
        this.winningScore = 10;
        this.aiController = null; // optional
    }

    // Getter & Setter
    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }
    public BallParams getBall() { return ball; }
    public List<PowerUpParams> getPowerUps() { return powerUps; }
    public GameMode getCurrentMode() { return currentMode; }
    public void setCurrentMode(GameMode mode) { this.currentMode = mode; }
    public AIController getAiController() { return aiController; }
    public void setAiController(AIController aiController) { this.aiController = aiController; }
    public int getScorePlayer1() { return player1.getScore(); }
    public int getScorePlayer2() { return player2.getScore(); }
    public void setWinningScore(int score) { this.winningScore = score; }
    public int getWinningScore() { return winningScore; }

    // -----------------------
    // Punkte-Methoden
    // -----------------------
    public void addPointPlayer1() {
        player1.addScore(1);
        resetBall();
    }
    public void addPointPlayer2() {
        player2.addScore(2);
        resetBall();
    }
    public boolean isGameOver() {
        return player1.getScore() >= winningScore || player2.getScore() >= winningScore;
    }

    // Ball & PowerUp Methods
    public void resetBall() {

        ball.setSpeed(5);
        // Richtung kann man nur ändern, wenn Setter hinzugefügt werden
    }

    public void spawnPowerUp(PowerUpType type, double x, double y, double dauer) {
        powerUps.add(new PowerUpParams(type, dauer, x, y));
    }

    public void removePowerUp(PowerUpParams powerUp) {
        powerUps.remove(powerUp);
    }


    // Utility-Methods
    public void resetGame() {
        player1.resetScore();
        player2.resetScore();
        resetBall();
        powerUps.clear();
    }
}
