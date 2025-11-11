package com.cincuentazo.model;

import com.cincuentazo.exception.EmptyDeckException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for the Deck class.
 * Tests deck initialization, shuffling, drawing, and discard pile management.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
@DisplayName("Deck Class Tests")
class DeckTest {

    private Deck deck;

    /**
     * Sets up a new deck before each test.
     * This runs BEFORE EACH test method.
     */
    @BeforeEach
    void setUp() {
        // ARRANGE: Crear un mazo nuevo para cada test
        deck = new Deck();
    }

    // ==================== TESTS DE INICIALIZACIÓN ====================

    /**
     * TEST 1: Verificar que el mazo se inicializa con 52 cartas.
     * Prueba: Constructor del Deck
     * Resultado esperado: 52 cartas únicas
     * Mensaje si falla: El mazo debe empezar con 52 cartas estándar
     */
    @Test
    @DisplayName("New deck should contain 52 cards")
    void testDeckInitialization() {
        // ASSERT: Verificar tamaño del mazo
        assertEquals(52, deck.size(), "New deck should have exactly 52 cards");
        assertFalse(deck.isEmpty(), "New deck should not be empty");
    }

    // ==================== TESTS DE DRAW ====================

    /**
     * TEST 2: Verificar que se puede sacar una carta.
     * Prueba: drawCard() método básico
     * Resultado esperado: Carta no nula y mazo con una carta menos
     * Mensaje: "No cards available to draw" si el mazo está vacío
     */
    @Test
    @DisplayName("Should successfully draw a card from deck")
    void testDrawCard() throws EmptyDeckException {
        // ARRANGE: Tamaño inicial
        int initialSize = deck.size();

        // ACT: Sacar una carta
        Card card = deck.drawCard();

        // ASSERT: Verificar que la carta existe y el tamaño disminuyó
        assertNotNull(card, "Drawn card should not be null");
        assertEquals(initialSize - 1, deck.size(), "Deck size should decrease by 1 after drawing");
    }

    /**
     * TEST 3: Verificar que se pueden sacar todas las 52 cartas.
     * Prueba: Sacar todas las cartas del mazo
     * Resultado esperado: 52 cartas únicas
     * Causa del error: "Deck is not properly initialized with all cards"
     */
    @Test
    @DisplayName("Should be able to draw all 52 cards")
    void testDrawAllCards() throws EmptyDeckException {
        // ARRANGE: Set para verificar que todas sean únicas
        Set<Card> drawnCards = new HashSet<>();

        // ACT: Sacar las 52 cartas
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            assertNotNull(card, "Card " + (i+1) + " should not be null");
            drawnCards.add(card);
        }

