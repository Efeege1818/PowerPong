package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Allows creation of new moves with custom effects.
 *
 * @param type the type of the move.
 * @param amount number by which the selected stat will be changed.
 * @param stat the stat that will be influenced by this move e.g. hp, attack, critChance,...
 * @param duration the number of turns the move will last.
 * @param cooldown the number of turns the move will be on cooldown.
 * @param isSpecial indicates if the move is special.
 * @param description a description of the move.
 */
public record Move(MoveType type, int amount, String stat, int duration, int cooldown, boolean isSpecial, String description) {
}