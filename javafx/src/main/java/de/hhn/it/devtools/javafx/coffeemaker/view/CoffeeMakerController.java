package de.hhn.it.devtools.javafx.coffeemaker.view;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.javafx.coffeemaker.custom.RecipeConfigurator;
import de.hhn.it.devtools.javafx.coffeemaker.custom.SliderRecipeConfigurator;
import de.hhn.it.devtools.javafx.coffeemaker.viewmodel.CoffeeMakerViewModel;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


/**
 * Controller for a single CoffeeMakerViewModel object.
 */
public class CoffeeMakerController extends AnchorPane {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(CoffeeMakerController.class);
  private final RecipeConfigurator recipeConfigurator;

  @FXML
  Label locationLabel;
  @FXML
  Label modelLabel;
  @FXML
  Label statusLabel;
  @FXML
  Button onButton;
  @FXML
  Button offButton;
  @FXML
  Button cleanItButton;
  @FXML
  Button brewButton;
  @FXML
  Label cupsLabel;
  @FXML
  HBox recipeConfiguratorHbox;

  private final CoffeeMakerViewModel viewModel;

  /**
   * Constructor.
   *
   * @param viewModel ViewModel to communicate with
   */
  public CoffeeMakerController(CoffeeMakerViewModel viewModel) {
    logger.info("Creating CoffeeMakerController for {}", viewModel);
    this.viewModel = viewModel;
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coffeemaker"
            + "/CoffeeMakerControl.fxml"));

    loader.setRoot(this);
    loader.setController(this);

    try {
      loader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    recipeConfigurator = new SliderRecipeConfigurator(viewModel);
    recipeConfiguratorHbox.getChildren().add(recipeConfigurator);

    // bind enable/disable of buttons and text of labels to properties of the view model
    onButton.disableProperty().bind(viewModel.onButtonDisabledProperty());
    offButton.disableProperty().bind(viewModel.offButtonDisabledProperty());
    cleanItButton.disableProperty().bind(viewModel.cleanButtonDisabledProperty());
    brewButton.disableProperty().bind(viewModel.brewButtonDisabledProperty());
    locationLabel.textProperty().bind(viewModel.locationProperty());
    modelLabel.textProperty().bind(viewModel.modelProperty());
    statusLabel.textProperty().bind(viewModel.stateProperty().asString());
    cupsLabel.textProperty().bind(viewModel.cupsProperty().asString());
  }

  private void raiseExceptionToUi(final Exception e, final String header) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(header);
    alert.setContentText(e.getMessage());
    alert.showAndWait();
  }

  /**
   * Act when the CoffeeMaker has been switched on.
   *
   * @param event event of the UI element
   */
  public void onSwitchOn(ActionEvent event) {
    logger.debug("Switch on pressed.");
    try {
      viewModel.switchOn();
    } catch (IllegalParameterException | IllegalStateException e) {
      raiseExceptionToUi(e, "Error in SWITCH_ON");
    }
  }

  /**
   * Act when the CoffeeMaker has been switched off.
   *
   * @param event event of the UI element
   */
  public void onSwitchOff(ActionEvent event) {
    logger.debug("Switch off pressed.");
    try {
      viewModel.switchOff();
    } catch (IllegalParameterException | IllegalStateException e) {
      raiseExceptionToUi(e, "error in SWITCH_OFF");
    }
  }

  /**
   * Act when the CoffeeMaker shall clean its interior.
   *
   * @param event event of the UI element
   */
  public void onCleanIt(ActionEvent event) {
    logger.debug("CleanIt pressed.");
    try {
      viewModel.cleanIt();
    } catch (IllegalParameterException | IllegalStateException e) {
      raiseExceptionToUi(e, "error in CLEAN_IT");
    }
  }

  /**
   * Act when the CoffeeMaker has to brew.
   *
   * @param event event of the UI element
   */
  public void onBrew(ActionEvent event) {
    logger.debug("Brew pressed.");
    try {
      viewModel.brewCoffee();
    } catch (IllegalParameterException | IllegalStateException e) {
      raiseExceptionToUi(e, "error in BREW");
    }
  }

}
