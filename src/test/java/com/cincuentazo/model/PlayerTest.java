package com.cincuentazo.model;

import com.cincuentazo.exception.InvalidCardException;
import com.cincuentazo.exception.GameException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Unit tests for the Player class.
 * Tests player creation, hand management, and card playability.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
@DisplayName("Player Class Tests")
class PlayerTest {

    private Player humanPlayer;
    private Player machinePlayer;
    private Card testCard1;
    private Card testCard2;
    private Card aceCard;

    /**
     * Sets up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        // ARRANGE: Crear jugadores y cartas de prueba
        humanPlayer = new Player("TestPlayer", true);
        machinePlayer = new Player("Machine1", false);

        testCard1 = new Card(Card.Rank.FIVE, Card.Suit.HEARTS);
        testCard2 = new Card(Card.Rank.TEN, Card.Suit.DIAMONDS);
        aceCard = new Card(Card.Rank.ACE, Card.Suit.SPADES);
    }

    // ==================== TESTS DE CONSTRUCCIÓN ====================

    /**
     * TEST 1: Verificar que el jugador se crea correctamente.
     * Prueba: Constructor con nombre y tipo
     * Resultado esperado: Atributos correctos
     */
    @Test
    @DisplayName("Should create player with correct name and type")
    void testPlayerCreation() {
        // ASSERT: Verificar nombre y tipo
        assertEquals("TestPlayer", humanPlayer.getName(),
                "Player should have the provided name");
        assertTrue(humanPlayer.isHuman(),
                "Player created with isHuman=true should be human");

        assertEquals("Machine1", machinePlayer.getName(),
                "Machine player should have correct name");
        assertFalse(machinePlayer.isHuman(),
                "Player created with isHuman=false should not be human");
    }

    /**
     * TEST 2: Verificar estado inicial del jugador.
     * Prueba: Jugador recién creado
     * Resultado esperado: Mano vacía, no eliminado
     */
    @Test
    @DisplayName("New player should have empty hand and not be eliminated")
    void testInitialState() {
        // ASSERT: Estado inicial correcto
        assertEquals(0, humanPlayer.getHandSize(),
                "New player should have empty hand");
        assertFalse(humanPlayer.isEliminated(),
                "New player should not be eliminated");
        assertNotNull(humanPlayer.getHand(),
                "Player's hand should not be null");
    }

    // ==================== TESTS DE AÑADIR CARTAS ====================

    /**
     * TEST 3: Verificar que se pueden añadir cartas.
     * Prueba: addCard() método
     * Resultado esperado: Carta en la mano
     */
    @Test
    @DisplayName("Should add card to player's hand")
    void testAddCard() {
        // ACT: Añadir carta
        humanPlayer.addCard(testCard1);

        // ASSERT: Carta debe estar en la mano
        assertEquals(1, humanPlayer.getHandSize(),
                "Hand size should be 1 after adding card");
        assertTrue(humanPlayer.getHand().contains(testCard1),
                "Hand should contain the added card");
    }

    /**
     * TEST 4: Verificar que las cartas del jugador humano quedan boca arriba.
     * Prueba: Cartas añadidas a jugador humano
     * Resultado esperado: setFaceUp(true) llamado automáticamente
     */
    @Test
    @DisplayName("Human player cards should be face up")
    void testHumanCardsFaceUp() {
        // ACT: Añadir carta a jugador humano
        humanPlayer.addCard(testCard1);

        // ASSERT: Carta debe estar boca arriba
        assertTrue(testCard1.isFaceUp(),
                "Cards added to human player should be face up");
    }

    /**
     * TEST 5: Verificar que las cartas del jugador máquina quedan boca abajo.
     * Prueba: Cartas añadidas a jugador máquina
     * Resultado esperado: Face up no se modifica (queda false)
     */
    @Test
    @DisplayName("Machine player cards should be face down")
    void testMachineCardsFaceDown() {
        // ARRANGE: Carta nueva (face down por defecto)
        Card card = new Card(Card.Rank.KING, Card.Suit.CLUBS);

        // ACT: Añadir a máquina
        machinePlayer.addCard(card);

        // ASSERT: Debe seguir boca abajo
        assertFalse(card.isFaceUp(),
                "Cards added to machine player should remain face down");
    }

