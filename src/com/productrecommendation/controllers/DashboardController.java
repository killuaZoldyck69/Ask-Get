/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.productrecommendation.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Tanver
 */
public class DashboardController implements Initializable {

    @FXML
    private BorderPane mainBorderPane; // Add this to reference the main BorderPane
    @FXML
    private TextField searchField;
    @FXML
    private Button notificationBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button dashboardBtn;
    @FXML
    private Button profileBtn;
    @FXML
    private Button allQueriesBtn;
    @FXML
    private Button myQueriesBtn;
    @FXML
    private Button myRecommendationsBtn;
    @FXML
    private Button recommendationsForMeBtn;
    @FXML
    private Label totalQueriesLabel;
    @FXML
    private Label recommendationsLabel;
    @FXML
    private Label thisWeekLabel;
    @FXML
    private Label greetingLabel;
    @FXML
    private Button newQueryBtn;
    @FXML
    private Label totalQueriesCardLabel;
    @FXML
    private Label totalQueriesChangeLabel;
    @FXML
    private Label myRecommendationsCardLabel;
    @FXML
    private Label myRecommendationsChangeLabel;
    @FXML
    private Label activeUsersCardLabel;
    @FXML
    private Label activeUsersChangeLabel;
    @FXML
    private Label engagementRateCardLabel;
    @FXML
    private Label engagementRateChangeLabel;
    @FXML
    private VBox recentQueriesBox;
    @FXML
    private Button recentQueriesSettingsBtn;
    @FXML
    private VBox queryItemsContainer;
    @FXML
    private VBox trendingCategoriesBox;
    @FXML
    private VBox categoryItemsContainer;

    // Store the original dashboard content for switching back
    private Node originalDashboardContent;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Store the original dashboard content
        originalDashboardContent = mainBorderPane.getCenter();
    }

    /**
     * Method to load FXML content into the center area
     */
    private void loadContentInCenter(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
        }
    }

    /**
     * Method to update navigation button styles
     */
    private void updateNavigationButtons(Button activeButton) {
        // Reset all buttons to inactive style
        Button[] navButtons = {dashboardBtn, profileBtn, allQueriesBtn, myQueriesBtn,
            myRecommendationsBtn, recommendationsForMeBtn};

        for (Button btn : navButtons) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6B7280; -fx-background-radius: 8;");
        }

        // Set active button style
        activeButton.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8;");
    }

    public void showMainDashboard() {
        mainBorderPane.setCenter(originalDashboardContent);
        updateNavigationButtons(dashboardBtn);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        // Implementation for search functionality
    }

    @FXML
    private void handleNotification(ActionEvent event) {
        // Implementation for notifications
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        // Implementation for settings
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Load the Login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/Login.fxml")); // Adjust path as needed
            Node loginRoot = loader.load();

            // Create a new scene with the login UI
            javafx.scene.Scene loginScene = new javafx.scene.Scene((javafx.scene.Parent) loginRoot);

            // Get the current stage (window) and replace the scene
            javafx.stage.Stage currentStage = (javafx.stage.Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Login.fxml");
        }
    }

    @FXML
    void handleDashboard(ActionEvent event) {
        // Return to original dashboard content
        mainBorderPane.setCenter(originalDashboardContent);
        updateNavigationButtons(dashboardBtn);
    }

    @FXML
    private void handleProfile(ActionEvent event) {
        // Load Profile.fxml in the center area
        loadContentInCenter("/resources/com/productrecommendation/fxml/Profile.fxml"); // Adjust path as needed
        updateNavigationButtons(profileBtn);
    }

    @FXML
    private void handleAllQueries(ActionEvent event) {
        // Load AllQueries.fxml in the center area
        loadContentInCenter("/fxml/AllQueries.fxml"); // Adjust path as needed
        updateNavigationButtons(allQueriesBtn);
    }

    @FXML
    private void handleMyQueries(ActionEvent event) {
        // Load MyQueries.fxml in the center area
        loadContentInCenter("/fxml/MyQueries.fxml"); // Adjust path as needed
        updateNavigationButtons(myQueriesBtn);
    }

    @FXML
    private void handleMyRecommendations(ActionEvent event) {
        // Load MyRecommendations.fxml in the center area
        loadContentInCenter("/fxml/MyRecommendations.fxml"); // Adjust path as needed
        updateNavigationButtons(myRecommendationsBtn);
    }

    @FXML
    private void handleRecommendationsForMe(ActionEvent event) {
        // Load RecommendationsForMe.fxml in the center area
        loadContentInCenter("/fxml/RecommendationsForMe.fxml"); // Adjust path as needed
        updateNavigationButtons(recommendationsForMeBtn);
    }

    @FXML
    private void handleNewQuery(ActionEvent event) {
        // Implementation for new query
        // Load AddQuery.fxml in the center area
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/AddQuery.fxml")); // Adjust path as needed
            Node content = loader.load();

            // Get the AddQueryController and set reference to this dashboard controller
            AddQueryController addQueryController = loader.getController();
            addQueryController.setDashboardController(this);

            mainBorderPane.setCenter(content);

            // Don't update navigation buttons since this is not a main navigation item
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading AddQuery.fxml");
        }
    }

    @FXML
    private void handleRecentQueriesSettings(ActionEvent event) {
        // Implementation for recent queries settings
    }
}
