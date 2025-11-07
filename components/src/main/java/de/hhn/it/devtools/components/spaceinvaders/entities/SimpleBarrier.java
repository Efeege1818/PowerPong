package de.hhn.it.devtools.components.spaceinvaders.entities;

import de.hhn.it.devtools.apis.spaceinvaders.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class SimpleBarrier {

  private static int X = 20, Y = 10;
  private Coordinate start;
  private ArrayList<Coordinate> hitbox;

  public SimpleBarrier(Coordinate start) {
    this.start = start;
    this.hitbox = new ArrayList<>();
  }

  ArrayList<Coordinate> fillHitBox(int x, int y) {
    ArrayList<Coordinate> coords = new ArrayList<>();
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < y; j++) {
        coords.add(new Coordinate(start.x() + i, start.y() + j));
      }
    }
    return coords;
  }

}
