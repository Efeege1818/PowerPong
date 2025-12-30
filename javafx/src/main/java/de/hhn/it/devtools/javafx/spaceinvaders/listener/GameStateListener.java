package de.hhn.it.devtools.javafx.spaceinvaders.listener;

import de.hhn.it.devtools.apis.spaceinvaders.GameState;
import de.hhn.it.devtools.javafx.spaceinvaders.custom.PopupConfigurations;
import de.hhn.it.devtools.javafx.spaceinvaders.viewmodel.SpaceInvadersViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * GameStateListener triggered by ViewModel.
 */
public class GameStateListener implements ChangeListener<GameState> {
  private final PopupConfigurations popupConfigurations;
  private final SpaceInvadersViewModel viewModel;

  /**
   * Constructor for GameStateListener.
   *
   * @param popupConfigurations popup configurations to open popups.
   * @param viewModel view model to get game data.
   */
  public GameStateListener(PopupConfigurations popupConfigurations,
                           SpaceInvadersViewModel viewModel) {
    this.popupConfigurations = popupConfigurations;
    this.viewModel = viewModel;
  }

  @Override
  public void changed(ObservableValue<? extends GameState> observableValue, GameState gameState,
                      GameState newState) {
    if (newState == GameState.ABORTED) {
      popupConfigurations.openEndingPopup();
    } else if (newState == GameState.PAUSED) {
      System.out.println(viewModel.getAliens().size());
      if (viewModel.getAliens().isEmpty()) {
        popupConfigurations.openNextRoundPopup();
      } else {
        popupConfigurations.openSettingsPopup();
      }
    }
  }
}
