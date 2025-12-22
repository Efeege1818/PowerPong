package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class SelectScreen extends VBox {
  public static final String SCREEN_NAME = "SelectScreen";

  private final SimpleScreenManager screenManager;
  private final SelectScreenViewModel viewModel;
  private boolean selected1 = false;
  private boolean selected2 = false;
  private Monster p1Monster;
  private Monster p2Monster;

  public SelectScreen(SimpleScreenManager screenManager) {
    this.screenManager = screenManager;
    viewModel = new SelectScreenViewModel();

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SelectScreen.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void MonsterForP1(Monster monster) {
    p1Monster = monster;
    selected1 = true;
  }

  public void MonsterForP2(Monster monster) {
    p2Monster = monster;
    selected2 = true;
  }

  public List<Monster> getAvailableMonsters(List<Monster> monsters) {
    return monsters;
  }

  public boolean isSelectionFinished() {
    return selected1 && selected2;
  }

  public Monster getP1Monster() {
    return p1Monster;
  }

  public Monster getP2Monster() {
    return p2Monster;
  }
}