        // ASSERT: Deben ser 52 cartas únicas
        assertEquals(52, drawnCards.size(), "All 52 cards should be unique");
    }

    /**
     * TEST 4: Verificar que sacar de un mazo vacío lanza excepción.
     * Prueba: drawCard() cuando no hay cartas
     * Resultado esperado: EmptyDeckException
     * Mensaje esperado: "No cards available to draw"
     */
    @Test
    @DisplayName("Drawing from empty deck should throw EmptyDeckException")
    void testDrawFromEmptyDeck() throws EmptyDeckException {
        // ARRANGE: Vaciar el mazo
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        // ACT & ASSERT: Intentar sacar otra carta debe lanzar excepción
        EmptyDeckException exception = assertThrows(
                EmptyDeckException.class,
                () -> deck.drawCard(),
                "Should throw EmptyDeckException when deck is empty"
        );

        // Verificar mensaje de la excepción
        assertTrue(exception.getMessage().contains("No cards available"),
                "Exception message should indicate no cards available");
    }

    // ==================== TESTS DE DISCARD PILE ====================

    /**
     * TEST 5: Verificar que se pueden añadir cartas a la pila de descarte.
     * Prueba: addToDiscardPile(Card)
     * Resultado esperado: Tamaño de discard pile aumenta
     */
    @Test
    @DisplayName("Should add cards to discard pile")
    void testAddToDiscardPile() {
        // ARRANGE: Crear una carta
        Card card = new Card(Card.Rank.ACE, Card.Suit.HEARTS);

        // ACT: Añadir a discard pile
        deck.addToDiscardPile(card);

        // ASSERT: Tamaño de discard pile debe ser 1
        assertEquals(1, deck.getDiscardPileSize(),
                "Discard pile should contain 1 card after adding");
    }

    /**
     * TEST 6: Verificar que se puede ver la carta superior del discard pile.
     * Prueba: peekDiscardPile()
     * Resultado esperado: Retorna la última carta añadida sin removerla
     */
    @Test
    @DisplayName("Should peek at top card of discard pile")
    void testPeekDiscardPile() {
        // ARRANGE: Discard pile inicialmente vacío
        assertNull(deck.peekDiscardPile(), "Empty discard pile should return null");

        // ARRANGE: Añadir una carta
        Card card = new Card(Card.Rank.KING, Card.Suit.SPADES);
        deck.addToDiscardPile(card);

        // ACT & ASSERT: Peek debe retornar la misma carta
        assertEquals(card, deck.peekDiscardPile(),
                "Peek should return the top card");
        assertEquals(1, deck.getDiscardPileSize(),
                "Peek should not remove the card");
    }

    /**
     * TEST 7: Verificar que se pueden añadir múltiples cartas al discard pile.
     * Prueba: addToDiscardPile(List<Card>)
     * Resultado esperado: Todas las cartas se añaden
     */
    @Test
    @DisplayName("Should add multiple cards to discard pile")
    void testAddMultipleToDiscard() throws EmptyDeckException {
        // ARRANGE: Sacar 5 cartas
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(deck.drawCard());
        }

        // ACT: Añadir todas al discard pile
        deck.addToDiscardPile(cards);

        // ASSERT: Discard pile debe tener 5 cartas
        assertEquals(5, deck.getDiscardPileSize(),
                "Discard pile should contain 5 cards");
    }

    // ==================== TESTS DE RESHUFFLE ====================

    /**
     * TEST 8: Verificar que el mazo se rebaraja cuando está vacío.
     * Prueba: Reshuffle automático al sacar carta
     * Resultado esperado: Cartas del discard pile vuelven al mazo
     * Causa: "Deck does not properly reshuffle from discard pile"
     */
    @Test
    @DisplayName("Should reshuffle discard pile when deck is empty")
    void testReshuffleFromDiscard() throws EmptyDeckException {
        // ARRANGE: Sacar todas las cartas y añadirlas al discard
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            deck.addToDiscardPile(card);
        }

        assertEquals(0, deck.size(), "Deck should be empty");
        assertEquals(52, deck.getDiscardPileSize(), "Discard should have 52 cards");

        // ACT: Sacar otra carta (debe activar reshuffle)
        Card card = deck.drawCard();

        // ASSERT: Debe haber cartas disponibles ahora
        assertNotNull(card, "Should be able to draw after reshuffle");
        assertTrue(deck.size() > 0, "Deck should have cards after reshuffle");
    }

    /**
     * TEST 9: Verificar que el reshuffle mantiene la carta superior.
     * Prueba: Última carta jugada se mantiene en discard pile
     * Resultado esperado: Top card no se mezcla de vuelta
     * Mensaje: "Reshuffle should keep top card in discard pile"
     */
    @Test
    @DisplayName("Reshuffle should keep top card in discard pile")
    void testReshuffleKeepsTopCard() throws EmptyDeckException {
        // ARRANGE: Sacar todas las cartas
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            deck.addToDiscardPile(card);
        }

        Card topCard = deck.peekDiscardPile();

        // ACT: Activar reshuffle sacando una carta
        deck.drawCard();

        // ASSERT: La carta superior debe seguir siendo la misma
        assertEquals(topCard, deck.peekDiscardPile(),
                "Top card should remain after reshuffle");
    }

    // ==================== TESTS DE SHUFFLE ====================

    /**
     * TEST 10: Verificar que shuffle cambia el orden de las cartas.
     * Prueba: Comparar mazos barajados
     * Resultado esperado: Orden diferente (probabilísticamente)
     * Causa: "Shuffle does not randomize card order"
     */
    @Test
    @DisplayName("Shuffling should randomize card order")
    void testShuffleRandomizes() throws EmptyDeckException {
        // ARRANGE: Crear dos mazos
        Deck deck1 = new Deck();
        Deck deck2 = new Deck();

        // ACT: Sacar primeras cartas de cada uno
        Card first1 = deck1.drawCard();
        Card first2 = deck2.drawCard();

        // ASSERT: Probabilísticamente deben ser diferentes
        // (Hay 1.9% de probabilidad de que sean iguales por azar)
        boolean foundDifference = !first1.equals(first2);

        // Verificar más cartas para aumentar confianza
        for (int i = 0; i < 10 && !foundDifference; i++) {
            Card c1 = deck1.drawCard();
            Card c2 = deck2.drawCard();
            if (!c1.equals(c2)) {
                foundDifference = true;
                break;
            }
        }

        assertTrue(foundDifference,
                "Shuffled decks should have different order (probabilistic test)");
    }

    // ==================== TESTS DE CLEAR ====================

    /**
     * TEST 11: Verificar que clear vacía todo.
     * Prueba: clear() método
     * Resultado esperado: Mazo y discard pile vacíos
     */
    @Test
    @DisplayName("Clear should empty both deck and discard pile")
    void testClear() {
        // ARRANGE: Añadir carta al discard pile
        deck.addToDiscardPile(new Card(Card.Rank.FIVE, Card.Suit.HEARTS));

        // ACT: Limpiar todo
        deck.clear();

        // ASSERT: Todo debe estar vacío
        assertEquals(0, deck.size(), "Deck should be empty after clear");
        assertEquals(0, deck.getDiscardPileSize(), "Discard pile should be empty after clear");
    }

    // ==================== TESTS DE isEmpty() ====================

    /**
     * TEST 12: Verificar que isEmpty funciona correctamente.
     * Prueba: isEmpty() en diferentes estados
     * Resultado esperado: Correcto en cada caso
     */
    @Test
    @DisplayName("Deck should be empty after drawing all cards")
    void testDeckEmpty() throws EmptyDeckException {
        // ARRANGE & ASSERT: Mazo nuevo no está vacío
        assertFalse(deck.isEmpty(), "New deck should not be empty");

        // ACT: Sacar todas las cartas
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        // ASSERT: Ahora sí debe estar vacío
        assertTrue(deck.isEmpty(), "Deck should be empty after drawing all cards");
    }

    // ==================== TESTS DE EDGE CASES ====================

    /**
     * TEST 13: Verificar que añadir null no afecta el discard pile.
     * Prueba: addToDiscardPile(null)
     * Resultado esperado: No se añade, no hay error
     */
    @Test
    @DisplayName("Adding null card should not affect discard pile")
    void testAddNullToDiscard() {
        // ARRANGE: Tamaño inicial
        int initialSize = deck.getDiscardPileSize();

        // ACT: Intentar añadir null
        deck.addToDiscardPile((Card) null);

        // ASSERT: Tamaño no debe cambiar
        assertEquals(initialSize, deck.getDiscardPileSize(),
                "Adding null should not change discard pile size");
    }

    /**
     * TEST 14: Verificar reshuffle con solo una carta en discard.
     * Prueba: Reshuffle cuando discard pile tiene ≤1 cartas
     * Resultado esperado: No se debe rebarajar (no hay suficientes cartas)
     */
    @Test
    @DisplayName("Should not reshuffle with only top card in discard")
    void testNoReshuffleWithOneCard() throws EmptyDeckException {
        // ARRANGE: Vaciar mazo excepto una carta
        for (int i = 0; i < 51; i++) {
            deck.drawCard();
        }
        Card lastCard = deck.drawCard();
        deck.addToDiscardPile(lastCard);

        assertEquals(0, deck.size(), "Deck should be empty");
        assertEquals(1, deck.getDiscardPileSize(), "Should have 1 card in discard");

        // ACT & ASSERT: No debe poder sacar más cartas
        assertThrows(EmptyDeckException.class, () -> deck.drawCard(),
                "Should not be able to draw when only top card remains");
    }

    /**
     * TEST 15: Verificar múltiples reshuffles consecutivos.
     * Prueba: Agotar y rebarajar varias veces
     * Resultado esperado: Funciona correctamente cada vez
     */
    @Test
    @DisplayName("Should handle multiple consecutive reshuffles")
    void testMultipleReshuffles() throws EmptyDeckException {
        // Primera ronda: Sacar 52 cartas
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            deck.addToDiscardPile(card);
        }

        // Primera reshuffle
        Card card1 = deck.drawCard();
        assertNotNull(card1, "First reshuffle should work");

        // Segunda ronda: Sacar todas de nuevo
        deck.addToDiscardPile(card1);
        for (int i = 0; i < 50; i++) {
            Card card = deck.drawCard();
            deck.addToDiscardPile(card);
        }

        // Segunda reshuffle
        Card card2 = deck.drawCard();
        assertNotNull(card2, "Second reshuffle should work");
    }
}