package com.productrecommendation;

import com.productrecommendation.models.MongoDBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            MongoDBConnection.connect();
            // Load the Login.fxml file from resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/Login.fxml"));

            // Debug print (optional)
            System.out.println("Attempting to load: " + getClass().getResource("/resources/com/productrecommendation/fxml/Login.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setTitle("Ask&Get - Product Recommendation System");
            primaryStage.setScene(scene);

            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Failed to load FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