    // ==================== TESTS DE REMOVER CARTAS ====================

    /**
     * TEST 6: Verificar que se pueden remover cartas.
     * Prueba: removeCard() con carta válida
     * Resultado esperado: Carta removida, tamaño disminuye
     * Mensaje de error si falla: "Card not found in player's hand"
     */
    @Test
    @DisplayName("Should remove card from player's hand")
    void testRemoveCard() throws InvalidCardException {
        // ARRANGE: Añadir dos cartas
        humanPlayer.addCard(testCard1);
        humanPlayer.addCard(testCard2);

        // ACT: Remover una carta
        boolean removed = humanPlayer.removeCard(testCard1);

        // ASSERT: Carta debe estar removida
        assertTrue(removed, "removeCard should return true");
        assertEquals(1, humanPlayer.getHandSize(),
                "Hand size should decrease after removing card");
        assertFalse(humanPlayer.getHand().contains(testCard1),
                "Removed card should not be in hand");
    }

    /**
     * TEST 7: Verificar que remover carta inexistente lanza excepción.
     * Prueba: removeCard() con carta que no está en mano
     * Resultado esperado: InvalidCardException
     * Mensaje esperado: "Card not found in player's hand"
     */
    @Test
    @DisplayName("Removing non-existent card should throw InvalidCardException")
    void testRemoveNonExistentCard() {
        // ARRANGE: Añadir solo una carta
        humanPlayer.addCard(testCard1);

        // ACT & ASSERT: Intentar remover otra carta debe fallar
        InvalidCardException exception = assertThrows(
                InvalidCardException.class,
                () -> humanPlayer.removeCard(testCard2),
                "Should throw InvalidCardException when card is not in hand"
        );

        assertTrue(exception.getMessage().contains("not found"),
                "Exception message should indicate card not found");
    }

    /**
     * TEST 8: Verificar que remover null lanza excepción.
     * Prueba: removeCard(null)
     * Resultado esperado: InvalidCardException
     * Mensaje esperado: "Cannot remove null card"
     */
    @Test
    @DisplayName("Removing null card should throw InvalidCardException")
    void testRemoveNullCard() {
        // ACT & ASSERT: Remover null debe lanzar excepción
        InvalidCardException exception = assertThrows(
                InvalidCardException.class,
                () -> humanPlayer.removeCard(null),
                "Should throw InvalidCardException when removing null"
        );

        assertTrue(exception.getMessage().contains("null"),
                "Exception message should mention null");
    }

    // ==================== TESTS DE PLAYABLE INTERFACE ====================

    /**
     * TEST 9: Verificar que canPlay detecta cartas jugables.
     * Prueba: canPlay() / hasPlayableCard()
     * Resultado esperado: true cuando hay cartas válidas
     */
    @Test
    @DisplayName("Should correctly identify if player has playable cards")
    void testHasPlayableCard() {
        // ARRANGE: Añadir cartas que no exceden 50
        humanPlayer.addCard(testCard1); // 5
        humanPlayer.addCard(testCard2); // 10

        // ACT & ASSERT: Verificar en diferentes sumas
        assertTrue(humanPlayer.hasPlayableCard(0),
                "Player should be able to play at sum 0");
        assertTrue(humanPlayer.hasPlayableCard(40),
                "Player should be able to play at sum 40 (40+5=45)");
        assertTrue(humanPlayer.hasPlayableCard(45),
                "Player should be able to play at sum 45 (45+5=50)");
    }

    /**
     * TEST 10: Verificar que canPlay retorna false cuando no hay cartas jugables.
     * Prueba: canPlay() cuando todas las cartas exceden 50
     * Resultado esperado: false
     */
    @Test
    @DisplayName("Should return false when no cards are playable")
    void testNoPlayableCards() {
        // ARRANGE: Añadir cartas
        humanPlayer.addCard(testCard1); // 5
        humanPlayer.addCard(testCard2); // 10

        // ACT & ASSERT: Con suma 46, ninguna carta es jugable
        assertFalse(humanPlayer.hasPlayableCard(46),
                "Player should not have playable cards (46+5=51, 46+10=56)");
    }

