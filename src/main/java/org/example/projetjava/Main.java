package org.example.projetjava;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Attempting to load FXML...");
        FXMLLoader loader =
                new FXMLLoader(getClass().getResource(
                        "/signin.fxml"));
        System.out.println("FXML Loaded: " + loader);

        // Debugging the styles.css path
        URL styleUrl = getClass().getResource("/styles.css");
        System.out.println("Style URL: " + styleUrl);

        if (styleUrl == null) {
            System.err.println("Error: styles.css not found in resources.");
        }

        Scene scene = new Scene(loader.load());

        // Only add the stylesheet if it's found
        if (styleUrl != null) {
            scene.getStylesheets().add(Objects.requireNonNull(styleUrl).toExternalForm());
        } else {
            System.err.println("No stylesheet found, proceeding without it.");
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
