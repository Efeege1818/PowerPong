module devtools.javafx {
  requires org.slf4j;
  requires devtools.apis;
  requires devtools.components;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.media;
  requires java.desktop;

  opens de.hhn.it.devtools.javafx.powerpong.view to javafx.fxml;

  exports de.hhn.it.devtools.javafx;
}
