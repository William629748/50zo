package com.cincuentazo.interfaces;

import com.cincuentazo.model.Player;

/**
 * Interface for handling general game events.
 * Implementations can respond to various game state changes.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public interface GameEventListener {

    /**
     * Called when a player's turn begins.
     *
     * @param player the player whose turn is starting
     */
    void onTurnStart(Player player);

    /**
     * Called when a player's turn ends.
     *
     * @param player the player whose turn is ending
     */
    void onTurnEnd(Player player);

    /**
     * Called when a player is eliminated from the game.
     *
     * @param player the player who was eliminated
     */
    void onPlayerEliminated(Player player);

    /**
     * Called when the game ends.
     *
     * @param winner the player who won the game
     */
    void onGameEnd(Player winner);
}
