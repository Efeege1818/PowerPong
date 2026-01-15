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
  private static final int SPAWN_DISTANCE_MIN = 300;
  private static final int SPAWN_DISTANCE_MAX = 400;

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

    Position playerPos = ctx.getPlayer().getPosition();

    double angle = r.nextDouble() * 2 * Math.PI;
    int distance = SPAWN_DISTANCE_MIN + r.nextInt(SPAWN_DISTANCE_MAX - SPAWN_DISTANCE_MIN);

    int x = playerPos.x() + (int) (Math.cos(angle) * distance);
    int y = playerPos.y() + (int) (Math.sin(angle) * distance);

    int hp = (int) (50 * ctx.getConfiguration().difficultyMultiplier());
    int statMulitpliertime = 1;

    if(service.getElapsedTime() >= 300 && service.getElapsedTime() < 600)
    {
      statMulitpliertime = 2;
    }else if(service.getElapsedTime()>= 600){
      statMulitpliertime = 4;
    }
    Enemy enemy = new Enemy(
            ctx.getNextEnemyId(),
            new Position(x, y),
            hp * statMulitpliertime, hp * statMulitpliertime,
            2.0,
            10 * statMulitpliertime,
            10
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