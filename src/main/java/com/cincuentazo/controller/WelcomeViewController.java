package com.cincuentazo.controller;

import com.cincuentazo.view.GameStage;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the main menu (welcome screen).
 * Handles player selection, username validation and game initialization.
 * Implements usability heuristics and event handling.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class WelcomeViewController {

    @FXML private RadioButton radio1Player;
    @FXML private RadioButton radio2Players;
    @FXML private RadioButton radio3Players;
    @FXML private ToggleGroup playerCountGroup;
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button exitButton;

    // NUEVO: Vinculamos el campo de texto
    @FXML private TextField usernameField;

    private Stage stage;

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        setupRadioButtons();
    }

    /**
     * Sets the stage reference for this controller.
     *
     * @param stage the primary stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets up radio button properties and user data.
     */
    private void setupRadioButtons() {
        radio1Player.setUserData(1);
        radio2Players.setUserData(2);
        radio3Players.setUserData(3);
    }

    /**
     * Handles the start game button action.
     * Validates inputs and starts the game.
     */
    @FXML
    private void handleStartGame() {
        boolean hasError = false;

        // 1. Validar selección de jugadores
        RadioButton selected = (RadioButton) playerCountGroup.getSelectedToggle();
        if (selected == null) {
            flashRadioButtons();
            hasError = true;
        }

        // 2. Validar nombre de usuario (NUEVO)
        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            flashUsernameField();
            hasError = true;
        }

        // Si hubo algún error, detenemos aquí
        if (hasError) {
            return;
        }

        // Si todo está bien, procedemos
        int numMachines = (int) selected.getUserData();
        System.out.println("Starting game for user '" + username + "' with " + numMachines + " opponent(s)...");

        // Start the game
        startGame(numMachines, username);
    }

    /**
     * Makes the RadioButtons flash red.
     */
    private void flashRadioButtons() {
        String errorStyle = "-fx-text-fill: #e74c3c;"; // Rojo

        radio1Player.setStyle(errorStyle);
        radio2Players.setStyle(errorStyle);
        radio3Players.setStyle(errorStyle);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            radio1Player.setStyle(null);
            radio2Players.setStyle(null);
            radio3Players.setStyle(null);
        });
        pause.play();
    }

    /**
     * NUEVO: Makes the TextField flash red border.
     */
    private void flashUsernameField() {
        // Borde rojo fuerte
        String errorStyle = "-fx-border-color: #e74c3c; -fx-border-width: 2px;";

        usernameField.setStyle(errorStyle);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            // Volver al estilo original (definido en CSS)
            usernameField.setStyle(null);
        });
        pause.play();
    }

    /**
     * Starts the game with specified parameters.
     *
     * @param numMachines number of computer opponents
     * @param username name of the human player
     */
    private void startGame(int numMachines, String username) {
        try {
            // Aquí podrías pasarle el username al GameStage si quisieras en el futuro
            Stage gameStage = new Stage();
            // GameStage game = new GameStage(username); // Idea para el futuro
            GameStage game = new GameStage();
            game.show();

            // Close the welcome screen
            if (stage != null) {
                stage.close();
            }
        } catch (Exception e) {
            showError("Failed to start game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the show rules button action.
     */
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

    /**
     * Handles the exit button action.
     */
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
     * Handles keyboard events for shortcuts.
     */
    @FXML
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        switch (code) {
            case DIGIT1, NUMPAD1 -> radio1Player.setSelected(true);
            case DIGIT2, NUMPAD2 -> radio2Players.setSelected(true);
            case DIGIT3, NUMPAD3 -> radio3Players.setSelected(true);
            case ENTER -> handleStartGame();
            case ESCAPE -> handleExit();
            default -> {}
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}