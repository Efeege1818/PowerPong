package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.components.shapesurvivor.helper.EnemyState;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

class EnemySystem {

    private static final long PLAYER_HIT_COOLDOWN_MS = 600;

    private final GameContext ctx;
    private final EventDispatcher events;
    private final SimpleShapeSurvivorService service;

    EnemySystem(GameContext ctx,
                EventDispatcher events,
                SimpleShapeSurvivorService service) {
        this.ctx = ctx;
        this.events = events;
        this.service = service;
    }

    void update(long now) {
        spawnIfNeeded(now);
        updateMovementAndCollisions(now);
    }

    private void spawnIfNeeded(long now) {
        if (now - ctx.lastWaveSpawnTime < 10_000) return;

        ctx.currentWave++;
        int count = (int) (5 * ctx.currentWave * ctx.configuration.enemySpawnRate());

        for (int i = 0; i < count; i++) {
            spawnEnemy();
        }

        ctx.lastWaveSpawnTime = now;
        events.notifyEnemyWaveSpawned(ctx.currentWave, count);
    }

    private void spawnEnemy() {
        Random r = ThreadLocalRandom.current();
        int side = r.nextInt(4);

        int x, y;
        y = switch (side) {
            case 0 -> { x = r.nextInt(ctx.configuration.fieldWidth()); yield -20; }
            case 1 -> { x = ctx.configuration.fieldWidth() + 20; yield r.nextInt(ctx.configuration.fieldHeight()); }
            case 2 -> { x = r.nextInt(ctx.configuration.fieldWidth()); yield ctx.configuration.fieldHeight() + 20; }
            default -> { x = -20; yield r.nextInt(ctx.configuration.fieldHeight()); }
        };

        int hp = (int) (50 * ctx.configuration.difficultyMultiplier());

        Enemy enemy = new Enemy(
                ctx.nextEnemyId++,
                new Position(x, y),
                hp, hp,
                2.0,
                10,
                20
        );

        ctx.enemies.add(new EnemyState(enemy));
    }

    private void updateMovementAndCollisions(long now) {
        Position player = ctx.player.position();

        for (Iterator<EnemyState> it = ctx.enemies.iterator(); it.hasNext();) {
            EnemyState e = it.next();

            int dx = player.x() - e.x;
            int dy = player.y() - e.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 0) {
                e.x += (int) ((dx / dist) * e.speed);
                e.y += (int) ((dy / dist) * e.speed);
            }

            if (dist < 25 && now - ctx.lastPlayerHitTime >= PLAYER_HIT_COOLDOWN_MS) {
                service.damagePlayer(e.contactDamage);
                ctx.lastPlayerHitTime = now;
            }

            if (e.currentHealth <= 0) {
                it.remove();
            }
        }

        events.notifyEnemiesUpdated();
    }
}
