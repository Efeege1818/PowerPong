package de.hhn.it.devtools.javafx.turnbasedbattle;

import javafx.scene.input.KeyCode;

enum MoveSlot {
  S1(1, KeyCode.Q, KeyCode.U, "Q", "U"),
  S2(2, KeyCode.W, KeyCode.I, "W", "I"),
  S3(3, KeyCode.A, KeyCode.J, "A", "J"),
  S4(4, KeyCode.S, KeyCode.K, "S", "K"),
  S5(5, KeyCode.D, KeyCode.L, "D", "L");

  final int moveIndex;
  final KeyCode p1KeyCode;
  final KeyCode p2KeyCode;
  final String p1KeyLabel;
  final String p2KeyLabel;

  MoveSlot(int moveIndex, KeyCode p1KeyCode, KeyCode p2KeyCode, String p1KeyLabel, String p2KeyLabel) {
    this.moveIndex = moveIndex;
    this.p1KeyCode = p1KeyCode;
    this.p2KeyCode = p2KeyCode;
    this.p1KeyLabel = p1KeyLabel;
    this.p2KeyLabel = p2KeyLabel;
  }

  String keyLabel(boolean p1Turn) {
    return p1Turn ? p1KeyLabel : p2KeyLabel;
  }

  boolean matches(KeyCode code, boolean p1Turn) {
    return p1Turn ? (code == p1KeyCode) : (code == p2KeyCode);
  }
}
