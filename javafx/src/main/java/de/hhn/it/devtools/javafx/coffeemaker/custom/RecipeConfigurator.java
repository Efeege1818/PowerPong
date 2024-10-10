package de.hhn.it.devtools.javafx.coffeemaker.custom;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import javafx.scene.layout.VBox;

public abstract class RecipeConfigurator extends VBox {
  public abstract Recipe getRecipe();
}
