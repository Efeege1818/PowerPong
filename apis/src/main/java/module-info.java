/**
 * Module descriptor for the apis module.
 */
module devtools.apis{
        requires org.slf4j;
        requires transitive javafx.graphics;
        exports de.hhn.it.devtools.apis.examples.coffeemakerservice;
        exports de.hhn.it.devtools.apis.exceptions;
        exports de.hhn.it.devtools.apis.powerPong;
        }
