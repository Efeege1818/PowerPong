package de.hhn.it.devtools.javafx.controllers;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.ServiceState;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
import de.hhn.it.devtools.javafx.coffeemaker.helper.PersistenceManager;
import de.hhn.it.devtools.javafx.coffeemaker.view.CoffeeMakerController;
import de.hhn.it.devtools.javafx.coffeemaker.viewmodel.CoffeeMakerViewModel;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/**
 * Top level controller for CoffeeMakerService to be integrated into tab navigation.
 */
public class CoffeeMakerServiceController extends Controller implements Initializable {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(CoffeeMakerServiceController.class);
  private final PersistenceManager persistenceManager;
  private final AdminCoffeeMakerService adminCoffeeMakerService;
  @FXML
  ListView<CoffeeMakerConfiguration> makerListView;
  @FXML
  AnchorPane controlAnchorPane;
  Node actualControlAnchorPaneNode;
  Label pleaseSelectLabel;
  List<CoffeeMakerConfiguration> configurations;
  ObservableList<CoffeeMakerConfiguration> observableConfigurations;
  CoffeeMakerConfiguration actualMaker;
  private CoffeeMakerService coffeeMakerService;
  private Map<Integer, CoffeeMakerController> id2CoffeeMakerControllerMap;

  /**
   * Constructor.
   */
  public CoffeeMakerServiceController() {
    persistenceManager = new PersistenceManager(Paths.get("./coffeemaker.ser"));
    WnckCoffeeMakerService wnckCoffeeMakerService = new WnckCoffeeMakerService();
    adminCoffeeMakerService = wnckCoffeeMakerService;
    coffeeMakerService = wnckCoffeeMakerService;
    observableConfigurations = FXCollections.observableArrayList();
    id2CoffeeMakerControllerMap = new HashMap<>();
    pleaseSelectLabel = new Label("Please select a coffee maker");

    try {
      loadConfigurationOrAddDemoData();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    configurations = coffeeMakerService.getCoffeeMakers();
    logger.info("Service has {} descriptors.", configurations.size());
    observableConfigurations.clear();
    observableConfigurations.addAll(configurations);
  }

  private void loadConfigurationOrAddDemoData() throws IOException, ClassNotFoundException {
    if (persistenceManager.doesExist()) {
      ServiceState state = persistenceManager.load();
      for (CoffeeMakerConfiguration configuration : state.getCoffeeMakerConfigurationList()) {
        try {
          adminCoffeeMakerService.addCoffeeMaker(configuration);
        } catch (IllegalParameterException e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      try {
        CoffeeMakerConfiguration
            descriptor1 =
            new CoffeeMakerConfiguration("A106", "Senseo muddy brown", 12, 37);
        adminCoffeeMakerService.addCoffeeMaker(descriptor1);
        CoffeeMakerConfiguration descriptor2 =
            new CoffeeMakerConfiguration("F229", "Senseo dirty grey");
        adminCoffeeMakerService.addCoffeeMaker(descriptor2);
        CoffeeMakerConfiguration descriptor3 =
            new CoffeeMakerConfiguration("A317", "Nespresso basics", 3, 1255);
        adminCoffeeMakerService.addCoffeeMaker(descriptor3);
        configurations = coffeeMakerService.getCoffeeMakers();

        configurations.forEach((d) -> observableConfigurations.add(d));

      } catch (IllegalParameterException e) {
        e.printStackTrace();
      }

      persistCurrentState();

    }
  }

  private void persistCurrentState() {
    logger.debug("persistCurrentState: - ");
    try {
      persistenceManager.save(adminCoffeeMakerService.getStateForSave());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private CoffeeMakerController getCoffeeMakerController(CoffeeMakerConfiguration configuration) {
    if (!id2CoffeeMakerControllerMap.containsKey(configuration.id())) {
      CoffeeMakerViewModel viewModel = new CoffeeMakerViewModel(configuration, coffeeMakerService);
      id2CoffeeMakerControllerMap.put(configuration.id(),
          new CoffeeMakerController(viewModel));
    }
    return id2CoffeeMakerControllerMap.get(configuration.id());
  }

  /**
   * Called to initialize a controller after its root element has been
   * completely processed.
   *
   * @param location  The location used to resolve relative paths for the root object, or
   *                  <code>null</code> if the location is not known.
   * @param resources The resources used to localize the root object, or <code>null</code> if
   */
  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    makerListView.setItems(observableConfigurations);
    makerListView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> switchCoffeeMaker(oldValue, newValue));
    makerListView.setCellFactory((list) -> new CoffeeMakerCell());

    actualControlAnchorPaneNode = pleaseSelectLabel;
    controlAnchorPane.getChildren().add(pleaseSelectLabel);
  }

  private void switchCoffeeMaker(final CoffeeMakerConfiguration oldValue,
                                 final CoffeeMakerConfiguration newValue) {
    assert newValue != null;
    assert newValue != oldValue;

    controlAnchorPane.getChildren().clear();
    actualMaker = newValue;
    CoffeeMakerController actualCoffeeMakerController = getCoffeeMakerController(newValue);
    actualControlAnchorPaneNode = actualCoffeeMakerController;
    controlAnchorPane.getChildren().add(actualCoffeeMakerController);

  }

  private void raiseExceptionToUi(final Exception e, final String header) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(header);
    alert.setContentText(e.getMessage());
    alert.showAndWait();
  }

  @Override
  public void pause() {
    logger.debug("pause: CoffeeMaker is pausing from stage ...");
  }

  @Override
  public void resume() {
    logger.debug("resume: CoffeeMaker is back on stage ...");
  }

  @Override
  public void shutdown() {
    logger.debug("shutdown: - ");
    persistCurrentState();
  }

  private class CoffeeMakerCell extends ListCell<CoffeeMakerConfiguration> {
    @Override
    protected void updateItem(final CoffeeMakerConfiguration item, final boolean empty) {
      super.updateItem(item, empty);
      Label label = new Label();
      if (item != null) {
        label.setText(item.location() + " : " + item.model());
      }
      setGraphic(label);

    }
  }
}