    /**
     * TEST 11: Verificar que getPlayableCards retorna lista correcta.
     * Prueba: getPlayableCards()
     * Resultado esperado: Solo cartas que no exceden 50
     */
    @Test
    @DisplayName("Should return list of playable cards")
    void testGetPlayableCards() {
        // ARRANGE: Añadir tres cartas
        humanPlayer.addCard(testCard1); // 5
        humanPlayer.addCard(testCard2); // 10
        humanPlayer.addCard(aceCard);   // 1 o 10

        // ACT: Obtener cartas jugables con suma 42
        List<Card> playable = humanPlayer.getPlayableCards(42);

        // ASSERT: Solo el 5 y el As(1) son jugables
        assertTrue(playable.contains(testCard1),
                "Five should be playable at sum 42 (42+5=47)");
        assertTrue(playable.contains(aceCard),
                "Ace should be playable at sum 42 (42+1=43)");
        assertFalse(playable.contains(testCard2),
                "Ten should not be playable at sum 42 (42+10=52)");
    }

    /**
     * TEST 12: Verificar que getPlayableCards retorna lista vacía cuando no hay cartas jugables.
     * Prueba: getPlayableCards() sin opciones
     * Resultado esperado: Lista vacía
     */
    @Test
    @DisplayName("Should return empty list when no cards are playable")
    void testGetPlayableCardsEmpty() {
        // ARRANGE: Añadir carta que excede
        humanPlayer.addCard(testCard2); // 10

        // ACT: Buscar cartas jugables con suma 45
        List<Card> playable = humanPlayer.getPlayableCards(45);

        // ASSERT: Lista debe estar vacía (45+10=55)
        assertTrue(playable.isEmpty(),
                "Should return empty list when no cards are playable");
    }

    /**
     * TEST 13: Verificar método playCard de la interfaz Playable.
     * Prueba: playCard() que implementa Playable
     * Resultado esperado: Nueva suma correcta, carta removida
     * Mensaje de error: "Card would exceed table sum of 50"
     */
    @Test
    @DisplayName("playCard should calculate new sum and remove card")
    void testPlayCardInterface() throws GameException {
        // ARRANGE: Añadir carta
        humanPlayer.addCard(testCard1); // 5

        // ACT: Jugar carta con suma inicial 20
        int newSum = humanPlayer.playCard(testCard1, 20);

        // ASSERT: Nueva suma debe ser 25, carta removida
        assertEquals(25, newSum, "New sum should be 20+5=25");
        assertEquals(0, humanPlayer.getHandSize(),
                "Card should be removed after playing");
    }

