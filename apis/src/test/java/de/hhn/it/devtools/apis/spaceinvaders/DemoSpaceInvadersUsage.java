package de.hhn.it.devtools.apis.spaceinvaders;

import de.hhn.it.devtools.apis.spaceinvaders.exceptions.IllegalConfigurationException;

public class DemoSpaceInvadersUsage {
  private static final org.slf4j.Logger logger =
          org.slf4j.LoggerFactory.getLogger(DemoSpaceInvadersUsage.class);

  public static void main(String[] args) {
    SpaceInvadersService service = new SpaceInvadersService() {
      @Override
      public void reset() {

      }

      @Override
      public void start() throws IllegalStateException {

      }

      @Override
      public void abort() throws IllegalStateException {

      }

      @Override
      public void pause() throws IllegalStateException {

      }

      @Override
      public void resume() throws IllegalStateException {

      }

      @Override
      public void nextRound() throws IllegalStateException {

      }

      @Override
      public void move(Direction direction) throws IllegalStateException {

      }

      @Override
      public void shoot() throws IllegalStateException {

      }

      @Override
      public void playSound(String sound) throws IllegalStateException {

      }

      @Override
      public boolean addListener(SpaceInvadersListener listener) {
        return false;
      }

      @Override
      public boolean removeListener(SpaceInvadersListener listener) {
        return false;
      }

      @Override
      public void configure(GameConfiguration configuration) throws IllegalStateException, IllegalConfigurationException {

      }

      @Override
      public GameConfiguration getConfiguration() {
        return null;
      }
    }

    SpaceInvadersService service2 = Singleton.service

    service.reset();


    //added Listeners for the service.
    SpaceInvadersListener listener = null;
    service.addListener(listener);

    //first thing we will add a new Configuration that will contain our desired difficulty and number of Barriers
    GameConfiguration configuration = new GameConfiguration(3, Difficulty.NORMAL);

    //now the GameService will configure the game based on this selection and start the game
    service.configure(configuration);
    service.start();
    logger.debug(">>>> Start");

    //the player now moves left and right a bit and tries to shoot the Aliens.
    service.moveLeft();
    logger.debug(">>>> Left");
    service.moveRight();
    logger.debug(">>>> Right");
    service.moveRight();
    logger.debug(">>>> Right");
    service.shoot();
    logger.debug(">>>> Shoot");

    /*
     * After Fighting for hours the Player destroyed all the Aliens and after getting notified by the Field
     * wipes the Field and starts the next round
     */
    service.resetField();

    //Afterward the player gets so bored that he paused the game and resumes before restarting the game.
    service.pause();
    logger.debug(">>>> Paused");
    service.resume();
    logger.debug(">>>> Resumed");
    service.pause();
    logger.debug(">>>> Paused");
    service.reset();
    logger.debug(">>>> Reset");


    //The player quits because he doesn't want to do level 2.
    service.pause();
    logger.debug(">>>> Paused");
    service.abort();
    logger.debug(">>>> Quit");
  }
}
