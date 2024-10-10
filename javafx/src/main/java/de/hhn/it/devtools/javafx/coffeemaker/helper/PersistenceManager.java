package de.hhn.it.devtools.javafx.coffeemaker.helper;

import de.hhn.it.devtools.apis.examples.coffeemakerservice.ServiceState;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Objects;

/**
 * The PersistenceManager can store a ServiceState object into a file and can load it from there.
 */
public class PersistenceManager {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(PersistenceManager.class);

  private Path path;
  private File file;

  public PersistenceManager(final Path path) {
    Objects.requireNonNull(path);
    this.path = path;
    this.file = path.toFile();

    if (!file.exists()) {
      logger.warn("Persistence file {} does not exist.", file.getAbsolutePath());
    }
  }

  public void save(ServiceState state) throws IOException {
    // Use try statement with resource to have Autoclosable functionality of the resource
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
      out.writeObject(state);
    }
  }

  public ServiceState load() throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
      ServiceState state = (ServiceState) in.readObject();
      return state;
    }
  }

  public boolean doesExist() {
    return file.exists();
  }
}
