package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Allows creation of new moves with custom effects.
 *
 * @param type the type of the move.
 * @param amount number by which the selected stat will be changed.
 * @param stat the stat that will be influenced by this move e.g. hp, attack, critChance,...
 */
public record Move(MoveType type, int amount, String stat) {
}