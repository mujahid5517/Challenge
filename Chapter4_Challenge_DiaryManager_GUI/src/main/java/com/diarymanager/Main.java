package com.diarymanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load main FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/diarymanager/views/main-view.fxml"));
        Parent root = loader.load();

        // Set up main scene
        Scene scene = new Scene(root, 1200, 800);

        // Load light theme by default
        scene.getStylesheets().add(getClass().getResource("/com/diarymanager/css/styles-light.css").toExternalForm());

        // Set application icon
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/diarymanager/icons/logo.png")));

        primaryStage.setTitle("Personal Diary Manager");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();

        // Initialize diary directory
        initializeDiaryDirectory();
    }

    private void initializeDiaryDirectory() {
        java.nio.file.Path diaryDir = java.nio.file.Paths.get("diary-entries");
        if (!java.nio.file.Files.exists(diaryDir)) {
            try {
                java.nio.file.Files.createDirectories(diaryDir);
                System.out.println("Diary directory created: " + diaryDir.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Failed to create diary directory: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}