package towerdefense;

import de.hhn.it.devtools.apis.towerdefenseapi.Coordinates;
import de.hhn.it.devtools.apis.towerdefenseapi.Enemy;
import de.hhn.it.devtools.apis.towerdefenseapi.EnemyType;
import de.hhn.it.devtools.apis.towerdefenseapi.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @Test
    void checkPlayerHealthAndMoney(){
        Player player = new Player(10, 20);

        assertEquals(10, player.health());
        assertEquals(20, player.money());
    }

    @Test
    void constructorStoresAllValues() {
        Coordinates pos = new Coordinates(3, 4);
        Coordinates position = new Coordinates(3,4);
        Enemy enemy = new Enemy(1, pos, EnemyType.SMALL, 50, 0);

        assertEquals(1, enemy.id());
        assertEquals(pos, enemy.coordinates());
        assertEquals(EnemyType.SMALL, enemy.type());
        assertEquals(50, enemy.currentHealth());
        assertEquals(0, enemy.index());
    }
}
