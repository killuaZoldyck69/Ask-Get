/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

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
