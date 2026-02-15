package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.MonsterBattleState;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static javafx.application.Application.launch;

public class PauseScreenViewModel {

		private static final Logger logger =
						LoggerFactory.getLogger(PauseScreenViewModel.class);

		private final MonsterBattleState monster1;
		private final MonsterBattleState monster2;
		private MonsterBattleState shownMonster;
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


		public PauseScreenViewModel(MonsterBattleState monster, MonsterBattleState monster2) {
				this.monster1 = monster;
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
				this.monster2= monster2;

				// default shown monster is the first provided monster
				this.shownMonster = monster;
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

		private void updateShownMonster(MonsterBattleState monster) {
				if (monster == null) {
						logger.warn("updateShownMonster called with null — ignoring");
						return;
				}
				this.shownMonster = monster;
				this.monsterName = monster.getName();
				this.maxHp = monster.getMaxHp();
				this.currentHp = monster.getCurrentHp();
				this.atk = monster.getAttack();
				this.def = monster.getDefense();
				this.element = monster.getElement();
				this.moves = monster.getMoves();
				this.focusInfo = monster.getFocus();
				this.passiveInfo = monster.getPassiveInfo();
				this.imagePath = monster.getImagePath();

		}

		public void switchShownMonster() {
				if (shownMonster == monster1) {
						updateShownMonster(monster2);
				} else {
						updateShownMonster(monster1);
				}
		}

		public MonsterBattleState getShownMonster() {
				return shownMonster;
		}

		public void submit() {
				System.out.println("Submit button clicked");
				// business logic here
				switchShownMonster();
		}
}