module devtools.javafx {
  requires org.slf4j;
  requires transitive devtools.apis;
  requires devtools.components;
  requires javafx.controls;
  requires javafx.media;
  requires javafx.fxml;
  requires javafx.graphics;
  requires java.desktop;
  requires java.naming;

  uses de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService;
  uses de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService;
  uses de.hhn.it.devtools.apis.towerdefense.TowerDefenseService;
  uses de.hhn.it.devtools.components.spaceinvaders.SimpleSpaceInvadersService;
  uses de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersService;
  uses de.hhn.it.devtools.apis.spaceinvaders.SpaceInvadersListener;
  uses de.hhn.it.devtools.components.shapesurvivor.SimpleShapeSurvivorService;

  opens de.hhn.it.devtools.javafx.controllers to javafx.fxml;
  opens de.hhn.it.devtools.javafx.coffeemaker.view to javafx.fxml;
  opens de.hhn.it.devtools.javafx.powerpong.view to javafx.fxml;
  opens de.hhn.it.devtools.javafx.controllers.template to javafx.fxml;
  opens de.hhn.it.devtools.javafx.shapesurvivor.view to javafx.fxml;
  opens de.hhn.it.devtools.javafx.shapesurvivor.viewmodel to javafx.fxml;

  exports de.hhn.it.devtools.javafx;
  exports de.hhn.it.devtools.javafx.controllers;
  exports de.hhn.it.devtools.javafx.coffeemaker.view;
  exports de.hhn.it.devtools.javafx.towerdefense.controllers;
  exports de.hhn.it.devtools.javafx.spaceinvaders.view;
}
