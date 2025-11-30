package de.hhn.it.devtools.components.powerPong.provider;

import de.hhn.it.devtools.apis.powerPong.PowerUpState;
import de.hhn.it.devtools.apis.powerPong.PowerUpType;
import de.hhn.it.devtools.components.powerPong.provider.PhysicsEngine.Ball;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Manages power-up spawning, collection, and effects for PowerPong.
 * Handles power-up lifecycle including spawning, collision detection with
 * balls,
 * applying effects, and reverting effects after duration.
 *
 * This class is responsible for:
 * - Spawning power-ups at regular intervals
 * - Detecting collisions between balls and power-ups
 * - Applying power-up effects to the physics engine
 * - Managing timed effects and reverting them after expiration
 * - Handling shield power-ups that block scoring
 */
public class PowerUpManager {

    private static final double POWERUP_SPAWN_INTERVAL = 6.0; // seconds
    private static final double POWERUP_RADIUS = 12.0;
    private static final double EFFECT_DURATION_SECONDS = 8.0;
    private static final double ENLARGE_FACTOR = 1.4;
    private static final double SHRINK_FACTOR = 0.65;
    private static final double SLOW_FACTOR = 0.5;
    private static final double FAST_BALL_FACTOR = 1.35;

    private final Random random;
    private final PhysicsEngine physics;

    private final List<FieldPowerUp> powerUps = new ArrayList<>();
    private final List<ActiveEffect> activeEffects = new ArrayList<>();
    private double spawnTimer;

    private boolean leftShield;
    private boolean rightShield;

    public PowerUpManager(PhysicsEngine physics, Random random) {
        this.physics = physics;
        this.random = random;
    }

    public void reset() {
        powerUps.clear();
        activeEffects.clear();
        spawnTimer = 0;
        leftShield = false;
        rightShield = false;
    }

    public void update(double deltaSeconds) {
        spawnTimer += deltaSeconds;
        maybeSpawnPowerUp();
        handlePowerUpCollisions();
        updateEffects(deltaSeconds);
        applyContinuousEffects();
    }

    private void maybeSpawnPowerUp() {
        if (spawnTimer < POWERUP_SPAWN_INTERVAL || powerUps.size() >= 3) {
            return;
        }
        spawnTimer -= POWERUP_SPAWN_INTERVAL;
        double margin = PhysicsEngine.BALL_RADIUS * 2;
        double x = margin + random.nextDouble() * (PhysicsEngine.FIELD_WIDTH - 2 * margin);
        double y = margin + random.nextDouble() * (PhysicsEngine.FIELD_HEIGHT - 2 * margin);
        PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
        powerUps.add(new FieldPowerUp(x, y, type));
    }

    private void handlePowerUpCollisions() {
        if (powerUps.isEmpty()) {
            return;
        }
        List<FieldPowerUp> collected = new ArrayList<>();
        Ball ball = physics.getBall();
        Ball secondaryBall = physics.getSecondaryBall();

        for (FieldPowerUp powerUp : powerUps) {
            if (collides(ball, powerUp)) {
                int owner = ball.x < PhysicsEngine.FIELD_WIDTH / 2.0 ? 1 : 2;
                applyPowerUp(owner, powerUp.type);
                collected.add(powerUp);
            } else if (secondaryBall != null && collides(secondaryBall, powerUp)) {
                int owner = secondaryBall.x < PhysicsEngine.FIELD_WIDTH / 2.0 ? 1 : 2;
                applyPowerUp(owner, powerUp.type);
                collected.add(powerUp);
            }
        }
        powerUps.removeAll(collected);
    }

    private boolean collides(Ball candidate, FieldPowerUp powerUp) {
        if (candidate == null) {
            return false;
        }
        double dx = candidate.x - powerUp.x;
        double dy = candidate.y - powerUp.y;
        double radius = PhysicsEngine.BALL_RADIUS + POWERUP_RADIUS;
        return dx * dx + dy * dy <= radius * radius;
    }

