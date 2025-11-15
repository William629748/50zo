package com.cincuentazo.controller;

import com.cincuentazo.model.*;
import com.cincuentazo.exception.*;
import com.cincuentazo.interfaces.*;
import com.cincuentazo.view.VictoryStage;
import com.cincuentazo.view.WelcomeStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.List;
import java.util.Random;

/**
 * Controller for the main game view.
 * This class manages the game flow, user interactions, and updates the UI based on game state changes.
 * It implements several interfaces to listen for card selections, game events, and UI update requests,
 * acting as the bridge between the game model and the visual presentation.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameController implements CardSelectionListener, GameEventListener, UIUpdateListener {

    // FXML UI Elements
    @FXML private Label tableSumLabel;
    @FXML private ImageView tableCardImage;
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

    // Game Logic Attributes
    private GameModel gameModel;
    private boolean cardPlayed;
    private MachinePlayerThread machineThread;
    private final Random random = new Random();
    private Stage gameStage;

    /**
     * Inner class representing a thread for handling machine player turns.
     * This thread introduces a delay to simulate thinking time for AI players
     * and executes their turn logic on the JavaFX Application Thread.
     */
    private class MachinePlayerThread extends Thread implements TurnCallback {
        private volatile boolean running = true;
        private final Player machinePlayer;

        /**
         * Constructs a new MachinePlayerThread.
         *
         * @param player The machine player whose turn will be managed by this thread.
         */
        public MachinePlayerThread(Player player) {
            this.machinePlayer = player;
            this.setDaemon(true); // Allows the application to exit even if this thread is running.
        }

        /**
         * Stops the execution of this thread gracefully.
         */
        public void stopThread() {
            running = false;
        }

        /**
         * The main execution logic for the machine player's turn.
         * It introduces a random delay and then schedules the actual turn logic
         * to be run on the JavaFX Application Thread.
         */
        @Override
        public void run() {
            try {
                int waitTime = 2000 + random.nextInt(2000); // Random delay between 2-4 seconds
                Thread.sleep(waitTime);
                if (!running) return; // Check if the thread has been stopped during sleep

                Platform.runLater(() -> {
                    try {
                        playMachineTurn();
                    } catch (Exception e) {
                        onTurnError("Machine player error: " + e.getMessage());
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupt status
                System.err.println("Machine player thread interrupted: " + e.getMessage());
            }
        }

        /**
         * Callback method invoked when a turn is completed.
         * The "turn ended" message logic is centralized in the GameController's onTurnEnd method.
         */
        @Override
        public void onTurnCompleted() {
            // La lógica de mensaje de "terminó turno" está centralizada en onTurnEnd del GameController.
        }

        /**
         * Callback method invoked when an error occurs during a machine player's turn.
         * Displays the error message to the user.
         *
         * @param error The error message to display.
         */
        @Override
        public void onTurnError(String error) {
            showError(error);
        }
    }

    /**
     * An adapter class to handle keyboard input for game shortcuts.
     * This class implements the {@code javafx.event.EventHandler<KeyEvent>} interface
     * to process key press events.
     */
    public class KeyboardAdapter implements javafx.event.EventHandler<KeyEvent> {
        /**
         * Handles the key event triggered by a keyboard press.
         * It checks for specific key codes (SPACE, ESCAPE, H) to perform game actions.
         *
         * @param event The KeyEvent that occurred.
         */
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.SPACE && !cardPlayed) {
                // If SPACE is pressed and a card hasn't been played yet (human player's turn)
                if (gameModel.getCurrentPlayer() != null && gameModel.getCurrentPlayer().isHuman()) {
                    endTurnButton.fire(); // Simulate a click on the end turn button
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                // If ESCAPE is pressed, show the game menu
                showGameMenu();
            } else if (event.getCode() == KeyCode.H) {
                // If H is pressed, show the help/rules dialog
                showHelp();
            }
        }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up a new GameModel and prepares for keyboard input handling.
     */
    @FXML
    public void initialize() {
        gameModel = new GameModel();
        cardPlayed = false;
        setupKeyboardHandling(); // Placeholder for future keyboard setup if needed
    }

    /**
     * Placeholder method for setting up keyboard event handling.
     * Currently, the KeyboardAdapter is used directly by GameStage.
     */
    private void setupKeyboardHandling() {}

    /**
     * Sets the primary game stage for this controller.
     * This is typically the window where the game is displayed.
     *
     * @param stage The Stage instance representing the game window.
     */
    public void setGameStage(Stage stage) {
        this.gameStage = stage;
    }

    /**
     * Starts a new game with the specified number of machine opponents and human player username.
     * Initializes the game model, sets up listeners, and updates the UI.
     *
     * @param numMachines The number of machine players (1-3) to include in the game.
     * @param humanUsername The username for the human player.
     */
    public void startGame(int numMachines, String humanUsername) {
        try {
            gameModel.initializeGame(numMachines, humanUsername);
            gameModel.setGameEventListener(this);
            gameModel.setUiUpdateListener(this);
            setupMachineBoxes(numMachines);
            onUIUpdateRequired(); // Initial UI update
            onStatusMessage("¡Inicio el juego! Tu turno, " + humanUsername + ".");
            if (!gameModel.isGameEnded()) {
                onTurnStart(gameModel.getCurrentPlayer()); // Start the first player's turn
            }
        } catch (GameException e) {
            showError("Failed to start game: " + e.getMessage());
        }
    }

    /**
     * Configures the visibility of machine player hand boxes based on the number of machine players.
     *
     * @param numMachines The total number of machine players in the game.
     */
    private void setupMachineBoxes(int numMachines) {
        machine1Box.setVisible(numMachines >= 1);
        machine2Box.setVisible(numMachines >= 2);
        machine3Box.setVisible(numMachines >= 3);
    }

    /**
     * Handles the event when a card is selected by the human player.
     * Validates the card and attempts to play it through the game model.
     *
     * @param card The Card object that was selected.
     */
    @Override
    public void onCardSelected(Card card) {
        if (cardPlayed) {
            onStatusMessage("Ya seleccionaste una carta. Da click tu boton derecho");
            return;
        }
        if (gameModel.getCurrentPlayer() == null || !gameModel.getCurrentPlayer().isHuman()) {
            onStatusMessage("¡Espera tu turno!");
            return;
        }
        try {
            gameModel.playCard(card);
            cardPlayed = true;
            onUIUpdateRequired();
            onStatusMessage("¡Carta jugada! Da click en tu boton derecho para continuar.");
            endTurnButton.setDisable(false); // Enable end turn button after a card is played
        } catch (InvalidCardException | GameException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Handles the event when a player's turn begins.
     * Updates the current player label and initiates machine player turns if applicable.
     *
     * @param player The Player whose turn is starting.
     */
    @Override
    public void onTurnStart(Player player) {
        currentPlayerLabel.setText("Turno de: " + player.getName());
        if (!player.isHuman() && !gameModel.isGameEnded()) {
            endTurnButton.setDisable(true); // Disable end turn button for machine players
            onStatusMessage("Turno de la máquina: " + player.getName() + "...");
            startMachineTurn(); // Start the machine's turn logic
        } else if (player.isHuman()) {
            // For human player, enable end turn if a card has already been played
            endTurnButton.setDisable(!cardPlayed);
            onStatusMessage("¡Tu turno, " + player.getName() + "!");
        }
    }

    /**
     * Handles the event when a player's turn ends.
     * Resets the card played status, advances to the next turn, and updates the UI.
     *
     * @param player The Player whose turn has just ended.
     */
    @Override
    public void onTurnEnd(Player player) {
        onStatusMessage(player.getName() + " terminó su turno.");
        cardPlayed = false; // Reset for the next human player's turn
        gameModel.nextTurn(); // Advance the game to the next player
        onUIUpdateRequired(); // Update all UI components

        if (!gameModel.isGameEnded()) {
            Player nextPlayer = gameModel.getCurrentPlayer();
            onTurnStart(nextPlayer); // Start the next player's turn
        }
    }

    /**
     * Handles the event when a player is eliminated from the game.
     * Displays a status message and updates the UI.
     *
     * @param player The Player who was eliminated.
     */
    @Override
    public void onPlayerEliminated(Player player) {
        onStatusMessage(player.getName() + " fue eliminado!");
        onUIUpdateRequired(); // Reflect elimination in the UI
    }

    /**
     * Handles the event when the game ends.
     * Schedules the display of the victory screen on the JavaFX Application Thread.
     *
     * @param winner The winning Player of the game.
     */
    @Override
    public void onGameEnd(Player winner) {
        Platform.runLater(() -> showVictoryScreen(winner));
    }

    /**
     * Displays the victory screen, closing the current game stage.
     * Stops any active machine player threads before showing the new stage.
     *
     * @param winner The Player who won the game.
     */
    private void showVictoryScreen(Player winner) {
        if (machineThread != null && machineThread.isAlive()) {
            machineThread.stopThread(); // Ensure machine threads are stopped
        }

        String winnerName = winner.getName();

        if (gameStage != null) {
            gameStage.close(); // Close the main game window
        }

        Stage victoryStageWindow = new Stage();
        VictoryStage victory = new VictoryStage(victoryStageWindow, winnerName);
        victory.show();
    }

    /**
     * Callback method to signal that the UI needs to be updated.
     * Triggers updates for table information, player hands, and checks for game end.
     */
    @Override
    public void onUIUpdateRequired() {
        updateTableInfo();
        updatePlayerHands();
        checkGameEnd();
    }

    /**
     * Callback method invoked when the table sum changes.
     * Updates the corresponding UI label.
     *
     * @param newSum The new sum on the table.
     */
    @Override
    public void onTableSumChanged(int newSum) {
        tableSumLabel.setText("Suma: " + newSum);
    }

    /**
     * Callback method invoked to display a status message to the user.
     * Updates the status label in the UI.
     *
     * @param message The status message to display.
     */
    @Override
    public void onStatusMessage(String message) {
        statusLabel.setText(message);
    }

    /**
     * Updates the visual information displayed on the table,
     * including the current sum and the image of the top card.
     */
    private void updateTableInfo() {
        onTableSumChanged(gameModel.getTableSum()); // Update sum label
        Card tableCard = gameModel.getTableCard();

        if (tableCard != null) {
            try {
                String imagePath = getCardImagePath(tableCard);
                Image cardImage = new Image(getClass().getResourceAsStream(imagePath));
                tableCardImage.setImage(cardImage);
            } catch (Exception e) {
                System.err.println("Error loading table card image: " + getCardImagePath(tableCard));
                tableCardImage.setImage(null); // Clear image on error
            }
        } else {
            tableCardImage.setImage(null); // Clear image if no card on table
        }
        deckSizeLabel.setText("Mazo: " + gameModel.getDeck().size()); // Update deck size
    }

    /**
     * Updates the visual representation of all players' hands (human and machine).
     * Iterates through players and calls specific update methods for each type.
     */
    private void updatePlayerHands() {
        List<Player> players = gameModel.getPlayers();
        if (players == null || players.isEmpty()) return;

        // Find and update human player's hand
        Player humanPlayer = null;
        for (Player p : players) {
            if (p.isHuman()) {
                humanPlayer = p;
                break;
            }
        }
        if (humanPlayer != null) updateHumanHand(humanPlayer);

        // Update machine players' hands
        int machineIndex = 1;
        for (Player player : players) {
            if (!player.isHuman() && machineIndex <= 3) {
                updateMachineHand(player, machineIndex);
                machineIndex++;
            }
        }
    }

    /**
     * Updates the display for the human player's hand.
     * Clears existing cards and adds buttons for each card in hand.
     * If the player is eliminated, an "ELIMINATED" label is shown.
     *
     * @param player The human Player whose hand is to be updated.
     */
    private void updateHumanHand(Player player) {
        humanHandBox.getChildren().clear();
        if (player.isEliminated()) {
            Label eliminatedLabel = new Label("ELIMINADO");
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
     * Determines the image path for a given card based on its rank and suit.
     *
     * @param card The Card object for which to get the image path.
     * @return A String representing the resource path to the card image.
     */
    private String getCardImagePath(Card card) {
        String rank = card.getRank().getSymbol();
        String suit = getSuitName(card.getSuit());

        // Special handling for face cards and Ace
        if (rank.equals("J") || rank.equals("Q") || rank.equals("K") || rank.equals("A")) {
            return "/com/cincuentazo/com.cincuentazo.images/" + rank + suit + ".png";
        }
        // Numeric cards
        return "/com/cincuentazo/com.cincuentazo.images/" + rank.toLowerCase() + suit + ".png";
    }

    /**
     * Converts a Card.Suit enum value to its corresponding string name for image file naming.
     *
     * @param suit The Card.Suit enum value.
     * @return A String representation of the suit (e.g., "hearts", "diamonds").
     */
    private String getSuitName(Card.Suit suit) {
        return switch (suit) {
            case HEARTS -> "hearts";
            case DIAMONDS -> "diamonds";
            case CLUBS -> "clubs";
            case SPADES -> "spades";
        };
    }

    /**
     * Creates a graphical button for a card in the human player's hand.
     * The button displays the card's image and handles selection events.
     * Buttons for cards that would exceed 50 are disabled.
     *
     * @param card The Card object to create a button for.
     * @return A Button configured to display and interact with the card.
     */
    private Button createCardButton(Card card) {
        Button btn = new Button();
        try {
            String imagePath = getCardImagePath(card);
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imageView.setFitWidth(70);
            imageView.setFitHeight(95);
            imageView.setPreserveRatio(true);
            btn.setGraphic(imageView);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-cursor: hand;");
            btn.setPrefSize(75, 100);
        } catch (Exception e) {
            // Fallback to text if image fails to load
            btn.setText(card.toString());
            System.err.println("Failed to load image for card: " + card.toString() + ". Error: " + e.getMessage());
        }

        btn.setOnMouseClicked(e -> onCardSelected(card));

        // Disable cards that would make the sum exceed 50
        int newSum = gameModel.getTableSum() + card.calculateValue(gameModel.getTableSum());
        if (newSum > 50) {
            btn.setDisable(true);
            btn.setOpacity(0.4); // Visually indicate it's disabled
        }
        return btn;
    }

    /**
     * Updates the display for a machine player's hand.
     * Shows the player's name and a series of card backs (or placeholders)
     * to represent the cards in their hand.
     * If the player is eliminated, an "ELIMINATED" label is shown.
     *
     * @param player The machine Player whose hand is to be updated.
     * @param machineIndex The index of the machine player (1, 2, or 3) for UI box selection.
     */
    private void updateMachineHand(Player player, int machineIndex) {
        VBox machineBox = getMachineBox(machineIndex);
        if (machineBox == null) return;
        machineBox.getChildren().clear();

        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        machineBox.getChildren().add(nameLabel);

        if (player.isEliminated()) {
            Label eliminatedLabel = new Label("ELIMINADO");
            eliminatedLabel.setStyle("-fx-text-fill: red;");
            machineBox.getChildren().add(eliminatedLabel);
            return;
        }

        HBox cardsBox = new HBox(5);
        cardsBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < player.getHandSize(); i++) {
            try {
                ImageView cardBack = new ImageView(new Image(getClass().getResourceAsStream("/com/cincuentazo/com.cincuentazo.images/back.png")));
                cardBack.setFitWidth(50);
                cardBack.setFitHeight(70);
                cardBack.setPreserveRatio(true);
                cardsBox.getChildren().add(cardBack);
            } catch (Exception e) {
                // Fallback to a unicode card symbol if card back image fails
                Label cardLabel = new Label("🂠");
                cardLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: lightgray;");
                cardsBox.getChildren().add(cardLabel);
                System.err.println("Failed to load card back image. Error: " + e.getMessage());
            }
        }
        machineBox.getChildren().add(cardsBox);
    }

    /**
     * Returns the VBox corresponding to a specific machine player index.
     *
     * @param index The index of the machine player (1, 2, or 3).
     * @return The VBox UI element for that machine player, or null if index is invalid.
     */
    private VBox getMachineBox(int index) {
        return switch (index) {
            case 1 -> machine1Box;
            case 2 -> machine2Box;
            case 3 -> machine3Box;
            default -> null;
        };
    }

    /**
     * This method is a placeholder and currently does not perform any action.
     * The current player label is updated directly in {@link #onTurnStart(Player)}.
     */
    private void updateCurrentPlayerLabel() {
        // No hace falta llamar a onTurnStart aquí.
    }

    /**
     * Initiates the turn sequence for a machine player.
     * Stops any previously running machine thread and starts a new one.
     */
    private void startMachineTurn() {
        Player currentPlayer = gameModel.getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.isHuman() || gameModel.isGameEnded()) return;

        // Stop any existing machine thread to prevent multiple threads running concurrently
        if (machineThread != null && machineThread.isAlive()) {
            machineThread.stopThread();
        }
        machineThread = new MachinePlayerThread(currentPlayer);
        machineThread.start();
    }

    /**
     * Executes the logic for a machine player's turn.
     * This includes selecting and playing a card, drawing a new card, and ending the turn.
     * All UI updates are scheduled on the JavaFX Application Thread.
     */
    private void playMachineTurn() {
        try {
            Player currentPlayer = gameModel.getCurrentPlayer();
            if (currentPlayer == null || currentPlayer.isHuman() || gameModel.isGameEnded()) return;

            List<Card> playableCards = currentPlayer.getPlayableCards(gameModel.getTableSum());

            if (playableCards.isEmpty()) {
                Platform.runLater(() -> onStatusMessage(currentPlayer.getName() + " no tiene cartas jugables y pasa su turno!"));
                Thread.sleep(1500); // Small delay to show message
                Platform.runLater(this::handleEndTurn); // End turn for the machine
                return;
            }

            // Machine randomly selects a playable card
            Card selectedCard = playableCards.get(random.nextInt(playableCards.size()));
            gameModel.playCard(selectedCard); // Play the card
            Platform.runLater(() -> {
                onUIUpdateRequired(); // Update UI to show played card
                onStatusMessage(currentPlayer.getName() + " jugó " + selectedCard.getRank().getSymbol() + " de " + getSuitName(selectedCard.getSuit()) + ".");
            });

            Thread.sleep(1000 + random.nextInt(1000)); // Delay after playing card
            gameModel.drawCard(); // Machine draws a new card
            Platform.runLater(this::onUIUpdateRequired); // Update UI for drawn card
            Thread.sleep(500); // Short delay
            Platform.runLater(this::handleEndTurn); // End the machine's turn
        } catch (Exception e) {
            Platform.runLater(() -> showError("Error en el turno de la máquina: " + e.getMessage()));
        }
    }

    /**
     * Handles the "End Turn" button action.
     * For human players, it ensures a card has been played before drawing a new card and ending the turn.
     * Then it calls {@link #onTurnEnd(Player)} to advance the game.
     */
    @FXML
    private void handleEndTurn() {
        try {
            Player currentPlayer = gameModel.getCurrentPlayer();
            if (currentPlayer == null) return;

            if (currentPlayer.isHuman()) {
                if (!cardPlayed) {
                    onStatusMessage("¡Juega una carta primero!");
                    return;
                }
                gameModel.drawCard(); // Human player draws a card after playing
            }
            onTurnEnd(currentPlayer); // End the current player's turn
        } catch (GameException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Checks if the game has reached an end condition.
     * If the game has ended, it calls {@link #onGameEnd(Player)} to declare the winner.
     */
    @FXML
    private void checkGameEnd() {
        if (gameModel.isGameEnded()) {
            Player winner = gameModel.getWinner();
            onGameEnd(winner);
        }
    }

    /**
     * Displays an error message to the user in an Alert dialog.
     *
     * @param message The error message to be displayed.
     */
    @FXML
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays the game menu (pause menu) to the user.
     * Allows the user to resume the game or exit to the main menu.
     * Stops machine player threads if active.
     */
    @FXML
    private void showGameMenu() {
        if (machineThread != null && machineThread.isAlive()) {
            machineThread.stopThread(); // Stop machine turn if game is paused
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Menú del Juego");
        alert.setHeaderText("Juego Pausado");
        alert.setContentText("¿Quieres reanudar o salir al menú principal?");

        ButtonType resumeButton = new ButtonType("Reanudar");
        ButtonType exitToMenuButton = new ButtonType("Salir al Menú Principal");

        alert.getButtonTypes().setAll(resumeButton, exitToMenuButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == resumeButton) {
                // If current player was a machine, restart its turn logic
                if (gameModel.getCurrentPlayer() != null && !gameModel.getCurrentPlayer().isHuman() && !gameModel.isGameEnded()) {
                    startMachineTurn();
                }
            } else if (response == exitToMenuButton) {
                if (gameStage != null) {
                    gameStage.close(); // Close the game window
                }
                // Open the welcome screen
                Stage newWelcomeStage = new Stage();
                new WelcomeStage(newWelcomeStage).show();
            }
            // If response is neither resume nor exit, do nothing (alert closes, game remains paused)
        });
    }

    /**
     * Displays a help/rules dialog to the user.
     */
    @FXML
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ayuda");
        alert.setHeaderText("Resumen de reglas");
        alert.setContentText("Manten la suma <= 50. Buena Suerte!");
        alert.showAndWait();
    }

    /**
     * Provides an instance of the {@link KeyboardAdapter} for handling global keyboard shortcuts
     * within the game stage.
     *
     * @return A new instance of KeyboardAdapter.
     */
    public KeyboardAdapter getKeyboardAdapter() {
        return new KeyboardAdapter();
    }
}