package com.productrecommendation.controllers;

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
        // Get the email and password values
        String email = emailField.getText();
        String password = passwordField.getText();

        // Here you would typically:
        // 1. Validate the input
        // 2. Check credentials against database
        // 3. If valid, redirect to Dashboard
        // For demonstration, we'll just navigate to the Dashboard
        // In real implementation, add proper authentication logic
        try {
            navigateToDashboard(event);
        } catch (IOException e) {
            System.err.println("Error loading Dashboard: " + e.getMessage());
            e.printStackTrace();
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