    /**
     * TEST 14: Verificar que playCard lanza excepción cuando excede 50.
     * Prueba: playCard() con carta que excede límite
     * Resultado esperado: InvalidCardException
     * Mensaje: "Playing this card would exceed 50"
     */
    @Test
    @DisplayName("playCard should throw exception when exceeding 50")
    void testPlayCardExceedsLimit() {
        // ARRANGE: Añadir carta de 10
        humanPlayer.addCard(testCard2);

        // ACT & ASSERT: Intentar jugar con suma 45 debe fallar (45+10=55)
        InvalidCardException exception = assertThrows(
                InvalidCardException.class,
                () -> humanPlayer.playCard(testCard2, 45),
                "Should throw exception when card would exceed 50"
        );

        assertTrue(exception.getMessage().contains("exceed"),
                "Exception should mention exceeding limit");
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    /**
     * TEST 15: Verificar que se puede marcar jugador como eliminado.
     * Prueba: setEliminated()
     * Resultado esperado: Estado cambia correctamente
     */
    @Test
    @DisplayName("Should correctly set elimination status")
    void testSetEliminated() {
        // ARRANGE & ASSERT: Inicialmente no eliminado
        assertFalse(humanPlayer.isEliminated(),
                "Player should start not eliminated");

        // ACT: Eliminar jugador
        humanPlayer.setEliminated(true);

        // ASSERT: Debe estar eliminado
        assertTrue(humanPlayer.isEliminated(),
                "Player should be eliminated after setEliminated(true)");

        // ACT: Restaurar jugador
        humanPlayer.setEliminated(false);

        // ASSERT: No debe estar eliminado
        assertFalse(humanPlayer.isEliminated(),
                "Player should not be eliminated after setEliminated(false)");
    }

    /**
     * TEST 16: Verificar que clearHand vacía la mano.
     * Prueba: clearHand()
     * Resultado esperado: Mano vacía, cartas retornadas
     */
    @Test
    @DisplayName("Should clear all cards from hand")
    void testClearHand() {
        // ARRANGE: Añadir tres cartas
        humanPlayer.addCard(testCard1);
        humanPlayer.addCard(testCard2);
        humanPlayer.addCard(aceCard);

        // ACT: Limpiar mano
        List<Card> clearedCards = humanPlayer.clearHand();

        // ASSERT: Mano vacía, cartas retornadas
        assertEquals(0, humanPlayer.getHandSize(),
                "Hand should be empty after clearHand");
        assertEquals(3, clearedCards.size(),
                "clearHand should return all cleared cards");
        assertTrue(clearedCards.contains(testCard1),
                "Cleared cards should include testCard1");
        assertTrue(clearedCards.contains(testCard2),
                "Cleared cards should include testCard2");
        assertTrue(clearedCards.contains(aceCard),
                "Cleared cards should include aceCard");
    }

    // ==================== TESTS DE toString() ====================

    /**
     * TEST 17: Verificar representación en string.
     * Prueba: toString()
     * Resultado esperado: Incluye nombre y tipo
     */
    @Test
    @DisplayName("toString should include player name and type")
    void testToString() {
        // ACT
        String humanStr = humanPlayer.toString();
        String machineStr = machinePlayer.toString();

        // ASSERT: Debe contener información relevante
        assertTrue(humanStr.contains("TestPlayer"),
                "Human player string should contain name");
        assertTrue(humanStr.contains("Human"),
                "Human player string should indicate type");

        assertTrue(machineStr.contains("Machine1"),
                "Machine player string should contain name");
        assertTrue(machineStr.contains("Machine"),
                "Machine player string should indicate type");
    }

    // ==================== TESTS DE EDGE CASES ====================

    /**
     * TEST 18: Verificar que añadir null no afecta la mano.
     * Prueba: addCard(null)
     * Resultado esperado: No se añade, no hay error
     */
    @Test
    @DisplayName("Adding null card should not affect hand")
    void testAddNullCard() {
        // ARRANGE: Tamaño inicial
        int initialSize = humanPlayer.getHandSize();

        // ACT: Intentar añadir null
        humanPlayer.addCard(null);

        // ASSERT: Tamaño no debe cambiar
        assertEquals(initialSize, humanPlayer.getHandSize(),
                "Adding null should not change hand size");
    }

    /**
     * TEST 19: Verificar gestión de múltiples cartas.
     * Prueba: Múltiples operaciones en la mano
     * Resultado esperado: Todas las operaciones correctas
     */
    @Test
    @DisplayName("Should handle multiple cards in hand correctly")
    void testMultipleCards() {
        // ACT: Añadir varias cartas
        humanPlayer.addCard(testCard1);
        humanPlayer.addCard(testCard2);
        humanPlayer.addCard(aceCard);

        // ASSERT: Verificar estado
        assertEquals(3, humanPlayer.getHandSize(),
                "Should have 3 cards");

        List<Card> hand = humanPlayer.getHand();
        assertTrue(hand.contains(testCard1), "Hand should contain testCard1");
        assertTrue(hand.contains(testCard2), "Hand should contain testCard2");
        assertTrue(hand.contains(aceCard), "Hand should contain aceCard");
    }

    /**
     * TEST 20: Verificar caso límite con suma exacta de 50.
     * Prueba: Cartas jugables cuando la suma es 50
     * Resultado esperado: Solo carta 9 (neutral) es jugable
     */
    @Test
    @DisplayName("Should handle edge case at sum of 50")
    void testPlayableCardsAtFifty() {
        // ARRANGE: Añadir carta neutral (9)
        Card nineCard = new Card(Card.Rank.NINE, Card.Suit.HEARTS);
        humanPlayer.addCard(nineCard);

        // ACT & ASSERT: Con suma 50, solo el 9 es jugable
        assertTrue(humanPlayer.hasPlayableCard(50),
                "Nine should be playable at sum 50");

        List<Card> playable = humanPlayer.getPlayableCards(50);
        assertTrue(playable.contains(nineCard),
                "Playable cards at 50 should include nine");
    }
}