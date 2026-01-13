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
    public void renderWeapons(
            GraphicsContext gc,
            Player player,
            double canvasWidth,
            double canvasHeight
    ) {
        int px = getScreenPlayerX(canvasWidth);
        int py = getScreenPlayerY(canvasHeight);

        for (Weapon weapon : player.equippedWeapons()) {
            WeaponAnimationState state = animationStates.get(weapon.type());
            if (state == null) continue;

            switch (weapon.type()) {
                case SWORD -> renderSword(gc, px, py, weapon, state);
                case AURA  -> renderAura(gc, px, py, weapon, state);
                case WHIP  -> renderWhip(gc, px, py, weapon, state);
            }
        }
    }

    private int getScreenPlayerX(double canvasWidth) {
        return (int) (canvasWidth / 2);
    }

    private int getScreenPlayerY(double canvasHeight) {
        return (int) (canvasHeight / 2);
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


    private void renderSword(
            GraphicsContext gc,
            int px,
            int py,
            Weapon weapon,
            WeaponAnimationState state
    ) {
        gc.save();
        double angle = state.getAngle();

        double gripOffset = 50;
        double radius = weapon.range() - gripOffset;

        double sx = px + Math.cos(angle) * radius;
        double sy = py + Math.sin(angle) * radius;

        gc.save();
        gc.translate(sx, sy);
        gc.rotate(Math.toDegrees(angle) + 90);

        gc.setFill(Color.SILVER);
        gc.fillRect(-3, -90, 6, 90);

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(-8, 0, 16, 4);

        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(-4, 4, 8, 10);

        gc.setFill(Color.GOLD);
        gc.fillOval(-4, 14, 8, 8);

        gc.restore();
    }


    private void renderAura(
            GraphicsContext gc,
            int px,
            int py,
            Weapon weapon,
            WeaponAnimationState state
    ) {
        gc.save();
        double pulse = Math.sin(state.getAngle() * 3) * 10 + weapon.range();

        gc.setStroke(Color.rgb(100, 200, 255, 0.3));
        gc.setLineWidth(3);
        gc.strokeOval(
                px - pulse,
                py - pulse,
                pulse * 2,
                pulse * 2
        );
        gc.restore();
    }


    private void renderWhip(
            GraphicsContext gc,
            int startX,
            int startY,
            Weapon weapon,
            WeaponAnimationState state
    ) {
        if (state.isNotAttacking()) return;

        gc.save();

        double progress = Math.min(1.0, state.getAttackProgress() / 300.0);
        boolean isLeft = state.isAttackingLeft();

        int whipLength = (int) (weapon.range() * progress);
        int whipWidth = 80;

      int endX = startX + (isLeft ? -whipLength : whipLength);

        gc.setStroke(Color.ORANGE);
        gc.setLineWidth(6);
        gc.setGlobalAlpha(1.0);

        for (int i = 0; i < 5; i++) {
            double t = i / 4.0;
            double curve = Math.sin(t * Math.PI) * 20 * progress;

            int x1 = (int) (startX + (endX - startX) * t);
            int y1 = (int) (startY + curve);

            int x2 = (int) (startX + (endX - startX) * (t + 0.25));
            int y2 = (int) (startY + Math.sin((t + 0.25) * Math.PI) * 20 * progress);

            gc.strokeLine(x1, y1, x2, y2);
        }

        // Hitbox visualization (outline was missing before)
        if (progress > 0.3) {
            gc.setStroke(Color.rgb(255, 165, 0, 0.6));
            gc.setLineWidth(2);
            gc.strokeRect(
                    isLeft ? startX - whipLength : startX,
                    startY - whipWidth / 2.0,
                    whipLength,
                    whipWidth
            );
        }

        gc.restore();
    }


}