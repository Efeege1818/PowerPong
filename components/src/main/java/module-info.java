module devtools.components {
  exports de.hhn.it.devtools.components.example.coffeemakerservice.provider;
    exports de.hhn.it.devtools.components.towerdefensecomponents;
    requires org.slf4j;
  requires devtools.apis;
  requires java.desktop;
  requires java.rmi;
  provides  de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService
          with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
  provides  de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService
          with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
  provides de.hhn.it.devtools.apis.towerdefenseapi.TowerDefenseService
          with de.hhn.it.devtools.components.towerdefensecomponents.SimpleTowerDefenseService;
        }
