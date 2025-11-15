package com.cincuentazo.model;

import com.cincuentazo.exception.*;
import com.cincuentazo.interfaces.GameEventListener;
import com.cincuentazo.interfaces.UIUpdateListener;

import java.util.*;

/**
 * Main game model that manages the overall game state and core logic of Cincuentazo.
 * It handles player management, card dealing, turn progression, card playing,
 * player elimination, and game-end conditions.
 * It uses a LinkedList for managing players to facilitate turn rotation.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameModel {

    private static final int MAX_TABLE_SUM = 50;
    private static final int INITIAL_HAND_SIZE = 4;

    private Deck deck;
    private LinkedList<Player> players;
    private int currentPlayerIndex;
    private int tableSum;
    private Card tableCard; // The last card played on the table
    private boolean gameStarted;
    private boolean gameEnded;
    private Player winner;

    // Listeners for game events and UI updates
    private GameEventListener gameEventListener;
    private UIUpdateListener uiUpdateListener;

    /**
     * Constructs a new GameModel.
     * Initializes the deck, player list, and game state variables.
     */
    public GameModel() {
        this.deck = new Deck();
        this.players = new LinkedList<>();
        this.currentPlayerIndex = 0;
        this.tableSum = 0;
        this.gameStarted = false;
        this.gameEnded = false;
        this.winner = null;
    }

    /**
     * Sets the game event listener. This listener is notified of significant
     * game events such as turn start/end, player elimination, and game end.
     *
     * @param listener The {@link GameEventListener} to set.
     */
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    /**
     * Sets the UI update listener. This listener is notified when the UI needs
     * to be refreshed to reflect changes in the game state.
     *
     * @param listener The {@link UIUpdateListener} to set.
     */
    public void setUiUpdateListener(UIUpdateListener listener) {
        this.uiUpdateListener = listener;
    }

    /**
     * Initializes a new game instance. This involves creating players (human and machines),
     * dealing initial cards, setting up the table, and preparing the game state.
     *
     * @param numMachinePlayers The number of AI opponents (must be between 1 and 3).
     * @param humanUsername The desired username for the human player.
     * @throws GameException If the provided number of machine players is out of the valid range.
     * @throws EmptyDeckException If the deck runs out of cards during initial dealing or table setup.
     */
    public void initializeGame(int numMachinePlayers, String humanUsername) throws GameException {
        if (numMachinePlayers < 1 || numMachinePlayers > 3) {
            throw new GameException("Number of machine players must be between 1 and 3");
        }

        players.clear(); // Clear any previous game's players
        deck = new Deck(); // Create a new, shuffled deck

        // Add human player with custom or default username
        String playerName = (humanUsername != null && !humanUsername.trim().isEmpty())
                ? humanUsername.trim()
                : "You"; // Default name if username is empty
        players.add(new Player(playerName, true)); // 'true' indicates a human player

        // Add specified number of machine players
        for (int i = 1; i <= numMachinePlayers; i++) {
            players.add(new Player("\uD83D\uDC64 CPU " + i, false)); // 'false' indicates a machine player
        }

        dealInitialCards(); // Distribute cards to all players
        setupTable();       // Place the first card on the table

        gameStarted = true;
        gameEnded = false;
        winner = null;
        currentPlayerIndex = 0; // Start with the first player in the list (human)

        // Notify listeners that the UI needs an initial update and the first turn is starting
        if (uiUpdateListener != null) uiUpdateListener.onUIUpdateRequired();
        if (gameEventListener != null) gameEventListener.onTurnStart(getCurrentPlayer());
    }

    /**
     * Deals the initial set of cards to each player at the beginning of the game.
     * Each player receives {@value #INITIAL_HAND_SIZE} cards.
     *
     * @throws EmptyDeckException If the deck does not have enough cards to deal to all players.
     */
    private void dealInitialCards() throws EmptyDeckException {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                Card card = deck.drawCard(); // Draw a card from the deck
                player.addCard(card);       // Add the card to the player's hand
            }
        }
    }

    /**
     * Sets up the game table by drawing the first card from the deck and placing it face-up.
     * This card also establishes the initial `tableSum`.
     *
     * @throws EmptyDeckException If the deck is empty when attempting to draw the initial table card.
     */
    private void setupTable() throws EmptyDeckException {
        tableCard = deck.drawCard(); // Draw the first card for the table
        tableCard.setFaceUp(true);   // Ensure it's face-up
        deck.addToDiscardPile(tableCard); // Add it to the discard pile (conceptually on the table)
        tableSum = tableCard.calculateValue(0); // Calculate initial sum, assuming 0 before this card
        if (uiUpdateListener != null) uiUpdateListener.onTableSumChanged(tableSum);
    }

    /**
     * Allows the current player to play a selected card from their hand onto the table.
     * The card's value is added to the `tableSum`.
     *
     * @param card The {@link Card} to be played by the current player.
     * @throws GameException If the game has not started, has ended, or the current player is eliminated.
     * @throws InvalidCardException If the selected card cannot be played (e.g., it would exceed {@value #MAX_TABLE_SUM}).
     * @throws NoSuchElementException If the card is not found in the player's hand.
     */
    public void playCard(Card card) throws GameException, InvalidCardException, NoSuchElementException {
        if (!gameStarted) {
            throw new GameException("Game has not started");
        }
        if (gameEnded) {
            throw new GameException("Game has ended");
        }

        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer.isEliminated()) {
            throw new GameException("Current player is eliminated");
        }
        if (!currentPlayer.getHand().contains(card)) {
            throw new NoSuchElementException("Card " + card + " not found in current player's hand.");
        }

        // Validate if the card can be played without exceeding MAX_TABLE_SUM
        int cardValue = card.calculateValue(tableSum);
        int newSum = tableSum + cardValue;

        if (newSum > MAX_TABLE_SUM) {
            throw new InvalidCardException("Card would exceed table sum of " + MAX_TABLE_SUM + " (current: " + tableSum + ", card value: " + cardValue + ")");
        }

        currentPlayer.removeCard(card); // Remove the card from the player's hand
        tableCard = card;               // Set the played card as the new table card
        tableCard.setFaceUp(true);      // Ensure it's face-up on the table
        tableSum = newSum;              // Update the table sum
        deck.addToDiscardPile(card);    // Add the played card to the discard pile

        // Notify UI listeners about changes
        if (uiUpdateListener != null) {
            uiUpdateListener.onTableSumChanged(tableSum);
            uiUpdateListener.onUIUpdateRequired(); // General UI update
        }
    }

    /**
     * Allows the current player to draw a card from the deck and add it to their hand.
     * This typically occurs at the end of a player's turn (after playing a card).
     *
     * @throws EmptyDeckException If the deck is empty and no more cards can be drawn.
     * @throws GameException If the game has not started.
     */
    public void drawCard() throws EmptyDeckException, GameException {
        if (!gameStarted) {
            throw new GameException("Game has not started");
        }
        if (deck.isEmpty()) {
            throw new EmptyDeckException("The deck is empty, no more cards to draw.");
        }

        Player currentPlayer = getCurrentPlayer();
        Card card = deck.drawCard();    // Draw a card
        currentPlayer.addCard(card);    // Add to player's hand

        // Notify UI listeners about changes
        if (uiUpdateListener != null) {
            uiUpdateListener.onUIUpdateRequired(); // General UI update (e.g., hand size, deck size)
        }
    }

    /**
     * Advances the game to the next player's turn.
     * It handles cycling through players, skipping eliminated players,
     * checking for new eliminations, and determining if the game has ended.
     */
    public void nextTurn() {
        // Move to the next player in the circular list
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // Skip any players who have already been eliminated
        while (getCurrentPlayer().isEliminated() && getActivePlayers().size() > 1) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        Player currentPlayer = getCurrentPlayer();

        // Check if the current player should be eliminated (no playable cards)
        if (!currentPlayer.isEliminated() && !currentPlayer.hasPlayableCard(tableSum)) {
            eliminatePlayer(currentPlayer);
            if (gameEventListener != null) gameEventListener.onPlayerEliminated(currentPlayer);
        }

        // After potential elimination, check if the game has ended
        checkGameEnd();

        // If the game hasn't ended, notify listeners for the next turn start
        if (!gameEnded && gameEventListener != null) {
            gameEventListener.onTurnStart(getCurrentPlayer());
        }
    }

    /**
     * Eliminates a player from the game.
     * The player's hand is cleared, and their cards are added to the discard pile.
     *
     * @param player The {@link Player} to be eliminated.
     */
    private void eliminatePlayer(Player player) {
        player.setEliminated(true); // Mark the player as eliminated
        List<Card> cardsInHand = player.clearHand(); // Remove all cards from their hand
        deck.addToDiscardPile(cardsInHand); // Add these cards to the discard pile

        // Notify UI to update (e.g., display "ELIMINATED" for the player)
        if (uiUpdateListener != null) uiUpdateListener.onUIUpdateRequired();
    }

    /**
     * Checks the current game state to determine if the game has ended.
     * The game ends when only one active player remains. The remaining player is declared the winner.
     * If the game ends, {@link #gameEnded} is set to true and {@link #winner} is assigned.
     */
    private void checkGameEnd() {
        List<Player> activePlayers = getActivePlayers();
        if (activePlayers.size() == 1) {
            gameEnded = true;
            winner = activePlayers.get(0);
            if (gameEventListener != null) gameEventListener.onGameEnd(winner);
        } else if (activePlayers.isEmpty()) {
            // This case should ideally not happen if there's always a winner,
            // but can be a fallback for unexpected scenarios (e.g., all players eliminated simultaneously).
            gameEnded = true;
            winner = null; // No winner if all eliminated
            if (gameEventListener != null) gameEventListener.onGameEnd(null);
        }
    }

    /**
     * Retrieves the player whose turn it currently is.
     *
     * @return The {@link Player} object representing the current player.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Retrieves a list of all players who are currently active (not eliminated) in the game.
     *
     * @return A {@link List} of {@link Player} objects who are still playing.
     */
    public List<Player> getActivePlayers() {
        List<Player> active = new ArrayList<>();
        for (Player player : players) {
            if (!player.isEliminated()) {
                active.add(player);
            }
        }
        return active;
    }

    /**
     * Retrieves the complete list of all players initially involved in the game,
     * including those who may have been eliminated.
     *
     * @return A {@link LinkedList} of all {@link Player} objects.
     */
    public LinkedList<Player> getPlayers() {
        return players;
    }

    /**
     * Retrieves the current total sum of card values on the table.
     *
     * @return An integer representing the current table sum.
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Retrieves the last card that was played and is currently on the table.
     *
     * @return The {@link Card} object currently on the table.
     */
    public Card getTableCard() {
        return tableCard;
    }

    /**
     * Retrieves the game's deck, which manages drawing and discarding cards.
     *
     * @return The {@link Deck} object used in the game.
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Checks if the game has officially started.
     *
     * @return {@code true} if the game has been initialized and started, {@code false} otherwise.
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Checks if the game has reached an end condition (e.g., a winner has been determined).
     *
     * @return {@code true} if the game has ended, {@code false} otherwise.
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Retrieves the {@link Player} who won the game.
     *
     * @return The winning {@link Player}, or {@code null} if the game has not yet ended or ended without a clear winner.
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Gets the maximum allowed sum on the table before a player is eliminated.
     *
     * @return The maximum table sum (constant value of 50).
     */
    public static int getMaxTableSum() {
        return MAX_TABLE_SUM;
    }
}