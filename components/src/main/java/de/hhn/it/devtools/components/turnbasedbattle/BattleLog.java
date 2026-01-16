package de.hhn.it.devtools.components.turnbasedbattle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BattleLog {
  private static final List<Consumer<String>> listeners = new ArrayList<>();

  private BattleLog() {}

  public static void post(String message) {
    for (Consumer<String> l : listeners) {
      l.accept(message);
    }
  }

  public static void addListener(Consumer<String> listener) {
    listeners.add(listener);
  }

  public static void removeListener(Consumer<String> listener) {
    listeners.remove(listener);
  }
}
