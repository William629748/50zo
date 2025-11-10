package com.cincuentazo.model;

import com.cincuentazo.exception.InvalidCardException;
import com.cincuentazo.exception.GameException;
import com.cincuentazo.interfaces.Playable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game with a hand of cards.
 * Can be either a human player or a machine player.
 * Implements Playable interface to define gameplay contract.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class Player implements Playable {

    private final String name;
    private final boolean isHuman;
    private ArrayList<Card> hand;
    private boolean isEliminated;

    /**
     * Constructs a Player with the specified name and type.
     *
     * @param name the player's name
     * @param isHuman true if human player, false if machine
     */
    public Player(String name, boolean isHuman) {
        this.name = name;
        this.isHuman = isHuman;
        this.hand = new ArrayList<>();
        this.isEliminated = false;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the player is human.
     *
     * @return true if human, false if machine
     */
    public boolean isHuman() {
        return isHuman;
    }

    /**
     * Gets the player's hand of cards.
     *
     * @return the list of cards in hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Checks if the player is eliminated.
     *
     * @return true if eliminated, false otherwise
     */
    public boolean isEliminated() {
        return isEliminated;
    }

    /**
     * Sets the player's elimination status.
     *
     * @param eliminated true to eliminate, false otherwise
     */
    public void setEliminated(boolean eliminated) {
        this.isEliminated = eliminated;
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
            if (isHuman) {
                card.setFaceUp(true);
            }
        }
    }

    /**
     * Removes a card from the player's hand.
     *
     * @param card the card to remove
     * @return true if card was removed, false otherwise
     * @throws InvalidCardException if card is not in hand
     */
    public boolean removeCard(Card card) throws InvalidCardException {
        if (card == null) {
            throw new InvalidCardException("Cannot remove null card");
        }

        if (!hand.contains(card)) {
            throw new InvalidCardException("Card not found in player's hand");
        }

        return hand.remove(card);
    }

    /**
     * Gets the number of cards in the player's hand.
     *
     * @return the number of cards
     */
    public int getHandSize() {
        return hand.size();
    }

    // ==================== Playable Interface Implementation ====================

    @Override
    public boolean canPlay(int currentSum) {
        return hasPlayableCard(currentSum);
    }

    @Override
    public int playCard(Card card, int currentSum) throws GameException {
        if (!hand.contains(card)) {
            throw new InvalidCardException("Card not in player's hand");
        }

        int cardValue = card.calculateValue(currentSum);
        int newSum = currentSum + cardValue;

        if (newSum > 50) {
            throw new InvalidCardException("Playing this card would exceed 50");
        }

        removeCard(card);
        return newSum;
    }

    @Override
    public int getCardCount() {
        return getHandSize();
    }

    // ==================== Helper Methods ====================

    /**
     * Checks if the player has any playable card for the current sum.
     *
     * @param currentSum the current sum on the table
     * @return true if player has at least one playable card
     */
    public boolean hasPlayableCard(int currentSum) {
        for (Card card : hand) {
            int newSum = currentSum + card.calculateValue(currentSum);
            if (newSum <= 50) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all playable cards for the current sum.
     *
     * @param currentSum the current sum on the table
     * @return list of playable cards
     */
    public List<Card> getPlayableCards(int currentSum) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            int newSum = currentSum + card.calculateValue(currentSum);
            if (newSum <= 50) {
                playableCards.add(card);
            }
        }
        return playableCards;
    }

    /**
     * Clears all cards from the player's hand.
     *
     * @return the list of cards that were in hand
     */
    public List<Card> clearHand() {
        List<Card> cards = new ArrayList<>(hand);
        hand.clear();
        return cards;
    }

    /**
     * Returns a string representation of the player.
     *
     * @return string with player name and type
     */
    @Override
    public String toString() {
        return name + (isHuman ? " (Human)" : " (Machine)");
    }
}
