package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Allows creation of new moves with custom effects.
 *
 * @param type the type of the move.
 * @param element element of the move. Affects the amount of damage done based one the targeted monster's element.
 * @param amount number by which the selected stat will be changed.
 * @param stat the stat that will be influenced by this move e.g. hp, attack, critChance,...
 * @param duration the number of turns the move will last. 0 means the move is instant.
 * @param cooldown the number of turns the move will be on cooldown. 0 means no cooldown.
 * @param isSpecial indicates if the move is special.
 * @param description a description of the move.
 */
public record Move(MoveType type,Element element, double amount, String stat, int duration, int cooldown, boolean isSpecial, String description) {
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(Move.class);

    /**
     * Checks if the move parameters are valid.
     *
     * @throws IllegalArgumentException if any parameter is invalid.
     */
    public Move {
        if (type == null) {
            throw new IllegalArgumentException("move type must not be null");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
        if (stat == null || stat.isEmpty()) {
            throw new IllegalArgumentException("stat must not be null or empty");
        }
        if (duration < 0 || cooldown < 0) {
            throw new IllegalArgumentException("duration and cooldown must be non-negative");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("description must not be null or empty");
        }
    }


}