package de.hhn.it.devtools.apis.turnbasedbattle;

public class DemoTurnBasedBattleUsage {

    public static void main(String[] args) {

        // Create Service
        TurnBasedBattleService service = null;

        // Create Monsters
        Monster monster1 = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE);
        Monster monster2 = new Monster(120, 8, 8, 0.2, 0.2, Element.GRASS);


        service.setupPlayers(monster1, monster2);


        service.start();



//
//        // Create Battle Manager
//        BattleManager battle = new BattleManager(player1.getMonster(), player2.getMonster());
//        battle.startBattle();
//
//        // Demo Battle Loop
//        // We'll simulate a limited number of turns
//        int maxTurns = 20;
//        int turnCount = 0;
//
//        while (!battle.isBattleOver() && turnCount < maxTurns) {
//
//            Monster current = battle.getcurrentMonster();
//
//            Player controllingPlayer;
//            if (current == player1.getMonster()) {
//                controllingPlayer = player1;
//            } else {
//                controllingPlayer = player2;
//            }
//
//            // Player decides what their monster does
//            controllingPlayer.commandMonster(); // demo action
//
//            // Let BattleManager handle switching turns
//            battle.nextTurn();
//
//            turnCount++;
//        }
//
//        // Announce Winner
//        Player winner = battle.getWinner();
//        try {
//            Monster monster =  new Monster(100, 10, 5, 0.2, 0.1, Element.FIRE);
//        } catch (Exception e) {
//            System.out.println("Exception caught: " + e.getClass().getSimpleName());
//            System.out.println("Message: " + e.getMessage());
//            e.printStackTrace(); // optional: volle Stacktrace
//        }
    }
}
