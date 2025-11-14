package com.cincuentazo.view;

import com.cincuentazo.controller.VictoryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Victory screen stage that displays the winner.
 * Loads VictoryView.fxml and shows the winner.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class VictoryStage {

    private Stage victoryStage;
    private VictoryController controller;
    private String winnerName;

    /**
     * Constructs a VictoryStage with the winner's name.
     *
     * @param victoryStage the stage to display
     * @param winnerName the name of the winning player
     */
    public VictoryStage(Stage victoryStage, String winnerName) {
        this.victoryStage = victoryStage;
        this.winnerName = winnerName;
        setupUI();
    }

    /**
     * Sets up the user interface by loading FXML.
     */
    private void setupUI() {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/cincuentazo/view/VictoryView.fxml")
            );

            Parent root = loader.load();
            controller = loader.getController();

            // Pass stage and winner name to controller
            controller.setStage(victoryStage);
            controller.setWinner(winnerName);

            // Create scene
            Scene scene = new Scene(root, 900, 650);

            // Configure stage
            victoryStage.setScene(scene);
            victoryStage.setTitle("Victory!");
            victoryStage.setResizable(false);

            // ==================== AGREGAR ÍCONO ====================
            try {
                Image icon = new Image(
                        getClass().getResourceAsStream("/com.cincuentazo.images/favicon.png")
                );
                victoryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Could not load application icon: " + e.getMessage());
            }
            // ========================================================

        } catch (IOException e) {
            System.err.println("Error loading VictoryView.fxml: " + e.getMessage());
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

        javafx.scene.control.Label winnerLabel = new javafx.scene.control.Label(
                winnerName + " WINS!"
        );
        winnerLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: gold; -fx-font-weight: bold;");

        javafx.scene.control.Button exitButton = new javafx.scene.control.Button("Exit");
        exitButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 30;");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(winnerLabel, exitButton);

        Scene scene = new Scene(root, 900, 650);
        victoryStage.setScene(scene);
        victoryStage.setTitle("Victory!");
    }

    /**
     * Shows the victory stage.
     */
    public void show() {
        victoryStage.show();
    }

    /**
     * Gets the controller instance.
     *
     * @return the victory controller
     */
    public VictoryController getController() {
        return controller;
    }
}