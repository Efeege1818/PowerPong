package de.hhn.it.devtools.components.turnbasedbattle.junit;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.AttackMove;
import de.hhn.it.devtools.apis.turnbasedbattle.move.BuffMove;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.monster.WaterMonster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TestWaterMonster {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(TestWaterMonster.class);

    private WaterMonster waterMonster;
    private HashMap<Integer, Move> moves;

    @BeforeEach
    void setup() {
        Move move1 = new AttackMove("Water Gun", Element.WATER, 20, false, 0, false, "Water Gun", 1);
        Move move2 = new AttackMove("Hydro Pump", Element.WATER, 40, false, 1, true, "Hydro Pump", 1);
        Move move3 = new BuffMove("Aqua Shield", Element.WATER, "defense", 15, 3, 2, false, "Aqua Shield", 1);
        Move move4 = new AttackMove("Tidal Wave", Element.WATER, 35, false, 2, false, "Tidal Wave", 1);
        Move move5 = new AttackMove("Special Move", Element.WATER, 50, false, 3, true, "Special Move", 1);

        HashMap<Integer, Move> testMoves = new HashMap<>();
        testMoves.put(1, move1);
        testMoves.put(2, move2);
        testMoves.put(3, move3);
        testMoves.put(4, move4);
        testMoves.put(5, move5);
        this.moves = testMoves;

        Monster monster = new Monster(100, 15, 12, 0.1, 0.15, Element.WATER, moves);
        this.waterMonster = new WaterMonster(monster);
    }

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Test creating a WaterMonster")
    void testCreatingWaterMonster() {
        Monster monster = new Monster(120, 18, 14, 0.12, 0.18, Element.WATER, moves);
        WaterMonster water = new WaterMonster(monster);

        assertEquals(120, water.getMaxHp());
        assertEquals(120, water.getCurrentHp());
        assertEquals(18, water.getAttack());
        assertEquals(14, water.getDefense());
        assertEquals(0.12, water.getEvasionChance(), 0.001);
        assertEquals(0.18, water.getCritChance(), 0.001);
        assertEquals(Element.WATER, water.getElement());
        assertEquals("Water Monster", water.getName());
        assertEquals("Manipulates Dodge and Crit chances", water.getFocus());
    }

    @Test
    @DisplayName("Test WaterMonster has correct name")
    void testWaterMonsterName() {
        assertEquals("Water Monster", waterMonster.getName());
    }

    @Test
    @DisplayName("Test WaterMonster has correct element")
    void testWaterMonsterElement() {
        assertEquals(Element.WATER, waterMonster.getElement());
    }

    @Test
    @DisplayName("Test WaterMonster has correct focus")
    void testWaterMonsterFocus() {
        assertEquals("Manipulates Dodge and Crit chances", waterMonster.getFocus());
    }

    @Test
    @DisplayName("Test WaterMonster has passive info")
    void testWaterMonsterPassiveInfo() {
        String passiveInfo = waterMonster.getPassiveInfo();
        assertNotNull(passiveInfo);
        assertTrue(passiveInfo.contains("Water Flow"));
        assertTrue(passiveInfo.contains("crit"));
        assertTrue(passiveInfo.contains("dodge"));
    }

    @Test
    @DisplayName("Test WaterMonster move 5 is initially locked")
    void testMove5InitiallyLocked() {
        assertTrue(waterMonster.isMoveLocked(5));
    }

    @Test
    @DisplayName("Test WaterMonster starts in attack stance")
    void testInitialStance() {
        assertTrue(waterMonster.isAttackStance());
        assertFalse(waterMonster.isDefenseStance());
    }

    // ========== Stance Switching Tests ==========

    @Test
    @DisplayName("Test switchStance changes from attack to defense")
    void testSwitchStanceToDefense() {
        assertTrue(waterMonster.isAttackStance());
        assertFalse(waterMonster.isDefenseStance());

        waterMonster.switchStance();

        assertFalse(waterMonster.isAttackStance());
        assertTrue(waterMonster.isDefenseStance());
    }

    @Test
    @DisplayName("Test switchStance changes from defense to attack")
    void testSwitchStanceToAttack() {
        waterMonster.switchStance(); // Switch to defense
        assertTrue(waterMonster.isDefenseStance());

        waterMonster.switchStance(); // Switch back to attack
        assertTrue(waterMonster.isAttackStance());
        assertFalse(waterMonster.isDefenseStance());
    }

    @Test
    @DisplayName("Test switchStance multiple times")
    void testSwitchStanceMultipleTimes() {
        for (int i = 0; i < 5; i++) {
            boolean expectedAttack = (i % 2 == 0);
            assertEquals(expectedAttack, waterMonster.isAttackStance());
            assertEquals(!expectedAttack, waterMonster.isDefenseStance());
            waterMonster.switchStance();
        }
    }

    @Test
    @DisplayName("Test switchStance resets crit passive stacks")
    void testSwitchStanceResetsCritStacks() {
        double initialCritChance = waterMonster.getCritChance();

        // Simulate gaining crit stacks in attack stance
        waterMonster.handleCriticalHit();
        waterMonster.handleCriticalHit();
        waterMonster.handleCriticalHit();

        // Crit chance should have increased
        assertTrue(waterMonster.getCritChance() > initialCritChance);

        // Switch stance should reset crit stacks
        waterMonster.switchStance();

        // Crit chance should be back to initial
        assertEquals(initialCritChance, waterMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test switchStance resets defense passive stacks")
    void testSwitchStanceResetsDefenseStacks() {
        waterMonster.switchStance(); // Switch to defense stance
        double initialEvasion = waterMonster.getEvasionChance();

        // Simulate gaining defense stacks in defense stance
        waterMonster.handleDodge();
        waterMonster.handleDodge();
        waterMonster.handleDodge();

        // Evasion chance should have increased
        assertTrue(waterMonster.getEvasionChance() > initialEvasion);

        // Switch stance should reset defense stacks
        waterMonster.switchStance();

        // Evasion chance should be back to initial
        assertEquals(initialEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    // ========== Critical Hit Passive Tests ==========

    @Test
    @DisplayName("Test handleCriticalHit increases crit chance in attack stance")
    void testHandleCriticalHitInAttackStance() {
        double initialCritChance = waterMonster.getCritChance();

        waterMonster.handleCriticalHit();

        double expectedCritChance = initialCritChance + 0.01;
        assertEquals(expectedCritChance, waterMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test handleCriticalHit does not increase crit chance in defense stance")
    void testHandleCriticalHitInDefenseStance() {
        waterMonster.switchStance(); // Switch to defense stance
        double initialCritChance = waterMonster.getCritChance();

        waterMonster.handleCriticalHit();

        assertEquals(initialCritChance, waterMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test handleCriticalHit stacks multiple times")
    void testHandleCriticalHitMultipleStacks() {
        double initialCritChance = waterMonster.getCritChance();

        for (int i = 1; i <= 5; i++) {
            waterMonster.handleCriticalHit();
            double expectedCritChance = initialCritChance + (i * 0.01);
            assertEquals(expectedCritChance, waterMonster.getCritChance(), 0.001);
        }
    }

    @Test
    @DisplayName("Test handleCriticalHit caps at 20 stacks")
    void testHandleCriticalHitCapsAt20Stacks() {
        double initialCritChance = waterMonster.getCritChance();

        // Add 25 crit hits (should cap at 20)
        for (int i = 0; i < 25; i++) {
            waterMonster.handleCriticalHit();
        }

        double expectedCritChance = initialCritChance + (20 * 0.01);
        assertEquals(expectedCritChance, waterMonster.getCritChance(), 0.001);
    }

    // ========== Dodge Passive Tests ==========

    @Test
    @DisplayName("Test handleDodge increases evasion chance in defense stance")
    void testHandleDodgeInDefenseStance() {
        waterMonster.switchStance(); // Switch to defense stance
        double initialEvasion = waterMonster.getEvasionChance();

        waterMonster.handleDodge();

        double expectedEvasion = initialEvasion + 0.015;
        assertEquals(expectedEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test handleDodge does not increase evasion chance in attack stance")
    void testHandleDodgeInAttackStance() {
        double initialEvasion = waterMonster.getEvasionChance();

        waterMonster.handleDodge();

        assertEquals(initialEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test handleDodge stacks multiple times")
    void testHandleDodgeMultipleStacks() {
        waterMonster.switchStance(); // Switch to defense stance
        double initialEvasion = waterMonster.getEvasionChance();

        for (int i = 1; i <= 5; i++) {
            waterMonster.handleDodge();
            double expectedEvasion = initialEvasion + (i * 0.015);
            assertEquals(expectedEvasion, waterMonster.getEvasionChance(), 0.001);
        }
    }

    @Test
    @DisplayName("Test handleDodge caps at 20 stacks")
    void testHandleDodgeCapsAt20Stacks() {
        waterMonster.switchStance(); // Switch to defense stance
        double initialEvasion = waterMonster.getEvasionChance();

        // Add 25 dodges (should cap at 20)
        for (int i = 0; i < 25; i++) {
            waterMonster.handleDodge();
        }

        double expectedEvasion = initialEvasion + (20 * 0.015);
        assertEquals(expectedEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    // ========== Special Move Unlock Tests ==========

    @Test
    @DisplayName("Test special move progress starts at 0/9")
    void testInitialSpecialProgress() {
        assertEquals("0/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test handleCriticalHit increases special progress in attack stance")
    void testCriticalHitIncreasesSpecialProgress() {
        assertEquals("0/9", waterMonster.getSpecialProgress());

        waterMonster.handleCriticalHit();

        assertEquals("1/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test handleDodge increases special progress in defense stance")
    void testDodgeIncreasesSpecialProgress() {
        waterMonster.switchStance(); // Switch to defense stance
        assertEquals("0/9", waterMonster.getSpecialProgress());

        waterMonster.handleDodge();

        assertEquals("1/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test special move unlocks at 9 stacks via critical hits")
    void testSpecialMoveUnlocksAt9StacksViaCrits() {
        assertTrue(waterMonster.isMoveLocked(5));

        for (int i = 0; i < 9; i++) {
            waterMonster.handleCriticalHit();
        }

        assertFalse(waterMonster.isMoveLocked(5));
        assertEquals("0/9", waterMonster.getSpecialProgress()); // Should reset
    }

    @Test
    @DisplayName("Test special move unlocks at 9 stacks via dodges")
    void testSpecialMoveUnlocksAt9StacksViaDodges() {
        waterMonster.switchStance(); // Switch to defense stance
        assertTrue(waterMonster.isMoveLocked(5));

        for (int i = 0; i < 9; i++) {
            waterMonster.handleDodge();
        }

        assertFalse(waterMonster.isMoveLocked(5));
        assertEquals("0/9", waterMonster.getSpecialProgress()); // Should reset
    }

    @Test
    @DisplayName("Test special move unlocks with mixed crits and dodges")
    void testSpecialMoveUnlocksWithMixedActions() {
        assertTrue(waterMonster.isMoveLocked(5));

        // 5 crits in attack stance
        for (int i = 0; i < 5; i++) {
            waterMonster.handleCriticalHit();
        }
        assertEquals("5/9", waterMonster.getSpecialProgress());

        // Switch to defense and 4 dodges
        waterMonster.switchStance();
        for (int i = 0; i < 4; i++) {
            waterMonster.handleDodge();
        }

        assertFalse(waterMonster.isMoveLocked(5));
        assertEquals("0/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test special progress does not increase when move is unlocked")
    void testSpecialProgressDoesNotIncreaseWhenUnlocked() {
        // Unlock the move
        for (int i = 0; i < 9; i++) {
            waterMonster.handleCriticalHit();
        }
        assertFalse(waterMonster.isMoveLocked(5));
        assertEquals("0/9", waterMonster.getSpecialProgress());

        // Try to increase progress again
        waterMonster.handleCriticalHit();

        // Progress should still be 0/9 since move is unlocked
        assertEquals("0/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test special progress resets after unlocking and relocking")
    void testSpecialProgressResetsAfterRelocking() {
        // Unlock the move
        for (int i = 0; i < 9; i++) {
            waterMonster.handleCriticalHit();
        }
        assertFalse(waterMonster.isMoveLocked(5));

        // Lock it again
        waterMonster.lockMove(5);
        assertTrue(waterMonster.isMoveLocked(5));

        // Progress should start from 0 again
        assertEquals("0/9", waterMonster.getSpecialProgress());

        waterMonster.handleCriticalHit();
        assertEquals("1/9", waterMonster.getSpecialProgress());
    }

    @Test
    @DisplayName("Test special progress does not increase beyond threshold")
    void testSpecialProgressDoesNotExceedThreshold() {
        for (int i = 0; i < 15; i++) {
            waterMonster.handleCriticalHit();
        }

        // After unlocking, progress should be 0/9, not continue counting
        assertEquals("0/9", waterMonster.getSpecialProgress());
    }

    // ========== Stance and Passive Interaction Tests ==========

    @Test
    @DisplayName("Test switching stance loses all crit stacks but keeps evasion")
    void testSwitchingFromAttackLosesCritStacks() {
        double initialCrit = waterMonster.getCritChance();
        double initialEvasion = waterMonster.getEvasionChance();

        // Gain crit stacks
        waterMonster.handleCriticalHit();
        waterMonster.handleCriticalHit();
        waterMonster.handleCriticalHit();

        assertTrue(waterMonster.getCritChance() > initialCrit);

        // Switch to defense
        waterMonster.switchStance();

        // Crit should be reset, evasion unchanged
        assertEquals(initialCrit, waterMonster.getCritChance(), 0.001);
        assertEquals(initialEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test switching stance loses all defense stacks but keeps crit")
    void testSwitchingFromDefenseLosesDefenseStacks() {
        waterMonster.switchStance(); // Switch to defense
        double initialCrit = waterMonster.getCritChance();
        double initialEvasion = waterMonster.getEvasionChance();

        // Gain defense stacks
        waterMonster.handleDodge();
        waterMonster.handleDodge();
        waterMonster.handleDodge();

        assertTrue(waterMonster.getEvasionChance() > initialEvasion);

        // Switch to attack
        waterMonster.switchStance();

        // Evasion should be reset, crit unchanged
        assertEquals(initialEvasion, waterMonster.getEvasionChance(), 0.001);
        assertEquals(initialCrit, waterMonster.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test alternating stances maintains correct passive behavior")
    void testAlternatingStances() {
        double initialCrit = waterMonster.getCritChance();
        double initialEvasion = waterMonster.getEvasionChance();

        // Attack stance - gain crit
        waterMonster.handleCriticalHit();
        assertTrue(waterMonster.getCritChance() > initialCrit);

        // Switch to defense - lose crit
        waterMonster.switchStance();
        assertEquals(initialCrit, waterMonster.getCritChance(), 0.001);

        // Defense stance - gain evasion
        waterMonster.handleDodge();
        assertTrue(waterMonster.getEvasionChance() > initialEvasion);

        // Switch to attack - lose evasion
        waterMonster.switchStance();
        assertEquals(initialEvasion, waterMonster.getEvasionChance(), 0.001);
    }

    // ========== Edge Cases and Integration Tests ==========

    @Test
    @DisplayName("Test WaterMonster inherits SimpleMonster functionality")
    void testInheritsSimpleMonsterFunctionality() {
        // Test basic health management
        waterMonster.takeDotDamage(30);
        assertEquals(70, waterMonster.getCurrentHp());

        waterMonster.addHealth(20);
        assertEquals(90, waterMonster.getCurrentHp());

        // Test alive status
        assertTrue(waterMonster.isAlive());
    }

    @Test
    @DisplayName("Test WaterMonster with zero initial evasion and crit")
    void testWaterMonsterWithZeroChances() {
        Monster monster = new Monster(100, 10, 10, 0.0, 0.0, Element.WATER, moves);
        WaterMonster water = new WaterMonster(monster);

        assertEquals(0.0, water.getEvasionChance(), 0.001);
        assertEquals(0.0, water.getCritChance(), 0.001);

        // Should still be able to gain stacks
        water.handleCriticalHit();
        assertEquals(0.01, water.getCritChance(), 0.001);

        water.switchStance();
        water.handleDodge();
        assertEquals(0.015, water.getEvasionChance(), 0.001);
    }

    @Test
    @DisplayName("Test WaterMonster with high initial stats")
    void testWaterMonsterWithHighStats() {
        Monster monster = new Monster(200, 50, 40, 0.5, 0.5, Element.WATER, moves);
        WaterMonster water = new WaterMonster(monster);

        assertEquals(200, water.getMaxHp());
        assertEquals(50, water.getAttack());
        assertEquals(40, water.getDefense());
        assertEquals(0.5, water.getEvasionChance(), 0.001);
        assertEquals(0.5, water.getCritChance(), 0.001);
    }

    @Test
    @DisplayName("Test special progress format is correct")
    void testSpecialProgressFormat() {
        String progress = waterMonster.getSpecialProgress();
        assertTrue(progress.matches("\\d+/\\d+"));
        assertTrue(progress.contains("/9"));
    }

    @Test
    @DisplayName("Test WaterMonster toString contains relevant information")
    void testToString() {
        String result = waterMonster.toString();
        assertNotNull(result);
        assertTrue(result.contains("SimpleMonster"));
        assertTrue(result.contains("HP:"));
        assertTrue(result.contains("WATER"));
    }

    @Test
    @DisplayName("Test passive stacks persist across multiple actions in same stance")
    void testPassiveStacksPersistInSameStance() {
        double initialCrit = waterMonster.getCritChance();

        // Multiple crits in attack stance
        waterMonster.handleCriticalHit();
        double afterFirst = waterMonster.getCritChance();
        waterMonster.handleCriticalHit();
        double afterSecond = waterMonster.getCritChance();
        waterMonster.handleCriticalHit();
        double afterThird = waterMonster.getCritChance();

        // Each should increase
        assertTrue(afterFirst > initialCrit);
        assertTrue(afterSecond > afterFirst);
        assertTrue(afterThird > afterSecond);

        // Total increase should be 3 * 0.01
        assertEquals(initialCrit + 0.03, afterThird, 0.001);
    }

    @Test
    @DisplayName("Test WaterMonster has correct image paths")
    void testImagePaths() {
        assertNotNull(waterMonster.getImagePath());
        assertTrue(waterMonster.getImagePath().contains("WasserMon"));
        assertNotNull(waterMonster.getImagePathBack());
        assertTrue(waterMonster.getImagePathBack().contains("WasserMon Back"));
    }
}
