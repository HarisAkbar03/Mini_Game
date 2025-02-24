package org.example.mini_game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML for the main menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("start-view.fxml"));
        Pane root = loader.load();

        // Set up the scene and window size
        Scene scene = new Scene(root, 600, 400);  // Increased window size for better visuals

        // Set the window title and show it
        stage.setTitle("Maze Game Menu");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
