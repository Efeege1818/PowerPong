package de.hhn.it.devtools.apis.towerDefenseApis;

public interface test {


    public void selectMap();
    public void getRound();
    public void getEnemyType();
    public void getEnemyPath();
    public void getEnemySpeed();


    public void apiMethodsToDo();

    public void startGame();
    public void endGame();

    public void buyTower();
    public void sellTower();
    public void placeTower(); //geplant ist drag and drop
    public void upgradeTower(); // nice-to-have
    public void getTowerAttributes(); //range, dmg
    public void showBuyableTowers();

    public void showMoney();
    public void showHP();

}
