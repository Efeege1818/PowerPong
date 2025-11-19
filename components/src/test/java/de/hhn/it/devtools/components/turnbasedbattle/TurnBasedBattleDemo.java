package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;

import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class TurnBasedBattleDemo {

    public static void main(String[] args) {
        System.out.println("TurnBasedBattleDemo");

        TurnBasedBattleService service = new SimpleTurnBasedBattleService();
        System.out.println(service.getGameState());

        // Create Moves
        Move move1 = new Move(MoveType.ATTACK, Element.NORMAL, 20, "health", 0, 0, false, "Normal attack");
        Move move2 = new Move(MoveType.ATTACK, Element.FIRE, 25, "health", 0, 1, false, "Fire attack");
        Move move3 = new Move(MoveType.BUFF, Element.NORMAL, 30, "attack", 3, 2, false, "Increase damage");
        Move move4 = new Move(MoveType.DEBUFF, Element.NORMAL, 0.1, "evasionChance", 3, 1, false, "Decrease evasion chance");
        Move move5 = new Move(MoveType.ATTACK, Element.FIRE, 40, "health", 1, 10, true, "Strong fire attack");
        Move move6 = new Move(MoveType.ATTACK, Element.GRASS, 20, "health", 0, 0, false, "Grass attack");
        Move move7 = new Move(MoveType.BUFF, Element.GRASS, 0.1, "evasionChance",3, 3, false, "Increase evasion chance" );
        Move move8 = new Move(MoveType.DEBUFF, Element.GRASS, 20, "defense", 3, 10, true, "Decrease defense");

        // Create Monsters

        HashMap<Integer, Move> moves1 = new HashMap<>();
        moves1.put(1, move1);
        moves1.put(2, move2);
        moves1.put(3, move3);
        moves1.put(4, move4);
        moves1.put(5, move5);
        Monster monster1 = new Monster(100, 10, 10, 0.1, 0.1, Element.FIRE, moves1);

        HashMap<Integer, Move> moves2 = new HashMap<>();
        moves2.put(1, move1);
        moves2.put(2, move6);
        moves2.put(3, move7);
        moves2.put(4, move4);
        moves2.put(5, move8);
        Monster monster2 = new Monster(120, 8, 8, 0.2, 0.2, Element.GRASS, moves2);

        Player player1 = new Player(1, monster1, 0);
        Player player2 = new Player(2, monster2, 0);


        service.setupPlayers(player1, player2, monster1, monster2);

        service.start();

        Random random = new Random();


        Scanner scanner = new Scanner(System.in);
        // Fight with random moves
        while (!service.isBattleOver()) {

            System.out.println("Turn: " + service.getTurnCount());

            //int y = scanner.nextInt();
            service.executeTurn(random.nextInt(5) + 1);
            //service.executeTurn(y);


        }

        scanner.close();

        System.out.println("Player " + service.getWinner().playerId() + " won the battle!");



    }
}
