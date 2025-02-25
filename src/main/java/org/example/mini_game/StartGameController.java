package org.example.mini_game;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class StartGameController {

    @FXML
    private Button startGameButton;

    @FXML
    private void onStartGameClick() throws Exception {
        // Get the current stage from the button
        Stage stage = (Stage) startGameButton.getScene().getWindow();

        // Create a new MazeGame instance
        MazeGame mazeGame = new MazeGame();

        // Start the MazeGame without relying on FXML
        mazeGame.start(stage);  // Pass the current stage to the start method
    }
}
