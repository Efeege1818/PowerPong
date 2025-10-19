package de.hhn.it.devtools.apis.powerPong;

/**
 A data object that encapsulates the actions (key presses) of both players for a single frame.
 */
public class PlayerInput {

    // State for player 1 (W/S)
    private boolean player1Up;
    private boolean player1Down;

    // State for player 1 (Up/Down)
    private boolean player2Up;
    private boolean player2Down;

    // Getter and Setter
    public boolean isPlayer1Up() { return player1Up; }
    public void setPlayer1Up(boolean player1Up) { this.player1Up = player1Up; }

    public boolean isPlayer1Down() { return player1Down; }
    public void setPlayer1Down(boolean player1Down) { this.player1Down = player1Down; }

    public boolean isPlayer2Up() { return player2Up; }
    public void setPlayer2Up(boolean player2Up) { this.player2Up = player2Up; }

    public boolean isPlayer2Down() { return player2Down; }
    public void setPlayer2Down(boolean player2Down) { this.player2Down = player2Down; }
}
