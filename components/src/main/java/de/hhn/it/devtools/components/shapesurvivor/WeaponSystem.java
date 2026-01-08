package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.helper.EnemyState;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;

import java.util.*;

class WeaponSystem {

    private static final long WEAPON_UPDATE_INTERVAL_MS = 16;
    private static final long ENEMY_HIT_COOLDOWN_MS = 500;

    private static final double SWORD_GRIP_OFFSET = 50;
    private static final double SWORD_BLADE_LENGTH = 90;
    private static final double SWORD_HIT_RADIUS = 20;

    private final GameContext gameContext;
    private final EventDispatcher events;
    private final SimpleShapeSurvivorService service;

    WeaponSystem(GameContext gameContext,
                 EventDispatcher events,
                 SimpleShapeSurvivorService service) {
        this.gameContext = gameContext;
        this.events = events;
        this.service = service;
    }

    void update(long currentTime) {
        updateAnimations(currentTime);
        updateAttacks(currentTime);
    }

    private void updateAnimations(long currentTime) {
        if (currentTime - gameContext.getLastWeaponUpdateTime() < WEAPON_UPDATE_INTERVAL_MS) {
            return;
        }
        gameContext.setLastWeaponUpdateTime(currentTime);

        for (Weapon weapon : gameContext.getPlayer().getWeapons()) {
            WeaponAnimationState state = gameContext.getWeaponStates().get(weapon.type());
            if (state != null) {
                state.update(weapon);
            }
        }
    }

    // ---- Combat ----
    private void updateAttacks(long currentTime) {

        for (Weapon weapon : gameContext.getPlayer().getWeapons()) {
            if (!weapon.isActive()) continue;

            WeaponAnimationState state = gameContext.getWeaponStates().get(weapon.type());
            if (state == null) continue;

            if (weapon.type() == WeaponType.AURA && !state.canDealAuraDamage()) {
                continue;
            }

            boolean shouldAttack = switch (weapon.type()) {
                case SWORD, AURA -> true;
                case WHIP -> {
                    if (state.canAttack()) {
                        state.attack();
                        yield true;
                    }
                    yield false;
                }
            };

            if (!shouldAttack) continue;

            handleHits(weapon, state, currentTime);
        }
    }

    private void handleHits(Weapon weapon,
                            WeaponAnimationState state,
                            long currentTime) {

        List<EnemyState> toRemove = new ArrayList<>();

        for (EnemyState enemy : gameContext.getEnemies()) {

            Long lastHit = gameContext.getLastEnemyHitTime().get(enemy.getId());
            if (lastHit != null && currentTime - lastHit < ENEMY_HIT_COOLDOWN_MS) {
                continue;
            }

            boolean hit = switch (weapon.type()) {
                case SWORD -> checkSwordHit(enemy.toEnemy(), weapon, state);
                case AURA -> checkAuraHit(enemy.toEnemy(), weapon);
                case WHIP -> checkWhipHit(enemy.toEnemy(), weapon, state);
            };

            if (!hit) continue;

            gameContext.getLastEnemyHitTime().put(enemy.getId(), currentTime);

            int damage = weapon.damage() + gameContext.getPlayer().getBaseDamage();

            // --- Update EnemyState in place ---
            enemy.setCurrentHealth(enemy.getCurrentHealth() - damage);

            events.notifyEnemyDamaged(enemy.toEnemy(), damage);

            if (enemy.getCurrentHealth() <= 0) {
                toRemove.add(enemy);
                gameContext.getLastEnemyHitTime().remove(enemy.getId());
                service.gainExperience(enemy.getExperience());
                events.notifyEnemyKilled(enemy.toEnemy(), enemy.getExperience());
            }
        }

        gameContext.getEnemies().removeAll(toRemove);
    }

    private boolean checkSwordHit(Enemy enemy,
                                  Weapon weapon,
                                  WeaponAnimationState state) {

        double angle = state.getAngle();
        Position p = gameContext.getPlayer().getPosition();

        double baseX = p.x() + Math.cos(angle) * (weapon.range() - SWORD_GRIP_OFFSET);
        double baseY = p.y() + Math.sin(angle) * (weapon.range() - SWORD_GRIP_OFFSET);

        double tipX = baseX + Math.cos(angle) * SWORD_BLADE_LENGTH;
        double tipY = baseY + Math.sin(angle) * SWORD_BLADE_LENGTH;

        double distance = distancePointToSegment(
                enemy.position().x(), enemy.position().y(),
                baseX, baseY,
                tipX, tipY
        );

        return distance <= SWORD_HIT_RADIUS;
    }

    private boolean checkAuraHit(Enemy enemy, Weapon weapon) {
        return getDistance(gameContext.getPlayer().getPosition(), enemy.position()) < weapon.range();
    }

    private boolean checkWhipHit(Enemy enemy,
                                 Weapon weapon,
                                 WeaponAnimationState state) {

        if (state.isNotAttacking()) return false;

        Position e = enemy.position();
        Position p = gameContext.getPlayer().getPosition();

        int dx = e.x() - p.x();
        int dy = e.y() - p.y();

        double whipWidth = 80;
        double whipLength = weapon.range();

        return state.isAttackingLeft()
                ? dx < 0 && dx > -whipLength && Math.abs(dy) < whipWidth
                : dx > 0 && dx < whipLength && Math.abs(dy) < whipWidth;
    }

    private double distancePointToSegment(
            double px, double py,
            double x1, double y1,
            double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {
            return Math.hypot(px - x1, py - y1);
        }

        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));

        double cx = x1 + t * dx;
        double cy = y1 + t * dy;

        return Math.hypot(px - cx, py - cy);
    }

    private double getDistance(Position a, Position b) {
        int dx = a.x() - b.x();
        int dy = a.y() - b.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}