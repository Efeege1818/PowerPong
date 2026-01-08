module devtools.components {
  exports de.hhn.it.devtools.components.example.coffeemakerservice.provider;
  exports de.hhn.it.devtools.components.shapesurvivor;
    exports de.hhn.it.devtools.components.shapesurvivor.helper;
    requires org.slf4j;
  exports de.hhn.it.devtools.components.spaceinvaders;
  requires devtools.apis;
  requires java.desktop;
  provides  de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService
          with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
  provides  de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService
          with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
        }
