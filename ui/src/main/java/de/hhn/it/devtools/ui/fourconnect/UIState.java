package de.hhn.it.devtools.ui.fourconnect;

import de.hhn.it.devtools.apis.fourconnect.PlayerColor;

public class UIState {

  private static PlayerColor player1Color;
  private static PlayerColor player2Color;

  public static void setPlayer1Color(PlayerColor color) {
    player1Color = color;
  }

  public static void setPlayer2Color(PlayerColor color) {
    player2Color = color;
  }

  public static PlayerColor getPlayer1Color() {
    return player1Color;
  }

  public static PlayerColor getPlayer2Color() {
    return player2Color;
  }

  public static void reset() {
    player1Color = null;
    player2Color = null;
  }
}
