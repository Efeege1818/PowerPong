package de.hhn.it.devtools.javafx.turnbasedbattle;

public class BattleScreenViewModel {
  private String turnText = "Player 1 turn";
  private String turnColor = "RED";

  public String turnText() { return turnText; }
  public BattleScreenViewModel turnText(String v) { this.turnText = v; return this; }

  public String turnColor() { return turnColor; }
  public BattleScreenViewModel turnColor(String v) { this.turnColor = v; return this; }
}
