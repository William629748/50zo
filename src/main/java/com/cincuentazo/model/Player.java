package com.cincuentazo.model;

import com.cincuentazo.exception.InvalidCardException;
import com.cincuentazo.exception.GameException;
import com.cincuentazo.interfaces.Playable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic player in the Cincuentazo game.
 * This class holds the player's name, their hand of {@link Card} objects,
 * and their status (human/machine, eliminated/active).
 * It implements the {@link Playable} interface to define common gameplay actions.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class Player implements Playable {

    private final String name;
    private final boolean isHuman; // True for human, false for machine (AI)
    private ArrayList<Card> hand; // The cards currently held by the player
    private boolean isEliminated; // True if the player has been eliminated from the game

    /**
     * Constructs a Player with the specified name and type (human or machine).
     * Initializes the player's hand as an empty list and sets their status to not eliminated.
     *
     * @param name The name of the player.
     * @param isHuman A boolean flag, {@code true} if this is a human-controlled player, {@code false} for a machine.
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
     * @return A {@code String} representing the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the player is human.
     * This method can be overridden by subclasses (e.g., {@link HumanPlayer}, {@link MachinePlayer})
     * for more specific behavior.
     *
     * @return {@code true} if the player is human, {@code false} if the player is a machine.
     */
    public boolean isHuman() {
        return isHuman;
    }

    /**
     * Gets the current hand of cards held by the player.
     *
     * @return An {@link ArrayList} of {@link Card} objects representing the player's hand.
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Checks if the player has been eliminated from the game.
     * An eliminated player can no longer take turns or play cards.
     *
     * @return {@code true} if the player is eliminated, {@code false} otherwise.
     */
    public boolean isEliminated() {
        return isEliminated;
    }

    /**
     * Sets the player's elimination status.
     * When a player is eliminated, they are out of the current game round.
     *
     * @param eliminated {@code true} to mark the player as eliminated, {@code false} to mark them as active.
     */
    public void setEliminated(boolean eliminated) {
        this.isEliminated = eliminated;
    }

    /**
     * Adds a {@link Card} to the player's hand.
     * If the player is human, the added card is automatically set to face up.
     *
     * @param card The {@link Card} object to add to the hand.
     */
    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
            if (isHuman) {
                card.setFaceUp(true); // Human players see their cards
            }
        }
    }

    /**
     * Removes a specific {@link Card} from the player's hand.
     *
     * @param card The {@link Card} object to remove from the hand.
     * @return {@code true} if the card was successfully removed, {@code false} otherwise.
     * @throws InvalidCardException If the provided card is null or not found in the player's hand.
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
     * Gets the number of cards currently in the player's hand.
     *
     * @return An integer representing the size of the player's hand.
     */
    public int getHandSize() {
        return hand.size();
    }

    // ==================== Playable Interface Implementation ====================

    /**
     * {@inheritDoc}
     * Checks if the player has at least one card in their hand that can be played
     * without exceeding the maximum table sum (50).
     *
     * @param currentSum The current sum of cards on the table.
     * @return {@code true} if there's at least one playable card, {@code false} otherwise.
     */
    @Override
    public boolean canPlay(int currentSum) {
        return hasPlayableCard(currentSum);
    }

    /**
     * {@inheritDoc}
     * Simulates playing a card by removing it from the player's hand and
     * calculating the new table sum. It validates if the card can be played.
     *
     * @param card The {@link Card} to be played.
     * @param currentSum The current sum of cards on the table.
     * @return The new sum on the table after playing the card.
     * @throws InvalidCardException If the card is not in the player's hand or playing it would exceed 50.
     * @throws GameException If any other game-related issue occurs (though not explicitly thrown here).
     */
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

        removeCard(card); // Remove from hand as it's played
        return newSum;    // Return the new sum
    }

    /**
     * {@inheritDoc}
     * Returns the total count of cards in the player's hand.
     *
     * @return The number of cards in the player's hand.
     */
    @Override
    public int getCardCount() {
        return getHandSize();
    }

    // ==================== Helper Methods ====================

    /**
     * Determines if the player possesses any card in their hand that, when played,
     * would not cause the table sum to exceed 50.
     *
     * @param currentSum The current sum of cards on the table.
     * @return {@code true} if the player has at least one card that can be played legally, {@code false} otherwise.
     */
    public boolean hasPlayableCard(int currentSum) {
        for (Card card : hand) {
            int newSum = currentSum + card.calculateValue(currentSum);
            if (newSum <= 50) {
                return true; // Found at least one playable card
            }
        }
        return false; // No playable cards found
    }

    /**
     * Retrieves a list of all cards in the player's hand that can be played
     * without exceeding the maximum table sum (50).
     *
     * @param currentSum The current sum of cards on the table.
     * @return A {@link List} of {@link Card} objects that are currently playable.
     * Returns an empty list if no cards are playable.
     */
    public List<Card> getPlayableCards(int currentSum) {
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            int newSum = currentSum + card.calculateValue(currentSum);
            if (newSum <= 50) {
                playableCards.add(card); // Add playable card to the list
            }
        }
        return playableCards;
    }

    /**
     * Clears all cards from the player's hand.
     * This method is typically called when a player is eliminated from the game.
     *
     * @return A {@link List} of the {@link Card} objects that were removed from the hand.
     */
    public List<Card> clearHand() {
        List<Card> cards = new ArrayList<>(hand); // Create a copy of the hand
        hand.clear(); // Clear the actual hand
        return cards; // Return the removed cards
    }

    /**
     * Returns a string representation of the player, including their name and type (Human/Machine).
     *
     * @return A {@code String} formatted as "PlayerName (Type)".
     */
    @Override
    public String toString() {
        return name + (isHuman ? " (Human)" : " (Machine)");
    }
}