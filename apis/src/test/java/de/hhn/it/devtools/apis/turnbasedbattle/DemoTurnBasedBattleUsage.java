package de.hhn.it.devtools.apis.turnbasedbattle;

public class DemoTurnBasedBattleUsage {

    public static void main(String[] args) {
        TurnBasedBattleService service = null;

        TurnBasedBattleListener listener = null;


        //Two monsters are created
        FireMonster player1 = new FireMonster();
        WaterMonster player2 = new WaterMonster();

        BattleManager battle = new BattleManager(player1, player2);
        battle.startBattle();

        //Player 1 executes an elemental attack
        int dmg = player1.elementalAttack();
        player2.takeDamage(dmg);

        //Player 2 uses a buff on themselves
        player2.buff();

        //Player 1 inflicts a debuff on player 2
        Debuff dbf = player1.debuff();
        player2.takeDebuff(dbf.stat(), dbf.value());

        //Player 2 damages player 1 with a normal attack
        dmg = player2.normalAttack();
        player1.takeDamage(dmg);
    }
}
