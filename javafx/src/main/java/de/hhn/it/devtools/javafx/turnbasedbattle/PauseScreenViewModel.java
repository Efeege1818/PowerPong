package de.hhn.it.devtools.javafx.turnbasedbattle;

import de.hhn.it.devtools.apis.turnbasedbattle.Element;
import de.hhn.it.devtools.apis.turnbasedbattle.move.Move;
import de.hhn.it.devtools.components.turnbasedbattle.Data;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleBattleManager;
import de.hhn.it.devtools.components.turnbasedbattle.SimpleMonster;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static javafx.application.Application.launch;

public class PauseScreenViewModel {

		private static final Logger logger =
						LoggerFactory.getLogger(PauseScreenViewModel.class);

		private SimpleMonster monster1;
		private SimpleMonster monster2;
		private SimpleMonster shownMonster;
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

		// Optional battle manager reference — if provided we can query current/opponent from it.
		private SimpleBattleManager battleManager;


		public PauseScreenViewModel(SimpleMonster monster, SimpleMonster monster2) {
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

		private void updateShownMonster(SimpleMonster monster) {
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

		public SimpleMonster getShownMonster() {
				return shownMonster;
		}

		public void submit() {
				System.out.println("Submit button clicked");
				// business logic here
				switchShownMonster();
		}

		/**
		 * Expose active buffs from the currently shown monster (defensive copy).
		 */
		public Map<Integer, Move> getActiveBuffs() {
				if (shownMonster != null) {
						return shownMonster.getActiveBuffs();
				}
				if (monster1 != null) {
						return monster1.getActiveBuffs();
				}
				return new HashMap<>();
		}

		/**
		 * Expose active DOTs from the currently shown monster (defensive copy).
		 */
		public Map<Integer, Move> getActiveDots() {
				if (shownMonster != null) {
						return shownMonster.getActiveDots();
				}
				if (monster1 != null) {
						return monster1.getActiveDots();
				}
				return new HashMap<>();
		}

		/**
		 * Try to heuristically split active buffs into "buffs" and "debuffs".
		 * Simple heuristic based on text in name/description; this is defensive
		 * so the UI can show separate sections even if the model stores both
		 * types in a single map. Adjust or replace with explicit debuff support
		 * when the model exposes it.
		 */
		public Map<Integer, Move> getActiveDebuffs() {
				Map<Integer, Move> result = new HashMap<>();
				Map<Integer, Move> allBuffs = getActiveBuffs();
				for (Map.Entry<Integer, Move> e : allBuffs.entrySet()) {
						Move m = e.getValue();
						String combined = (m.name() + " " + m.description()).toLowerCase();
						// simple heuristics for common negative keywords
						if (combined.contains("debuff")
										|| combined.contains("reduce")
										|| combined.contains("lower")
										|| combined.contains("weaken")
										|| combined.contains("slow")
										|| combined.contains("poison")
										|| combined.contains("burn")) {
								result.put(e.getKey(), m);
						}
				}
				return result;
		}

		/**
		 * Get purely positive buffs by filtering out heuristically detected debuffs.
		 */
		public Map<Integer, Move> getActivePositiveBuffs() {
				Map<Integer, Move> positives = new HashMap<>();
				Map<Integer, Move> allBuffs = getActiveBuffs();
				Map<Integer, Move> debuffs = getActiveDebuffs();
				for (Map.Entry<Integer, Move> e : allBuffs.entrySet()) {
						if (!debuffs.containsKey(e.getKey())) {
								positives.put(e.getKey(), e.getValue());
						}
				}
				return positives;
		}

		/**
		 * Attach a SimpleBattleManager so the view model can query the current/opponent monsters.
		 * This is optional — if not set the UI will fallback to the local switchShownMonster() logic.
		 */
		public void setBattleManager(SimpleBattleManager manager) {
				this.battleManager = manager;
		}

		public SimpleBattleManager getBattleManager() {
				return this.battleManager;
		}

		/**
		 * Show the opponent monster from the attached SimpleBattleManager. If no manager is attached,
		 * fallback to toggling between the two known monsters.
		 */
		public void showOpponentFromBattleManager() {
				if (battleManager != null) {
						SimpleMonster opp = battleManager.getOpponentMonster();
						if (opp != null) {
								updateShownMonster(opp);
								return;
						}
						logger.warn("Battle manager returned null opponent monster, falling back to switch");
				}
				// fallback:
				switchShownMonster();
		}

		/**
		 * Show the current monster from the attached SimpleBattleManager. If no manager is attached,
		 * fallback to first monster (monster1).
		 */
		public void showCurrentFromBattleManager() {
				if (battleManager != null) {
						SimpleMonster cur = battleManager.getCurrentMonster();
						if (cur != null) {
								updateShownMonster(cur);
								return;
						}
						logger.warn("Battle manager returned null current monster, falling back to monster1");
				}
				if (monster1 != null) {
						updateShownMonster(monster1);
				}
		}
}