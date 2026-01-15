module devtools.components {
    requires devtools.apis;
    requires java.logging;
    requires org.slf4j;

    exports de.hhn.it.devtools.components.example.coffeemakerservice.provider;
    exports de.hhn.it.devtools.components.fourconnect.provider;

    provides de.hhn.it.devtools.apis.examples.coffeemakerservice.CoffeeMakerService
            with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;

    provides de.hhn.it.devtools.apis.examples.coffeemakerservice.AdminCoffeeMakerService
            with de.hhn.it.devtools.components.example.coffeemakerservice.provider.WnckCoffeeMakerService;
}
