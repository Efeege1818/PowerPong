package de.hhn.it.devtools.apis.examples.coffeemakerservice;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The ServiceState object contains all data of the CoffeeMakerService which need to be persisted
 * between different instantiations of the program.
 */
public class ServiceState implements Serializable {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(ServiceState.class);

  /**
   * List of all coffee maker configurations to be persisted and restored.
   */
  private List<CoffeeMakerConfiguration> coffeeMakerConfigurationList;

  /**
   * Timestamp of creation of the object.
   */
  private LocalDateTime created;

  /**
   * Constructs a ServiceState object and stores the timestamp information of the creation.
   */
  public ServiceState() {
    coffeeMakerConfigurationList = new ArrayList<>();
    created = LocalDateTime.now();
  }

  /**
   * Adds a coffee maker configuration to the ServiceState object.
   *
   * @param configuration configuration of a coffee maker
   */
  public void addConfiguration(final CoffeeMakerConfiguration configuration) {
    coffeeMakerConfigurationList.add(configuration);
  }

  /**
   * Returns the list of coffee maker configurations.  This list may be empty.
   *
   * @return List of CoffeeMakerConfiguration objects.
   */
  public List<CoffeeMakerConfiguration> getCoffeeMakerConfigurationList() {
    return coffeeMakerConfigurationList;
  }

  /**
   * Returns the timestamp of the creation of the ServiceState object.
   *
   * @return LocalDateTime object marking the timestamp of creation of the ServiceState object
   */
  public LocalDateTime getCreated() {
    return created;
  }
}
