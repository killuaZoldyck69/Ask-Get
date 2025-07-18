/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.productrecommendation.models.MongoDBConnection;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.Document;



/**
 *
 * @author Tasin Ahmed
 */
public class RegistrationController {

    @FXML
    private TextField displayNameField;
    @FXML
    private TextField photoUrlField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void onForgotPassword(ActionEvent event) {
    }

@FXML
private void onSignUpButtonClicked(ActionEvent event) {
    String displayName = displayNameField.getText();
    String photoUrl = photoUrlField.getText();
    String email = emailField.getText();
    String password = passwordField.getText();

    // Basic validation
    if (displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
        System.out.println("All required fields must be filled.");
        return;
    }

    // Create a user document
    Document userDoc = new Document()
            .append("display_name", displayName)
            .append("photo_url", photoUrl)
            .append("email", email)
            .append("password", password) // ⚠️ For security, hash passwords in production
            .append("registered_at", System.currentTimeMillis());

    try {
        // Connect to MongoDB
        MongoDBConnection.connect();
        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> usersCollection = db.getCollection("users");

        // Optional: Check if email already exists
        Document existingUser = usersCollection.find(new Document("email", email)).first();
        if (existingUser != null) {
            System.out.println("Email already registered!");
            return;
        }

        // Insert user
        usersCollection.insertOne(userDoc);
        System.out.println("User registered successfully.");

        // Redirect to Login screen
        Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/productrecommendation/fxml/Login.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        boolean isMaximized = stage.isMaximized();
        stage.setScene(new Scene(loginRoot));
        stage.setMaximized(isMaximized);
        stage.show();

    } catch (Exception e) {
        System.err.println("Error inserting user: " + e.getMessage());
        e.printStackTrace();
    } finally {
        MongoDBConnection.close();
    }
}


    @FXML
    private void onGoogleSignUp(ActionEvent event) {
    }

    @FXML
    private void onLoginClicked(ActionEvent event) {
        try {
            // Load the login FXML file
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/resources/com/productrecommendation/fxml/Login.fxml"));

            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(loginRoot));

            // Enable full screen
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // You can replace this with a user-friendly alert if needed
        }
    }

}
