package com.cincuentazo.controller;

import com.cincuentazo.view.WelcomeStage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;

/**
 * Controller for the Victory screen (VictoryView.fxml).
 * This class manages the display of the winner, plays a background video,
 * and handles options to play again or exit the application.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class VictoryController {

    @FXML private MediaView backgroundVideo;
    @FXML private Label winnerLabel;
    @FXML private Button playAgainButton;
    @FXML private Button exitButton;

    private String winnerName;
    private Stage stage; // This variable MUST hold the reference to the victory window
    private MediaPlayer mediaPlayer;

    /**
     * Initializes the controller. This method is called automatically
     * after the FXML file has been loaded. It initiates the video setup.
     */
    @FXML
    public void initialize() {
        setupVideo();
    }

    /**
     * Configures and plays the background video.
     * It locates the video file, sets it to loop indefinitely, and starts playback.
     */
    private void setupVideo() {
        try {
            String videoPath = "/com.cincuentazo.images/VictoryVideo.mp4";

            URL mediaUrl = getClass().getResource(videoPath);
            if (mediaUrl != null) {
                Media media = new Media(mediaUrl.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                backgroundVideo.setMediaPlayer(mediaPlayer);
                backgroundVideo.setPreserveRatio(false);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.play();
            } else {
                System.err.println("ERROR: Archivo de video no encontrado en: " + videoPath);
            }
        } catch (Exception e) {
            System.err.println("Error cargando el video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the primary stage for this controller and binds the video's dimensions
     * to the stage's scene properties. This ensures the video scales with the window.
     * It also registers a handler to stop the video when the stage is closed.
     * This method is crucial for storing the reference to the victory window,
     * allowing it to be closed when "Play Again" or "Exit" is clicked.
     *
     * @param stage The {@link Stage} instance representing the victory window.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(e -> stopVideo());

        // This listener ensures that the MediaView is bound to the Scene's dimensions
        // only after the Scene has been set on the Stage, preventing NullPointerExceptions.
        if (backgroundVideo != null) {
            stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && backgroundVideo.getMediaPlayer() != null) {
                    backgroundVideo.fitWidthProperty().bind(newScene.widthProperty());
                    backgroundVideo.fitHeightProperty().bind(newScene.heightProperty());
                }
            });
        }
    }

    /**
     * Sets the name of the winning player to be displayed on the victory screen.
     *
     * @param winnerName The name of the player who won the game.
     */
    public void setWinner(String winnerName) {
        this.winnerName = winnerName;
        if (winnerLabel != null) {
            winnerLabel.setText("¡" + winnerName + " GANA!");
        }
    }

    /**
     * Handles the action when the "Play Again" button is clicked.
     * It stops the background video, closes the current victory stage,
     * and opens a new {@link WelcomeStage} to start a new game.
     * It explicitly checks if the {@code stage} reference is available before attempting to close it.
     */
    @FXML
    private void handlePlayAgain() {
        stopVideo(); // Stop the background video

        // Crucial: Close the current victory window BEFORE opening the new welcome stage.
        if (stage != null) {
            stage.close();
        } else {
            // Log an error if the stage reference was not set, indicating a potential setup issue.
            System.err.println("ERROR: The 'stage' in VictoryController is null. Cannot close the victory window.");
        }

        // Open the main menu (WelcomeStage)
        try {
            Stage newWelcomeStage = new Stage();
            new WelcomeStage(newWelcomeStage).show();
        } catch (Exception e) {
            e.printStackTrace();
            // Display an error if the main menu cannot be loaded
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to load the main menu.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action when the "Exit" button is clicked.
     * It stops the background video, closes the current victory stage,
     * and exits the entire JavaFX application.
     */
    @FXML
    private void handleExit() {
        stopVideo();
        if (stage != null) stage.close();
        Platform.exit(); // Terminate the JavaFX application
        System.exit(0);  // Ensure all threads are terminated
    }

    /**
     * Stops and disposes of the {@link MediaPlayer} to release system resources.
     * This method is crucial for preventing memory leaks when the victory screen is closed.
     */
    public void stopVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }
}