package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * Allows creation of new moves with custom effects
 *
 * @param target player targeted by the move
 * @param amount number by which the selected stat will be changed
 * @param stat the stat that will be influenced by this move e.g. hp, attack, critChance,...
 */
public record Move(Player target, int amount, String stat) {
}