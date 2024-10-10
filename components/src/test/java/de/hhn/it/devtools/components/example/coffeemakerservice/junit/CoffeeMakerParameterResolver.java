package de.hhn.it.devtools.components.example.coffeemakerservice.junit;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerConfiguration;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CoffeeMakerParameterResolver implements ParameterResolver {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(CoffeeMakerParameterResolver.class);

  /**
   * Determine if this resolver supports resolution of an argument for the
   * {@link Parameter} in the supplied {@link ParameterContext} for the supplied
   * {@link ExtensionContext}.
   *
   * <p>The {@link Method} or {@link Constructor}
   * in which the parameter is declared can be retrieved via
   * {@link ParameterContext#getDeclaringExecutable()}.
   *
   * @param parameterContext the context for the parameter for which an argument should
   *                         be resolved; never {@code null}
   * @param extensionContext the extension context for the {@code Executable}
   *                         about to be invoked; never {@code null}
   * @return {@code true} if this resolver can resolve an argument for the parameter
   * @see #resolveParameter
   * @see ParameterContext
   */
  @Override
  public boolean supportsParameter(final ParameterContext parameterContext, final
  ExtensionContext extensionContext) throws ParameterResolutionException {
    Parameter parameter = parameterContext.getParameter();
    return Objects.equals(parameter.getParameterizedType().getTypeName(),
        "java.util.List<de.hhn.it.devtools.apis.examples.coffeemakerservice" +
            ".CoffeeMakerConfiguration>");
  }

  /**
   * Resolve an argument for the {@link Parameter} in the supplied {@link ParameterContext}
   * for the supplied {@link ExtensionContext}.
   *
   * <p>This method is only called by the framework if {@link #supportsParameter}
   * previously returned {@code true} for the same {@link ParameterContext}
   * and {@link ExtensionContext}.
   *
   * <p>The {@link Method} or {@link Constructor}
   * in which the parameter is declared can be retrieved via
   * {@link ParameterContext#getDeclaringExecutable()}.
   *
   * @param parameterContext the context for the parameter for which an argument should
   *                         be resolved; never {@code null}
   * @param extensionContext the extension context for the {@code Executable}
   *                         about to be invoked; never {@code null}
   * @return the resolved argument for the parameter; may only be {@code null} if the
   * parameter type is not a primitive
   * @see #supportsParameter
   * @see ParameterContext
   */
  @Override
  public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext
      extensionContext) throws ParameterResolutionException {
    CoffeeMakerConfiguration configuration1 =
        new CoffeeMakerConfiguration("A317", "Nespresso de Luxe");
    CoffeeMakerConfiguration configuration2 =
        new CoffeeMakerConfiguration("A106", "Senseo muddy brown");
    CoffeeMakerConfiguration configuration3 =
        new CoffeeMakerConfiguration("F141", "Magic Coffee Maker");

    List<CoffeeMakerConfiguration> descriptors = new ArrayList<>();
    descriptors.add(configuration1);
    descriptors.add(configuration2);
    descriptors.add(configuration3);
    return descriptors;
  }
}
