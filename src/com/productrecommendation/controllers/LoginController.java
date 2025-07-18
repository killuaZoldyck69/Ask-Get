package com.productrecommendation.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.productrecommendation.models.MongoDBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import org.bson.Document;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    public void initialize() {
        // Any initialization code can go here
    }

@FXML
public void onLoginButtonClicked(ActionEvent event) {
    String email = emailField.getText();
    String password = passwordField.getText();

    if (email.isEmpty() || password.isEmpty()) {
        System.out.println("Email and password cannot be empty.");
        return;
    }

    try {
        // Connect to MongoDB
        MongoDBConnection.connect();
        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> usersCollection = db.getCollection("users");

        // Find user by email
        Document user = usersCollection.find(new Document("email", email)).first();

        if (user != null) {
            String storedPassword = user.getString("password");

            // Compare passwords (⚠️ Plaintext for now, use hashing in production!)
            if (password.equals(storedPassword)) {
                System.out.println("Login successful! Redirecting to Dashboard...");

                // Navigate to Dashboard
                Parent dashboardView = FXMLLoader.load(getClass().getResource("/resources/com/productrecommendation/fxml/Dashboard.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                boolean isMaximized = stage.isMaximized();
                stage.setScene(new Scene(dashboardView));
                stage.setMaximized(isMaximized);
                stage.show();

            } else {
                System.out.println("Incorrect password.");
            }
        } else {
            System.out.println("No user found with that email.");
        }

    } catch (Exception e) {
        System.err.println("Login error: " + e.getMessage());
        e.printStackTrace();
    } finally {
        MongoDBConnection.close();
    }
}


    @FXML
    public void onForgotPassword() {
        // Implement password reset functionality
        System.out.println("Password reset requested");
        // This would typically show a dialog or navigate to password reset page
    }

    @FXML
    public void onGoogleLogin(ActionEvent event) {
        // Implement Google OAuth login
        System.out.println("Google login requested");
        // In real implementation, add OAuth logic
    }

    @FXML
    public void onSignUpClicked(ActionEvent event) {
        try {
            // Load the Registration view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/Registration.fxml"));
            Parent registrationView = loader.load();

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the registration scene
            Scene scene = new Scene(registrationView);
            stage.setScene(scene);

            // Enable full screen
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Registration view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(ActionEvent event) throws IOException {
        // Load the Dashboard view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/productrecommendation/fxml/Dashboard.fxml"));
        Parent dashboardView = loader.load();

        // Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Set the dashboard scene
        Scene scene = new Scene(dashboardView);
        stage.setScene(scene);
        stage.show();
    }
}
