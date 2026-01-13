package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.Direction;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;
import de.hhn.it.devtools.components.shapesurvivor.helper.PlayerState;

class PlayerSystem {

  private static final int PLAYER_RADIUS = 15;

  private final GameContext context;
  private final EventDispatcher events;

  PlayerSystem(GameContext context, EventDispatcher events) {
    this.context = context;
    this.events = events;
  }

  /**
   * Moves the player in a single direction.
   *
   * @param direction the direction to move
   */
  void move(Direction direction) {
    PlayerState p = context.getPlayer();
    GameMap map = context.getGameMap();

    int x = p.getPosition().x();
    int y = p.getPosition().y();
    int speed = (int) p.getMovementSpeed();

    Position newPos = switch (direction) {
      case UP -> new Position(x, y - speed);
      case DOWN -> new Position(x, y + speed);
      case LEFT -> new Position(x - speed, y);
      case RIGHT -> new Position(x + speed, y);
    };

    // Check if new position is valid
    if (map.isValidPosition(newPos, PLAYER_RADIUS)) {
      p.setPosition(newPos);
      map.ensureChunksLoaded(newPos);
    } else {
      // Try to adjust to nearest valid position
      Position adjusted = map.adjustToValidPosition(newPos, PLAYER_RADIUS);
      p.setPosition(adjusted);
    }

    events.notifyPlayerUpdated();
  }

  /**
   * Moves the player in multiple directions simultaneously (for diagonal movement).
   * This method normalizes the movement vector to prevent faster diagonal movement.
   *
   * @param directions array of directions to move in
   */
  void moveMultiple(Direction[] directions) {
    if (directions == null || directions.length == 0) {
      return;
    }

    PlayerState p = context.getPlayer();
    GameMap map = context.getGameMap();

    int x = p.getPosition().x();
    int y = p.getPosition().y();
    double speed = (p.getMovementSpeed() * 0.6);

    int dx = 0;
    int dy = 0;

    for (Direction direction : directions) {
      switch (direction) {
        case UP -> dy -= 1;
        case DOWN -> dy += 1;
        case LEFT -> dx -= 1;
        case RIGHT -> dx += 1;
        default -> { }
      }
    }

    // Calculate new position
    int newX = x;
    int newY = y;

    if (dx != 0 && dy != 0) {
      double normalizedSpeed = speed / 1.414;
      newX += (int) (dx * normalizedSpeed);
      newY += (int) (dy * normalizedSpeed);
    } else {
      newX += (int) (dx * speed);
      newY += (int) (dy * speed);
    }

    Position newPos = new Position(newX, newY);

    // Check if new position is valid
    if (map.isValidPosition(newPos, PLAYER_RADIUS)) {
      p.setPosition(newPos);
      map.ensureChunksLoaded(newPos);
    } else {
      // Try to slide along walls
      Position xonly = new Position(newX, y);
      Position yonly = new Position(x, newY);

      if (map.isValidPosition(xonly, PLAYER_RADIUS)) {
        p.setPosition(xonly);
        map.ensureChunksLoaded(xonly);
      } else if (map.isValidPosition(yonly, PLAYER_RADIUS)) {
        p.setPosition(yonly);
        map.ensureChunksLoaded(yonly);
      } else {
        // Stay at current position or adjust
        Position adjusted = map.adjustToValidPosition(newPos, PLAYER_RADIUS);
        if (!adjusted.equals(p.getPosition())) {
          p.setPosition(adjusted);
        }
      }
    }

    events.notifyPlayerUpdated();
  }

  void damage(int rawDamage) {
    PlayerState p = context.getPlayer();

    int actualDamage = (int) (rawDamage * (1 - p.getDamageResistance()));
    p.setCurrentHealth(Math.max(0, p.getCurrentHealth() - actualDamage));

    events.notifyPlayerDamaged(actualDamage);
    events.notifyPlayerUpdated();
  }

  void healToFull() {
    PlayerState p = context.getPlayer();
    p.setCurrentHealth(p.getMaxHealth());
    events.notifyPlayerUpdated();
  }

  void upgradeWeapons(Weapon[] newWeapons) {
    context.getPlayer().setWeapons(newWeapons);
    events.notifyPlayerUpdated();
  }

  void levelUp() {
    PlayerState p = context.getPlayer();
    p.setLevel(p.getLevel() + 1);
    p.setExperience(0);
    p.setExperienceToNextLevel((int) (p.getExperienceToNextLevel() * 1.5));

    events.notifyPlayerLeveledUp();
    events.notifyPlayerUpdated();
  }
}