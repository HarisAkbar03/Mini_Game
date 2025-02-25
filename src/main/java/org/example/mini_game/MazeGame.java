package org.example.mini_game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class MazeGame extends Application {
    private static final int STEP = 10;
    private ImageView robot;
    private PixelReader pixelReader;
    private double scaleX, scaleY;
    private Image mazeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/maze.png")));
    private double startX = 15, startY = 380;
    private double endX = 570, endY = 345;
    private final double END_RANGE = 20;
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        // Set up TabPane and tabs
        TabPane tabPane = new TabPane();
        Tab mazeTab = new Tab("Maze Game");
        Tab carTab = new Tab("Car Game");
        mazeTab.setClosable(false);
        carTab.setClosable(false);

        // Setup maze scene
        Pane mazeRoot = new Pane();
        Label endLabel = new Label("End");
        endLabel.setFont(Font.font(14));
        endLabel.setLayoutY(345);
        endLabel.setLayoutX(570);

        ImageView maze = new ImageView(mazeImage);
        maze.setFitWidth(600);
        maze.setFitHeight(600);

        scaleX = mazeImage.getWidth() / maze.getFitWidth();
        scaleY = mazeImage.getHeight() / maze.getFitHeight();
        pixelReader = mazeImage.getPixelReader();

        robot = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/robot.png"))));
        robot.setFitWidth(20);
        robot.setFitHeight(20);
        robot.setX(startX);
        robot.setY(startY);

        mazeRoot.getChildren().addAll(maze, endLabel, robot);
        mazeTab.setContent(mazeRoot);

        mazeRoot.setOnKeyPressed(event -> {
            double newX = robot.getX();
            double newY = robot.getY();

            if (event.getCode() == KeyCode.RIGHT) newX += STEP;
            else if (event.getCode() == KeyCode.LEFT) newX -= STEP;
            else if (event.getCode() == KeyCode.UP) newY -= STEP;
            else if (event.getCode() == KeyCode.DOWN) newY += STEP;

            if (isValidMove(newX, newY)) {
                robot.setX(newX);
                robot.setY(newY);
                logPixelData(newX, newY);

                if (reachedEnd(newX, newY)) {
                    System.out.println("🎉 Goal Reached! Moving to the Car scene...");
                    Platform.runLater(() -> tabPane.getSelectionModel().select(carTab)); // Switch to car tab
                }
            }
        });

        // Set up car scene
        Car carScene = new Car();
        carTab.setContent(carScene.start());

        // Add tabs to TabPane
        tabPane.getTabs().addAll(mazeTab, carTab);
        tabPane.getSelectionModel().select(mazeTab); // Start with the maze tab

        // Create and display the scene
        Scene scene = new Scene(tabPane, 600, 600);
        stage.setTitle("Maze and Car Game");
        stage.setScene(scene);
        stage.show();
    }

    private void logPixelData(double x, double y) {
        int px = (int) (x * scaleX);
        int py = (int) (y * scaleY);

        if (px >= 0 && py >= 0 && px < mazeImage.getWidth() && py < mazeImage.getHeight()) {
            Color color = pixelReader.getColor(px, py);
            System.out.printf("Pixel at (%.0f, %.0f) -> RGB: (%.2f, %.2f, %.2f)%n", x, y, color.getRed(), color.getGreen(), color.getBlue());
        }
    }

    private boolean reachedEnd(double x, double y) {
        return Math.abs(x - endX) < END_RANGE && Math.abs(y - endY) < END_RANGE;
    }

    private boolean isValidMove(double x, double y) {
        int px = (int) (x * scaleX);
        int py = (int) (y * scaleY);

        if (px < 0 || py < 0 || px >= mazeImage.getWidth() || py >= mazeImage.getHeight()) return false;
        return !isWall(pixelReader.getColor(px, py));
    }

    private boolean isWall(Color color) {
        return !color.equals(Color.WHITE);
    }

    public static void main(String[] args) {
        launch();
    }
}
