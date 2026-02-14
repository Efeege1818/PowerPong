package de.hhn.it.devtools.components.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.*;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class TurnBasedBattleDemo {

    public static void main(String[] args) {
        System.out.println("TurnBasedBattleDemo");

        SimpleTurnBasedBattleService service = new SimpleTurnBasedBattleService();
        System.out.println(service.getGameState());

        SimpleData data = new SimpleData();

        Monster monster1 = data.getMonsters()[1];
        Monster monster2 = data.getMonsters()[2];

        Player player1 = new Player(1, monster1, 0);
        Player player2 = new Player(2, monster2, 0);


        service.setupPlayers(player1, player2, monster1, monster2);

        service.start();

        Random random = new Random();


        Scanner scanner = new Scanner(System.in);
        while (!service.isBattleOver()) {

            System.out.println("Turn: " + service.getTurnCount());

            System.out.print("Enter move index (1-" + service.getCurrentMonster().getMoves().size() + ") or type -1 to see moves: ");
            int y;
            try {
                y = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and " + service.getCurrentMonster().getMoves().size() + " or -1.");
                continue;
            }
            if (y == -1) {
                HashMap<Integer, Move> moves = service.getCurrentMonster().getMoves();
                for (Map.Entry<Integer, Move> entry : moves.entrySet()) {
                    System.out.println(entry.getKey() + " - " + entry.getValue().name() +
                            " (Cooldown: " + service.getCurrentMonster().getRemainingCooldown(entry.getKey()) + " turns)");
                }
            } else if (y >= 1 && y <= service.getCurrentMonster().getMoves().size()) {
                //service.executeTurn(random.nextInt(5) + 1);

                try {
                    service.executeTurn(y);
                } catch (IllegalStateException e) {
                    System.out.println("Enter a different move index.");
                }
            } else {
                System.out.println("Invalid move index. Please enter a number between 1 and " + service.getCurrentMonster().getMoves().size() + " or -1.");
            }


        }

        scanner.close();

        System.out.println("Player " + service.getWinner().playerId() + " won the battle!");



    }
}
