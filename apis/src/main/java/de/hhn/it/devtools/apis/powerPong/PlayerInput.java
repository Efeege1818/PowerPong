package de.hhn.it.devtools.apis.powerPong;

import java.util.EnumSet;
import java.util.Set;

/**
 * Thin facade around input handling. Instead of keeping boolean flags per paddle we simply
 * capture the currently active {@link InputAction InputActions} reported by the UI layer.
 * <p>Example integration:</p>
 * <pre>{@code
 * PlayerInput input = new PlayerInput();
 * scene.setOnKeyPressed(event -> input.keyPressed(KeyCodeMapper.map(event.getCode())));
 * scene.setOnKeyReleased(event -> input.keyReleased(KeyCodeMapper.map(event.getCode())));
 * animationTimer.handle(...) -> powerPongService.updateGame(input);
 * }</pre>
 *
 * No game logic lives here—this class just mirrors what the UI reported last frame.
 */
public class PlayerInput {

    private final Set<InputAction> pressedActions = EnumSet.noneOf(InputAction.class);

    /**
     * Marks an action as active. Intended to be called from the UI adapter.
     *
     * @param action logical input action, ignored when null.
     */
    public void keyPressed(InputAction action) {
        if (action != null) {
            pressedActions.add(action);
        }
    }

    /**
     * Marks an action as released. Intended to be wired to the UI adapter.
     *
     * @param action logical input action, ignored when null.
     */
    public void keyReleased(InputAction action) {
        if (action != null) {
            pressedActions.remove(action);
        }
    }

    /**
     * Checks whether the given action is currently active.
     *
     * @param action logical input action.
     * @return true if the action is part of the tracked set.
     */
    public boolean isPressed(InputAction action) {
        return action != null && pressedActions.contains(action);
    }

    /**
     * Returns an immutable snapshot of the pressed actions for the current frame.
     */
    public Set<InputAction> getPressedKeysSnapshot() {
        return Set.copyOf(pressedActions);
    }

    /**
     * Clears all tracked actions (useful if the scene loses focus).
     */
    public void clear() {
        pressedActions.clear();
    }
}
