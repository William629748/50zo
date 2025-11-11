package com.cincuentazo.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Card class.
 * Tests card creation, value calculation, and behavior.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
@DisplayName("Card Class Tests")
class CardTest {

    private Card aceCard;
    private Card nineCard;
    private Card kingCard;
    private Card fiveCard;

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        aceCard = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        nineCard = new Card(Card.Rank.NINE, Card.Suit.DIAMONDS);
        kingCard = new Card(Card.Rank.KING, Card.Suit.CLUBS);
        fiveCard = new Card(Card.Rank.FIVE, Card.Suit.SPADES);
    }

    /**
     * Tests card creation with valid parameters.
     */
    @Test
    @DisplayName("Should create card with correct rank and suit")
    void testCardCreation() {
        assertNotNull(aceCard);
        assertEquals(Card.Rank.ACE, aceCard.getRank());
        assertEquals(Card.Suit.HEARTS, aceCard.getSuit());
    }

    /**
     * Tests initial face up state of cards.
     */
    @Test
    @DisplayName("Cards should initially be face down")
    void testInitialFaceUpState() {
        assertFalse(aceCard.isFaceUp());
        assertFalse(nineCard.isFaceUp());
    }

    /**
     * Tests setting face up state.
     */
    @Test
    @DisplayName("Should correctly set face up state")
    void testSetFaceUp() {
        aceCard.setFaceUp(true);
        assertTrue(aceCard.isFaceUp());

        aceCard.setFaceUp(false);
        assertFalse(aceCard.isFaceUp());
    }

    /**
     * Tests value calculation for number cards.
     */
    @Test
    @DisplayName("Number cards should return their numeric value")
    void testNumberCardValue() {
        assertEquals(5, fiveCard.calculateValue(0));
        assertEquals(5, fiveCard.calculateValue(20));
        assertEquals(5, fiveCard.calculateValue(45));
    }

    /**
     * Tests value calculation for nine card.
     */
    @Test
    @DisplayName("Nine card should return zero")
    void testNineCardValue() {
        assertEquals(0, nineCard.calculateValue(0));
        assertEquals(0, nineCard.calculateValue(30));
        assertEquals(0, nineCard.calculateValue(50));
    }

    /**
     * Tests value calculation for face cards (J, Q, K).
     */
    @Test
    @DisplayName("Face cards should return -10")
    void testFaceCardValue() {
        assertEquals(-10, kingCard.calculateValue(20));

        Card jack = new Card(Card.Rank.JACK, Card.Suit.HEARTS);
        assertEquals(-10, jack.calculateValue(30));

        Card queen = new Card(Card.Rank.QUEEN, Card.Suit.DIAMONDS);
        assertEquals(-10, queen.calculateValue(40));
    }

    /**
     * Tests Ace card returns 10 when it doesn't exceed 50.
     */
    @Test
    @DisplayName("Ace should return 10 when sum won't exceed 50")
    void testAceCardReturns10() {
        assertEquals(10, aceCard.calculateValue(0));
        assertEquals(10, aceCard.calculateValue(20));
        assertEquals(10, aceCard.calculateValue(40));
    }

    /**
     * Tests Ace card returns 1 when 10 would exceed 50.
     */
    @Test
    @DisplayName("Ace should return 1 when 10 would exceed 50")
    void testAceCardReturns1() {
        assertEquals(1, aceCard.calculateValue(41));
        assertEquals(1, aceCard.calculateValue(45));
        assertEquals(1, aceCard.calculateValue(50));
    }

    /**
     * Tests Ace card edge case at exactly 40.
     */
    @Test
    @DisplayName("Ace at sum 40 should return 10")
    void testAceCardAtBoundary() {
        assertEquals(10, aceCard.calculateValue(40));
    }

    /**
     * Tests card string representation.
     */
    @Test
    @DisplayName("toString should return rank and suit symbols")
    void testToString() {
        assertTrue(aceCard.toString().contains("A"));
        assertTrue(aceCard.toString().contains("♥"));

        assertTrue(kingCard.toString().contains("K"));
        assertTrue(kingCard.toString().contains("♣"));
    }

    /**
     * Tests card equality based on rank and suit.
     */
    @Test
    @DisplayName("Cards with same rank and suit should be equal")
    void testCardEquality() {
        Card anotherAce = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        assertEquals(aceCard, anotherAce);

        Card differentAce = new Card(Card.Rank.ACE, Card.Suit.DIAMONDS);
        assertNotEquals(aceCard, differentAce);
    }

    /**
     * Tests card inequality.
     */
    @Test
    @DisplayName("Cards with different rank or suit should not be equal")
    void testCardInequality() {
        assertNotEquals(aceCard, kingCard);
        assertNotEquals(fiveCard, nineCard);
    }

    /**
     * Tests hashCode consistency with equals.
     */
    @Test
    @DisplayName("Equal cards should have same hashCode")
    void testHashCode() {
        Card anotherAce = new Card(Card.Rank.ACE, Card.Suit.HEARTS);
        assertEquals(aceCard.hashCode(), anotherAce.hashCode());
    }

    /**
     * Tests all ranks have correct default values.
     */
    @Test
    @DisplayName("All ranks should have correct default values")
    void testAllRankValues() {
        assertEquals(2, new Card(Card.Rank.TWO, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(3, new Card(Card.Rank.THREE, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(4, new Card(Card.Rank.FOUR, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(5, new Card(Card.Rank.FIVE, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(6, new Card(Card.Rank.SIX, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(7, new Card(Card.Rank.SEVEN, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(8, new Card(Card.Rank.EIGHT, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(0, new Card(Card.Rank.NINE, Card.Suit.HEARTS).calculateValue(0));
        assertEquals(10, new Card(Card.Rank.TEN, Card.Suit.HEARTS).calculateValue(0));
    }
}