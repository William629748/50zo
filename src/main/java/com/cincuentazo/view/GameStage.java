package com.cincuentazo.view;

import com.cincuentazo.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Game stage that displays the main game interface.
 * Loads FXML and connects with GameController.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class GameStage {

    private Stage primaryStage;
    private Stage gameStage;
    private int numMachines;
    private String humanUsername;
    private GameController controller;

    /**
     * Constructs a GameStage.
     */
    public GameStage(Stage primaryStage, int numMachines, String humanUsername) {
        this.primaryStage = primaryStage;
        this.numMachines = numMachines;
        this.humanUsername = humanUsername;
        this.gameStage = new Stage();
        setupUI();
    }

    /**
     * Sets up the game user interface using FXML.
     */
    private void setupUI() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/GameView.fxml")
            );

            Parent root = loader.load();
            controller = loader.getController();

            Scene scene = new Scene(root, 900, 700);

            // Setup keyboard handling
            scene.setOnKeyPressed(controller.getKeyboardAdapter());

            gameStage.setScene(scene);
            gameStage.setTitle("Cincuentazo - Game in Progress");
            gameStage.setResizable(false);

            // Pass stage reference to controller
            controller.setGameStage(gameStage);

            // Start the game with username
            controller.startGame(numMachines, humanUsername);

            // Handle window close
            gameStage.setOnCloseRequest(e -> {
                e.consume();
                handleExit();
            });

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            createFallbackUI();
        }
    }

    /**
     * Creates a fallback UI if FXML loading fails.
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
    }

    /**
     * Handles the exit action with confirmation.
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
                gameStage.close();

                // Return to welcome screen
                WelcomeStage welcomeStage = new WelcomeStage(primaryStage);
                welcomeStage.show();
            }
        });
    }

    /**
     * Shows the game stage.
     */
    public void show() {
        gameStage.show();
    }

    /**
     * Gets the game controller.
     *
     * @return the controller
     */
    public GameController getController() {
        return controller;
    }
}