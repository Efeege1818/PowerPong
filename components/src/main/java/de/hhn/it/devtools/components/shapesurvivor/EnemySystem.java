package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.Enemy;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.components.shapesurvivor.helper.EnemyState;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;

import java.util.Iterator;
import java.util.Random;
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
    if (now - ctx.getLastWaveSpawnTime() < 10_000) {
      return;
    }

    ctx.setCurrentWave(ctx.getCurrentWave() + 1);
    int count = (int) (5 * ctx.getCurrentWave() * ctx.getConfiguration().enemySpawnRate());

    for (int i = 0; i < count; i++) {
      spawnEnemy();
    }

    ctx.setLastWaveSpawnTime(now);
    events.notifyEnemyWaveSpawned(ctx.getCurrentWave(), count);
  }

  private void spawnEnemy() {
    Random r = ThreadLocalRandom.current();
    int side = r.nextInt(4);

    int x;
    int y;
    y = switch (side) {
      case 0 -> {
        x = r.nextInt(ctx.getConfiguration().fieldWidth());
        yield -20;
      }
      case 1 -> {
        x = ctx.getConfiguration().fieldWidth() + 20;
        yield r.nextInt(ctx.getConfiguration().fieldHeight());
      }
      case 2 -> {
        x = r.nextInt(ctx.getConfiguration().fieldWidth());
        yield ctx.getConfiguration().fieldHeight() + 20;
      }
      default -> {
        x = -20;
        yield r.nextInt(ctx.getConfiguration().fieldHeight());
      }
    };

    int hp = (int) (50 * ctx.getConfiguration().difficultyMultiplier());

    Enemy enemy = new Enemy(
            ctx.getNextEnemyId(),
            new Position(x, y),
            hp, hp,
            2.0,
            10,
            20
    );

    ctx.getEnemies().add(new EnemyState(enemy));
    ctx.incrementNextEnemyId();
  }

  private void updateMovementAndCollisions(long now) {
    Position player = ctx.getPlayer().getPosition();

    for (Iterator<EnemyState> it = ctx.getEnemies().iterator(); it.hasNext();) {
      EnemyState e = it.next();

      int dx = player.x() - e.getXpos();
      int dy = player.y() - e.getYpos();
      double dist = Math.sqrt(dx * dx + dy * dy);

      if (dist > 0) {
        e.setXpos(e.getXpos() + (int) ((dx / dist) * e.getSpeed()));
        e.setYpos(e.getYpos() + (int) ((dy / dist) * e.getSpeed()));
      }

      if (dist < 25 && now - ctx.getLastPlayerHitTime() >= PLAYER_HIT_COOLDOWN_MS) {
        service.damagePlayer(e.getContactDamage());
        ctx.setLastPlayerHitTime(now);
      }

      if (e.getCurrentHealth() <= 0) {
        it.remove();
      }
    }

    events.notifyEnemiesUpdated();
  }
}