module devtools.components {
  exports de.hhn.it.devtools.components.powerpong.provider;

  requires org.slf4j;
  requires devtools.apis;
  requires java.desktop;
  requires java.logging;

  provides de.hhn.it.devtools.apis.powerPong.PowerPongService
      with de.hhn.it.devtools.components.powerpong.provider.PowerPongMatchEngine;
}
