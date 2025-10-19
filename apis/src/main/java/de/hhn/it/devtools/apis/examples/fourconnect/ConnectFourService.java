package de.hhn.it.devtools.apis.examples.fourconnect;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.exceptions.OperationNotSupportedException;

/**
 * Die zentrale Fassade (das Interface) für den Connect Four Service (Toxische Edition).
 * Dieses Interface dient als zentraler Kontrollpunkt für alle Spiellogik und Aktionen.
 */
public interface ConnectFourService {

  /**
   * Führt einen Zug aus und wirft einen Chip in die angegebene Spalte.
   * Dies ist die primäre Aktion des Spielers, die den Spielzustand ändert.
   * Der Aufruf löst nach dem Setzen des Chips auch die Zerfallslogik aus.
   *
   * @param column Die Spalte, in die der Chip geworfen wird.
   * @return Die Reihe, in die der Chip gefallen ist (zur Rückmeldung an die UI).
   * @throws IllegalParameterException Wenn die angegebene Spalte ungültig ist (z.B. außerhalb des Spielfelds) oder bereits voll ist.
   * @throws OperationNotSupportedException Wenn versucht wird, zu ziehen, obwohl das Spiel beendet ist (z.B. nach Gewinn/Unentschieden).
   */
  int dropChip(int column) throws IllegalParameterException, OperationNotSupportedException;

  /**
   * Liefert den aktuellen Zustand des Spielfelds an die UI zurück.
   *
   * @return Ein 2D-Array, das den Zustand jeder Zelle (Besitzer/Typ) repräsentiert.
   */
  Player[][] getBoardState();


  /**
   * Liefert den Spieler, der aktuell am Zug ist.
   *
   * @return Das Player-Objekt des aktuellen Spielers.
   */
  Player getCurrentPlayer();


  /**
   * Prüft, ob der zuletzt gesetzte Chip zu einer Gewinnbedingung geführt hat.
   *
   * @return Wahr (true), wenn der Spieler, der zuletzt gezogen hat, gewonnen hat.
   */
  boolean checkForWin();


  /**
   * Setzt den Spielzustand komplett zurück und startet ein neues Spiel.
   * Das Spielfeld wird geleert und die toxischen Felder werden neu initialisiert.
   */
  void resetGame();


  /**
   * Prüft, ob das Spielfeld voll ist und kein Spieler gewonnen hat (Unentschieden).
   *
   * @return Wahr (true), wenn das Spiel unentschieden ist.
   */
  boolean checkForDraw();


  /**
   * **SPEZIALREGEL:** Löst den Zerfallsprozess der Chips auf toxischen Feldern aus.
   * Diese Methode reduziert die Timer und lässt alle Chips, deren Timer abgelaufen ist, verschwinden.
   */
  void applyToxicDecay();


  /**
   * Aktualisiert den Punktestand eines bestimmten Spielers um die angegebene Punktzahl.
   * (Hinweis: Dies sollte idealerweise intern durch den Service gesteuert werden.)
   *
   * @param player Der Spieler, dessen Punktestand geändert wird.
   * @param points Die Anzahl der Punkte, die hinzugefügt oder abgezogen werden.
   */
  void updateScore(Player player, int points);

  /**
   * Setzt den internen Timer (Turn-Wert) für die Zerfallslogik zurück.
   * (Hinweis: Diese Methode ist interne Logik des Service und sollte idealerweise nicht in der Fassade sein.)
   *
   * @return Der zurückgesetzte Turn-Wert.
   */
  int resetTurn();

  /**
   * Erhöht den internen Timer (Turn-Wert) für die Zerfallslogik.
   * (Hinweis: Diese Methode ist interne Logik des Service und sollte idealerweise nicht in der Fassade sein.)
   */
  void incrementTurn();

  /**
   * Setzt das Spielfeld zurück, ohne das gesamte Spiel zu resetten.
   * (Hinweis: Diese Funktionalität ist in resetGame() enthalten und sollte idealerweise nicht als separate Methode in der Fassade sein.)
   */
  void resetBoard();

  /**
   * Legt zufällige Positionen für die toxischen Felder fest.
   * (Hinweis: Dies ist interne Initialisierungslogik und sollte in resetGame() gekapselt werden.)
   */
  void placeRandomToxicFields();
}