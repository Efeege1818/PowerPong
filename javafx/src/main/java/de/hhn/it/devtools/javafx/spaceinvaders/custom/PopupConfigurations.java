package de.hhn.it.devtools.javafx.spaceinvaders.custom;

import de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
import de.hhn.it.devtools.javafx.spaceinvaders.helper.PopupProvider;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import javafx.stage.Stage;

/**
 * Class for default PopupConfigurations.
 */
public class PopupConfigurations {
  private final SpaceInvadersService spaceInvadersService;
  private final SpaceInvadersViewModel spaceInvadersViewModel;
  private final Stage mainStage;
  private final Stage owner;
  private Stage settingsStage;
  private Stage startStage;
  private Stage nextRoundStage;

  /**
   * Initialize PopupConfigurations.
   *
   * @param service service.
   * @param mainStage main stage.
   * @param owner owner stage.
   * @param viewModel view model.
   */
  public PopupConfigurations(SpaceInvadersService service, Stage mainStage, Stage owner,
                             SpaceInvadersViewModel viewModel) {
    this.spaceInvadersViewModel = viewModel;
    this.spaceInvadersService = service;
    this.mainStage = mainStage;
    this.owner = owner;
    initPopup();
  }

  /**
   * Open Settings Popup.
   */
  public void openSettingsPopup() {
    settingsStage.showAndWait();
  }

  /**
   * Open Start Popup.
   */
  public void openStartPopup() {
    startStage.showAndWait();
  }

  /**
   * Open Next Round Popup.
   */
  public void openNextRoundPopup() {
    nextRoundStage.showAndWait();
  }

  /**
   * Open Ending Popup.
   */
  public void openEndingPopup() {
    new PopupProvider(owner)
            .setTitle("Game Over")
            .addLabel("Your Score")
            .addLabel(String.valueOf(spaceInvadersViewModel.getScoreProperty().get()))
            .addLabel("Reached Level")
            .addLabel(String.valueOf(spaceInvadersViewModel.getCurrentRoundProperty().get()))
            .addButton((e) -> {
              spaceInvadersService.removeListener(spaceInvadersViewModel);
              owner.close();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              owner.close();
              mainStage.show();
            }).build().showAndWait();
  }

  private void initPopup() {
    // Settings Popup.
    this.settingsStage = new PopupProvider(owner)
            .setTitle("Settings")
            .addButton((e) -> spaceInvadersService.resume(), "Resume")
            .addButton((e) -> spaceInvadersService.abort(), "Quit")
            .setCloseRequest((e) -> spaceInvadersService.resume()).build();
    // Start Popup.
    this.startStage = new PopupProvider(owner)
            .setTitle("SpaceInvaders")
            .addButton((e) -> spaceInvadersService.start(), "Start Game")
            .addButton((e) -> {
              spaceInvadersService.removeListener(spaceInvadersViewModel);
              spaceInvadersService.abort();
              owner.close();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              spaceInvadersService.removeListener(spaceInvadersViewModel);
              spaceInvadersService.abort();
              owner.close();
              mainStage.show();
            }).build();
    // Next Round Popup.
    this.nextRoundStage = new PopupProvider(owner)
            .setTitle("Level Complete")
            .addButton((e) -> spaceInvadersService.nextRound(), "Next Level")
            .addButton((e) -> {
              spaceInvadersService.abort();
              mainStage.show();
            }, "Quit")
            .setCloseRequest((e) -> {
              spaceInvadersService.abort();
              owner.close();
            }).build();
  }
}
