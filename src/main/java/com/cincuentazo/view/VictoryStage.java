package com.cincuentazo.view;

import com.cincuentazo.controller.VictoryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * Represents the victory screen window (Stage) of the Cincuentazo game.
 * This class is responsible for loading the {@code VictoryView.fxml},
 * setting up the scene, connecting to the {@link VictoryController},
 * and displaying the winner's information.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class VictoryStage {

    private Stage victoryStage; // The actual stage for the victory interface
    private String winnerName;  // The name of the player who won the game

    /**
     * Constructs a new VictoryStage.
     * Initializes the victory window with specific settings and loads the UI.
     *
     * @param victoryStage The {@link Stage} instance that will host the victory screen.
     * @param winnerName The name of the player who won the game.
     */
    public VictoryStage(Stage victoryStage, String winnerName) {
        this.victoryStage = victoryStage;
        this.winnerName = winnerName;
        this.victoryStage.initStyle(StageStyle.UNDECORATED); // Sets the window to be undecorated (no native title bar/buttons)
        setupUI(); // Calls method to load FXML and set up the scene
    }

    /**
     * Sets up the victory screen user interface by loading the {@code VictoryView.fxml} file.
     * It retrieves the associated {@link VictoryController}, passes the winner's name and
     * the stage reference to it, and then sets the scene for the stage.
     */
    private void setupUI() {
        try {
            // Ensure the path to the FXML file is correct.
            // If VictoryView.fxml is in the root of resources, use "/VictoryView.fxml"
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VictoryView.fxml"));

            Parent root = loader.load(); // Load the FXML root element

            // Get the controller instance and pass necessary data (stage and winner name) to it
            VictoryController controller = loader.getController();
            controller.setStage(victoryStage);
            controller.setWinner(winnerName); // Pass the winner's name (e.g., "User" or "Bot 1")

            Scene scene = new Scene(root); // Create a new scene from the loaded FXML
            victoryStage.setScene(scene);
            victoryStage.setTitle("Victory!"); // Set the title for the victory window
            victoryStage.setResizable(false); // Prevent resizing of the victory window

            // Add Icon
            try {
                Image icon = new Image(
                        getClass().getResourceAsStream("/com.cincuentazo.images/favicon.png")
                );
                victoryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Could not load application icon: " + e.getMessage());
            }
            // ============================================================

        } catch (IOException e) {
            // Prints the stack trace if there's an error loading the FXML file
            System.err.println("Error loading FXML for VictoryStage: " + e.getMessage());
            e.printStackTrace();
            // In a production app, you might want a more user-friendly error display here.
        }
    }

    /**
     * Displays the victory stage, making it visible to the user.
     */
    public void show() {
        victoryStage.show();
    }
}