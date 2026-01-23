package de.hhn.it.devtools.components.powerpong.provider;

import org.junit.jupiter.api.Assertions;
import de.hhn.it.devtools.apis.powerPong.GameMode;
import de.hhn.it.devtools.apis.powerPong.PlayerInput;
import java.lang.reflect.Field;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PowerPongMatchEngineCoverageTest {

    private PowerPongMatchEngine engine;
    private MockRandom mockRandom;

    // A simple MockRandom to control AI decisions
    static class MockRandom extends Random {
        private double nextDoubleValue = 0.5;
        private int nextIntValue = 0;
        private boolean nextBooleanValue = true;

        @Override
        public double nextDouble() {
            return nextDoubleValue;
        }

        @Override
        public int nextInt(int bound) {
            return nextIntValue % bound;
        }

        @Override
        public boolean nextBoolean() {
            return nextBooleanValue;
        }

        public void setNextDouble(double v) {
            this.nextDoubleValue = v;
        }

        public void setNextInt(int v) {
            this.nextIntValue = v;
        }

        public void setNextBoolean(boolean v) {
            this.nextBooleanValue = v;
        }
    }

    @BeforeEach
    void setUp() {
        mockRandom = new MockRandom();
        engine = new PowerPongMatchEngine(mockRandom);
    }

    @Test
    void testAiMistakeFreeze() throws Exception {
        engine.startGame(GameMode.PLAYER_VS_AI);
        engine.setAiDifficulty(0.0); // Easy mode = high mistake chance (15%)

        // Force mistake trigger: rand < chance (0.15)
        mockRandom.setNextDouble(0.01);

        // Force mistake type 1 (Freeze)
        // Code: aiMistakeType = 1 + random.nextInt(3);
        // We want result 1, so nextInt(3) should return 0.
        mockRandom.setNextInt(0);

        // Move ball towards AI to trigger logic
        PhysicsEngine physics = getPhysics(engine);
        physics.launchBall(1); // Launch right (towards AI)
        PhysicsEngine.Ball ball = physics.getBall();
        ball.vx = 500;

        // Update game
        // Update game enough time to trigger reaction (0.5s delay + margin)
        // Engine uses fixed FRAME_TIME (0.016) for timer decrement, so we need ~30+
        // frames
        for (int i = 0; i < 50; i++) {
            mockRandom.setNextDouble(0.01);
            mockRandom.setNextInt(0);
            engine.updateGame(new PlayerInput(), 0.1);
        }

        // Verify mistake type is 1 (Freeze) via reflection
        Field typeField = PowerPongMatchEngine.class.getDeclaredField("aiMistakeType");
        typeField.setAccessible(true);
        int type = typeField.getInt(engine);
        Assertions.assertEquals(1, type, "AI should be in Freeze mistake mode");
    }

    @Test
    void testAiMistakeWrongDirection() throws Exception {
        engine.startGame(GameMode.PLAYER_VS_AI);
        engine.setAiDifficulty(0.0);

        // Force mistake trigger
        mockRandom.setNextDouble(0.01);

        // Force mistake type 2 (Wrong Dir)
        // 1 + nextInt(3) -> need 2. nextInt must return 1.
        mockRandom.setNextInt(1);

        PhysicsEngine physics = getPhysics(engine);
        physics.launchBall(1);
        physics.getBall().vx = 500;
        physics.getBall().posY = 100; // Ball at top
        physics.setPaddle2Y(300); // Paddle lower

        // Normal AI would move UP (decrease Y).
        // Wrong Direction AI should move DOWN (increase Y).

        for (int i = 0; i < 50; i++) {
            mockRandom.setNextDouble(0.01);
            mockRandom.setNextInt(1);
            engine.updateGame(new PlayerInput(), 0.1);
        }

        Field typeField = PowerPongMatchEngine.class.getDeclaredField("aiMistakeType");
        typeField.setAccessible(true);
        int type = typeField.getInt(engine);
        Assertions.assertEquals(2, type, "AI should be in WrongDirection mistake mode");
    }

    @Test
    void testAiMistakeHesitate() throws Exception {
        engine.startGame(GameMode.PLAYER_VS_AI);
        engine.setAiDifficulty(0.0);

        mockRandom.setNextDouble(0.01);
        // Force mistake type 3 (Hesitate) -> nextInt return 2
        mockRandom.setNextInt(2);

        PhysicsEngine physics = getPhysics(engine);
        physics.launchBall(1);
        physics.getBall().vx = 500;

        for (int i = 0; i < 50; i++) {
            mockRandom.setNextDouble(0.01);
            mockRandom.setNextInt(2); // Hesitate
            engine.updateGame(new PlayerInput(), 0.1);
        }

        Field typeField = PowerPongMatchEngine.class.getDeclaredField("aiMistakeType");
        typeField.setAccessible(true);
        int type = typeField.getInt(engine);
        Assertions.assertEquals(3, type, "AI should be in Hesitate mistake mode");
    }

    @Test
    void testSurvivalRallyScore() throws Exception {
        engine.startGame(GameMode.SURVIVAL);
        PhysicsEngine physics = getPhysics(engine);

        // Force a rally hit count increase
        // We can't easily force physics.rallyHitCount without reflection/update
        // But we can simulate ball bouncing off player

        physics.launchBall(-1);
        physics.getBall().posX = 5; // At player paddle
        physics.getBall().vx = -100;

        // Reflect rallyHitCount in PhysicsEngine
        Field rallyField = PhysicsEngine.class.getDeclaredField("rallyHitCount");
        rallyField.setAccessible(true);
        rallyField.setInt(physics, 1); // Simulate hit

        // Ball must be moving RIGHT (vx > 0) for survival score increase
        physics.getBall().vx = 100;

        engine.updateGame(new PlayerInput(), 0.016);

        Field scoreField = PowerPongMatchEngine.class.getDeclaredField("survivalScore");
        scoreField.setAccessible(true);
        // Expect score 1
        Assertions.assertEquals(1, scoreField.getInt(engine));
    }

    private PhysicsEngine getPhysics(PowerPongMatchEngine engine) throws Exception {
        Field f = PowerPongMatchEngine.class.getDeclaredField("physics");
        f.setAccessible(true);
        return (PhysicsEngine) f.get(engine);
    }
}
