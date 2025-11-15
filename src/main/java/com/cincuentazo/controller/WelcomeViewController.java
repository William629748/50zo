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
 * Controller for the main menu (WelcomeView.fxml).
 * This class handles the selection of the number of opponents,
 * validation of the human player's username, and the initialization
 * and launch of the main game via {@link GameStage}.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class WelcomeViewController {

    // FXML UI Elements
    @FXML private RadioButton radio1Player;
    @FXML private RadioButton radio2Players;
    @FXML private RadioButton radio3Players;
    @FXML private ToggleGroup playerCountGroup; // Group for the radio buttons
    @FXML private Button startButton;
    @FXML private Button rulesButton;
    @FXML private Button exitButton;
    @FXML private TextField usernameField;
    @FXML private Label statusLabel; // For displaying messages to the user

    // Controller Attributes
    private Stage stage; // Reference to the primary stage of this view

    /**
     * Initializes the controller. This method is automatically called
     * after the FXML file has been loaded. Any default setup or
     * initializations can be placed here.
     */
    @FXML
    public void initialize() {
        // No default radio button selection or calls to removed methods are present.
    }

    /**
     * Sets the primary {@link Stage} for this controller.
     * This method is typically called by the {@link com.cincuentazo.view.WelcomeStage}
     * to provide the controller with a reference to its host window.
     *
     * @param stage The primary stage of the welcome screen.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Handles the action triggered by the "Start Game" button.
     * It performs validation checks for player count selection and username input.
     * If all inputs are valid, it proceeds to initialize and launch the game.
     * Otherwise, it provides visual feedback to the user about the missing input.
     */
    @FXML
    private void handleStartGame() {
        // 1. Validate player count selection
        RadioButton selected = (RadioButton) playerCountGroup.getSelectedToggle();
        if (selected == null) {
            flashRadioButtons(); // Provide visual feedback for missing selection
            return;
        }

        // 2. Validate username input
        String username = usernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            flashUsernameField(); // Provide visual feedback for empty username
            if (statusLabel != null) {
                statusLabel.setText("¡Ingresa tu nombre!"); // Display an error message
                statusLabel.setStyle("-fx-text-fill: #e74c3c;"); // Set text color to red for error
            }
            return;
        }

        // Determine the number of machine opponents based on the selected radio button
        int numMachines = 1; // Default to 1 machine
        if (selected == radio2Players) {
            numMachines = 2;
        } else if (selected == radio3Players) {
            numMachines = 3;
        }

        System.out.println("Starting game for user '" + username + "' with " + numMachines + " opponent(s)...");

        // Start the game with the validated parameters
        startGame(numMachines, username);
    }

    /**
     * Provides visual feedback by temporarily changing the text color of the
     * player count radio buttons to red. This indicates that a selection is required.
     */
    private void flashRadioButtons() {
        String errorStyle = "-fx-text-fill: #e74c3c;"; // CSS style for red text

        radio1Player.setStyle(errorStyle);
        radio2Players.setStyle(errorStyle);
        radio3Players.setStyle(errorStyle);

        // Reset the style after a short delay (1 second)
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            radio1Player.setStyle(null); // Remove error style
            radio2Players.setStyle(null);
            radio3Players.setStyle(null);
        });
        pause.play();
    }

    /**
     * Provides visual feedback by temporarily changing the border color of the
     * username text field to red. This indicates that the field requires input.
     */
    private void flashUsernameField() {
        String errorStyle = "-fx-border-color: #e74c3c; -fx-border-width: 2px;"; // CSS style for red border

        usernameField.setStyle(errorStyle);

        // Reset the style after a short delay (1 second)
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            usernameField.setStyle(null); // Remove error style
        });
        pause.play();
    }

    /**
     * Initiates the main game. This method creates a new {@link GameStage}
     * with the specified game parameters and then closes the current welcome stage.
     *
     * @param numMachines The number of computer opponents to play against.
     * @param username The name entered by the human player.
     */
    private void startGame(int numMachines, String username) {
        try {
            // Create a new GameStage instance, passing the current stage, number of machines, and username.
            GameStage game = new GameStage(stage, numMachines, username);
            game.show(); // Display the game stage

            // Close the welcome screen's window
            if (stage != null) {
                stage.close();
            }
        } catch (Exception e) {
            showError("Failed to start game: " + e.getMessage()); // Show error if game launch fails
            e.printStackTrace();
        }
    }

    /**
     * Handles the action triggered by the "Rules" button.
     * Displays an information alert dialog that summarizes the game rules.
     */
    @FXML
    private void handleShowRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Rules");
        alert.setHeaderText("How to Play Cincuentazo");

        String rules = """
            OBJECTIVE: Be the last player remaining.
            
            RULES:
            • Table sum must never exceed 50.
            • Cards 2-8, 10: Add value.
            • 9: Adds 0.
            • J, Q, K: Subtract 10.
            • A: Adds 1 or 10.
            """;

        alert.setContentText(rules);
        alert.showAndWait();
    }

    /**
     * Handles the action triggered by the "Exit" button.
     * Displays a confirmation dialog to the user before terminating the application.
     */
    @FXML
    private void handleExit() {
        // Restore the confirmation dialog before exiting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar Juego");
        alert.setHeaderText("Seguro que quieres salir?");
        alert.setContentText("La aplicacion se cerrara");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0); // Exit the application if confirmed
            }
        });
    }

    /**
     * Handles global keyboard press events for shortcuts on the welcome screen.
     * Allows users to select player count, start the game, or exit using keyboard input.
     *
     * @param event The {@link KeyEvent} triggered by a key press.
     */
    @FXML
    public void handleKeyPressed(KeyEvent event) {
        KeyCode code = event.getCode();

        switch (code) {
            case DIGIT1, NUMPAD1 -> radio1Player.setSelected(true); // Select 1 player
            case DIGIT2, NUMPAD2 -> radio2Players.setSelected(true); // Select 2 players
            case DIGIT3, NUMPAD3 -> radio3Players.setSelected(true); // Select 3 players
            case ENTER -> handleStartGame(); // Start game
            case ESCAPE -> handleExit();     // Show exit confirmation
            default -> { /* Do nothing for other keys */ }
        }
    }

    /**
     * Displays a simple error alert dialog to the user.
     *
     * @param message The error message to be displayed.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}