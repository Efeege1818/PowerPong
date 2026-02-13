package de.hhn.it.devtools.components.turnbasedbattle.junit;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.*;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleTurnBasedBattleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestSimpleMonster {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestSimpleMonster.class);

    private SimpleTurnBasedBattleService service;
    private HashMap<Integer, Move> moves;
    private Data data = new Data();
    private SimpleMonster testMonster;
    private SimpleMonster targetMonster;

    @BeforeEach
    void setup() {
        this.service = new SimpleTurnBasedBattleService();
        Move move1 = new AttackMove("Normal attack", Element.NORMAL, 20, false, 0, false, "Normal attack", 1);
        Move move2 = new AttackMove("Strong fire attack", Element.FIRE, 40, false, 1, true, "Strong fire attack", 1);
        Move move3 = new BuffMove("Increase damage", Element.NORMAL, "attack", 30, 3, 2, false, "Increase damage", 1);
        Move move4 = new DebuffMove("Decrease evasion chance", Element.NORMAL, "evasionChance", 0.1, 3, 1, false, "Decrease evasion chance", 1);
        Move move5 = new BuffMove("Increase critical hit chance", Element.FIRE, "critChance", 0.1, 3, 2, true, "Increase critical hit chance", 1);
        HashMap<Integer, Move> testMoves = new HashMap<>();
        testMoves.put(1, move1);
        testMoves.put(2, move2);
        testMoves.put(3, move3);
        testMoves.put(4, move4);
        testMoves.put(5, move5);
        this.moves = testMoves;

        // Create test monsters
        Monster monster1 = new Monster(100, 10, 10, 0.1, 0.1, Element.GRASS, moves);
        this.testMonster = SimpleMonster.create(monster1);

        Monster monster2 = new Monster(100, 10, 10, 0.1, 0.1, Element.WATER, moves);
        this.targetMonster = SimpleMonster.create(monster2);
    }

    // ========== Constructor and Creation Tests ==========

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

    @Test
    @DisplayName("Test creating different element monsters")
    void testCreatingDifferentElementMonsters() {
        Monster fireMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, moves);
        Monster waterMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.WATER, moves);
        Monster grassMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.GRASS, moves);

        SimpleMonster fire = SimpleMonster.create(fireMonster);
        SimpleMonster water = SimpleMonster.create(waterMonster);
        SimpleMonster grass = SimpleMonster.create(grassMonster);

        assertEquals(Element.FIRE, fire.getElement());
        assertEquals(Element.WATER, water.getElement());
        assertEquals(Element.GRASS, grass.getElement());
    }

    // ========== Health Management Tests ==========

    @Test
    @DisplayName("Test addHealth method")
    void testAddHealth() {
        testMonster.takeDotDamage(50);
        assertEquals(50, testMonster.getCurrentHp());

        testMonster.addHealth(30);
        assertEquals(80, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test addHealth does not exceed maxHp")
    void testAddHealthDoesNotExceedMaxHp() {
        testMonster.takeDotDamage(20);
        assertEquals(80, testMonster.getCurrentHp());

        testMonster.addHealth(50);
        assertEquals(100, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test takeDotDamage method")
    void testTakeDotDamage() {
        testMonster.takeDotDamage(30);
        assertEquals(70, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test takeDotDamage does not go below zero")
    void testTakeDotDamageDoesNotGoBelowZero() {
        testMonster.takeDotDamage(150);
        assertEquals(0, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test takeDotDamage with zero damage")
    void testTakeDotDamageWithZero() {
        int initialHp = testMonster.getCurrentHp();
        testMonster.takeDotDamage(0);
        assertEquals(initialHp, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test takeDotDamage with negative damage")
    void testTakeDotDamageWithNegative() {
        int initialHp = testMonster.getCurrentHp();
        testMonster.takeDotDamage(-10);
        assertEquals(initialHp, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test isAlive method")
    void testIsAlive() {
        assertTrue(testMonster.isAlive());

        testMonster.takeDotDamage(100);
        assertFalse(testMonster.isAlive());
    }

    @Test
    @DisplayName("Test isAtFullHealth method")
    void testIsAtFullHealth() {
        assertTrue(testMonster.isAtFullHealth());

        testMonster.takeDotDamage(10);
        assertFalse(testMonster.isAtFullHealth());
    }

    @Test
    @DisplayName("Test resetToFullHealth method")
    void testResetToFullHealth() {
        testMonster.takeDotDamage(50);
        assertEquals(50, testMonster.getCurrentHp());

        testMonster.resetToFullHealth();
        assertEquals(100, testMonster.getCurrentHp());
        assertTrue(testMonster.isAtFullHealth());
    }

    // ========== Buff/Debuff Tests ==========

    @Test
    @DisplayName("Test buffMonster with attack stat")
    void testBuffMonsterAttack() {
        int initialAttack = testMonster.getAttack();
        Move buffMove = new BuffMove("Attack Buff", Element.NORMAL, "attack", 20, 3, 1, false, "Attack Buff", 1);

        testMonster.buffMonster(buffMove);
        assertEquals(initialAttack + 20, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test buffMonster with defense stat")
    void testBuffMonsterDefense() {
        int initialDefense = testMonster.getDefense();
        Move buffMove = new BuffMove("Defense Buff", Element.NORMAL, "defense", 15, 3, 1, false, "Defense Buff", 1);

        testMonster.buffMonster(buffMove);
        assertEquals(initialDefense + 15, testMonster.getDefense());
    }

    @Test
    @DisplayName("Test buffMonster with evasionChance stat")
    void testBuffMonsterEvasionChance() {
        double initialEvasion = testMonster.getEvasionChance();
        Move buffMove = new BuffMove("Evasion Buff", Element.NORMAL, "evasionChance", 0.2, 3, 1, false, "Evasion Buff", 1);

        testMonster.buffMonster(buffMove);
        assertEquals(initialEvasion + 0.2, testMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test buffMonster with critChance stat")
    void testBuffMonsterCritChance() {
        double initialCrit = testMonster.getCritChance();
        Move buffMove = new BuffMove("Crit Buff", Element.NORMAL, "critChance", 0.15, 3, 1, false, "Crit Buff", 1);

        testMonster.buffMonster(buffMove);
        assertEquals(initialCrit + 0.15, testMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test buffMonster with health stat")
    void testBuffMonsterHealth() {
        testMonster.takeDotDamage(30);
        int currentHp = testMonster.getCurrentHp();
        Move buffMove = new BuffMove("Health Buff", Element.NORMAL, "health", 20, 3, 1, false, "Health Buff", 1);

        testMonster.buffMonster(buffMove);
        assertEquals(currentHp + 20, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test debuffMonster with attack stat")
    void testDebuffMonsterAttack() {
        int initialAttack = testMonster.getAttack();
        Move debuffMove = new DebuffMove("Attack Debuff", Element.NORMAL, "attack", 5, 3, 1, false, "Attack Debuff", 1);

        testMonster.debuffMonster(debuffMove);
        assertEquals(initialAttack - 5, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test debuffMonster with defense stat")
    void testDebuffMonsterDefense() {
        int initialDefense = testMonster.getDefense();
        Move debuffMove = new DebuffMove("Defense Debuff", Element.NORMAL, "defense", 5, 3, 1, false, "Defense Debuff", 1);

        testMonster.debuffMonster(debuffMove);
        assertEquals(initialDefense - 5, testMonster.getDefense());
    }

    @Test
    @DisplayName("Test debuffMonster with evasionChance stat")
    void testDebuffMonsterEvasionChance() {
        double initialEvasion = testMonster.getEvasionChance();
        Move debuffMove = new DebuffMove("Evasion Debuff", Element.NORMAL, "evasionChance", 0.05, 3, 1, false, "Evasion Debuff", 1);

        testMonster.debuffMonster(debuffMove);
        assertEquals(initialEvasion - 0.05, testMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test removeBuff method")
    void testRemoveBuff() {
        Move buffMove = new BuffMove("Attack Buff", Element.NORMAL, "attack", 20, 3, 1, false, "Attack Buff", 1);
        int initialAttack = testMonster.getAttack();

        testMonster.buffMonster(buffMove);
        assertEquals(initialAttack + 20, testMonster.getAttack());

        testMonster.removeBuff(buffMove);
        assertEquals(initialAttack, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test removeDebuff method")
    void testRemoveDebuff() {
        Move debuffMove = new DebuffMove("Attack Debuff", Element.NORMAL, "attack", 10, 3, 1, false, "Attack Debuff", 1);
        int initialAttack = testMonster.getAttack();

        testMonster.debuffMonster(debuffMove);
        assertEquals(initialAttack - 10, testMonster.getAttack());

        testMonster.removeDebuff(debuffMove);
        assertEquals(initialAttack, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test addBuffOrDebuff with buff")
    void testAddBuffOrDebuff() {
        Move buffMove = new BuffMove("Attack Buff", Element.NORMAL, "attack", 15, 3, 1, false, "Attack Buff", 1);
        int initialAttack = testMonster.getAttack();

        testMonster.addBuffOrDebuff(buffMove);
        assertEquals(initialAttack + 15, testMonster.getAttack());

        Map<Integer, Move> activeBuffs = testMonster.getActiveBuffs();
        assertEquals(1, activeBuffs.size());
    }

    @Test
    @DisplayName("Test tickBuffs method")
    void testTickBuffs() {
        Move buffMove = new BuffMove("Attack Buff", Element.NORMAL, "attack", 20, 2, 1, false, "Attack Buff", 1);
        int initialAttack = testMonster.getAttack();

        testMonster.addBuffOrDebuff(buffMove);
        assertEquals(1, testMonster.getActiveBuffs().size());

        testMonster.tickBuffs();
        assertEquals(1, testMonster.getActiveBuffs().size());

        testMonster.tickBuffs();
        assertEquals(0, testMonster.getActiveBuffs().size());
        assertEquals(initialAttack, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test getActiveBuffs method")
    void testGetActiveBuffs() {
        Move buffMove1 = new BuffMove("Attack Buff", Element.NORMAL, "attack", 10, 3, 1, false, "Attack Buff", 1);
        Move buffMove2 = new BuffMove("Defense Buff", Element.NORMAL, "defense", 5, 2, 1, false, "Defense Buff", 1);

        testMonster.addBuffOrDebuff(buffMove1);
        testMonster.addBuffOrDebuff(buffMove2);

        Map<Integer, Move> activeBuffs = testMonster.getActiveBuffs();
        assertEquals(2, activeBuffs.size());
    }

    // ========== DOT Tests ==========

    @Test
    @DisplayName("Test addDot method")
    void testAddDot() {
        Move dotMove = new DotMove("Poison", Element.NORMAL, 10, 3, 1, false, "Poison", 1);

        testMonster.addDot(dotMove);
        Map<Integer, Move> activeDots = testMonster.getActiveDots();
        assertEquals(1, activeDots.size());
    }

    @Test
    @DisplayName("Test applyAndTickDots method")
    void testApplyAndTickDots() {
        Move dotMove = new DotMove("Burn", Element.FIRE, 15, 2, 1, false, "Burn", 1);
        int initialHp = testMonster.getCurrentHp();

        testMonster.addDot(dotMove);
        testMonster.applyAndTickDots();

        assertEquals(initialHp - 15, testMonster.getCurrentHp());
        assertEquals(1, testMonster.getActiveDots().size());

        testMonster.applyAndTickDots();
        assertEquals(initialHp - 30, testMonster.getCurrentHp());
        assertEquals(0, testMonster.getActiveDots().size());
    }

    @Test
    @DisplayName("Test getActiveDots method")
    void testGetActiveDots() {
        Move dotMove = new DotMove("Poison", Element.NORMAL, 10, 3, 1, false, "Poison", 1);

        testMonster.addDot(dotMove);
        Map<Integer, Move> activeDots = testMonster.getActiveDots();
        assertEquals(1, activeDots.size());
    }

    // ========== Cooldown Tests ==========

    @Test
    @DisplayName("Test applyCooldown method")
    void testApplyCooldown() {
        Move move = new AttackMove("Strong Attack", Element.FIRE, 40, false, 2, true, "Strong Attack", 1);

        testMonster.applyCooldown(1, move);
        assertTrue(testMonster.isMoveOnCooldown(1));
        assertEquals(2, testMonster.getRemainingCooldown(1));
    }

    @Test
    @DisplayName("Test isMoveOnCooldown method")
    void testIsMoveOnCooldown() {
        Move move = new AttackMove("Strong Attack", Element.FIRE, 40, false, 3, true, "Strong Attack", 1);

        assertFalse(testMonster.isMoveOnCooldown(1));

        testMonster.applyCooldown(1, move);
        assertTrue(testMonster.isMoveOnCooldown(1));
    }

    @Test
    @DisplayName("Test getRemainingCooldown method")
    void testGetRemainingCooldown() {
        Move move = new AttackMove("Strong Attack", Element.FIRE, 40, false, 3, true, "Strong Attack", 1);

        assertEquals(0, testMonster.getRemainingCooldown(1));

        testMonster.applyCooldown(1, move);
        assertEquals(3, testMonster.getRemainingCooldown(1));
    }

    @Test
    @DisplayName("Test tickCooldowns method")
    void testTickCooldowns() {
        Move move = new AttackMove("Strong Attack", Element.FIRE, 40, false, 3, true, "Strong Attack", 1);

        testMonster.applyCooldown(1, move);
        assertEquals(3, testMonster.getRemainingCooldown(1));

        testMonster.tickCooldowns();
        assertEquals(2, testMonster.getRemainingCooldown(1));

        testMonster.tickCooldowns();
        assertEquals(1, testMonster.getRemainingCooldown(1));

        testMonster.tickCooldowns();
        assertEquals(0, testMonster.getRemainingCooldown(1));
        assertFalse(testMonster.isMoveOnCooldown(1));
    }

    @Test
    @DisplayName("Test getMoveCooldowns method")
    void testGetMoveCooldowns() {
        Move move1 = new AttackMove("Strong Attack", Element.FIRE, 40, false, 2, true, "Strong Attack", 1);
        Move move2 = new AttackMove("Super Attack", Element.FIRE, 50, false, 3, true, "Super Attack", 1);

        testMonster.applyCooldown(1, move1);
        testMonster.applyCooldown(2, move2);

        Map<Integer, Integer> cooldowns = testMonster.getMoveCooldowns();
        assertEquals(2, cooldowns.size());
        assertEquals(2, cooldowns.get(1));
        assertEquals(3, cooldowns.get(2));
    }

    // ========== Move Lock Tests ==========

    @Test
    @DisplayName("Test lockMove method")
    void testLockMove() {
        assertFalse(testMonster.isMoveLocked(1));

        testMonster.lockMove(1);
        assertTrue(testMonster.isMoveLocked(1));
    }

    @Test
    @DisplayName("Test unlockMove method")
    void testUnlockMove() {
        testMonster.lockMove(1);
        assertTrue(testMonster.isMoveLocked(1));

        testMonster.unlockMove(1);
        assertFalse(testMonster.isMoveLocked(1));
    }

    @Test
    @DisplayName("Test isMoveLocked method")
    void testIsMoveLocked() {
        assertFalse(testMonster.isMoveLocked(1));

        testMonster.lockMove(1);
        assertTrue(testMonster.isMoveLocked(1));
    }

    // ========== Element Effectiveness Tests ==========

    @Test
    @DisplayName("Test isElementEffective - Fire vs Water")
    void testIsElementEffectiveFireVsWater() {
        Monster fireMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, moves);
        SimpleMonster fire = SimpleMonster.create(fireMonster);

        assertTrue(fire.isElementEffective(fire, Element.WATER));
        assertFalse(fire.isElementEffective(fire, Element.FIRE));
        assertFalse(fire.isElementEffective(fire, Element.GRASS));
    }

    @Test
    @DisplayName("Test isElementEffective - Water vs Grass")
    void testIsElementEffectiveWaterVsGrass() {
        Monster waterMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.WATER, moves);
        SimpleMonster water = SimpleMonster.create(waterMonster);

        assertTrue(water.isElementEffective(water, Element.GRASS));
        assertFalse(water.isElementEffective(water, Element.WATER));
        assertFalse(water.isElementEffective(water, Element.FIRE));
    }

    @Test
    @DisplayName("Test isElementEffective - Grass vs Fire")
    void testIsElementEffectiveGrassVsFire() {
        Monster grassMonster = new Monster(100, 10, 10, 0.1, 0.1, Element.GRASS, moves);
        SimpleMonster grass = SimpleMonster.create(grassMonster);

        assertTrue(grass.isElementEffective(grass, Element.FIRE));
        assertFalse(grass.isElementEffective(grass, Element.GRASS));
        assertFalse(grass.isElementEffective(grass, Element.WATER));
    }

    // ========== Move Management Tests ==========

    @Test
    @DisplayName("Test hasMove method")
    void testHasMove() {
        assertTrue(testMonster.hasMove(1));
        assertTrue(testMonster.hasMove(5));
        assertFalse(testMonster.hasMove(10));
    }

    @Test
    @DisplayName("Test getMove method")
    void testGetMove() {
        Move move = testMonster.getMove(1);
        assertNotNull(move);
        assertEquals("Normal attack", move.name());
    }

    @Test
    @DisplayName("Test getMoves method")
    void testGetMoves() {
        HashMap<Integer, Move> moves = testMonster.getMoves();
        assertNotNull(moves);
        assertEquals(5, moves.size());
    }

    // ========== Stat Management Tests ==========

    @Test
    @DisplayName("Test changeStat with attack")
    void testChangeStatAttack() {
        int initialAttack = testMonster.getAttack();

        testMonster.changeStat("attack", 15);
        assertEquals(initialAttack + 15, testMonster.getAttack());

        testMonster.changeStat("attack", -10);
        assertEquals(initialAttack + 5, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test changeStat with defense")
    void testChangeStatDefense() {
        int initialDefense = testMonster.getDefense();

        testMonster.changeStat("defense", 20);
        assertEquals(initialDefense + 20, testMonster.getDefense());
    }

    @Test
    @DisplayName("Test changeStat with evasionChance")
    void testChangeStatEvasionChance() {
        double initialEvasion = testMonster.getEvasionChance();

        testMonster.changeStat("evasionChance", 0.15);
        assertEquals(initialEvasion + 0.15, testMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test getStat method for all stats")
    void testGetStat() {
        assertEquals(String.valueOf(testMonster.getCurrentHp()), testMonster.getStat("health"));
        assertEquals(String.valueOf(testMonster.getAttack()), testMonster.getStat("attack"));
        assertEquals(String.valueOf(testMonster.getDefense()), testMonster.getStat("defense"));
        assertNotNull(testMonster.getStat("evasionChance"));
        assertNotNull(testMonster.getStat("critChance"));
        assertNotNull(testMonster.getStat("damageReduction"));
    }

    @Test
    @DisplayName("Test getStat with invalid stat")
    void testGetStatInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            testMonster.getStat("invalidStat");
        });
    }

    // ========== Getter Tests ==========

    @Test
    @DisplayName("Test getMaxHp method")
    void testGetMaxHp() {
        assertEquals(100, testMonster.getMaxHp());
    }

    @Test
    @DisplayName("Test getCurrentHp method")
    void testGetCurrentHp() {
        assertEquals(100, testMonster.getCurrentHp());
        testMonster.takeDotDamage(30);
        assertEquals(70, testMonster.getCurrentHp());
    }

    @Test
    @DisplayName("Test getAttack method")
    void testGetAttack() {
        assertEquals(10, testMonster.getAttack());
    }

    @Test
    @DisplayName("Test getDefense method")
    void testGetDefense() {
        assertEquals(10, testMonster.getDefense());
    }

    @Test
    @DisplayName("Test getEvasionChance method")
    void testGetEvasionChance() {
        assertEquals(0.1, testMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test getCritChance method")
    void testGetCritChance() {
        assertEquals(0.1, testMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test getElement method")
    void testGetElement() {
        assertEquals(Element.GRASS, testMonster.getElement());
    }

    @Test
    @DisplayName("Test getDamageReduction method")
    void testGetDamageReduction() {
        assertEquals(0.0, testMonster.getDamageReduction(), 0.001);
    }

    // ========== Poison Tracking Tests ==========

    @Test
    @DisplayName("Test setTimesHitPoison method")
    void testSetTimesHitPoison() {
        testMonster.setTimesHitPoison(5);
        // Note: There's no getter for timesHitPoison, but we can verify it doesn't throw
        assertDoesNotThrow(() -> testMonster.setTimesHitPoison(5));
    }

    @Test
    @DisplayName("Test resetTimesHitPoison method")
    void testResetTimesHitPoison() {
        testMonster.setTimesHitPoison(5);
        testMonster.resetTimesHitPoison();
        assertEquals(0, testMonster.getTimesHitByPoison());
    }

    @Test
    @DisplayName("Test getTimesHitByPoison method")
    void testGetTimesHitByPoison() {
        assertEquals(0, testMonster.getTimesHitByPoison());
    }

    // ========== Tick All Effects Tests ==========

    @Test
    @DisplayName("Test tickAllEffects method")
    void testTickAllEffects() {
        Move buffMove = new BuffMove("Attack Buff", Element.NORMAL, "attack", 10, 2, 1, false, "Attack Buff", 1);
        Move dotMove = new DotMove("Burn", Element.FIRE, 10, 2, 1, false, "Burn", 1);
        Move cooldownMove = new AttackMove("Strong Attack", Element.FIRE, 40, false, 2, true, "Strong Attack", 1);

        testMonster.addBuffOrDebuff(buffMove);
        testMonster.addDot(dotMove);
        testMonster.applyCooldown(1, cooldownMove);

        int initialHp = testMonster.getCurrentHp();

        testMonster.tickAllEffects();

        assertEquals(1, testMonster.getActiveBuffs().size());
        assertEquals(1, testMonster.getActiveDots().size());
        assertEquals(1, testMonster.getRemainingCooldown(1));
        assertEquals(initialHp - 10, testMonster.getCurrentHp());
    }

    // ========== ToString Test ==========

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        String result = testMonster.toString();
        assertNotNull(result);
        assertTrue(result.contains("SimpleMonster"));
        assertTrue(result.contains("HP:"));
        assertTrue(result.contains("ATK:"));
        assertTrue(result.contains("DEF:"));
    }

}
