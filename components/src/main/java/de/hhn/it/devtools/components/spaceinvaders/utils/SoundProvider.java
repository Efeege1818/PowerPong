package de.hhn.it.devtools.components.spaceinvaders.utils;

import de.hhn.it.devtools.apis.spaceinvaders.Sound;

import java.util.HashMap;
import java.util.Map;


public class SoundProvider {

  public static final Map<Sound, String> soundFiles = new HashMap<>();

  static {
    soundFiles.put(Sound.SHOOT, "/sounds/shoot.wav");
  }
}
