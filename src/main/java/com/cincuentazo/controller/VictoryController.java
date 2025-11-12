package com.cincuentazo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.cincuentazo.view.WelcomeStage;

/**
 * Controller for the Victory screen.
 * Displays the winner and provides options to play again or exit.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class VictoryController {

    @FXML
    private Label winnerLabel;

    @FXML
    private Button playAgainButton;

    @FXML
    private Button exitButton;

    private String winnerName;
    private Stage stage;

    /**
     * Initializes the controller.
     * This method is called automatically after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Setup button actions if they exist in FXML
        if (playAgainButton != null) {
            playAgainButton.setOnAction(e -> handlePlayAgain());
        }
        if (exitButton != null) {
            exitButton.setOnAction(e -> handleExit());
        }
    }

    /**
     * Sets the winner name and updates the label.
     * This method should be called from GameStage after loading the FXML.
     *
     * @param winnerName the name of the winning player
     */
    public void setWinner(String winnerName) {
        this.winnerName = winnerName;
        updateWinnerLabel();
    }

    /**
     * Sets the stage reference.
     *
     * @param stage the stage displaying this controller
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Updates the winner label with the winner's name.
     */
    private void updateWinnerLabel() {
        if (winnerLabel != null && winnerName != null) {
            winnerLabel.setText(winnerName + " WINS!");
        }
    }

    /**
     * Handles the play again button action.
     * Returns to the welcome screen to start a new game.
     */
    @FXML
    private void handlePlayAgain() {
        if (stage != null) {
            // Close current victory window
            stage.close();

            // Open welcome screen
            Stage welcomeStage = new Stage();
            WelcomeStage welcome = new WelcomeStage(welcomeStage);
            welcome.show();
        }
    }

    /**
     * Handles the exit button action.
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        if (stage != null) {
            stage.close();
        }
        System.exit(0);
    }
}