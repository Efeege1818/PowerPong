package de.hhn.it.devtools.apis.examples.coffeemakerservice;


import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;

/**
 * Admin interface to the WnckCoffeMakerService to add / remove coffee makers to / from the service.
 */
public interface AdminCoffeeMakerService {

  /**
   * Adds a new CoffeeMaker to the service.
   *
   * @param configuration configuration of the new coffee maker
   * @throws IllegalParameterException if the configuration is incomplete
   * @throws NullPointerException if the configuration is a null reference
   */
  void addCoffeeMaker(CoffeeMakerConfiguration configuration) throws IllegalParameterException;

  /**
   * Removes a CoffeeMaker from the service.
   *
   * @param coffeeMakerId id of the coffee maker to be removed
   * @throws IllegalParameterException if the id of the coffee maker does not exist.
   */
  void removeCoffeeMaker(int coffeeMakerId) throws IllegalParameterException;

  /**
   * Returns a ServiceState object containing all data to be persisted.
   *
   * @return ServiceState object with the actual state of the service with regard to persistence
   */
  ServiceState getStateForSave();

  /**
   * Loads persisted data into the service.
   *
   * @param state ServiceState object containing persisted state information
   * @throws IllegalStateException if the state object is incomplete or wrong
   * @throws NullPointerException is the state object is a null reference
   */
  void loadState(ServiceState state) throws IllegalStateException, NullPointerException;
}
