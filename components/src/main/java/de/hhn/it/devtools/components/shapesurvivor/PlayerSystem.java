package de.hhn.it.devtools.components.shapesurvivor;

import de.hhn.it.devtools.apis.shapesurvivor.Direction;
import de.hhn.it.devtools.apis.shapesurvivor.Position;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;
import de.hhn.it.devtools.components.shapesurvivor.helper.EventDispatcher;
import de.hhn.it.devtools.components.shapesurvivor.helper.PlayerState;

class PlayerSystem {

  private final GameContext context;
  private final EventDispatcher events;

  PlayerSystem(GameContext context, EventDispatcher events) {
    this.context = context;
    this.events = events;
  }

  void move(Direction direction) {
    PlayerState p = context.getPlayer();

    int x = p.getPosition().x();
    int y = p.getPosition().y();
    int speed = (int) p.getMovementSpeed();

    switch (direction) {
      case UP -> y -= speed;
      case DOWN -> y += speed;
      case LEFT -> x -= speed;
      case RIGHT -> x += speed;
      default -> { }
    }

    x = Math.max(0, Math.min(x, context.getConfiguration().fieldWidth()));
    y = Math.max(0, Math.min(y, context.getConfiguration().fieldHeight()));

    p.setPosition(new Position(x, y));
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