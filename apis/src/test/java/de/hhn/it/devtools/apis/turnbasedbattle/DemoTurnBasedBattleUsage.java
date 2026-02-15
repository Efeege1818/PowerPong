package de.hhn.it.devtools.apis.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.move.*;

import java.util.HashMap;

public class DemoTurnBasedBattleUsage {

    public static void main(String[] args) {

        // Create Service
//        TurnBasedBattleService service = null;
//
//        // Create Moves
//        Move move1 = new AttackMove("Normal attack", Element.NORMAL, 20, 0, false, "Normal attack", 1);
//        Move move2 = new AttackMove("Fire attack", Element.FIRE, 25, 1, false, "Fire attack", 1);
//        Move move3 = new BuffMove("Attack buff", Element.NORMAL, "attack", 30, 3, 2, false, "Increase attack of your monster", 1);
//        Move move4 = new DebuffMove("Evasion debuff", Element.NORMAL, "evasionChance", 0.1, 3, 2, false, "Decrease evasion chance of your monster", 1);
//        Move move5 = new AttackMove("Strong fire attack", Element.FIRE, 40, 1, true, "Strong fire attack", 1);
//        Move move6 = new AttackMove("Grass attack", Element.GRASS, 20, 1, false, "Grass attack", 1);
//        Move move7 = new BuffMove("Evasion buff", Element.GRASS, "evasionChance", 0.1, 3, 3, false, "Increase evasion chance of your monster", 1);
//        DebuffMove move8 = new DebuffMove("Defense debuff", Element.GRASS, "defense", 10, 10, 1, true, "Decrease defense of your monster", 1);
//
//        // Create Monsters
//
//        HashMap<Integer, Move> moves1 = new HashMap<>();
//        moves1.put(1, move1);
//        moves1.put(2, move2);
//        moves1.put(3, move3);
//        moves1.put(4, move4);
//        moves1.put(5, move5);
//        Monster monster1 = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, moves1);
//
//        HashMap<Integer, Move> moves2 = new HashMap<>();
//        moves2.put(1, move1);
//        moves2.put(2, move6);
//        moves2.put(3, move7);
//        moves2.put(4, move4);
//        moves2.put(5, move8);
//        Monster monster2 = new Monster(120, 8, 8, 0.2, 0.2, Element.GRASS, moves2);
//
//        Player player1 = new Player(1, monster1, 0);
//        Player player2 = new Player(2, monster2, 0);
//
//
//        service.setupPlayers(player1, player2, monster1, monster2);
//
//
//        service.start();
//
//        int maxTurns = 20;
//        int turnCount = 0;
//
//        while (!service.isBattleOver() && turnCount < maxTurns) {
//
//            service.executeTurn(0);
//            service.nextTurn();
//
//            turnCount++;
//        }
//
//        // Announce Winner
//        service.getWinner();



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
