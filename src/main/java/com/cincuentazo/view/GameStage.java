package com.cincuentazo.view;

import com.cincuentazo.controller.GameController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameStage extends Stage {

    private GameController gameController;

    // Constructor principal: ahora recibe el número de máquinas y el nombre de usuario
    public GameStage(int numMachines, String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GameView.fxml"));
        Parent root = loader.load();
        this.gameController = loader.getController(); // Obtenemos el controlador después de cargar FXML

        // Es crucial llamar a startGame() aquí para inicializar el GameModel
        this.gameController.startGame(numMachines, username);

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(gameController.getKeyboardAdapter()); // Asigna el KeyboardAdapter a la escena

        this.setTitle("Cincuentazo - The Game");
        this.setScene(scene);
        this.setResizable(false);
    }

    // Constructor sin parámetros (opcional, si aún lo usas en algún lugar)
    // Puede lanzar un error o usar valores por defecto para forzar el uso del constructor con parámetros.
    public GameStage() throws IOException {
        // Llamar al constructor principal con valores por defecto
        this(1, "Default Player");
    }

    public GameController getGameController() {
        return gameController;
    }
}