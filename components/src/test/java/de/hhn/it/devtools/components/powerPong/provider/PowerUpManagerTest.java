package de.hhn.it.devtools.components.powerPong.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hhn.it.devtools.apis.powerPong.PaddleState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PowerUpManagerTest {

    private PowerUpManager manager;
    private PhysicsEngine physics;
    private MockRandom random;

    @BeforeEach
    void setUp() {
        random = new MockRandom();
        physics = new PhysicsEngine(random);
        manager = new PowerUpManager(physics, random);
    }

    @Test
    void testSpawnPowerUp() {
        manager.reset();
        manager.update(10.0);
        assertEquals(1, manager.getPowerUpStates().size());
        assertEquals(12.0, manager.getPowerUpStates().get(0).radius(), 0.001);
    }

    @Test
    void testCollectBiggerPaddle() {
        testPowerUpEffect(PowerUpType.BIGGER_PADDLE, () -> {
            PaddleState left = physics.getLeftPaddleState();
            assertTrue(left.height() > PhysicsEngine.PADDLE_HEIGHT);
        });
    }

    @Test
    void testCollectSmallerEnemyPaddle() {
        // If player 1 collects, player 2 (enemy) should shrink
        testPowerUpEffect(PowerUpType.SMALLER_ENEMY_PADDLE, () -> {
            PaddleState right = physics.getRightPaddleState();
            assertTrue(right.height() < PhysicsEngine.PADDLE_HEIGHT);
        });
    }

    @Test
    void testCollectDoubleBall() {
        testPowerUpEffect(PowerUpType.DOUBLE_BALL, () -> {
            assertNotNull(physics.getSecondaryBall());
        });
    }

    @Test
    void testCollectBarrierless() {
        testPowerUpEffect(PowerUpType.BARRIERLESS, () -> {
            // Move ball to top
            PhysicsEngine.Ball ball = physics.getBall();
            ball.y = -10;
            ball.vy = -100;
            physics.updateBalls(0.016);
            // Should wrap to bottom (approx FIELD_HEIGHT)
            assertTrue(ball.y > PhysicsEngine.FIELD_HEIGHT - 50);
        });
    }

    @Test
    void testRevertEffects() {
        // Apply BIGGER_PADDLE
        testPowerUpEffect(PowerUpType.BIGGER_PADDLE, () -> {
            PaddleState left = physics.getLeftPaddleState();
            assertTrue(left.height() > PhysicsEngine.PADDLE_HEIGHT);
        });

        // Update for duration + 1
        manager.update(9.0); // Duration is 8.0

        // Should be reverted
        PaddleState left = physics.getLeftPaddleState();
        assertEquals(PhysicsEngine.PADDLE_HEIGHT, left.height(), 0.001);
    }

    @Test
    void testConsumeShield() {
        // Apply SHIELD
        testPowerUpEffect(PowerUpType.SHIELD, () -> {
            // Player 1 collected shield, so left paddle has shield
            assertTrue(manager.hasShield(1));
            manager.consumeShield(1);
            assertFalse(manager.hasShield(1)); // Should be consumed
        });
    }

    @Test
    void testSlowEnemyPaddle() {
        testPowerUpEffect(PowerUpType.SLOW_ENEMY_PADDLE, () -> {
            // Player 2 should be slow
            // Move player 2 paddle and check distance
            double initialY = physics.getRightPaddleState().yPosition();
            physics.movePaddle(false, 1.0, 0.1);
            double moved = Math.abs(physics.getRightPaddleState().yPosition() - initialY);

            // Normal move: BASE_SPEED * 1.0 * 0.1
            // Slow move: BASE_SPEED * 0.5 * 1.0 * 0.1
            double expected = 400.0 * 0.5 * 0.1;
            assertEquals(expected, moved, 0.001);
        });
    }

    @Test
    void testFasterBallEnemySide() {
        testPowerUpEffect(PowerUpType.FASTER_BALL_ENEMY_SIDE, () -> {
            // Player 1 collected, ball should be faster on enemy (right) side
            PhysicsEngine.Ball ball = physics.getBall();

            // Move ball to right side (enemy for player 1)
            ball.x = PhysicsEngine.FIELD_WIDTH * 0.75;
            ball.y = PhysicsEngine.FIELD_HEIGHT / 2.0;

            // Update to trigger the faster ball logic
            manager.update(0.1);

            // Ball speed should be scaled up
            double speed = Math.hypot(ball.vx, ball.vy);
            assertTrue(speed > physics.getBaseBallSpeed());
        });
    }

    @Test
    void testNoSpawnBeforeInterval() {
        manager.reset();
        // Update for slightly less than spawn interval (6.0)
        manager.update(5.9);
        assertTrue(manager.getPowerUpStates().isEmpty(), "No powerup should spawn before 6.0 seconds");

        // Update just past the interval
        manager.update(0.2);
        assertEquals(1, manager.getPowerUpStates().size(), "Powerup should spawn after 6.0 seconds");
    }

    private void testPowerUpEffect(PowerUpType type, Runnable assertion) {
        manager.reset();
        physics.reset();

        // Setup random to return specific type
        int typeIndex = -1;
        PowerUpType[] values = PowerUpType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == type) {
                typeIndex = i;
                break;
            }
        }
        random.setNextInt(typeIndex);

        manager.update(10.0); // Spawn
        var pup = manager.getPowerUpStates().get(0);

        // Teleport ball to powerup
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();
        // Ensure ball is owned by player 1 (x < width/2)
        ball.x = pup.xPosition();
        ball.y = pup.yPosition();

        manager.update(0.1); // Collect

        assertion.run();
    }

    private void assertNotNull(Object o) {
        if (o == null)
            throw new AssertionError("Expected not null");
    }

    // Mock Random to control powerup type
    static class MockRandom extends Random {
        private int nextIntVal = 0;

        public void setNextInt(int val) {
            this.nextIntVal = val;
        }

        @Override
        public int nextInt(int bound) {
            return nextIntVal % bound;
        }

        @Override
        public double nextDouble() {
            return 0.25; // Left side
        }
    }
}
