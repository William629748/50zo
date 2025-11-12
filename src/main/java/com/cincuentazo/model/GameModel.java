package com.cincuentazo.model;

import com.cincuentazo.exception.GameException;
import com.cincuentazo.exception.InvalidCardException;
import com.cincuentazo.interfaces.GameEventListener;
import com.cincuentazo.interfaces.UIUpdateListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the game model, handling game state, rules, and player interactions.
 * Implements the core logic of the "Cincuentazo" game.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameModel {

    private LinkedList<Player> players;
    private LinkedList<Card> deck;
    private LinkedList<Card> discardPile;
    private int tableSum;
    private Card tableCard;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Player winner;

    // Listeners para notificar al controlador
    private GameEventListener gameEventListener;
    private UIUpdateListener uiUpdateListener;

    public GameModel() {
        this.players = new LinkedList<>();
        this.deck = new LinkedList<>();
        this.discardPile = new LinkedList<>();
        this.tableSum = 0;
        this.currentPlayerIndex = 0;
        this.gameOver = false;
        this.winner = null;
    }

    // Setters para los listeners
    public void setGameEventListener(GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    public void setUiUpdateListener(UIUpdateListener uiUpdateListener) {
        this.uiUpdateListener = uiUpdateListener;
    }

    /**
     * Initializes the game with the specified number of machine players and a human player.
     *
     * @param numMachines the number of machine players (1-3)
     * @param humanUsername the name for the human player
     * @throws GameException if the number of machines is invalid or deck creation fails
     */
    public void initializeGame(int numMachines, String humanUsername) throws GameException {
        if (numMachines < 1 || numMachines > 3) {
            throw new GameException("Invalid number of machine players. Must be between 1 and 3.");
        }

        players.clear();
        deck = createStandardDeck();
        discardPile.clear();
        tableSum = 0;
        tableCard = null;
        gameOver = false;
        winner = null;

        // Crear al jugador humano
        players.add(new HumanPlayer(humanUsername));

        // Crear a los jugadores máquina
        for (int i = 0; i < numMachines; i++) {
            players.add(new MachinePlayer("Machine " + (i + 1)));
        }

        Collections.shuffle(players); // Aleatoriza el orden
        dealInitialCards();

        currentPlayerIndex = 0; // El primer jugador empieza

        // Poner la primera carta en la mesa del mazo
        if (!deck.isEmpty()) {
            tableCard = deck.removeFirst();
            tableSum = tableCard.calculateValue(0); // Valor inicial
            // Si sale un comodín o carta especial al inicio, se aplica su valor base
        }

        // Notificar UI
        if (uiUpdateListener != null) {
            uiUpdateListener.onUIUpdateRequired();
        }
    }

    /**
     * Creates a standard deck of cards using Enums.
     */
    private LinkedList<Card> createStandardDeck() {
        LinkedList<Card> newDeck = new LinkedList<>();

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                // CORRECCIÓN: Primero 'rank', luego 'suit'
                newDeck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(newDeck);
        return newDeck;
    }

    /**
     * Deals 4 cards to each player.
     */
    private void dealInitialCards() {
        for (Player player : players) {
            player.getHand().clear();
            for (int i = 0; i < 4; i++) {
                if (!deck.isEmpty()) {
                    player.addCard(deck.removeFirst());
                }
            }
        }
    }

    /**
     * Plays a card from the current player's hand.
     */
    public void playCard(Card card) throws GameException {
        if (gameOver) throw new GameException("Game is over.");

        Player current = getCurrentPlayer();
        if (!current.getHand().contains(card)) {
            throw new InvalidCardException("Player does not have this card.");
        }

        int cardValue = card.calculateValue(tableSum);
        if (tableSum + cardValue > 50) {
            eliminatePlayer(current);
            throw new InvalidCardException("Playing this card exceeds 50! Player eliminated.");
        }

        // Jugar la carta
        current.removeCard(card);
        discardPile.add(card);
        tableCard = card;
        tableSum += cardValue;

        // Notificar cambio
        if (uiUpdateListener != null) {
            uiUpdateListener.onTableSumChanged(tableSum);
            uiUpdateListener.onUIUpdateRequired();
        }
    }

    /**
     * Current player draws a card from the deck.
     */
    public void drawCard() throws GameException {
        if (deck.isEmpty()) {
            if (discardPile.isEmpty()) {
                throw new GameException("No cards left in deck or discard pile.");
            }
            // Reciclar descarte
            deck.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(deck);
        }

        Player current = getCurrentPlayer();
        if (current != null && !current.isEliminated()) {
            current.addCard(deck.removeFirst());
            if (uiUpdateListener != null) uiUpdateListener.onUIUpdateRequired();
        }
    }

    /**
     * Advances to the next turn.
     */
    public void nextTurn() {
        if (players.isEmpty()) return;

        int attempts = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            attempts++;
        } while (getCurrentPlayer().isEliminated() && attempts < players.size());

        // Verificar si queda solo un jugador
        checkWinCondition();
    }

    private void eliminatePlayer(Player player) {
        player.setEliminated(true);
        // Devolver cartas al mazo (opcional, o al descarte)
        discardPile.addAll(player.getHand());
        player.getHand().clear();

        if (gameEventListener != null) {
            gameEventListener.onPlayerEliminated(player);
        }
        checkWinCondition();
    }

    private void checkWinCondition() {
        long activePlayers = players.stream().filter(p -> !p.isEliminated()).count();
        if (activePlayers <= 1) {
            gameOver = true;
            winner = players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
            if (gameEventListener != null && winner != null) {
                gameEventListener.onGameEnd(winner);
            }
        }
    }

    // Getters
    public Player getCurrentPlayer() {
        if (players == null || players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public LinkedList<Card> getDeck() {
        return deck;
    }

    public int getTableSum() {
        return tableSum;
    }

    public Card getTableCard() {
        return tableCard;
    }

    public boolean isGameEnded() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }
}