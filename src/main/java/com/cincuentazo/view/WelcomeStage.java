package com.cincuentazo.view;

import com.cincuentazo.controller.WelcomeViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // <-- AÑADE ESTE IMPORT
import java.io.IOException;

/**
 * Represents the welcome screen window (Stage) of the Cincuentazo application.
 * This class is responsible for loading the {@code WelcomeView.fxml} file,
 * setting up the scene, connecting to the {@link WelcomeViewController},
 * and managing the lifecycle of the welcome window.
 * It serves as the main entry point for the user to start a new game or exit.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class WelcomeStage {

    private Stage primaryStage;     // The primary stage of the entire application.
    private Stage welcomeStage;     // The actual stage for the welcome screen.
    private WelcomeViewController controller; // The controller managing the welcome screen's logic.

    /**
     * Constructs a new WelcomeStage.
     * Initializes the welcome window and sets it to be undecorated, then proceeds to set up the UI.
     *
     * @param primaryStage The main application stage, typically passed from the `Application` class.
     */
    public WelcomeStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.welcomeStage = new Stage();

        // ==================== LÍNEA AÑADIDA ====================
        // This line removes all native window decorations (title bar, close/minimize buttons).
        this.welcomeStage.initStyle(StageStyle.UNDECORATED);
        // ========================================================

        setupUI(); // Calls the method to load FXML and set up the scene.
    }

    /**
     * Sets up the user interface for the welcome screen by loading the {@code WelcomeView.fxml} file.
     * It connects the FXML elements to the {@link WelcomeViewController}, passes the stage reference
     * to the controller, sets up keyboard event handling, and configures the stage properties.
     * If FXML loading fails, a fallback UI is created.
     */
    private void setupUI() {
        try {
            // Load FXML for the welcome view.
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/WelcomeView.fxml")
            );

            Parent root = loader.load(); // Load the FXML root element.
            controller = loader.getController(); // Get the WelcomeViewController instance associated with the FXML.

            // Pass the welcome stage reference to the controller, allowing the controller to interact with the stage.
            controller.setStage(welcomeStage);

            // Create a new scene from the loaded FXML root. The size will be determined by FXML layout.
            Scene scene = new Scene(root);

            // Add a global keyboard event handler to the scene.
            // The controller's handleKeyPressed method will process key events.
            scene.setOnKeyPressed(controller::handleKeyPressed);

            // Configure the stage properties.
            welcomeStage.setScene(scene);
            // welcomeStage.setTitle("Cincuentazo - Welcome"); // <-- This line is no longer needed with UNDECORATED style.
            welcomeStage.setResizable(false); // Prevent resizing of the welcome window.

            // Set a custom handler for when the window close request occurs (e.g., from an internal exit button).
            welcomeStage.setOnCloseRequest(e -> {
                e.consume(); // Consume the event to prevent default close behavior.
                handleClose(); // Call custom close handling with confirmation.
            });

        } catch (IOException e) {
            System.err.println("Error loading WelcomeView.fxml: " + e.getMessage());
            e.printStackTrace();
            createFallbackUI(); // Provide a simple error UI if FXML fails to load.
        }
    }

    /**
     * Creates a simple fallback user interface (UI) to display an error message
     * and an exit button. This method is called if there is an {@link IOException}
     * during the loading of the {@code WelcomeView.fxml} file, preventing the main UI from showing.
     */
    private void createFallbackUI() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20); // Create a vertical box with spacing.
        root.setAlignment(javafx.geometry.Pos.CENTER); // Center content in the box.
        root.setStyle("-fx-background-color: #2c3e50; -fx-padding: 40;"); // Apply dark background and padding.

        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading main menu interface.\nPlease check that WelcomeView.fxml exists in:\nsrc/main/resources/"
        );
        errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-text-alignment: center;"); // Style for error text.

        javafx.scene.control.Button exitButton = new javafx.scene.control.Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;"); // Style for exit button.
        exitButton.setOnAction(e -> System.exit(1)); // Set action to exit application on button click.

        root.getChildren().addAll(errorLabel, exitButton); // Add elements to the root.

        Scene scene = new Scene(root); // Create a new scene for the fallback UI.
        welcomeStage.setScene(scene);
        // welcomeStage.setTitle("Error - Cincuentazo"); // <-- This line is no longer needed with UNDECORATED style.
    }

    /**
     * Handles the window close request for the welcome stage.
     * It displays a confirmation dialog to the user, and if confirmed,
     * the application is terminated.
     */
    private void handleClose() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION
        );
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("The application will close.");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                System.exit(0); // Terminate the application if the user confirms.
            }
        });
    }

    /**
     * Displays the welcome stage, making it visible to the user.
     */
    public void show() {
        welcomeStage.show();
    }

    /**
     * Retrieves the {@link WelcomeViewController} instance associated with this welcome stage.
     *
     * @return The {@link WelcomeViewController} instance.
     */
    public WelcomeViewController getController() {
        return controller;
    }

    /**
     * Retrieves the {@link Stage} instance that hosts the welcome screen.
     *
     * @return The {@link Stage} object representing the welcome window.
     */
    public Stage getStage() {
        return welcomeStage;
    }
}