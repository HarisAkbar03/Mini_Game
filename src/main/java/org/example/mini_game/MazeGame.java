package org.example.mini_game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
    private static final int STEP = 5;
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

        Pane root = new Pane();
        root.getChildren().addAll(maze, endLabel, robot);
        Scene scene = new Scene(root, 600, 600);

        scene.setOnKeyPressed(event -> {
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
                    Platform.runLater(this::showCarScene);
                }
            }
        });

        stage.setTitle("Maze Puzzle");
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

    private void showCarScene() {
        Car carScene = new Car();
        Scene newScene = new Scene(carScene.start(), 600, 600);
        stage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
