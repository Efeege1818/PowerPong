package de.hhn.it.devtools.components.example.coffeemakerservice;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Quantity;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;

import java.util.List;

public class CoffeeMakerServiceUsageDemo {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(CoffeeMakerServiceUsageDemo.class);

  public static void main(String[] args) throws IllegalParameterException, InterruptedException {
    WnckCoffeeMakerService  wnckCoffeeMakerService = new WnckCoffeeMakerService();
    AdminCoffeeMakerService adminCoffeeMakerService = wnckCoffeeMakerService;
    CoffeeMakerService coffeeMakerService = wnckCoffeeMakerService;

    // register two coffee makers via admin interface
    CoffeeMakerConfiguration coffeeMakerConfiguration1 = new CoffeeMakerConfiguration("A317",
        "BrewWizard");
    adminCoffeeMakerService.addCoffeeMaker(coffeeMakerConfiguration1);
    CoffeeMakerConfiguration coffeeMakerConfiguration2 = new CoffeeMakerConfiguration("F141", "BrewGuru");
    adminCoffeeMakerService.addCoffeeMaker(coffeeMakerConfiguration2);

    // Now use the client interface
    List<CoffeeMakerConfiguration> makerConfigurations = coffeeMakerService.getCoffeeMakers();
    CoffeeMakerConfiguration configuration0 = makerConfigurations.get(0);

    int makerId0 = configuration0.id();
    logger.debug("" + configuration0);

    logger.info(">>> switch on");
    // Switch it on, start heating, get ready
    coffeeMakerService.switchOn(makerId0);
    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.info("" + configuration0);

    Thread.sleep(3000);

    logger.info(">>> check again");
    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.info("" + configuration0);

    Thread.sleep(1000);

    logger.info(">>> cleaning");
    // clean it before you brew something
    coffeeMakerService.cleanIt(makerId0);
    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.info("" + configuration0);

    Thread.sleep(3000);

    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.debug("" + configuration0);


    // now get some coffee, black, no milk, no sugar, no milk froth
    logger.debug(">>> brewing");
    Recipe recipe = new Recipe(Quantity.LARGE, Quantity.NONE, Quantity.NONE, Quantity.NONE);
    coffeeMakerService.brewCoffee(makerId0, recipe);

    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.debug("" + configuration0);

    Thread.sleep(3000);

    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.debug("" + configuration0);

    // switch it off
    logger.debug(">>> switch off");
    coffeeMakerService.switchOff(makerId0);
    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.debug("" + configuration0);

    Thread.sleep(3000);

    configuration0 = coffeeMakerService.getCoffeeMaker(makerId0);
    logger.debug("" + configuration0);



  }

}
