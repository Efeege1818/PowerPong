package de.hhn.it.devtools.components.powerPong.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.hhn.it.devtools.apis.powerPong.PaddleState;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PhysicsEngineTest {

    private PhysicsEngine physics;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(123); // Fixed seed for deterministic tests
        physics = new PhysicsEngine(random);
    }

    @Test
    void testInitialState() {
        physics.reset();
        PaddleState left = physics.getLeftPaddleState();
        PaddleState right = physics.getRightPaddleState();

        assertEquals(PhysicsEngine.FIELD_HEIGHT / 2.0, left.yPosition(), 0.001);
        assertEquals(PhysicsEngine.LEFT_PADDLE_X, left.xPosition(), 0.001);
        assertEquals(PhysicsEngine.PADDLE_WIDTH, left.width(), 0.001);

        assertEquals(PhysicsEngine.FIELD_HEIGHT / 2.0, right.yPosition(), 0.001);
        assertEquals(PhysicsEngine.RIGHT_PADDLE_X, right.xPosition(), 0.001);
        assertEquals(PhysicsEngine.PADDLE_WIDTH, right.width(), 0.001);
        assertTrue(physics.getBallStates().isEmpty());
    }

    @Test
    void testLaunchBall() {
        physics.launchBall(1);
        assertNotNull(physics.getBall());
        assertEquals(PhysicsEngine.FIELD_WIDTH / 2.0, physics.getBall().x, 0.001);
        assertTrue(physics.getBall().vx > 0);
        assertEquals(PhysicsEngine.BALL_RADIUS, physics.getBallStates().get(0).radius(), 0.001);
    }

    @Test
    void testScoring() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();

        // Place ball past right edge and out of paddle range
        ball.x = PhysicsEngine.FIELD_WIDTH + PhysicsEngine.BALL_RADIUS + 1;
        ball.y = 0;
        ball.vx = 100; // Moving right

        int result = physics.updateBalls(0.016);
        assertEquals(1, result); // Player 1 scored

        // Place ball past left edge
        physics.launchBall(1);
        ball = physics.getBall();
        ball.x = -PhysicsEngine.BALL_RADIUS - 1;
        ball.y = 0;
        ball.vx = -100; // Moving left

        result = physics.updateBalls(0.016);
        assertEquals(2, result); // Player 2 scored
    }

    @Test
    void testSecondaryBall() {
        physics.launchBall(1);
        physics.spawnSecondaryBall();
        assertNotNull(physics.getSecondaryBall());
        assertEquals(2, physics.getBallStates().size());

        physics.removeSecondaryBall();
        assertEquals(1, physics.getBallStates().size());
    }

    @Test
    void testNoWalls() {
        physics.launchBall(1);
        physics.setNoWalls(true);
        PhysicsEngine.Ball ball = physics.getBall();

        // Move ball above top
        ball.y = -10;
        ball.vy = -100;

        physics.updateBalls(0.016);

        // Should wrap to bottom
        assertTrue(ball.y > PhysicsEngine.FIELD_HEIGHT - 50);
    }

    @Test
    void testScaleVelocity() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();
        double initialVx = ball.vx;

        physics.scaleBallVelocity(2.0);
        assertEquals(initialVx * 2.0, ball.vx, 0.001);
    }

    @Test
    void testSetBallVelocity() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();
        physics.setBallVelocity(ball, 100, 200);
        assertEquals(100, ball.vx, 0.001);
        assertEquals(200, ball.vy, 0.001);
    }

    @Test
    void testPaddleMovementClamping() {
        physics.reset();

        // Try to move paddle past top boundary
        for (int i = 0; i < 100; i++) {
            physics.movePaddle(true, -1.0, 0.1);
        }

        // Should be clamped at minimum
        PaddleState left = physics.getLeftPaddleState();
        assertTrue(left.yPosition() >= left.height() / 2.0);

        // Try to move paddle past bottom boundary
        for (int i = 0; i < 200; i++) {
            physics.movePaddle(true, 1.0, 0.1);
        }

        // Should be clamped at maximum
        assertTrue(left.yPosition() <= PhysicsEngine.FIELD_HEIGHT - left.height() / 2.0);
    }

    @Test
    void testUpdateBallsNoBall() {
        physics.reset();
        int result = physics.updateBalls(0.016);
        assertEquals(0, result); // No scoring if no ball
    }

    @Test
    void testResetModifiers() {
        physics.launchBall(1);
        physics.setLeftHeightFactor(2.0);
        physics.setRightSpeedFactor(0.5);
        physics.setNoWalls(true);

        physics.resetModifiers();

        // Modifiers should be reset to default
        PaddleState left = physics.getLeftPaddleState();
        assertEquals(PhysicsEngine.PADDLE_HEIGHT, left.height(), 0.001);
    }

    @Test
    void testBallPaddleCollision() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();

        // Position ball to collide with left paddle
        double paddleY = physics.getLeftPaddleState().yPosition();
        ball.x = 50; // Near left side
        ball.y = paddleY; // At paddle height
        ball.vx = -100; // Moving towards paddle

        // Update should detect collision
        physics.updateBalls(0.016);

        // Ball should have bounced (vx flipped to positive)
        assertTrue(ball.vx > 0);
    }

    @Test
    void testBallPaddleCollisionRightPaddle() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();

        // Position ball to collide with right paddle
        double paddleY = physics.getRightPaddleState().yPosition();
        ball.x = PhysicsEngine.FIELD_WIDTH - 50; // Near right side
        ball.y = paddleY; // At paddle height
        ball.vx = 100; // Moving towards right paddle

        // Update should detect collision
        physics.updateBalls(0.016);

        // Ball should have bounced (vx flipped to negative)
        assertTrue(ball.vx < 0);
    }

    @Test
    void testDifficultyScaling() {
        physics.launchBall(1);
        PhysicsEngine.Ball ball = physics.getBall();
        double initialVx = ball.vx;

        physics.setDifficultyMultiplier(1.5);

        // Velocity should be updated immediately or on next update/launch?
        // implementation check: setDifficultyMultiplier calls
        // updateBallVelocityForDifficulty
        // which updates existing balls speed.

        // Speed magnitude should increase
        double initialSpeed = Math.abs(initialVx);
        double newSpeed = Math.hypot(ball.vx, ball.vy);

        assertTrue(newSpeed > initialSpeed);
        assertEquals(1.5, physics.getDifficultyMultiplier(), 0.001);
    }

    @Test
    void testExplicitSetters() {
        physics.setLeftHeightFactor(2.0);
        physics.setRightHeightFactor(0.5);
        physics.setLeftSpeedFactor(1.5);
        physics.setRightSpeedFactor(0.8);

        // No direct getters for factors, but we can verify effect on PaddleState or
        // internal state if exposed
        // Or simply verify no exceptions thrown and simple state checks if possible.
        // The resetModifiers test already verifies they do *something* (restore to
        // 1.0).
        // Here we just ensure setters work as API points.

        // Indirect verification via paddle height
        // physics.reset(); // Don't reset, as it clears modifiers!
        assertEquals(PhysicsEngine.PADDLE_HEIGHT * 2.0, physics.getLeftPaddleState().height(), 0.001);
        assertEquals(PhysicsEngine.PADDLE_HEIGHT * 0.5, physics.getRightPaddleState().height(), 0.001);
    }

    @Test
    void testGetPaddle2Y() {
        physics.reset();
        assertEquals(PhysicsEngine.FIELD_HEIGHT / 2.0, physics.getPaddle2Y(), 0.001);
    }

    @Test
    void testGetBaseBallSpeed() {
        assertTrue(physics.getBaseBallSpeed() > 0);
    }
}