package com.productrecommendation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Method 1: Using getClass().getResource() with correct path
            // Make sure the path starts with a forward slash
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/Dashboard.fxml"));

            // Alternative Method 2: If Method 1 doesn't work, try this
            // FXMLLoader loader = new FXMLLoader();
            // loader.setLocation(Main.class.getResource("/com/productrecommendation/fxml/Login.fxml"));
            // Alternative Method 3: Using an absolute path (for debugging)
            // This is NOT recommended for production, but helpful for debugging
            // String absolutePath = System.getProperty("user.dir") + "/src/main/resources/com/productrecommendation/fxml/Login.fxml";
            // FXMLLoader loader = new FXMLLoader(new File(absolutePath).toURI().toURL());
            // Print the resource URL to help debug
            System.out.println("Attempting to load: " + getClass().getResource("/com/productrecommendation/fxml/Dashboard.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Ask&Get - Product Recommendation System");
            primaryStage.setScene(scene);
            // Full screen mode
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
