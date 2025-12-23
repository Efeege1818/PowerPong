package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.Monster;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class InfoScreenViewModel {

    private static final Logger logger =
            LoggerFactory.getLogger(InfoScreenViewModel.class);

    private SimpleMonster monster;
    private ImageView imageView;
    private String imagePath;
    private String monsterName;
    private int maxHp;
    private int currentHp;
    private int atk;
    private int def;
    private String focusInfo;
    private String passiveInfo;
    private Element element;
    private Map<Integer, Move> moves = new HashMap<>();
    //may be make an Special move outside the move list or mark it somehow to reffer to it for the UI
    //should have an name and descriptions like the other moves
    String specialMove;


    public InfoScreenViewModel(SimpleMonster monster) {
        this.monster = monster;
        this.monsterName = monster.getName();
        this.maxHp=monster.getMaxHp();
        this.atk=monster.getAttack();
        this.def=monster.getDefense();
        this.element=monster.getElement();
        this.moves=monster.getMoves();
        this.specialMove="SpecialMove";
        this.focusInfo=monster.getFocus();
        this.passiveInfo=monster.getPassiveInfo();
        this.imagePath= monster.getImagePath();
    }


    public String getMonsterName(){
        return monsterName;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public String getFocus() {
        return focusInfo;
    }

    public ImageView getImageView() {
        try {
            ImageView imageView = new ImageView(imagePath);
            return imageView;

        }catch(Exception e){
            return null;
        }
    }

    public String getPassiveInfo() {
        return passiveInfo;
    }

    public Element getElement() {
        return element;
    }

    public Map<Integer, Move> getMoves() {
        return moves;
    }
}
