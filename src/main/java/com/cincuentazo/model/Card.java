package com.cincuentazo.model;

/**
 * Represents a playing card with a rank and suit.
 * Cards have specific values according to the game rules.
 *
 * @author William May
 * @version 1.0.0
 */
public class Card {

    /**
     * Enumeration representing the possible card ranks.
     */
    public enum Rank {
        TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5),
        SIX("6", 6), SEVEN("7", 7), EIGHT("8", 8), NINE("9", 0),
        TEN("10", 10), JACK("J", -10), QUEEN("Q", -10), KING("K", -10), ACE("A", 1);

        private final String symbol;
        private final int value;

        /**
         * Constructor for Rank enumeration.
         *
         * @param symbol the symbol representation of the rank
         * @param value the default value of the rank
         */
        Rank(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }

        /**
         * Gets the symbol of the rank.
         *
         * @return the symbol as a string
         */
        public String getSymbol() {
            return symbol;
        }

        /**
         * Gets the default value of the rank.
         *
         * @return the numeric value
         */
        public int getValue() {
            return value;
        }
    }

    /**
     * Enumeration representing the possible card suits.
     */
    public enum Suit {
        HEARTS("♥"), DIAMONDS("♦"), CLUBS("♣"), SPADES("♠");

        private final String symbol;

        /**
         * Constructor for Suit enumeration.
         *
         * @param symbol the symbol representation of the suit
         */
        Suit(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Gets the symbol of the suit.
         *
         * @return the symbol as a string
         */
        public String getSymbol() {
            return symbol;
        }
    }

    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;

    /**
     * Constructs a Card with the specified rank and suit.
     *
     * @param rank the rank of the card
     * @param suit the suit of the card
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = false;
    }

    /**
     * Gets the rank of the card.
     *
     * @return the card's rank
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Gets the suit of the card.
     *
     * @return the card's suit
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Checks if the card is face up.
     *
     * @return true if face up, false otherwise
     */
    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Sets whether the card is face up or face down.
     *
     * @param faceUp true to set face up, false for face down
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Calculates the value of the card based on current table sum.
     * For Ace cards, returns 1 or 10 depending on which is more convenient.
     *
     * @param currentSum the current sum on the table
     * @return the calculated value of the card
     */
    public int calculateValue(int currentSum) {
        if (rank == Rank.ACE) {
            // Choose 1 or 10 based on what doesn't exceed 50
            if (currentSum + 10 <= 50) {
                return 10;
            }
            return 1;
        }
        return rank.getValue();
    }

    /**
     * Returns a string representation of the card.
     *
     * @return string in format "Rank Suit"
     */
    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }

    /**
     * Checks if two cards are equal based on rank and suit.
     *
     * @param obj the object to compare
     * @return true if cards are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return rank == card.rank && suit == card.suit;
    }

    /**
     * Generates a hash code for the card.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return 31 * rank.hashCode() + suit.hashCode();
    }
}
