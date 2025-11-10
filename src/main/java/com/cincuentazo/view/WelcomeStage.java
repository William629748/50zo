package com.cincuentazo.view;

import com.cincuentazo.controller.WelcomeViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Welcome screen stage that loads the main menu from FXML.
 * Displays player selection and game start options.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class WelcomeStage {

    private Stage primaryStage;
    private Stage welcomeStage;
    private WelcomeViewController controller;

    /**
     * Constructs a WelcomeStage with reference to primary stage.
     *
     * @param primaryStage the main application stage
     */
    public WelcomeStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.welcomeStage = new Stage();
        setupUI();
    }

    /**
     * Sets up the user interface by loading FXML.
     */
    private void setupUI() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/cincuentazo/view/WelcomeViewWelcomeView.fxml")
            );

            Parent root = loader.load();
            controller = loader.getController();

            // Pass stage reference to controller
            controller.setStage(welcomeStage);

            // Create scene
            Scene scene = new Scene(root, 800, 600);

            // Add keyboard event handler
            scene.setOnKeyPressed(controller::handleKeyPressed);

            // Configure stage
            welcomeStage.setScene(scene);
            welcomeStage.setTitle("Cincuentazo - Welcome");
            welcomeStage.setResizable(false);

            // Handle close request
            welcomeStage.setOnCloseRequest(e -> {
                e.consume();
                handleClose();
            });

        } catch (IOException e) {
            System.err.println("Error loading WelcomeView.fxml: " + e.getMessage());
            e.printStackTrace();
            createFallbackUI();
        }
    }

    /**
     * Creates a fallback UI if FXML loading fails.
     */
    private void createFallbackUI() {
        javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: #2c3e50; -fx-padding: 40;");

        javafx.scene.control.Label errorLabel = new javafx.scene.control.Label(
                "Error loading main menu interface.\nPlease check that WelcomeView.fxml exists in:\nsrc/main/resources/com/cincuentazo/view/"
        );
        errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-text-alignment: center;");

        javafx.scene.control.Button exitButton = new javafx.scene.control.Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        exitButton.setOnAction(e -> System.exit(1));

        root.getChildren().addAll(errorLabel, exitButton);

        Scene scene = new Scene(root, 800, 600);
        welcomeStage.setScene(scene);
        welcomeStage.setTitle("Error - Cincuentazo");
    }

    /**
     * Handles the window close request.
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
                System.exit(0);
            }
        });
    }

    /**
     * Shows the welcome stage.
     */
    public void show() {
        welcomeStage.show();
    }

    /**
     * Gets the controller instance.
     *
     * @return the main menu controller
     */
    public WelcomeViewController getController() {
        return controller;
    }

    /**
     * Gets the welcome stage.
     *
     * @return the stage
     */
    public Stage getStage() {
        return welcomeStage;
    }
}