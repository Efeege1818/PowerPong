package de.hhn.it.devtools.apis.examples.coffeemakerservice;

import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import java.io.Serializable;
import java.util.Objects;

/**
 * Holds a configuration for a coffee maker.
 *
 * @param location location where the coffee maker can be found
 * @param model type / model of the coffee maker
 * @param id unique id of the coffee maker. If the id is not yet assigned the value is -1.
 * @param cups number of cups brewed since last service
 */
public record CoffeeMakerConfiguration(String location, String model, int id, int cups)
    implements Serializable {
  // TODO: Fixing javadoc issue with records,
  //  see https://stackoverflow.com/questions/71726595/how-to-javadoc-a-record-without-warnings-from-xdoclint
  // and https://bugs.openjdk.org/browse/JDK-8275192
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(CoffeeMakerConfiguration.class);

  /**
   * Constructor.
   *
   * @param location place where the coffee maker is located
   * @param model model of the coffee maker
   * @param id id of the coffee maker. If the id is not yet assigned the value is -1.
   * @param cups number of cups brewed since last service
   */
  public CoffeeMakerConfiguration(final String location, final String model, int id, int cups) {
    logger.debug("Constructor: {} | {} | {} | {}", location, model, id, cups);
    Objects.requireNonNull(model);
    Objects.requireNonNull(location);

    if (location.trim().isEmpty()) {
      throw new IllegalArgumentException("location may not be an empty string.");
    }
    if (model.trim().isEmpty()) {
      throw new IllegalArgumentException("model may not be an empty string.");
    }

    this.location = location;
    this.model = model;
    this.id = id;
    this.cups = cups;
  }

  /**
   * Constructor.
   *
   * @param location must be a non-empty string
   * @param model must be a non-empty string
   */
  public CoffeeMakerConfiguration(final String location, final String model) {
    this(location, model, -1, 0);
    logger.debug("Constructor: {} | {}", location, model);
  }

  /**
   * Creates a new configuration by copying all values but the id.
   *
   * @param id id in the resulting configuration
   * @return configuration with updated id
   */
  public CoffeeMakerConfiguration withId(int id) {
    return new CoffeeMakerConfiguration(location, model, id, cups);
  }

  /**
   * Creates a new configuration by copying all values but the number of cups.
   *
   * @param cups number of cups in the resulting configuration
   * @return configuration with updated number of cups
   */
  public CoffeeMakerConfiguration withCups(int cups) {
    return new CoffeeMakerConfiguration(location, model, id, cups);
  }
}
