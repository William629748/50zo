package com.cincuentazo.view;

import javafx.fxml.FXMLLoader; // Necesario para cargar el FXML
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GameStage extends Stage {

    public GameStage() {
        try {
            // 1. Cargar el FXML para GameView
            FXMLLoader loader = new FXMLLoader(
                    // Asumiendo que GameView.fxml está en la misma ubicación que WelcomeView.fxml
                    getClass().getResource("/GameView.fxml")
            );

            // 2. Cargar el nodo raíz del FXML
            Parent root = loader.load();

            // 3. Crear la Scene usando solo el 'root'.
            // Esto asegura que se usen las dimensiones (900x650) definidas en GameView.fxml.
            Scene scene = new Scene(root);

            // 4. Configurar el Stage
            this.setScene(scene);
            this.setTitle("Cincuentazo - The Game");
            this.setResizable(false);

        } catch (IOException e) {
            // Manejo de error si el archivo FXML no se encuentra o no se puede cargar
            System.err.println("Error loading GameView.fxml: " + e.getMessage());
            e.printStackTrace();
            // Opcional: Podrías crear una UI de respaldo aquí, similar a WelcomeStage.
        }
    }
}