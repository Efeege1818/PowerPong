package de.hhn.it.devtools.javafx.shapesurvivor.view;

import de.hhn.it.devtools.apis.shapesurvivor.Player;
import de.hhn.it.devtools.apis.shapesurvivor.Weapon;
import de.hhn.it.devtools.apis.shapesurvivor.WeaponType;
import de.hhn.it.devtools.components.shapesurvivor.WeaponAnimationState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class WeaponRenderer {

    private Map<WeaponType, WeaponAnimationState> animationStates;


    public WeaponRenderer() {
        this.animationStates = new HashMap<>();
    }

    public void setAnimationStates(Map<WeaponType, WeaponAnimationState> states) {
        this.animationStates = states;
    }
    /**
     * Renders all equipped weapons for the player.
     */
    public void renderWeapons(GraphicsContext gc, Player player) {
        for (Weapon weapon : player.equippedWeapons()) {
            WeaponAnimationState state = animationStates.get(weapon.type());
            if (state == null) continue;

            switch (weapon.type()) {
                case SWORD -> renderSword(gc, player, weapon, state);
                case AURA -> renderAura(gc, player, weapon, state);
                case WHIP -> renderWhip(gc, player, weapon, state);
            }
        }
    }

    /**
     * Clears animation states.
     */
    public void reset() {
        animationStates.clear();
    }

    /**
     * Initializes animation states for new weapons.
     */
    public void initializeWeapon(WeaponType type) {
        animationStates.putIfAbsent(type, new WeaponAnimationState());
    }


    private void renderSword(GraphicsContext gc, Player player, Weapon weapon, WeaponAnimationState state) {
        double angle = state.getAngle();

        int px = player.position().x();
        int py = player.position().y();

        double gripOffset = 50;
        double radius = weapon.range() - gripOffset;

        double sx = px + Math.cos(angle) * radius;
        double sy = py + Math.sin(angle) * radius;

        double bladeLength = 90;
        double bladeWidth = 6;
        double handleLength = 10;
        double handleWidth = 8;
        double guardWidth = 16;
        double guardHeight = 4;

        gc.save();
        gc.translate(sx, sy);
        gc.rotate(Math.toDegrees(angle) + 90);

        // Blade
        gc.setFill(Color.SILVER);
        gc.fillRect(-bladeWidth / 2, -bladeLength, bladeWidth, bladeLength);

        // Blade highlight
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);
        gc.strokeLine(0, -bladeLength, 0, 0);

        // Crossguard
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(-guardWidth / 2, 0, guardWidth, guardHeight);

        // Handle
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(-handleWidth / 2, guardHeight, handleWidth, handleLength);

        // Pommel
        gc.setFill(Color.GOLD);
        gc.fillOval(
                -handleWidth / 2,
                guardHeight + handleLength,
                handleWidth,
                handleWidth
        );

        gc.restore();
    }

    private void renderAura(GraphicsContext gc, Player player, Weapon weapon, WeaponAnimationState state) {
        double pulse = Math.sin(state.getAngle() * 3) * 10 + weapon.range();

        gc.setStroke(Color.rgb(100, 200, 255, 0.3));
        gc.setLineWidth(3);
        gc.strokeOval(
                player.position().x() - pulse,
                player.position().y() - pulse,
                pulse * 2,
                pulse * 2
        );
    }

    private void renderWhip(GraphicsContext gc, Player player, Weapon weapon, WeaponAnimationState state) {
        if (!state.isAttacking()) return;

        double progress = Math.min(1.0, state.getAttackProgress() / 300.0);
        boolean isLeft = state.isAttackingLeft();

        int whipLength = (int) (weapon.range() * progress);
        int whipWidth = 80;

        int startX = player.position().x();
        int startY = player.position().y();
        int endX = startX + (isLeft ? -whipLength : whipLength);

        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(6);

        for (int i = 0; i < 5; i++) {
            double t = i / 4.0;
            double curve = Math.sin(t * Math.PI) * 20 * progress;

            int x1 = (int) (startX + (endX - startX) * t);
            int y1 = (int) (startY + curve);

            int x2 = (int) (startX + (endX - startX) * (t + 0.25));
            int y2 = (int) (startY + Math.sin((t + 0.25) * Math.PI) * 20 * progress);

            gc.strokeLine(x1, y1, x2, y2);
        }

        if (progress > 0.3) {
            gc.setStroke(Color.rgb(255, 165, 0, 0.2));
            gc.setLineWidth(2);
            gc.strokeRect(
                    isLeft ? startX - whipLength : startX,
                    startY - (double) whipWidth / 2,
                    whipLength,
                    whipWidth
            );
        }
    }
}