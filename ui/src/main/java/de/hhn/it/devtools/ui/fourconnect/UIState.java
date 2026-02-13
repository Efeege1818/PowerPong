package de.hhn.it.devtools.ui.fourconnect;

import de.hhn.it.devtools.apis.fourconnect.PlayerColor;

public final class UIState {
  private UIState() {}

  private static PlayerColor player1Color = PlayerColor.RED;

  public static PlayerColor getPlayer1Color() {
    return player1Color;
  }

  public static void setPlayer1Color(PlayerColor color) {
    player1Color = color;
  }

  public static PlayerColor getPlayer2Color() {
    return (player1Color == PlayerColor.RED) ? PlayerColor.YELLOW : PlayerColor.RED;
  }
}
