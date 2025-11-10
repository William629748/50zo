package com.cincuentazo;

import javafx.application.Application;
import javafx.stage.Stage;
import com.cincuentazo.view.WelcomeStage;

/**
 * Main application class that launches the Cincuentazo game.
 * Entry point for the JavaFX application.
 *
 * @author Cincuentazo Team
 * @version 1.0.0
 */
public class MainApplication extends Application {

    private Stage primaryStage;

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Cincuentazo - Card Game");

        showWelcomeScreen();
    }

    /**
     * Shows the welcome screen where players select number of opponents.
     */
    private void showWelcomeScreen() {
        WelcomeStage welcomeStage = new WelcomeStage(primaryStage);
        welcomeStage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}