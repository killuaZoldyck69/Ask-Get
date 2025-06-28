/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Tasin Ahmed
 */
public class DashboardController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button allQueriesButton;
    @FXML
    private Button myQueriesButton;
    @FXML
    private Button myRecommendationsButton;
    @FXML
    private Button recommendationsForMeButton;
    @FXML
    private Label totalQueriesLabel;
    @FXML
    private Label totalRecommendationsLabel;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox welcomeScreen;
    @FXML
    private ScrollPane profileSection;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea bioArea;
    @FXML
    private VBox allQueriesSection;
    @FXML
    private TextField searchQueriesField;
    @FXML
    private TableView<?> allQueriesTable;
    @FXML
    private TableColumn<?, ?> queryIdColumn;
    @FXML
    private TableColumn<?, ?> queryUserColumn;
    @FXML
    private TableColumn<?, ?> queryTextColumn;
    @FXML
    private TableColumn<?, ?> queryDateColumn;
    @FXML
    private TableColumn<?, ?> queryStatusColumn;
    @FXML
    private VBox myQueriesSection;
    @FXML
    private TableView<?> myQueriesTable;
    @FXML
    private TableColumn<?, ?> myQueryIdColumn;
    @FXML
    private TableColumn<?, ?> myQueryTextColumn;
    @FXML
    private TableColumn<?, ?> myQueryDateColumn;
    @FXML
    private TableColumn<?, ?> myQueryStatusColumn;
    @FXML
    private TableColumn<?, ?> myQueryActionsColumn;
    @FXML
    private VBox myRecommendationsSection;
    @FXML
    private VBox myRecommendationsContainer;
    @FXML
    private VBox recommendationsForMeSection;
    @FXML
    private VBox recommendationsForMeContainer;

    @FXML
    private void handleLogout(ActionEvent event) {
    }

    @FXML
    private void showProfile(ActionEvent event) {
    }

    @FXML
    private void showAllQueries(ActionEvent event) {
    }

    @FXML
    private void showMyQueries(ActionEvent event) {
    }

    @FXML
    private void showMyRecommendations(ActionEvent event) {
    }

    @FXML
    private void showRecommendationsForMe(ActionEvent event) {
    }

    @FXML
    private void saveProfile(ActionEvent event) {
    }

    @FXML
    private void cancelProfileEdit(ActionEvent event) {
    }

    @FXML
    private void searchQueries(ActionEvent event) {
    }

    @FXML
    private void createNewQuery(ActionEvent event) {
    }

    @FXML
    private void refreshRecommendations(ActionEvent event) {
    }
    
}
