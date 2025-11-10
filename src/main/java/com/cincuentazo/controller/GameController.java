package com.cincuentazo.controller;

import com.cincuentazo.model.*;
import com.cincuentazo.exception.*;
import com.cincuentazo.interfaces.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.geometry.Pos;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Controller for the main game view.
 * Manages game flow, user interactions, and UI updates.
 * Implements multiple interfaces for event handling and implements MVC pattern.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameController implements CardSelectionListener, GameEventListener, UIUpdateListener {

    @FXML private Label tableSumLabel;
    @FXML private Label tableCardLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label deckSizeLabel;
    @FXML private HBox humanHandBox;
    @FXML private VBox machine1Box;
    @FXML private VBox machine2Box;
    @FXML private VBox machine3Box;
    @FXML private Label statusLabel;
    @FXML private Button endTurnButton;
    @FXML private Button rulesButton;
    @FXML private Button exitButton;



    private GameModel gameModel;
    private boolean cardPlayed;
    private MachinePlayerThread machineThread;
    private final Random random = new Random();

    /**
     * Inner class that handles machine player turns in a separate thread.
     * Implements Runnable for concurrent execution and TurnCallback for notifications.
     */
    private class MachinePlayerThread extends Thread implements TurnCallback {
        private volatile boolean running = true;
        private final Player machinePlayer;

        /**
         * Constructs a MachinePlayerThread for the specified player.
         *
         * @param player the machine player
         */
        public MachinePlayerThread(Player player) {
            this.machinePlayer = player;
            this.setDaemon(true);
        }

        /**
         * Stops the thread execution.
         */
        public void stopThread() {
            running = false;
        }

        @Override
        public void run() {
            try {
                // Wait 2-4 seconds before playing
                int waitTime = 2000 + random.nextInt(2000);
                Thread.sleep(waitTime);

                if (!running) return;

                Platform.runLater(() -> {
                    try {
                        playMachineTurn();
                        onTurnCompleted();
                    } catch (Exception e) {
                        onTurnError("Machine player error: " + e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public void onTurnCompleted() {
            onStatusMessage(machinePlayer.getName() + " completed their turn");
        }

        @Override
        public void onTurnError(String error) {
            showError(error);
        }
    }

    /**
     * Adapter class for handling keyboard events.
     * Implements JavaFX EventHandler interface.
     */
    public class KeyboardAdapter implements javafx.event.EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.SPACE && !cardPlayed) {
                endTurnButton.fire();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                showGameMenu();
            } else if (event.getCode() == KeyCode.H) {
                showHelp();
            }
        }
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        gameModel = new GameModel();
        cardPlayed = false;
        setupKeyboardHandling();
    }

    /**
     * Sets up keyboard event handling.
     */
    private void setupKeyboardHandling() {
        // KeyboardAdapter will be set on the scene in the view class
    }

    /**
     * Starts a new game with the specified number of machine players.
     *
     * @param numMachines number of machine players
     */

    /**
     * Sets up the visibility of machine player boxes.
     *
     * @param numMachines number of machine players
     */
    private void setupMachineBoxes(int numMachines) {
        machine1Box.setVisible(numMachines >= 1);
        machine2Box.setVisible(numMachines >= 2);
        machine3Box.setVisible(numMachines >= 3);
    }

    // ==================== CardSelectionListener Implementation ====================

    @Override
    public void onCardSelected(Card card) {
        if (cardPlayed) {
            onStatusMessage("You already played a card. Click 'End Turn'");
            return;
        }

        if (!gameModel.getCurrentPlayer().isHuman()) {
            onStatusMessage("Wait for your turn!");
            return;
        }
    }

    // ==================== GameEventListener Implementation ====================

    @Override
    public void onTurnStart(Player player) {
        currentPlayerLabel.setText("Current: " + player.getName());

        if (!player.isHuman() && !gameModel.isGameEnded()) {
            endTurnButton.setDisable(true);
            startMachineTurn();
        } else {
            endTurnButton.setDisable(cardPlayed);
        }
    }

    @Override
    public void onTurnEnd(Player player) {
        cardPlayed = false;
        gameModel.nextTurn();
        onUIUpdateRequired();

        if (!gameModel.isGameEnded()) {
            Player nextPlayer = gameModel.getCurrentPlayer();
            onTurnStart(nextPlayer);
            onStatusMessage(nextPlayer.getName() + "'s turn");
        }
    }

    @Override
    public void onPlayerEliminated(Player player) {
        onStatusMessage(player.getName() + " has been eliminated!");
        onUIUpdateRequired();
    }

    @Override
    public void onGameEnd(Player winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("We have a winner!");
        alert.setContentText(winner.getName() + " wins the game!");
        alert.showAndWait();

        if (machineThread != null) {
            machineThread.stopThread();
        }
    }

    // ==================== UIUpdateListener Implementation ====================

    @Override
    public void onUIUpdateRequired() {
        updateTableInfo();
        updatePlayerHands();
        updateCurrentPlayerLabel();
        checkGameEnd();
    }

    @Override
    public void onTableSumChanged(int newSum) {
        tableSumLabel.setText("Sum: " + newSum);
    }

    @Override
    public void onStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // ==================== UI Update Methods ====================

    /**
     * Updates table information display.
     */
    private void updateTableInfo() {
        onTableSumChanged(gameModel.getTableSum());
        Card tableCard = gameModel.getTableCard();
        if (tableCard != null) {
            tableCardLabel.setText(tableCard.toString());
            tableCardLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
        }
        deckSizeLabel.setText("Deck: " + gameModel.getDeck().size());
    }

    /**
     * Updates all player hands display.
     */
    private void updatePlayerHands() {
        // <-- CAMBIO AQUÍ: Toda esta sección fue reemplazada para Corregir Errores 3 y 4.
        // La lógica anterior usaba variables de ejemplo que no existían.
        // Esta nueva lógica obtiene los jugadores reales del 'gameModel'.

        // Asumiendo que 'gameModel.getPlayers()' existe y devuelve List<Player>
        // Primero, obtenemos la lista de objetos genéricos que devuelve tu modelo
        List<Object> objectList = gameModel.getPlayers(); // Esta es la llamada real

// Segundo, la convertimos a una List<Player>
        List<Player> players = objectList.stream()
                .map(obj -> (Player) obj)
                .collect(Collectors.toList());

// El resto del código sigue igual
        if (players == null || players.isEmpty()) {
            return; // No hay nada que actualizar
        }
// ...

        // Busca al jugador humano para actualizar su mano
        Player humanPlayer = null;
        for (Player p : players) {
            if (p.isHuman()) {
                humanPlayer = p;
                break;
            }
        }
        if (humanPlayer != null) {
            updateHumanHand(humanPlayer);
        }

        // Actualiza las manos de los jugadores máquina
        int machineIndex = 1;
        for (Player player : players) {
            if (!player.isHuman()) {
                // Se asegura de no pasar de 3 máquinas
                if (machineIndex <= 3) {
                    updateMachineHand(player, machineIndex++);
                }
            }
        }
        // --- FIN DEL CAMBIO ---
    }

    /**
     * Updates the human player's hand display with clickable cards.
     *
     * @param player the human player
     */
    private void updateHumanHand(Player player) {
        humanHandBox.getChildren().clear();

        if (player.isEliminated()) {
            Label eliminatedLabel = new Label("ELIMINATED");
            eliminatedLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            humanHandBox.getChildren().add(eliminatedLabel);
            return;
        }

        for (Card card : player.getHand()) {
            Button cardButton = createCardButton(card);
            humanHandBox.getChildren().add(cardButton);
        }
    }

    /**
     * Creates a button representing a card with mouse event handling.
     *
     * @param card the card to represent
     * @return the button
     */
    private Button createCardButton(Card card) {
        Button btn = new Button(card.toString());
        btn.setStyle("-fx-font-size: 20px; -fx-min-width: 60px; -fx-min-height: 80px;");

        // Mouse event handling
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-size: 20px; -fx-min-width: 60px; -fx-min-height: 80px; " +
                        "-fx-background-color: #e0e0e0;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-size: 20px; -fx-min-width: 60px; -fx-min-height: 80px;"
        ));

        btn.setOnMouseClicked(e -> onCardSelected(card));

        // Check if card is playable
        int newSum = gameModel.getTableSum() + card.calculateValue(gameModel.getTableSum());
        if (newSum > 50) {
            btn.setDisable(true);
            btn.setStyle(btn.getStyle() + "-fx-opacity: 0.5;");
        }

        return btn;
    }

    /**
     * Updates a machine player's hand display.
     *
     * @param player the machine player
     * @param machineIndex the index of the machine (1-3)
     */
    private void updateMachineHand(Player player, int machineIndex) {
        VBox machineBox = getMachineBox(machineIndex);
        machineBox.getChildren().clear();

        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");
        machineBox.getChildren().add(nameLabel);

        if (player.isEliminated()) {
            Label eliminatedLabel = new Label("ELIMINATED");
            eliminatedLabel.setStyle("-fx-text-fill: red;");
            machineBox.getChildren().add(eliminatedLabel);
            return;
        }

        HBox cardsBox = new HBox(5);
        cardsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < player.getHandSize(); i++) {
            Label cardLabel = new Label("🂠");
            cardLabel.setStyle("-fx-font-size: 24px;");
            cardsBox.getChildren().add(cardLabel);
        }
        machineBox.getChildren().add(cardsBox);
    }

    /**
     * Gets the VBox for a specific machine player.
     *
     * @param index the machine index (1-3)
     * @return the VBox
     */
    private VBox getMachineBox(int index) {
        return switch (index) {
            case 1 -> machine1Box;
            case 2 -> machine2Box;
            case 3 -> machine3Box;
            default -> machine1Box;
        };
    }

    /**
     * Updates the current player label.
     */
    private void updateCurrentPlayerLabel() {
        Player current = gameModel.getCurrentPlayer();
        onTurnStart(current);
    }

    /**
     * Starts a machine player's turn in a separate thread.
     */
    private void startMachineTurn() {
        Player currentPlayer = gameModel.getCurrentPlayer();
        machineThread = new MachinePlayerThread(currentPlayer);
        machineThread.start();
    }

    /**
     * Executes a machine player's turn logic.
     */
    private void playMachineTurn() {
        try {
            Player currentPlayer = gameModel.getCurrentPlayer();
            List<Card> playableCards = currentPlayer.getPlayableCards(gameModel.getTableSum());

            if (playableCards.isEmpty()) {
                onStatusMessage(currentPlayer.getName() + " has no playable cards!");
                Thread.sleep(1000);
                handleEndTurn();
                return;
            }

            // Select random playable card
            Card selectedCard = playableCards.get(random.nextInt(playableCards.size()));
            gameModel.playCard(selectedCard);
            onUIUpdateRequired();
            onStatusMessage(currentPlayer.getName() + " played " + selectedCard);

            // Wait 1-2 seconds before drawing
            Thread.sleep(1000 + random.nextInt(1000));

            gameModel.drawCard();
            onUIUpdateRequired();

            Thread.sleep(500);
            handleEndTurn();

        } catch (Exception e) {
            showError("Machine turn error: " + e.getMessage());
        }
    }

    /**
     * Handles the end turn button click.
     */
    @FXML
    private void handleEndTurn() {
        // Simplemente dejamos el código sin el try-catch

        Player currentPlayer = gameModel.getCurrentPlayer();

        if (!cardPlayed && currentPlayer.isHuman()) {
            onStatusMessage("You must play a card first!");
            return;
        }

        if (cardPlayed) {
            gameModel.drawCard();
        }

        onTurnEnd(currentPlayer);
    }

    /**
     * Checks if the game has ended and shows winner.
     */
    private void checkGameEnd() {
        if (gameModel.isGameEnded()) {
            Player winner = gameModel.getWinner();
            onGameEnd(winner);
        }
    }

    /**
     * Shows an error message dialog.
     *
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows the game menu (pause).
     */
    private void showGameMenu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Menu");
        alert.setHeaderText("Game Paused");
        alert.setContentText("Press OK to continue");
        alert.showAndWait();
    }

    /**
     * Shows help information.
     */
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("Game Rules");
        alert.setContentText(
                "• Keep table sum ≤ 50\n" +
                        "• Cards 2-8,10: add their value\n" +
                        "• Card 9: adds 0\n" +
                        "• J,Q,K: subtract 10\n" +
                        "• A: adds 1 or 10\n\n" +
                        "Shortcuts:\n" +
                        "• SPACE: End turn\n" +
                        "• ESC: Pause\n" +
                        "• H: Help"
        );
        alert.showAndWait();
    }
    @FXML
    private void handleShowRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Rules");
        alert.setHeaderText("How to Play Cincuentazo");

        String rules = """
            OBJECTIVE:
            Be the last player remaining in the game.
            
            BASIC RULES:
            • The table sum must never exceed 50
            • Each player has 4 cards in their hand
            • On your turn, play a card and draw a new one
            • If you cannot play any card without exceeding 50, you are eliminated
            
            CARD VALUES:
            • Cards 2-8 and 10: Add their number value to the sum
            • Card 9: Neutral card (adds 0 to the sum)
            • J, Q, K: Subtract 10 from the sum
            • A (Ace): Adds 1 or 10 (automatically chooses the best option)
            
            GAME FLOW:
            1. Each player starts with 4 cards
            2. One card is placed on the table to start the sum
            3. Players take turns playing cards
            4. After playing, draw a new card from the deck
            5. When the deck is empty, the discard pile is reshuffled
            6. Eliminated players' cards return to the deck
            7. Last player standing wins!
            
            KEYBOARD SHORTCUTS:
            • Number Keys (1/2/3): Quick select opponents
            • Enter: Start game
            • Esc: Exit game
            • Space (in-game): End turn quickly
            • H (in-game): Show help
            
            TIPS:
            • Plan ahead - think about what cards to save
            • Use J, Q, K strategically to lower the sum
            • Watch the current sum carefully
            • Remember: Ace is flexible (1 or 10)
            
            Good luck and have fun! 🎴
            """;

        alert.setContentText(rules);
        alert.getDialogPane().setPrefWidth(600);
        alert.getDialogPane().setPrefHeight(500);
        alert.showAndWait();
    }
    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("The application will close.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /**
     * Gets the keyboard adapter for external setup.
     *
     * @return the keyboard adapter
     */
    public KeyboardAdapter getKeyboardAdapter() {
        return new KeyboardAdapter();
    }
}