package com.cincuentazo.interfaces;

import com.cincuentazo.model.Card;

/**
 * Interface for handling card selection events.
 * Implementations of this interface respond to card selection actions
 * from the user interface.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public interface CardSelectionListener {

    /**
     * Called when a card is selected by the player.
     *
     * @param card the card that was selected
     */
    void onCardSelected(Card card);
}