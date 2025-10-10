package de.hhn.it.devtools.apis.turnbasedbattle;

public class Debuff {
  private final String stat;
  private final int value;

  public Debuff(String stat, int value){
    this.stat = stat;
    this.value = value;
  }

  public String getStat() {
    return stat;
  }

  public int getValue() {
    return value;
  }
}
