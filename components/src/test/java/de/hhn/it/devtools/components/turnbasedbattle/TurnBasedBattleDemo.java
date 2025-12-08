package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class TurnBasedBattleDemo {

    public static void main(String[] args) {
        System.out.println("TurnBasedBattleDemo");

        TurnBasedBattleService service = new SimpleTurnBasedBattleService();
        System.out.println(service.getGameState());

        Data data = new Data();

        Monster monster1 = data.getMonsters()[1];
        Monster monster2 = data.getMonsters()[0];

        Player player1 = new Player(1, monster1, 0);
        Player player2 = new Player(2, monster2, 0);


        service.setupPlayers(player1, player2, monster1, monster2);

        service.start();

        Random random = new Random();


        Scanner scanner = new Scanner(System.in);
        // Fight with random moves
        while (!service.isBattleOver()) {

            System.out.println("Turn: " + service.getTurnCount());

            System.out.print("Enter move index (1-5) or type -1 to see moves: ");
            int y = scanner.nextInt();
            if (y == -1) {
                HashMap<Integer, Move> moves = service.getCurrentPlayer().monster().moves();
                for (Map.Entry<Integer, Move> entry : moves.entrySet()) {
                    System.out.println(entry.getKey() + " - " + entry.getValue().description());
                }
            } else if (y >= 1 && y <= 5) {
                //service.executeTurn(random.nextInt(5) + 1);
                service.executeTurn(y);
            } else {
                System.out.println("Invalid move index. Please enter a number between 1 and 5 or -1.");
            }


        }

        scanner.close();

        System.out.println("Player " + service.getWinner().playerId() + " won the battle!");



    }
}
