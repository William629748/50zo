package com.cincuentazo.controller;

import com.cincuentazo.model.*;
import com.cincuentazo.exception.*;
import com.cincuentazo.interfaces.*;
import com.cincuentazo.view.VictoryStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

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
    // Estos botones no necesitan fx:id si solo se usan en FXML para onAction
    // @FXML private Button rulesButton;
    // @FXML private Button exitButton;

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
                // Verifica que el jugador actual sea humano para que SPACE funcione
                if (gameModel.getCurrentPlayer() != null && gameModel.getCurrentPlayer().isHuman()) {
                    endTurnButton.fire();
                } else {
                    onStatusMessage("It's not your turn or you already played a card.");
                }
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
        gameModel = new GameModel(); // Solo instancia el modelo, no lo inicializa con jugadores
        cardPlayed = false;
        setupKeyboardHandling();
        // IMPORTANT: No llamar a métodos del gameModel que dependan de jugadores aquí
        // porque startGame() aún no se ha ejecutado.
    }

    /**
     * Sets up keyboard event handling.
     */
    private void setupKeyboardHandling() {
        // KeyboardAdapter will be set on the scene in the view class
    }

    /**
     * Starts a new game with the specified number of machine players and human player's username.
     * Este método es llamado desde GameStage después de cargar el FXML.
     *
     * @param numMachines number of machine players
     * @param humanUsername the username for the human player
     */
    public void startGame(int numMachines, String humanUsername) {
        try {
            // Ahora sí inicializamos el modelo con los jugadores
            gameModel.initializeGame(numMachines, humanUsername);
            // Registramos este controlador como listener del juego y la UI
            gameModel.setGameEventListener(this);
            gameModel.setUiUpdateListener(this);

            setupMachineBoxes(numMachines);
            onUIUpdateRequired(); // Actualiza la UI después de inicializar el juego
            onStatusMessage("Game started! Your turn, " + humanUsername + ".");

            // Después de inicializar, comenzamos el primer turno
            if (!gameModel.isGameEnded()) {
                onTurnStart(gameModel.getCurrentPlayer());
            }

        } catch (GameException e) {
            showError("Failed to start game: " + e.getMessage());
        }
    }

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

        if (gameModel.getCurrentPlayer() == null || !gameModel.getCurrentPlayer().isHuman()) {
            onStatusMessage("Wait for your turn!");
            return;
        }

        try {
            gameModel.playCard(card);
            cardPlayed = true;
            onUIUpdateRequired();
            onStatusMessage("Card played! Click 'End Turn' to continue.");
            endTurnButton.setDisable(false); // Habilita el botón de fin de turno
        } catch (InvalidCardException | GameException e) {
            showError(e.getMessage());
        }
    }

    // ==================== GameEventListener Implementation ====================

    @Override
    public void onTurnStart(Player player) {
        currentPlayerLabel.setText("Current: " + player.getName());

        if (!player.isHuman() && !gameModel.isGameEnded()) {
            endTurnButton.setDisable(true); // Deshabilita el botón para máquinas
            startMachineTurn();
        } else if (player.isHuman()) {
            // Si es turno humano, el botón se habilita si ya jugó carta
            endTurnButton.setDisable(!cardPlayed);
            onStatusMessage("Your turn, " + player.getName() + "!");
        } else {
            endTurnButton.setDisable(true); // Por defecto deshabilitado si no es turno humano o juego terminado
        }
    }

    @Override
    public void onTurnEnd(Player player) {
        cardPlayed = false; // Reiniciar el estado de carta jugada

        // Antes de pasar al siguiente turno, asegúrate de que el jugador haya robado una carta
        // Esta lógica estaba en handleEndTurn, pero puede ser más limpia aquí si el robo es parte del fin de turno.
        // Ojo: Si el jugador ya robó en handleEndTurn, no lo hagas de nuevo aquí.

        gameModel.nextTurn();
        onUIUpdateRequired(); // Actualiza la UI para el siguiente turno

        if (!gameModel.isGameEnded()) {
            Player nextPlayer = gameModel.getCurrentPlayer();
            onTurnStart(nextPlayer); // Inicia el siguiente turno
            onStatusMessage(nextPlayer.getName() + "'s turn");
        } else {
            // Si el juego ha terminado, onGameEnd() ya se encargará de esto.
            // Asegúrate de que no haya ciclos infinitos aquí.
        }
    }

    @Override
    public void onPlayerEliminated(Player player) {
        onStatusMessage(player.getName() + " has been eliminated!");
        onUIUpdateRequired(); // Refrescar la UI para mostrar la eliminación
    }

    // ==================== UIUpdateListener Implementation ====================

    @Override
    public void onUIUpdateRequired() {
        updateTableInfo();
        updatePlayerHands();
        updateCurrentPlayerLabel(); // Asegura que el label del jugador actual esté siempre sincronizado
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
            tableCardLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;"); // Asegurar que sea visible
        } else {
            tableCardLabel.setText("-"); // Mostrar algo si no hay carta en la mesa
            tableCardLabel.setStyle(""); // Limpiar estilos si no hay carta
        }
        deckSizeLabel.setText("Deck: " + gameModel.getDeck().size());
    }

    /**
     * Updates all player hands display.
     */
    private void updatePlayerHands() {
        List<Player> players = gameModel.getPlayers();

        if (players == null || players.isEmpty()) {
            return; // No hay jugadores aún, salir.
        }

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

        int machineIndex = 1;
        for (Player player : players) {
            if (!player.isHuman() && machineIndex <= 3) {
                updateMachineHand(player, machineIndex);
                machineIndex++;
            }
        }
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

        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-font-size: 20px; -fx-min-width: 60px; -fx-min-height: 80px; " +
                        "-fx-background-color: #e0e0e0;"
        ));

        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-font-size: 20px; -fx-min-width: 60px; -fx-min-height: 80px;"
        ));

        btn.setOnMouseClicked(e -> onCardSelected(card));

        // Check if card is playable (logic moved to GameModel.isCardPlayable for robustness)
        // Por ahora, lo mantenemos aquí si gameModel.isCardPlayable() no existe
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
        if (machineBox == null) return; // Asegurar que el VBox exista

        machineBox.getChildren().clear();

        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
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
            Label cardLabel = new Label("🂠"); // Símbolo de carta oculta
            cardLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: lightgray;");
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
            default -> null; // Retornar null para índices inválidos
        };
    }

    /**
     * Updates the current player label.
     */
    private void updateCurrentPlayerLabel() {
        Player current = gameModel.getCurrentPlayer();
        if (current != null) { // Asegurarse de que haya un jugador actual
            onTurnStart(current);
        }
    }

    /**
     * Starts a machine player's turn in a separate thread.
     */
    private void startMachineTurn() {
        Player currentPlayer = gameModel.getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.isHuman() || gameModel.isGameEnded()) {
            return; // No iniciar turno de máquina si no hay jugador, es humano o el juego terminó.
        }
        if (machineThread != null && machineThread.isAlive()) {
            machineThread.stopThread(); // Detener el hilo anterior si sigue corriendo
        }
        machineThread = new MachinePlayerThread(currentPlayer);
        machineThread.start();
    }

    /**
     * Executes a machine player's turn logic.
     */
    private void playMachineTurn() {
        try {
            Player currentPlayer = gameModel.getCurrentPlayer();
            if (currentPlayer == null || currentPlayer.isHuman() || gameModel.isGameEnded()) {
                return; // Doble verificación para evitar ejecutar si no es el turno de la máquina
            }

            List<Card> playableCards = currentPlayer.getPlayableCards(gameModel.getTableSum());

            if (playableCards.isEmpty()) {
                Platform.runLater(() -> onStatusMessage(currentPlayer.getName() + " has no playable cards!"));
                Thread.sleep(1000); // Esperar un momento para que el mensaje sea visible
                Platform.runLater(() -> handleEndTurn()); // Finalizar turno si no hay cartas
                return;
            }

            // Select random playable card
            Card selectedCard = playableCards.get(random.nextInt(playableCards.size()));
            gameModel.playCard(selectedCard);
            Platform.runLater(() -> {
                onUIUpdateRequired();
                onStatusMessage(currentPlayer.getName() + " played " + selectedCard);
            });

            Thread.sleep(1000 + random.nextInt(1000)); // Esperar antes de robar

            gameModel.drawCard();
            Platform.runLater(() -> onUIUpdateRequired());

            Thread.sleep(500);
            Platform.runLater(() -> handleEndTurn()); // Finalizar el turno de la máquina
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Platform.runLater(() -> showError("Machine turn interrupted: " + e.getMessage()));
        } catch (Exception e) {
            Platform.runLater(() -> showError("Machine turn error: " + e.getMessage()));
        }
    }


    /**
     * Handles the end turn button click.
     */
    @FXML
    private void handleEndTurn() {
        try {
            Player currentPlayer = gameModel.getCurrentPlayer();
            if (currentPlayer == null) {
                onStatusMessage("No current player. Game might not be initialized.");
                return;
            }

            if (currentPlayer.isHuman()) {
                if (!cardPlayed) {
                    onStatusMessage("You must play a card first!");
                    return;
                }
                gameModel.drawCard(); // El jugador humano roba después de jugar y antes de terminar el turno
            }
            // Si es máquina, ya robó en playMachineTurn

            onTurnEnd(currentPlayer); // Llama a onTurnEnd para procesar el fin del turno y el siguiente
        } catch (GameException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Checks if the game has ended and shows winner.
     */
    @FXML // Asegurarse de que es accesible si se usa desde FXML, aunque aquí se llama desde Java
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
    @FXML // Asegurarse de que es accesible si se usa desde FXML
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows the game menu (pause).
     */
    @FXML
    private void showGameMenu() {
        // Detener el hilo de la máquina si está corriendo
        if (machineThread != null && machineThread.isAlive()) {
            machineThread.stopThread();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Menu");
        alert.setHeaderText("Game Paused");
        alert.setContentText("Press OK to resume game");

        // Opcional: podrías añadir botones "Exit Game", "Restart"
        ButtonType resumeButton = new ButtonType("Resume Game", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Exit Game", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(resumeButton, exitButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == exitButton) {
                // Lógica para salir del juego o volver al menú principal
                System.exit(0); // Cierra la aplicación
            }
            // Si es resumeButton, simplemente se cierra el alert y el juego continúa
            // Si el turno era de la máquina, reiniciarlo
            if (gameModel.getCurrentPlayer() != null && !gameModel.getCurrentPlayer().isHuman() && !gameModel.isGameEnded()) {
                startMachineTurn();
            }
        });
    }

    /**
     * Shows help information.
     */
    @FXML
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
                        "• ESC: Pause / Game Menu\n" +
                        "• H: Help"
        );
        alert.showAndWait();
    }

    /**
     * Gets the keyboard adapter for external setup.
     *
     * @return the keyboard adapter
     */
    public KeyboardAdapter getKeyboardAdapter() {
        return new KeyboardAdapter();
    }
    // ==================== Añadir al final de GameController ====================

    /**
     * Reference to the game stage (needed to close it when showing victory)
     */
    private Stage gameStage;

    /**
     * Sets the game stage reference.
     * Should be called from GameStage after loading the controller.
     *
     * @param stage the game stage
     */
    public void setGameStage(Stage stage) {
        this.gameStage = stage;
    }

    /**
     * Shows the victory screen with the winner's name.
     *
     * @param winner the winning player
     */
    private void showVictoryScreen(Player winner) {
        // Stop any running machine thread
        if (machineThread != null) {
            machineThread.stopThread();
        }

        // Close the game window
        if (gameStage != null) {
            gameStage.close();
        }

        // Open victory screen
        Stage victoryStage = new Stage();
        VictoryStage victory = new VictoryStage(victoryStage, winner.getName());
        victory.show();
    }

    // ==================== MODIFICAR EL MÉTODO onGameEnd ====================

    @Override
    public void onGameEnd(Player winner) {
        // Show victory screen instead of alert
        showVictoryScreen(winner);
    }
}

