package de.hhn.it.devtools.apis.towerdefense.junit;



import de.hhn.it.devtools.apis.towerdefense.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApiTestBadCasesTest {

  Tower tower;
  Enemy enemy;
  Grid grid;

  @Test
  void testCreateTowerWithNullReferences() {
    NullPointerException exception = assertThrows(NullPointerException.class, () ->
      tower = new Tower(null, null));
  }

  @Test
  void testCreateEnemyWithNullReferences() {
    NullPointerException exception = assertThrows(NullPointerException.class, () ->
            enemy = new Enemy(null, null, null, 1, 1));
  }

  @Test
  void testCreateGridWithNullReferences() {
    NullPointerException exception = assertThrows(NullPointerException.class, () ->
            grid = new Grid(null));
  }
}
