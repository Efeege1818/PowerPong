package de.hhn.it.devtools.apis.shapesurvivor;
import de.hhn.it.devtools.apis.exceptions.IllegalParameterException;
import de.hhn.it.devtools.apis.shapesurvivor.*;
import de.hhn.it.devtools.apis.shapesurvivor.exceptions.IllegalConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoShapeSurvivorUsage {
    private static final Logger logger = LoggerFactory.getLogger(DemoShapeSurvivorUsage.class);
    public static void main(String[] args) throws IllegalConfigurationException {

        ShapeSurvivorService service = null;
        GameConfiguration config = new GameConfiguration(900,
                2000, 2000, 100, 5, 10, 1,
                5, 1, new WeaponType[]{WeaponType.AURA});
        try {
            service.configure(config);
        }catch (IllegalConfigurationException e) {
            e.printStackTrace();
        } catch (IllegalParameterException e) {
            throw new RuntimeException(e);
        }

        ShapeSurvivorListener listener = null;
        boolean listenerAdded = service.addListener(listener);
        logger.info("Listener added: {}", listenerAdded);

        try {
            service.start();
            logger.info("Service started");
        }catch (IllegalStateException e) {
            e.printStackTrace();
        }

        logger.info("Gameplay now");
        try {
            listener.enemyWaveSpawned(1,10);
            listener.updateEnemies(null);

            service.movePlayer(Direction.RIGHT);
            listener.updatePlayer(null);
            service.movePlayer(Direction.LEFT);
            listener.updatePlayer(null);

            //player pauses the Game
            service.pause();
            listener.changedGameState(GameState.PAUSED);
            //player wants to see stats
            service.getStatistics();
            //player continues
            service.resume();
            listener.changedGameState(GameState.RUNNING);

            service.movePlayer(Direction.RIGHT);
            listener.updatePlayer(null);

            //player damages enemies
            listener.enemyDamaged(null,10);
            listener.enemyKilled(null,10);
            listener.updateExperience(10,10);


            //player Aborts or the game is finished
            service.abort();

            //Reset for new game
            service.reset();

        }catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
