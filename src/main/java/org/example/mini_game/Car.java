
package org.example.mini_game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Car {
    private static final int STEP = 5; // Movement step size
    private ImageView car;
    private PixelReader pixelReader;
    private double scaleX, scaleY;
    private double startX = 10, startY = 15;
    private final double END_X = 545, END_Y = 510; // End coordinates
    private final double END_RANGE = 20; // Tolerance area for reaching the "End"

    private Image maze2Image = new Image(getClass().getResourceAsStream("/maze2.png"));
    private ImageView maze2 = new ImageView(maze2Image);

    public Pane start() {
        Pane root1 = new Pane();

        // Labels for start and end
        Label endCar = new Label("End");
        endCar.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        endCar.setTextFill(Color.BLACK);
        endCar.setLayoutX(545);
        endCar.setLayoutY(510);

        Label startCar = new Label("Start");
        startCar.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        startCar.setTextFill(Color.BLACK);
        startCar.setLayoutX(30);
        startCar.setLayoutY(45);

        maze2.setFitWidth(600);
        maze2.setFitHeight(570);

        // Initialize car
        car = new ImageView(new Image(getClass().getResourceAsStream("/car.png")));
        car.setFitWidth(30);
        car.setFitHeight(30);
        car.setX(startX);
        car.setY(startY);

        root1.getChildren().addAll(maze2, endCar, startCar, car);

        // Calculate scaling factors for the car maze
        scaleX = maze2Image.getWidth() / maze2.getFitWidth();
        scaleY = maze2Image.getHeight() / maze2.getFitHeight();

        // PixelReader to read maze2 image
        pixelReader = maze2Image.getPixelReader();

        // Find the shortest path for the car using A* algorithm
        findCarPath();

         return root1;
    }

    private String getKey(double x, double y) {
        return x + "," + y;
    }

    private ArrayList<String> reconstructPath(Map<String, String> parents, String goalKey) {
        ArrayList<String> path = new ArrayList<>();
        String currentKey = goalKey;

        // Backtrack from the goal to the start using the parents map
        while (currentKey != null) {
            path.add(currentKey);
            currentKey = parents.get(currentKey);
        }

        // Reverse the path to go from start to goal
        Collections.reverse(path);
        return path;
    }

    // Find path using A* for the car
    public void findCarPath() {
        // Priority Queue to explore the nodes with the smallest f-value (f = g + h)
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));

        // Maps for shortest distance (g-cost) and parent nodes for path reconstruction
        Map<String, Integer> gCosts = new HashMap<>();
        Map<String, String> parents = new HashMap<>();

        // Add the start node to the priority queue
        String startKey = getKey(startX, startY);
        pq.add(new Node(startX, startY, 0, calculateHeuristic(startX, startY)));
        gCosts.put(startKey, 0);

        // Directions for movement (right, left, down, up)
        int[] moveX = {STEP, -STEP, 0, 0}; // Right, Left, Down, Up
        int[] moveY = {0, 0, STEP, -STEP}; // Right, Left, Down, Up

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            double currentX = current.x;
            double currentY = current.y;
            String currentKey = getKey(currentX, currentY);

            // If we reached the destination, reconstruct the path
            if (carReachedEnd(currentX, currentY)) {
                ArrayList<String> path = reconstructPath(parents, currentKey);
                moveCarAlongPath(path);
                return;
            }

            // Explore neighboring positions
            for (int i = 0; i < 4; i++) {
                double newX = currentX + moveX[i];
                double newY = currentY + moveY[i];

                // Skip out-of-bounds or wall positions
                if (!isValidMove(newX, newY)) continue;

                String newKey = getKey(newX, newY);
                int newGCost = current.g + 1;
                int newHCost = calculateHeuristic(newX, newY);
                int newFCost = newGCost + newHCost;

                // If the new position offers a shorter path, update it
                if (newGCost < gCosts.getOrDefault(newKey, Integer.MAX_VALUE)) {
                    gCosts.put(newKey, newGCost);
                    parents.put(newKey, currentKey);
                    pq.add(new Node(newX, newY, newGCost, newHCost));
                }
            }
        }
    }

    // Check if the car has reached the end (within tolerance range)
    private boolean carReachedEnd(double x, double y) {
        return Math.abs(x - END_X) < END_RANGE && Math.abs(y - END_Y) < END_RANGE;
    }

    // Check if a position is a valid move (not a wall)
    private boolean isValidMove(double x, double y) {
        int px = (int) (x * scaleX);
        int py = (int) (y * scaleY);

        if (px < 0 || py < 0 || px >= maze2Image.getWidth() || py >= maze2Image.getHeight()) {
            return false;
        }

        Color color = pixelReader.getColor(px, py);
        return !isWall(color);
    }

    // Helper method to check if a pixel is a wall (not white)
    private boolean isWall(Color color) {
        Color white = Color.web("#FFFFFF");
        return !color.equals(white);
    }

    // Calculate heuristic using Manhattan distance
    private int calculateHeuristic(double x, double y) {
        return (int) (Math.abs(x - END_X) + Math.abs(y - END_Y)); // Manhattan distance
    }

    // Move the car along the path (animate movement)
    private void moveCarAlongPath(ArrayList<String> path) {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(path.size());

        for (int i = 0; i < path.size(); i++) {
            String nodeKey = path.get(i);
            String[] parts = nodeKey.split(",");
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);

            // Adjust car position with some offset or scaling tweak
            double adjustedX = x - 20;
            double adjustedY = y - 15;

            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 50), e -> {
                car.setX(adjustedX);
                car.setY(adjustedY);
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        timeline.play();  // Start the animation
    }

    // Node class to store information about each position
    static class Node {
        double x, y;
        int g, h, f;

        Node(double x, double y, int g, int h) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }
    }
}