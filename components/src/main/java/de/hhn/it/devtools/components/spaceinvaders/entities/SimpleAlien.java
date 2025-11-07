package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;
import de.hhn.it.devtools.apis.spaceinvaders.entities.AlienType;

import java.util.ArrayList;

public class SimpleAlien {
  private static int X = 10;
  static Integer alienIdCounter;
  Coordinate coordinate;
  Integer hitPoints;
  AlienType alienType;
  Integer alienId;
  ArrayList<Coordinate> hitbox;

  public SimpleAlien(Coordinate coordinate, AlienType alienType) {
    this.coordinate = coordinate;
    hitbox = drawHitbox(X);
    this.alienType = alienType;
    this.alienId = alienIdCounter++;
    hitPoints = 3;
  }

  private ArrayList<Coordinate> drawHitbox(int x) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    int j = 0;
    for (int i = 0; i < x; i++) {
      for (j = 0; j < x; j++) {
        coords.add(new Coordinate(coordinate.x() + i, coordinate.y() + j));
      }
    }
    return coords;
  }
}
