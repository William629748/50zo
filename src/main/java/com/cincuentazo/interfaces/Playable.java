package com.cincuentazo.interfaces;

import com.cincuentazo.model.Card;
import com.cincuentazo.exception.GameException;

/**
 * Interface for objects that can participate in gameplay.
 * Defines the contract for playing cards and checking playability.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public interface Playable {

    /**
     * Checks if the entity can play any card given the current sum.
     *
     * @param currentSum the current sum on the table
     * @return true if at least one card can be played, false otherwise
     */
    boolean canPlay(int currentSum);

    /**
     * Plays a card from the entity's hand.
     *
     * @param card the card to play
     * @param currentSum the current sum on the table
     * @return the new sum after playing the card
     * @throws GameException if the card cannot be played
     */
    int playCard(Card card, int currentSum) throws GameException;

    /**
     * Gets the number of cards in the entity's hand.
     *
     * @return the number of cards
     */
    int getCardCount();
}