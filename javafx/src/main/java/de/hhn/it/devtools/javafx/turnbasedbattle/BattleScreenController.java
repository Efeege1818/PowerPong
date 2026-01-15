package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.GameState;
import de.hhn.it.devtools.apis.turnbasedbattle.TurnBasedBattleService;
import de.hhn.it.devtools.apis.turnbasedbattle.UnknownTransitionException;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleScreenController {

  @FXML private Label turnLabel;

  @FXML private ImageView player1MonsterImage;
  @FXML private ImageView player2MonsterImage;

  @FXML private VBox player1StatusBox;
  @FXML private VBox player2StatusBox;

  @FXML private Button btnElementalAtk;
  @FXML private Button btnNormalAtk;
  @FXML private Button btnBuffCrit;
  @FXML private Button btnDebuffHit;
  @FXML private Button btnSpecialAtk;
  @FXML private Button pauseButton;

  @FXML private Label keyLabelElemental;
  @FXML private Label keyLabelNormal;
  @FXML private Label keyLabelBuff;
  @FXML private Label keyLabelDebuff;
  @FXML private Label keyLabelSpecial;

  private TurnBasedBattleService service;
  private SimpleScreenManager screenManager;
  private final BattleScreenViewModel viewModel = new BattleScreenViewModel();

  private record SlotBinding(MoveSlot slot, Button button, Label keyLabel) {}
  private List<SlotBinding> slots;

  private static final double SLOT_LEFT_X = 209.0;
  private static final double SLOT_LEFT_TOP = 4.0;

  private static final double SLOT_RIGHT_X = 182.3485107421875;
  private static final double SLOT_RIGHT_TOP = 138.0;

  private static final double STATUS_LEFT_X = 90.0;
  private static final double STATUS_LEFT_BOTTOM = 15.0;

  private static final double STATUS_RIGHT_X = 80.0;
  private static final double STATUS_RIGHT_TOP = 35.0;

  private static final double LEFT_FIT_W  = 260.0;
  private static final double LEFT_FIT_H  = 260.0;

  private static final double RIGHT_FIT_W = 180.0;
  private static final double RIGHT_FIT_H = 180.0;

  private final Map<String, Image> imageCache = new HashMap<>();

  @FXML
  public void initialize() {
    slots = List.of(
        new SlotBinding(MoveSlot.S1, btnElementalAtk, keyLabelElemental),
        new SlotBinding(MoveSlot.S2, btnNormalAtk,    keyLabelNormal),
        new SlotBinding(MoveSlot.S3, btnBuffCrit,     keyLabelBuff),
        new SlotBinding(MoveSlot.S4, btnDebuffHit,    keyLabelDebuff),
        new SlotBinding(MoveSlot.S5, btnSpecialAtk,   keyLabelSpecial)
    );

    for (SlotBinding s : slots) {
      if (s.button() != null) {
        s.button().setOnAction(e -> executeMoveSafely(s.slot().moveIndex));
      }
    }

    render();
  }

  @FXML
  public void onActionPause() {
    StackPane root = (StackPane) turnLabel.getScene().getRoot();
    PauseScreenViewModel pauseViewModel = new PauseScreenViewModel(SimpleMonster.create(service.getPlayer1().monster()), SimpleMonster.create(service.getPlayer2().monster()));
    PauseScreenFx pauseScreen = new PauseScreenFx(screenManager, pauseViewModel);
    root.getChildren().add(pauseScreen);
  }


  public void setDependencies(SimpleScreenManager screenManager, TurnBasedBattleService service) {
    this.screenManager = screenManager;
    this.service = service;

    if (this.service == null) {
      throw new IllegalStateException("Battle service is null.");
    }

    refreshFromGameState();
    render();
  }

  public void installKeyHandling(Scene scene) {
    if (scene == null) return;

    scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
      if (service == null) return;
      if (service.getGameState() != GameState.RUNNING) return;

      boolean p1Turn = service.getCurrentPlayer() == service.getPlayer1();

      for (SlotBinding s : slots) {
        if (s.slot().matches(e.getCode(), p1Turn)) {
          executeMoveSafely(s.slot().moveIndex);
          e.consume();
          return;
        }
      }
    });
  }

  private void executeMoveSafely(int moveIndex) {
    if (service == null) return;
    if (service.getGameState() != GameState.RUNNING) return;

    if (service instanceof SimpleTurnBasedBattleService concrete) {
      boolean p1Turn = service.getCurrentPlayer() == service.getPlayer1();
      SimpleMonster current = p1Turn ? concrete.getPlayer1SimpleMonster() : concrete.getPlayer2SimpleMonster();
      if (current.isMoveOnCooldown(moveIndex)) {
        refreshFromGameState();
        render();
        return;
      }
    }

    try {
      service.executeTurn(moveIndex);
    } catch (IllegalStateException ex) {
      refreshFromGameState();
      render();
      return;
    }

    refreshFromGameState();
    render();

    if (service.getGameState() == GameState.END && screenManager != null) {
      var winner = service.getWinner(); // <-- kommt aus dem Service
      if (winner != null) {
        screenManager.setPendingWinner(winner.playerId(), winner.monster().element());
      }
      try {
        screenManager.switchTo(BattleScreen.SCREEN_NAME, EndScreen.SCREEN_NAME);
      } catch (UnknownTransitionException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  private void refreshFromGameState() {
    if (!(service instanceof SimpleTurnBasedBattleService concrete)) return;

    SimpleMonster m1 = concrete.getPlayer1SimpleMonster();
    SimpleMonster m2 = concrete.getPlayer2SimpleMonster();

    boolean p1Turn = service.getCurrentPlayer() == service.getPlayer1();
    int currentId = p1Turn ? 1 : 2;

    viewModel.turnText("Player " + currentId + " turn")
        .turnColor(p1Turn ? "RED" : "#006fff");

    updateKeyLabels(p1Turn);
    updateMoveButtons(p1Turn ? m1 : m2);   // current monster moves
    updateMonsterSides(p1Turn);
    updateStatusSides(p1Turn);
    updateMonsterImages(p1Turn, m1, m2);
  }

  private void render() {
    if (turnLabel != null) {
      turnLabel.setText(viewModel.turnText());
      turnLabel.setStyle("-fx-text-fill: " + viewModel.turnColor() + ";");
    }
  }

  private void updateKeyLabels(boolean p1Turn) {
    for (SlotBinding s : slots) {
      if (s.keyLabel() != null) {
        s.keyLabel().setText(s.slot().keyLabel(p1Turn));
      }
    }
  }

  private void updateMoveButtons(SimpleMonster current) {
    boolean p1Turn = service.getCurrentPlayer() == service.getPlayer1();
    for (SlotBinding s : slots) {
      configureMoveButton(s.button(), current, s.slot().moveIndex, p1Turn);
    }
  }

  private void configureMoveButton(Button button, SimpleMonster monster, int moveIndex, boolean p1Turn) {
    if (button == null) return;

    if (!monster.hasMove(moveIndex)) {
      button.setText("(no move)");
      button.setDisable(true);
      return;
    }

    Move move = monster.getMove(moveIndex);
    String title = (move.name() != null && !move.name().isBlank()) ? move.name() : ("Move " + moveIndex);

    boolean onCd = monster.isMoveOnCooldown(moveIndex);
    int remaining = monster.getRemainingCooldown(moveIndex);

    button.setDisable(onCd);
    button.setText(onCd ? (title + " (CD " + remaining + ")") : title);
  }

  private void updateMonsterSides(boolean p1Turn) {
    if (p1Turn) {
      placeMonsterLeft(player1MonsterImage);
      placeMonsterRight(player2MonsterImage);
    } else {
      placeMonsterLeft(player2MonsterImage);
      placeMonsterRight(player1MonsterImage);
    }
  }

  private void placeMonsterLeft(ImageView view) {
    if (view == null) return;

    AnchorPane.setRightAnchor(view, null);
    AnchorPane.setLeftAnchor(view, SLOT_LEFT_X);
    AnchorPane.setTopAnchor(view, SLOT_LEFT_TOP);

    view.setLayoutX(0);
    view.setLayoutY(0);

    view.setFitWidth(LEFT_FIT_W);
    view.setFitHeight(LEFT_FIT_H);
    view.setPreserveRatio(true);
  }

  private void placeMonsterRight(ImageView view) {
    if (view == null) return;

    AnchorPane.setLeftAnchor(view, null);
    AnchorPane.setRightAnchor(view, SLOT_RIGHT_X);
    AnchorPane.setTopAnchor(view, SLOT_RIGHT_TOP);

    view.setLayoutX(0);
    view.setLayoutY(0);

    view.setFitWidth(RIGHT_FIT_W);
    view.setFitHeight(RIGHT_FIT_H);
    view.setPreserveRatio(true);
  }

  private void updateStatusSides(boolean p1Turn) {
    if (player1StatusBox == null || player2StatusBox == null) return;

    if (p1Turn) {
      placeStatusLeftBottom(player1StatusBox);
      placeStatusRightTop(player2StatusBox);
    } else {
      placeStatusLeftBottom(player2StatusBox);
      placeStatusRightTop(player1StatusBox);
    }
  }

  private void placeStatusLeftBottom(VBox box) {
    AnchorPane.setRightAnchor(box, null);
    AnchorPane.setTopAnchor(box, null);
    AnchorPane.setLeftAnchor(box, STATUS_LEFT_X);
    AnchorPane.setBottomAnchor(box, STATUS_LEFT_BOTTOM);
  }

  private void placeStatusRightTop(VBox box) {
    AnchorPane.setLeftAnchor(box, null);
    AnchorPane.setBottomAnchor(box, null);
    AnchorPane.setRightAnchor(box, STATUS_RIGHT_X);
    AnchorPane.setTopAnchor(box, STATUS_RIGHT_TOP);
  }

  private void updateMonsterImages(boolean p1Turn, SimpleMonster m1, SimpleMonster m2) {
    if (p1Turn) {
      setMonsterImage(player1MonsterImage, spritePath(m1.getElement(), true));
      setMonsterImage(player2MonsterImage, spritePath(m2.getElement(), false));
    } else {
      setMonsterImage(player2MonsterImage, spritePath(m2.getElement(), true));
      setMonsterImage(player1MonsterImage, spritePath(m1.getElement(), false));
    }
  }

  private void setMonsterImage(ImageView view, String resourcePath) {
    if (view == null || resourcePath == null) return;

    Image img = imageCache.computeIfAbsent(resourcePath, p -> {
      var url = getClass().getResource(p);
      if (url == null) return null;
      return new Image(url.toExternalForm());
    });
    if (img != null) view.setImage(img);
  }

  private String spritePath(Element element, boolean back) {
    return switch (element) {
      case FIRE  -> back ? "/Monster Sprites/FeuerMon Back.png" : "/Monster Sprites/FeuerMon.png";
      case GRASS -> back ? "/Monster Sprites/PflanzeMon Back.png" : "/Monster Sprites/PflanzeMon.png";
      case WATER -> back ? "/Monster Sprites/WasserMon Back.png" : "/Monster Sprites/WasserMon.png";
      default    -> back ? "/Monster Sprites/FeuerMon Back.png" : "/Monster Sprites/PflanzeMon.png";
    };
  }
}
