package de.hhn.it.devtools.components.example.coffeemakerservice.junit;


import de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMaker;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test the admin interface with good cases.")
@ExtendWith(CoffeeMakerParameterResolver.class)
public class TestAdminInterfaceGoodCases {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(TestAdminInterfaceGoodCases.class);


  CoffeeMakerService coffeeMakerService;
  AdminCoffeeMakerService adminCoffeeMakerService;
  List<CoffeeMakerConfiguration> configurations;

  @BeforeEach
  void setup(List<CoffeeMakerConfiguration> configurations) {
//    WnckCoffeeMaker.resetIdCounter();
    WnckCoffeeMakerService wnckCoffeeMakerService = new WnckCoffeeMakerService();
    coffeeMakerService = wnckCoffeeMakerService;
    adminCoffeeMakerService = wnckCoffeeMakerService;
    this.configurations = configurations;
  }

  @Test
  @DisplayName("A new service has no coffee makers.")
  public void aNewServiceHasNoCoffeeMakers() {
    List<CoffeeMakerConfiguration>
            makers = coffeeMakerService.getCoffeeMakers();
    assertNotNull(makers);
    assertEquals(0, makers.size(), "The list should be empty.");
  }

  @Test
  @DisplayName("Adding one coffee maker to the service results in one entry.")
  public void addOneCoffeeMaker() throws IllegalParameterException {
    CoffeeMakerConfiguration configuration = new CoffeeMakerConfiguration("A106", "Senseo muddy brown");
    adminCoffeeMakerService.addCoffeeMaker(configuration);
    List<CoffeeMakerConfiguration> makers = coffeeMakerService.getCoffeeMakers();
    CoffeeMakerConfiguration configuration1 = makers.get(0);
    assertAll(
            () -> assertEquals(1, makers.size(), "There should be exactly one maker in the list."),
            () -> assertEquals(1, configuration1.id(), "Id of makers should start with 0")
    );
  }

  @Test
  @DisplayName("Add multiple coffee makers and check the result")
  public void addMultipleCoffeeMakers() throws IllegalParameterException {

    // add injected CoffeeMakerDescriptors to CoffeeMakerService
    for (CoffeeMakerConfiguration configuration : configurations) {
      adminCoffeeMakerService.addCoffeeMaker(configuration);
    }

    List<CoffeeMakerConfiguration> makers = coffeeMakerService.getCoffeeMakers();
    assertAll(
            () -> assertEquals(3, makers.size(), "Now we should " +
                    "have three makers."),
            () -> assertNotEquals(makers.get(0).id(), makers.get(1).id()),
            () -> assertNotEquals(makers.get(1).id(), makers.get(2).id()),
            () -> assertNotEquals(makers.get(0).id(), makers.get(2).id())
    );
  }

  @Test
  @DisplayName("remove one coffee maker and check the result")
  public void removeCoffeeMakers() throws IllegalParameterException {
    // add injected CoffeeMakerDescriptors to CoffeeMakerService
    for (CoffeeMakerConfiguration configuration : configurations) {
      adminCoffeeMakerService.addCoffeeMaker(configuration);
    }

    List<CoffeeMakerConfiguration> makers = coffeeMakerService.getCoffeeMakers();
    assertEquals(3, makers.size());

    // remove second maker
    adminCoffeeMakerService.removeCoffeeMaker(makers.get(1).id());
    makers = coffeeMakerService.getCoffeeMakers();
    assertEquals(2, makers.size());

  }

}

