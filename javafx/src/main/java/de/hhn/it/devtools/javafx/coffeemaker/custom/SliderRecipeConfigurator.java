package de.hhn.it.devtools.javafx.coffeemaker.custom;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.Quantity;
import de.hhn.it.devtools.apis.examples.coffeemakerservice.Recipe;
import de.hhn.it.devtools.javafx.coffeemaker.viewmodel.CoffeeMakerViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

/**
 * A slider-based RecipeConfigurator.
 */
public class SliderRecipeConfigurator extends RecipeConfigurator {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(SliderRecipeConfigurator.class);
  private final IngredientSlider coffeeSlider;
  private final IngredientSlider milkSlider;
  private final IngredientSlider sugarSlider;
  private final IngredientSlider milkFrothSlider;
  private final BooleanProperty disabled;
  private final ObjectProperty<Recipe> recipe;

  private SliderRecipeConfigurator() {
    disabled = new SimpleBooleanProperty(true);
    recipe = new SimpleObjectProperty<>(
        new Recipe(Quantity.SMALL, Quantity.NONE, Quantity.NONE, Quantity.NONE));
    coffeeSlider = new IngredientSlider("coffee", 0.333);
    milkSlider = new IngredientSlider("milk", 0.0);
    sugarSlider = new IngredientSlider("sugar", 0.0);
    milkFrothSlider = new IngredientSlider("milkfroth", 0.0);
    getChildren().addAll(coffeeSlider, milkSlider, sugarSlider, milkFrothSlider);
  }

  /**
   * Constructor.
   *
   * @param viewModel view model to communicate with
   */
  public SliderRecipeConfigurator(CoffeeMakerViewModel viewModel) {
    this();
    recipe.bindBidirectional(viewModel.recipeProperty());
    disabled.bind(viewModel.recipeConfigurationDisabledProperty());
  }

  /**
   * Reads the slider values and creates a new recipe which gets set into the recipe property.
   *
   * @return new recipe
   */
  @Override
  public Recipe getRecipe() {
    Recipe newRecipe = new Recipe(
        slider2Quantity(coffeeSlider),
        slider2Quantity(milkSlider),
        slider2Quantity(sugarSlider),
        slider2Quantity(milkFrothSlider)
    );

    recipe.setValue(newRecipe);
    logger.debug("getRecipe: {}", newRecipe);
    return newRecipe;
  }

  private Quantity slider2Quantity(final IngredientSlider slider) {
    double value = slider.getValue();
    if (value < 0.2) {
      return Quantity.NONE;
    }
    if (value < 0.4) {
      return Quantity.SMALL;
    }
    if (value < 0.7) {
      return Quantity.MEDIUM;
    }

    return Quantity.LARGE;
  }

  class IngredientSlider extends HBox {
    private final Label ingredientLabel;
    private final Slider quantitySlider;

    public IngredientSlider(String ingredientName, double defaultValue) {
      ingredientLabel = new Label(ingredientName);
      ingredientLabel.setPrefWidth(70.0);
      quantitySlider = new Slider(0.0, 1.0, defaultValue);
      quantitySlider.setPrefWidth(200.0);
      quantitySlider.setShowTickMarks(true);
      quantitySlider.setMajorTickUnit(0.333); // Distance between ticks
      quantitySlider.setMinorTickCount(0); // no minor ticks
      quantitySlider.setBlockIncrement(0.333); // Distance when keys are used to move the slider
      quantitySlider.setSnapToTicks(true); // No position between ticks is possible
      quantitySlider.setLabelFormatter(new SliderLabeler()); // get text labels instead of double
      // values
      quantitySlider.setShowTickLabels(true); // show the text labels
      quantitySlider.disableProperty().bind(disabled);
      quantitySlider.valueProperty().addListener(((observable, oldValue, newValue) -> getRecipe()));
      getChildren().addAll(ingredientLabel, quantitySlider);
    }

    public double getValue() {
      return quantitySlider.getValue();
    }
  }

  class SliderLabeler extends StringConverter<Double> {

    @Override
    public String toString(final Double n) {
      {
        if (n < 0.2) {
          return "None";
        }
        if (n < 0.4) {
          return "Small";
        }
        if (n < 0.7) {
          return "Medium";
        }

        return "Large";
      }
    }

    @Override
    public Double fromString(final String string) {
      return null;
    }
  }
}

