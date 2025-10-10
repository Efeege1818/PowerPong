package de.hhn.it.devtools.apis.turnbasedbattle;

public class DemoTurnBasedBattleUsage {

    public static void main(String[] args) {
        TurnBasedBattleService service = null;

        TurnBasedBattleListener listener = null;

        FireMonster player1 = new FireMonster();
        WaterMonster player2 = new WaterMonster();

        int dmg = player1.elementalAttack();
        player1.takeDamage(dmg);

        player2.buff();

        player1.debuff();
        player2.takeDebuff("test", 1); //TODO: How to get value from player1 input

        dmg = player2.normalAttack();
        player1.takeDamage(dmg);
    }
}
