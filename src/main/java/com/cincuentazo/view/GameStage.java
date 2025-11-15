package com.cincuentazo.view;

import com.cincuentazo.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.stage.StageStyle;

/**
 * Represents the main game window (Stage) for the Cincuentazo game.
 * This class is responsible for loading the {@code GameView.fxml},
 * setting up the scene, connecting to the {@link GameController},
 * and managing the lifecycle of the game window.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameStage {

    private Stage primaryStage;     // Reference to the main application stage (e.g., WelcomeStage's stage)
    private Stage gameStage;        // The actual stage for the game interface
    private int numMachines;        // Number of machine opponents selected
    private String humanUsername;   // Username of the human player
    private GameController controller; // The controller managing the game logic and UI updates

    /**
     * Constructs a new GameStage.
     * Initializes the game window with specific settings and loads the UI.
     *
     * @param primaryStage The primary stage of the application, used to return to the welcome screen.
     * @param numMachines The number of machine opponents for this game session.
     * @param humanUsername The username of the human player.
     */
    public GameStage(Stage primaryStage, int numMachines, String humanUsername) {
        this.primaryStage = primaryStage;
        this.numMachines = numMachines;
        this.humanUsername = humanUsername;
        this.gameStage = new Stage();
        this.gameStage.initStyle(StageStyle.UNDECORATED); // Makes the window without OS decorations
        setupUI(); // Calls method to load FXML and set up the scene
    }

    /**
     * Sets up the game user interface by loading the {@code GameView.fxml} file.
     * It connects the FXML elements to the {@link GameController}, sets up event handlers,
     * and initializes the game. If FXML loading fails, a fallback UI is created.
     */
    private void setupUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GameView.fxml") // Path to the FXML layout file
            );

            Parent root = loader.load(); // Load the FXML root element
            controller = loader.getController(); // Get the GameController instance associated with the FXML

            Scene scene = new Scene(root, 900, 700); // Create a new scene with specified dimensions

            // Set up keyboard event handling for the entire scene
            scene.setOnKeyPressed(controller.getKeyboardAdapter());

            gameStage.setScene(scene);
            gameStage.setTitle("Cincuentazo - Game in Progress");
            gameStage.setResizable(false); // Prevent resizing of the game window

            // Add Icon
            try {
                Image icon = new Image(
                        getClass().getResourceAsStream("/com.cincuentazo.images/favicon.png")
                );
                gameStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Could not load application icon: " + e.getMessage());
            }
            // ============================================================

            // Pass the game stage reference to the controller for managing window-specific actions (e.g., closing)
            controller.setGameStage(gameStage);

            // Start the actual game logic within the controller
            controller.startGame(numMachines, humanUsername);

            // Custom handler for when the game window is requested to be closed (e.g., by clicking X button)
            gameStage.setOnCloseRequest(e -> {
                e.consume(); // Consume the event to prevent default close behavior
                handleExit(); // Call custom exit handling with confirmation
            });

        } catch (IOException e) {
            System.err.println("Error loading FXML for GameStage: " + e.getMessage());
            e.printStackTrace();
            createFallbackUI(); // Provide a simple error UI if FXML fails to load
        }
    }

    /**
     * Creates a simple fallback user interface (UI) to display an error message.
     * This method is called if there is an {@link IOException} during the loading
     * of the {@code GameView.fxml} file.
     */
    private void createFallbackUI() {
        javafx.scene.layout.BorderPane root = new javafx.scene.layout.BorderPane();
        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading game interface. Please check FXML file."
        );
        errorLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: red;");
        root.setCenter(errorLabel);

        Scene scene = new Scene(root, 900, 700);
        gameStage.setScene(scene);
        gameStage.setTitle("Error - Cincuentazo");
    }

    /**
     * Handles the exit action for the game stage, typically triggered by closing the window.
     * It displays a confirmation dialog to the user before closing the game
     * and returning to the {@link WelcomeStage}.
     */
    private void handleExit() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION
        );
        alert.setTitle("Exit Game");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Your current game will be lost.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                gameStage.close(); // Close the current game window

                // Re-open or show the welcome screen (primaryStage)
                WelcomeStage welcomeStage = new WelcomeStage(primaryStage); // Creates a new welcome stage
                welcomeStage.show();
            }
        });
    }

    /**
     * Displays the game stage, making it visible to the user.
     */
    public void show() {
        gameStage.show();
    }

    /**
     * Retrieves the {@link GameController} instance associated with this game stage.
     * This can be used for direct interaction with the game's control logic.
     *
     * @return The {@link GameController} instance.
     */
    public GameController getController() {
        return controller;
    }
}