package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 A JavaFX application that displays an image gallery with thumbnails and full-image view functionality.
 */
public class HelloApplication extends Application {
    private final List<Image> images = new ArrayList<>(); // List to store loaded images

    @Override
    public void start(Stage primaryStage) {
        loadImages(); // Load images from the directory
        GridPane gridPane = createThumbnailGrid(); // Create a grid layout for image thumbnails

        ScrollPane scrollPane = new ScrollPane(gridPane); // Add scrolling for a large number of images
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        applyCSS(scene); // Apply external CSS for styling

        primaryStage.setTitle("Rich Internet Image Gallery"); // Set the window title
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        // Set application icon
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/demo/images/download.png")));
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    /**
     Loads image files from a predefined directory into the images list.
     */
    private void loadImages() {
        File imageDir = new File(Objects.requireNonNull(getClass().getResource("/org/example/demo/images")).getFile());
        File[] imageFiles = imageDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg"));

        if (imageFiles != null) {
            for (File file : imageFiles) {
                images.add(new Image(file.toURI().toString()));
            }
        }
    }

    /**
      Creates a grid layout populated with image thumbnails.
      Each thumbnail is wrapped in a button that opens the full image view when clicked.

     @return a GridPane containing the image thumbnails.
     */
    private GridPane createThumbnailGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < images.size(); i++) {
            ImageView thumbnail = createThumbnail(images.get(i)); // Create a scaled-down version of the image
            Button button = new Button("", thumbnail); // Button displaying the thumbnail
            button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            button.getStyleClass().add("thumbnail-button"); // Apply CSS style

            final int index = i;
            button.setOnAction(e -> openFullImage(index)); // Open full image on click
            gridPane.add(button, i % 6, i / 6); // Arrange thumbnails in a 6-column grid
        }

        return gridPane;
    }

    /**
      Creates an ImageView with a fixed size to serve as a thumbnail.
     Uses viewport cropping to ensure the image fits well.

     @param image the original image to be resized.
     @return a resized ImageView representing the thumbnail.
     */
    private ImageView createThumbnail(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(195);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Center crop effect
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double targetWidth = 195;
        double targetHeight = 100;
        double scale = Math.max(targetWidth / imageWidth, targetHeight / imageHeight);
        double newWidth = targetWidth / scale;
        double newHeight = targetHeight / scale;
        double xOffset = (imageWidth - newWidth) / 2;
        double yOffset = (imageHeight - newHeight) / 2;
        imageView.setViewport(new javafx.geometry.Rectangle2D(xOffset, yOffset, newWidth, newHeight));

        return imageView;
    }

    /**
     Opens a new window displaying the selected image in full size.
     Allows navigation between images using previous and next buttons.

     @param index the index of the image to display initially.
     */
    private void openFullImage(int index) {
        Stage stage = new Stage();
        BorderPane pane = new BorderPane();

        ImageView fullImageView = new ImageView(images.get(index));
        fullImageView.setFitHeight(600);
        fullImageView.setPreserveRatio(true);
        pane.setCenter(fullImageView);

        Button prevButton = new Button("◄");
        Button nextButton = new Button("►");
        Button backButton = new Button("Back");
        prevButton.getStyleClass().add("nav-button");
        nextButton.getStyleClass().add("nav-button2");
        backButton.getStyleClass().add("button2");

        VBox backBox = new VBox(backButton);
        backBox.setAlignment(Pos.CENTER);
        pane.setBottom(backBox);

        BorderPane.setAlignment(prevButton, Pos.CENTER_LEFT);
        BorderPane.setAlignment(nextButton, Pos.CENTER_RIGHT);
        pane.setLeft(prevButton);
        pane.setRight(nextButton);

        final int[] currentIndex = {index};
        prevButton.setVisible(currentIndex[0] > 0);
        nextButton.setVisible(currentIndex[0] < images.size() - 1);

        prevButton.setOnAction(e -> {
            if (currentIndex[0] > 0) {
                currentIndex[0]--;
                fullImageView.setImage(images.get(currentIndex[0]));
            }
            prevButton.setVisible(currentIndex[0] > 0);
            nextButton.setVisible(true);
        });

        nextButton.setOnAction(e -> {
            if (currentIndex[0] < images.size() - 1) {
                currentIndex[0]++;
                fullImageView.setImage(images.get(currentIndex[0]));
            }
            nextButton.setVisible(currentIndex[0] < images.size() - 1);
            prevButton.setVisible(true);
        });

        backButton.setOnAction(e -> stage.close());

        Scene scene = new Scene(pane, 850, 600);
        applyCSS(scene);

        stage.setScene(scene);
        stage.setTitle("Full Image Viewer");
        stage.setMaximized(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    /**
     Applies an external CSS file to the scene for consistent styling.

     @param scene the Scene to which the CSS will be applied.
     */
    private void applyCSS(Scene scene) {
        URL cssUrl = getClass().getResource("/org/example/demo/image.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("ERROR: CSS file not found!");
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}