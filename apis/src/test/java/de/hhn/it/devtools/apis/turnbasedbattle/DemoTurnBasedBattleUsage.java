package de.hhn.it.devtools.apis.turnbasedbattle;

public class DemoTurnBasedBattleUsage {

    public static void main(String[] args) {

        // Create Monsters
        Monster fireMonster = new FireMonster();
        Monster waterMonster = new WaterMonster(); // assume WaterMonster exists

        // Create Players and assign monsters
        Player player1 = new Player(1, fireMonster);
        Player player2 = new Player(2, waterMonster);

        // Create Battle Manager
        BattleManager battle = new BattleManager(player1.getMonster(), player2.getMonster());
        battle.startBattle();

        // Demo Battle Loop
        // We'll simulate a few turns manually for the demo
        while (!battle.isBattleOver()) {

            Monster current = battle.getcurrentMonster();

            Player controllingPlayer;
            if (current == player1.getMonster()) {
                controllingPlayer = player1;
            } else {
                controllingPlayer = player2;
            }

            // Player decides what their monster does
            controllingPlayer.commandMonster(); // this should call attack/buff/debuff on the monster

            // Let BattleManager handle switching turns
            battle.nextTurn();
        }

        // Announce Winner
        Player winner = battle.getWinner();
    }
}
