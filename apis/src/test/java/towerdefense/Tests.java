package towerdefense;

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

}
