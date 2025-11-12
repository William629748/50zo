package com.cincuentazo.model;

import com.cincuentazo.exception.*;
import com.cincuentazo.interfaces.GameEventListener;
import com.cincuentazo.interfaces.UIUpdateListener;

import java.util.*;

/**
 * Main game model that manages the game state and logic.
 * Uses LinkedList for managing players (queue behavior).
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
    private Card tableCard;
    private boolean gameStarted;
    private boolean gameEnded;
    private Player winner;

    /**
     * Constructs a new GameModel.
     */
    public GameModel() {
        this.deck = new Deck();
        this.players = new LinkedList<>();
        this.currentPlayerIndex = 0;
        this.tableSum = 0;
        this.gameStarted = false;
        this.gameEnded = false;
    }

    private GameEventListener gameEventListener;
    private UIUpdateListener uiUpdateListener;

    /**
     * Sets the game event listener.
     *
     * @param listener the listener to set
     */
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    /**
     * Sets the UI update listener.
     *
     * @param listener the listener to set
     */
    public void setUiUpdateListener(UIUpdateListener listener) {
        this.uiUpdateListener = listener;
    }

    /**
     * Initializes the game with the specified number of machine players.
     *
     * @param numMachinePlayers number of machine players (1-3)
     * @param humanUsername the username for the human player
     * @throws GameException if invalid number of players
     */
    public void initializeGame(int numMachinePlayers, String humanUsername) throws GameException {
        if (numMachinePlayers < 1 || numMachinePlayers > 3) {
            throw new GameException("Number of machine players must be between 1 and 3");
        }

        players.clear();
        deck = new Deck();

        // Add human player with custom username
        String playerName = (humanUsername != null && !humanUsername.trim().isEmpty())
                ? humanUsername.trim()
                : "You";
        players.add(new Player(playerName, true));

        // Add machine players
        for (int i = 1; i <= numMachinePlayers; i++) {
            players.add(new Player("Machine " + i, false));
        }

        dealInitialCards();
        setupTable();

        gameStarted = true;
        gameEnded = false;
        currentPlayerIndex = 0;
    }

    /**
     * Deals initial cards to all players.
     *
     * @throws EmptyDeckException if deck doesn't have enough cards
     */
    private void dealInitialCards() throws EmptyDeckException {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
                Card card = deck.drawCard();
                player.addCard(card);
            }
        }
    }

    /**
     * Sets up the table with an initial card.
     *
     * @throws EmptyDeckException if deck is empty
     */
    private void setupTable() throws EmptyDeckException {
        tableCard = deck.drawCard();
        tableCard.setFaceUp(true);
        deck.addToDiscardPile(tableCard);
        tableSum = tableCard.calculateValue(0);
    }

    /**
     * Plays a card from the current player's hand.
     *
     * @param card the card to play
     * @throws GameException if game hasn't started or card is invalid
     * @throws InvalidCardException if card cannot be played
     */
    public void playCard(Card card) throws GameException, InvalidCardException {
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

        // Validate card can be played
        int cardValue = card.calculateValue(tableSum);
        int newSum = tableSum + cardValue;

        if (newSum > MAX_TABLE_SUM) {
            throw new InvalidCardException("Card would exceed table sum of " + MAX_TABLE_SUM);
        }

        // Remove card from player's hand
        currentPlayer.removeCard(card);

        // Update table
        tableCard = card;
        tableCard.setFaceUp(true);
        tableSum = newSum;
        deck.addToDiscardPile(card);
    }

    /**
     * Current player draws a card from the deck.
     *
     * @throws EmptyDeckException if deck is empty
     * @throws GameException if game hasn't started
     */
    public void drawCard() throws EmptyDeckException, GameException {
        if (!gameStarted) {
            throw new GameException("Game has not started");
        }

        Player currentPlayer = getCurrentPlayer();
        Card card = deck.drawCard();
        currentPlayer.addCard(card);
    }

    /**
     * Advances to the next player's turn.
     * Checks for player elimination and game end conditions.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        // Skip eliminated players
        while (getCurrentPlayer().isEliminated() && getActivePlayers().size() > 1) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }

        // Check if current player should be eliminated
        Player currentPlayer = getCurrentPlayer();
        if (!currentPlayer.isEliminated() && !currentPlayer.hasPlayableCard(tableSum)) {
            eliminatePlayer(currentPlayer);
        }

        // Check for game end
        checkGameEnd();
    }

    /**
     * Eliminates a player from the game.
     *
     * @param player the player to eliminate
     */
    private void eliminatePlayer(Player player) {
        player.setEliminated(true);
        List<Card> cards = player.clearHand();
        deck.addToDiscardPile(cards);
    }

    /**
     * Checks if the game has ended and sets the winner.
     */
    private void checkGameEnd() {
        List<Player> activePlayers = getActivePlayers();
        if (activePlayers.size() == 1) {
            gameEnded = true;
            winner = activePlayers.get(0);
        }
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Gets all active (non-eliminated) players.
     *
     * @return list of active players
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
     * Gets all players in the game.
     *
     * @return list of all players
     */
    public LinkedList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current table sum.
     *
     * @return the table sum
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Gets the current card on the table.
     *
     * @return the table card
     */
    public Card getTableCard() {
        return tableCard;
    }

    /**
     * Gets the deck.
     *
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Checks if the game has started.
     *
     * @return true if started, false otherwise
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Checks if the game has ended.
     *
     * @return true if ended, false otherwise
     */
    public boolean isGameEnded() {
        return gameEnded;
    }

    /**
     * Gets the winner of the game.
     *
     * @return the winning player, or null if game hasn't ended
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Gets the maximum allowed table sum.
     *
     * @return the maximum sum (50)
     */
    public static int getMaxTableSum() {
        return MAX_TABLE_SUM;
    }
}