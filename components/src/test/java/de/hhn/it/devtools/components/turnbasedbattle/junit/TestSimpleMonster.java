package de.hhn.it.devtools.components.turnbasedbattle.junit;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.Move;
import de.hhn.it.devtools.apis.turnbasedbattle.MoveType;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestSimpleMonster {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestSimpleMonster.class);

    private SimpleTurnBasedBattleService service;
    private HashMap<Integer, Move> moves;

    @BeforeEach
    void setup() {
        this.service = new SimpleTurnBasedBattleService();
        Move move1 = new Move(MoveType.ATTACK, Element.NORMAL, 20, "health", 0, 0, false, "Normal attack");
        Move move2 = new Move(MoveType.ATTACK, Element.FIRE, 40, "health", 1, 10, true, "Strong fire attack");
        Move move3 = new Move(MoveType.BUFF, Element.NORMAL, 30, "attack", 3, 2, false, "Increase damage");
        Move move4 = new Move(MoveType.DEBUFF, Element.NORMAL, 0.1, "evasionChance", 3, 1, false, "Decrease evasion chance");
        Move move5 = new Move(MoveType.BUFF, Element.FIRE, 0.1, "critChance", 3, 2, true, "Increase critical hit chance");
        HashMap<Integer, Move> testMoves = new HashMap<>();
        testMoves.put(1, move1);
        testMoves.put(2, move2);
        testMoves.put(3, move3);
        testMoves.put(4, move4);
        testMoves.put(5, move5);
        this.moves = testMoves;
    }

    @Test
    @DisplayName("Test creating a SimpleMonster")
    void testCreatingSimpleMonster() {
        Monster monster = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, moves);
        SimpleMonster simpleMonster = SimpleMonster.create(monster);

        assertEquals(100, simpleMonster.getMaxHp());
        assertEquals(100, simpleMonster.getCurrentHp());
        assertEquals(10, simpleMonster.getDefense());
        assertEquals(0.1, simpleMonster.getEvasionChance());
        assertEquals(0.1, simpleMonster.getCritChance());
        assertEquals(Element.FIRE, simpleMonster.getElement());
        assertEquals(5, simpleMonster.getMoves().size());
    }

    @Test
    @DisplayName("Test creating a Monster with empty moves")
    void testCreatingMonsterWithEmptyMoveMap() {
        HashMap<Integer, Move> emptyMoveMap = new HashMap<>();
        assertThrows(IllegalArgumentException.class, () -> {
            Monster monster = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, emptyMoveMap);
        });
    }

    @Test
    @DisplayName("Test creating a Monster with a null move map")
    void testCreatingMonsterWithNullMoveMap() {
        assertThrows(NullPointerException.class, () -> {
            Monster monster = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, null);
        });
    }

//    @Test
//    @DisplayName("Test creating a Monster with a null element")
//    void testCreatingMonsterWithNullElement() {
//        assertThrows(NullPointerException.class, () -> {
//            Monster monster = new Monster(100, 10, 10, 0.1, 0.1, null, moves);
//        });
//    }

    @Test
    @DisplayName("Test creating a Monster with a negative maxHp")
    void testCreatingMonsterWithNegativeMaxHp() {
        assertThrows(IllegalArgumentException.class, () -> {
            Monster monster = new Monster(-100, 10, 10, 0.1, 0.1, Element.FIRE, moves);
        });
    }


}