    private void applyPowerUp(int owner, PowerUpType type) {
        switch (type) {
            case BIGGER_PADDLE -> {
                if (owner == 1)
                    physics.setLeftHeightFactor(ENLARGE_FACTOR);
                else
                    physics.setRightHeightFactor(ENLARGE_FACTOR);
                addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            }
            case SMALLER_ENEMY_PADDLE -> {
                if (owner == 1)
                    physics.setRightHeightFactor(SHRINK_FACTOR);
                else
                    physics.setLeftHeightFactor(SHRINK_FACTOR);
                addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            }
            case SLOW_ENEMY_PADDLE -> {
                if (owner == 1)
                    physics.setRightSpeedFactor(SLOW_FACTOR);
                else
                    physics.setLeftSpeedFactor(SLOW_FACTOR);
                addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            }
            case BARRIERLESS -> {
                physics.setNoWalls(true);
                addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            }
            case DOUBLE_BALL -> {
                physics.spawnSecondaryBall();
                addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            }
            case FASTER_BALL_ENEMY_SIDE -> addTimedEffect(type, owner, EFFECT_DURATION_SECONDS);
            case SHIELD -> {
                if (owner == 1)
                    leftShield = true;
                else
                    rightShield = true;
            }
        }
    }

    private void addTimedEffect(PowerUpType type, int owner, double duration) {
        Iterator<ActiveEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            ActiveEffect running = iterator.next();
            if (running.type == type && running.owner == owner) {
                iterator.remove();
                break;
            }
        }
        activeEffects.add(new ActiveEffect(type, owner, duration));
    }

    private void updateEffects(double deltaSeconds) {
        Iterator<ActiveEffect> iterator = activeEffects.iterator();
        while (iterator.hasNext()) {
            ActiveEffect effect = iterator.next();
            effect.remaining -= deltaSeconds;
            if (effect.remaining <= 0) {
                revertEffect(effect);
                iterator.remove();
            }
        }
    }

    private void revertEffect(ActiveEffect effect) {
        switch (effect.type) {
            case BIGGER_PADDLE -> {
                if (effect.owner == 1)
                    physics.setLeftHeightFactor(1.0);
                else
                    physics.setRightHeightFactor(1.0);
            }
            case SMALLER_ENEMY_PADDLE -> {
                if (effect.owner == 1)
                    physics.setRightHeightFactor(1.0);
                else
                    physics.setLeftHeightFactor(1.0);
            }
            case SLOW_ENEMY_PADDLE -> {
                if (effect.owner == 1)
                    physics.setRightSpeedFactor(1.0);
                else
                    physics.setLeftSpeedFactor(1.0);
            }
            case BARRIERLESS -> physics.setNoWalls(false);
            case DOUBLE_BALL -> physics.removeSecondaryBall();
            default -> {
            }
        }
    }

    private void applyContinuousEffects() {
        double half = PhysicsEngine.FIELD_WIDTH / 2.0;
        Ball ball = physics.getBall();
        applyFastBallLogic(ball, half);

        Ball secBall = physics.getSecondaryBall();
        applyFastBallLogic(secBall, half);
    }

    private void applyFastBallLogic(Ball ball, double halfWidth) {
        if (ball == null)
            return;

        double multiplier = 1.0;
        for (ActiveEffect effect : activeEffects) {
            if (effect.type == PowerUpType.FASTER_BALL_ENEMY_SIDE) {
                if (effect.owner == 1 && ball.x > halfWidth) {
                    multiplier = FAST_BALL_FACTOR;
                    break;
                }
                if (effect.owner == 2 && ball.x < halfWidth) {
                    multiplier = FAST_BALL_FACTOR;
                    break;
                }
            }
        }

        double speed = Math.hypot(ball.vx, ball.vy);
        if (speed == 0)
            return;

        double target = physics.getBaseBallSpeed() * multiplier;
        // Only adjust if significantly different to avoid jitter
        if (Math.abs(speed - target) > 1.0) {
            double factor = target / speed;
            ball.vx *= factor;
            ball.vy *= factor;
        }
    }

    public boolean hasShield(int player) {
        return player == 1 ? leftShield : rightShield;
    }

    public void consumeShield(int player) {
        if (player == 1)
            leftShield = false;
        else
            rightShield = false;

        // Reset effects when shield is consumed (as per original logic
        // resetAfterShield)
        activeEffects.clear();
        physics.resetModifiers();
        physics.removeSecondaryBall();
    }

    public List<PowerUpState> getPowerUpStates() {
        List<PowerUpState> states = new ArrayList<>();
        for (FieldPowerUp p : powerUps) {
            states.add(new PowerUpState(p.x, p.y, p.type));
        }
        return states;
    }

    private static class FieldPowerUp {
        final double x;
        final double y;
        final PowerUpType type;

        FieldPowerUp(double x, double y, PowerUpType type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    private static class ActiveEffect {
        final PowerUpType type;
        final int owner;
        double remaining;

        ActiveEffect(PowerUpType type, int owner, double duration) {
            this.type = type;
            this.owner = owner;
            this.remaining = duration;
        }
    }
}

