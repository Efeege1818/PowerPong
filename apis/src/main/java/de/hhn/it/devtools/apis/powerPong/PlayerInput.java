package de.hhn.it.devtools.apis.powerPong;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javafx.scene.input.KeyCode;

/**
 * Thin facade around JavaFX input handling. Instead of keeping boolean flags per paddle we simply
 * capture the currently pressed {@link KeyCode KeyCodes} reported by the JavaFX scene.
 * <p>Example integration:</p>
 * <pre>{@code
 * PlayerInput input = new PlayerInput();
 * scene.setOnKeyPressed(event -> input.keyPressed(event.getCode()));
 * scene.setOnKeyReleased(event -> input.keyReleased(event.getCode()));
 * animationTimer.handle(...) -> powerPongService.updateGame(input);
 * }</pre>
 *
 * No game logic lives here—this class just mirrors what the JavaFX event system saw last frame.
 */
public class PlayerInput {

    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);

    /**
     * Marks a key as pressed. Intended to be called directly from {@code Scene#setOnKeyPressed}.
     *
     * @param code JavaFX key code, ignored when null.
     */
    public void keyPressed(KeyCode code) {
        if (code != null) {
            pressedKeys.add(code);
        }
    }

    /**
     * Marks a key as released. Intended to be wired to {@code Scene#setOnKeyReleased}.
     *
     * @param code JavaFX key code, ignored when null.
     */
    public void keyReleased(KeyCode code) {
        if (code != null) {
            pressedKeys.remove(code);
        }
    }

    /**
     * Checks whether the given key is currently held down.
     *
     * @param code JavaFX key code.
     * @return true if the key is part of the tracked set.
     */
    public boolean isPressed(KeyCode code) {
        return code != null && pressedKeys.contains(code);
    }

    /**
     * Returns an immutable snapshot of the pressed key set for the current frame.
     */
    public Set<KeyCode> getPressedKeysSnapshot() {
        return Collections.unmodifiableSet(EnumSet.copyOf(pressedKeys));
    }

    /**
     * Clears all tracked key presses (useful if the scene loses focus).
     */
    public void clear() {
        pressedKeys.clear();
    }
}
