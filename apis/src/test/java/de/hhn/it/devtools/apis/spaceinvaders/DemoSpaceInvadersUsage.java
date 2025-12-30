package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.entities.*;
public class DemoSpaceInvadersUsage {

  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(DemoSpaceInvadersUsage.class);

  public static void main(String[] args) {
    SpaceInvadersService service = null;

    //Laden der Spielkonfiguration
    GameConfiguration configuration = new GameConfiguration(2, Difficulty.NORMAL);
    try {
      service.configure(configuration);
      service.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    //added Listeners for the service.
    SpaceInvadersListener listener = null;
    service.addListener(listener);


    //now the GameService will configure the game based on this selection and start the game
    service.configure(configuration);
    service.start();
    logger.debug(">>>> Start");

    //the player now moves left and right a bit and tries to shoot the Aliens.
    service.move(Direction.LEFT);
    listener.updateShip(null); //alternative listener.updateEntity()? Either update every Entity at once or everyone by itself
    logger.debug("Callback to update ship");
    logger.debug(">>>> Left");
    service.move(Direction.RIGHT);
    listener.updateShip(null);
    logger.debug("Callback to update ship");
    logger.debug(">>>> Right");
    service.move(Direction.RIGHT);
    listener.updateShip(null);
    logger.debug("Callback to update ship");
    logger.debug(">>>> Right");
    service.shoot();
    listener.updateProjectiles(null);
    logger.debug("Callback to update projectile");
    logger.debug(">>>> Shoot");

    /*
     * After Fighting for hours the Player destroyed all the Aliens and after getting notified by the Field
     * wipes the Field and starts the next round
     */
    service.nextRound();
    listener.updateRound(2);
    logger.debug("Callback to update round");

    //Afterward the player gets so bored that he paused the game and resumes before restarting the game.
    service.pause();
    listener.changedGameState(GameState.PAUSED);
    logger.debug("Callback to update Gamestate");
    logger.debug(">>>> Paused");
    service.resume();
    listener.changedGameState(GameState.RUNNING);
    logger.debug(">>>> Resumed");
    service.pause();
    listener.changedGameState(GameState.PAUSED);
    logger.debug(">>>> Paused");
    service.reset();
    listener.changedGameState(GameState.PREPARED);
    logger.debug(">>>> Reset");


    //The player quits because he doesn't want to do level 2.
    service.pause();
    listener.changedGameState(GameState.PAUSED);
    logger.debug(">>>> Paused");
    service.abort();
    listener.changedGameState(GameState.ABORTED);
    logger.debug(">>>> Quit");
  }

}