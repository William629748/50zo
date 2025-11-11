package com.cincuentazo.model;

import com.cincuentazo.exception.EmptyDeckException;
import java.util.*;

/**
 * Represents a deck of playing cards using a Stack data structure.
 * Provides functionality to shuffle, draw, and manage cards.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class Deck {

    private Stack<Card> cards;
    private Stack<Card> discardPile;

    /**
     * Constructs a standard 52-card deck and shuffles it.
     */
    public Deck() {
        cards = new Stack<>();
        discardPile = new Stack<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes the deck with all 52 cards (13 ranks x 4 suits).
     */
    private void initializeDeck() {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.push(new Card(rank, suit));
            }
        }
    }

    /**
     * Shuffles the deck using Collections.shuffle().
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the top of the deck.
     * If the deck is empty, reshuffles the discard pile.
     *
     * @return the drawn card
     * @throws EmptyDeckException if both deck and discard pile are empty
     */
    public Card drawCard() throws EmptyDeckException {
        if (cards.isEmpty()) {
            reshuffleDiscardPile();
        }

        if (cards.isEmpty()) {
            throw new EmptyDeckException("No cards available to draw");
        }

        return cards.pop();
    }

    /**
     * Adds a card to the discard pile.
     *
     * @param card the card to add to discard pile
     */
    public void addToDiscardPile(Card card) {
        if (card != null) {
            discardPile.push(card);
        }
    }

    /**
     * Adds multiple cards to the discard pile.
     *
     * @param cardsToAdd the list of cards to add
     */
    public void addToDiscardPile(List<Card> cardsToAdd) {
        if (cardsToAdd != null) {
            discardPile.addAll(cardsToAdd);
        }
    }

    /**
     * Reshuffles the discard pile back into the deck, keeping the top card.
     * According to game rules, all cards except the last played card are reshuffled.
     */
    private void reshuffleDiscardPile() {
        if (discardPile.size() > 1) {
            Card topCard = discardPile.pop(); // Keep the last played card
            cards.addAll(discardPile);
            discardPile.clear();
            discardPile.push(topCard); // Put back the top card
            shuffle();
        }
    }

    /**
     * Gets the number of cards remaining in the deck.
     *
     * @return the number of cards
     */
    public int size() {
        return cards.size();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty() && discardPile.size() <= 1;
    }

    /**
     * Gets the top card from the discard pile without removing it.
     *
     * @return the top card of discard pile, or null if empty
     */
    public Card peekDiscardPile() {
        return discardPile.isEmpty() ? null : discardPile.peek();
    }

    /**
     * Gets the size of the discard pile.
     *
     * @return the number of cards in discard pile
     */
    public int getDiscardPileSize() {
        return discardPile.size();
    }

    /**
     * Clears both deck and discard pile.
     */
    public void clear() {
        cards.clear();
        discardPile.clear();
    }
}
