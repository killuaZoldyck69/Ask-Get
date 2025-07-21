/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.productrecommendation.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Tanver
 */
public class AddQueryController implements Initializable {
    
    @FXML private Button backBtn;
    @FXML private Button saveAsDraftBtn;
    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField minBudgetField;
    @FXML private TextField maxBudgetField;
    @FXML private ComboBox<String> currencyComboBox;
    @FXML private RadioButton lowPriorityRadio;
    @FXML private RadioButton mediumPriorityRadio;
    @FXML private RadioButton highPriorityRadio;
    @FXML private RadioButton urgentPriorityRadio;
    @FXML private TextArea descriptionArea;
    @FXML private TextField tagsField;
    @FXML private CheckBox allowPublicCheckBox;
    @FXML private CheckBox emailNotificationCheckBox;
    @FXML private CheckBox allowCommentsCheckBox;
    @FXML private Button cancelBtn;
    @FXML private Button submitBtn;
    @FXML private Label statusLabel;
    
    private ToggleGroup priorityGroup;
    
    // Reference to the main dashboard controller for navigation
    private DashboardController dashboardController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPriorityGroup();
        setupComboBoxes();
        setupValidation();
    }
    
    private void setupPriorityGroup() {
        priorityGroup = new ToggleGroup();
        lowPriorityRadio.setToggleGroup(priorityGroup);
        mediumPriorityRadio.setToggleGroup(priorityGroup);
        highPriorityRadio.setToggleGroup(priorityGroup);
        urgentPriorityRadio.setToggleGroup(priorityGroup);
        
        // Set medium as default
        mediumPriorityRadio.setSelected(true);
    }
    
    private void setupComboBoxes() {
        // Setup category options
        categoryComboBox.setItems(FXCollections.observableArrayList(
            "Electronics",
            "Computers & Technology",
            "Mobile Phones & Accessories",
            "Gaming",
            "Home & Kitchen",
            "Fashion & Clothing",
            "Sports & Outdoors",
            "Health & Beauty",
            "Books & Education",
            "Automotive",
            "Travel & Luggage",
            "Music & Instruments",
            "Toys & Games",
            "Furniture",
            "Garden & Tools",
            "Office Supplies",
            "Pet Supplies",
            "Food & Beverages",
            "Other"
        ));
        
        // Setup currency options
        currencyComboBox.setItems(FXCollections.observableArrayList(
            "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "BDT"
        ));
        currencyComboBox.setValue("USD");
    }
    
    private void setupValidation() {
        // Add input validation listeners
        titleField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                titleField.setText(oldText);
            }
        });
        
        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 1000) {
                descriptionArea.setText(oldText);
            }
        });
        
        // Only allow numbers in budget fields
        minBudgetField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*\\.?\\d*")) {
                minBudgetField.setText(oldText);
            }
        });
        
        maxBudgetField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*\\.?\\d*")) {
                maxBudgetField.setText(oldText);
            }
        });
    }
    
    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        if (dashboardController != null) {
            dashboardController.handleDashboard(event);
        }
    }
    
    @FXML
    private void handleSaveAsDraft(ActionEvent event) {
        if (!validateForm(false)) {
            return;
        }
        
        // Create query object with draft status
        Query query = createQueryFromForm();
        query.setStatus("draft");
        
        // TODO: Save to database as draft
        saveQueryToDatabase(query);
        
        showStatusMessage("Query saved as draft successfully! ✅", Color.GREEN);
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        // Show confirmation dialog if form has content
        if (hasFormContent()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancel");
            alert.setHeaderText("Unsaved Changes");
            alert.setContentText("You have unsaved changes. Are you sure you want to cancel?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                handleBack(event);
            }
        } else {
            handleBack(event);
        }
    }
    
    @FXML
    private void handleSubmit(ActionEvent event) {
        if (!validateForm(true)) {
            return;
        }
        
        // Create query object
        Query query = createQueryFromForm();
        query.setStatus("active");
        
        // TODO: Save to database and notify users
        saveQueryToDatabase(query);
        
        showStatusMessage("Query submitted successfully! 🚀", Color.GREEN);
        
        // Navigate back to dashboard after 2 seconds
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.seconds(2), e -> {
                if (dashboardController != null) {
                    dashboardController.handleDashboard(event);
                }
            })
        );
        timeline.play();
    }
    
    private boolean validateForm(boolean isSubmit) {
        StringBuilder errors = new StringBuilder();
        
        // Required field validation
        if (titleField.getText().trim().isEmpty()) {
            errors.append("• Title is required\n");
        }
        
        if (categoryComboBox.getValue() == null) {
            errors.append("• Category is required\n");
        }
        
        if (descriptionArea.getText().trim().isEmpty()) {
            errors.append("• Description is required\n");
        }
        
        // Budget validation
        if (!minBudgetField.getText().trim().isEmpty() && !maxBudgetField.getText().trim().isEmpty()) {
            try {
                double minBudget = Double.parseDouble(minBudgetField.getText());
                double maxBudget = Double.parseDouble(maxBudgetField.getText());
                
                if (minBudget >= maxBudget) {
                    errors.append("• Maximum budget must be greater than minimum budget\n");
                }
            } catch (NumberFormatException e) {
                errors.append("• Budget values must be valid numbers\n");
            }
        }
        
        if (errors.length() > 0) {
            showErrorDialog("Validation Errors", errors.toString());
            return false;
        }
        
        return true;
    }
    
    private Query createQueryFromForm() {
        Query query = new Query();
        query.setTitle(titleField.getText().trim());
        query.setCategory(categoryComboBox.getValue());
        query.setDescription(descriptionArea.getText().trim());
        query.setTags(tagsField.getText().trim());
        query.setMinBudget(minBudgetField.getText().trim().isEmpty() ? null : Double.parseDouble(minBudgetField.getText()));
        query.setMaxBudget(maxBudgetField.getText().trim().isEmpty() ? null : Double.parseDouble(maxBudgetField.getText()));
        query.setCurrency(currencyComboBox.getValue());
        query.setPriority(getSelectedPriority());
        query.setIsPublic(allowPublicCheckBox.isSelected());
        query.setEmailNotification(emailNotificationCheckBox.isSelected());
        query.setAllowComments(allowCommentsCheckBox.isSelected());
        query.setCreatedDate(java.time.LocalDateTime.now());
        
        return query;
    }
    
    private String getSelectedPriority() {
        RadioButton selected = (RadioButton) priorityGroup.getSelectedToggle();
        return selected.getText().toLowerCase();
    }
    
    private boolean hasFormContent() {
        return !titleField.getText().trim().isEmpty() ||
               !descriptionArea.getText().trim().isEmpty() ||
               categoryComboBox.getValue() != null ||
               !minBudgetField.getText().trim().isEmpty() ||
               !maxBudgetField.getText().trim().isEmpty() ||
               !tagsField.getText().trim().isEmpty();
    }
    
    private void showStatusMessage(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setTextFill(color);
        statusLabel.setVisible(true);
        
        // Fade out after 3 seconds
        FadeTransition fade = new FadeTransition(Duration.seconds(3), statusLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> statusLabel.setVisible(false));
        fade.play();
    }
    
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void saveQueryToDatabase(Query query) {
        // TODO: Implement database saving logic
        // This is where you would save the query to your database
        System.out.println("Saving query: " + query.getTitle());
    }
    
    // Inner class for Query model
    public static class Query {
        private String title;
        private String category;
        private String description;
        private String tags;
        private Double minBudget;
        private Double maxBudget;
        private String currency;
        private String priority;
        private String status;
        private boolean isPublic;
        private boolean emailNotification;
        private boolean allowComments;
        private java.time.LocalDateTime createdDate;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
        
        public Double getMinBudget() { return minBudget; }
        public void setMinBudget(Double minBudget) { this.minBudget = minBudget; }
        
        public Double getMaxBudget() { return maxBudget; }
        public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }
        
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public boolean isPublic() { return isPublic; }
        public void setIsPublic(boolean isPublic) { this.isPublic = isPublic; }
        
        public boolean isEmailNotification() { return emailNotification; }
        public void setEmailNotification(boolean emailNotification) { this.emailNotification = emailNotification; }
        
        public boolean isAllowComments() { return allowComments; }
        public void setAllowComments(boolean allowComments) { this.allowComments = allowComments; }
        
        public java.time.LocalDateTime getCreatedDate() { return createdDate; }
        public void setCreatedDate(java.time.LocalDateTime createdDate) { this.createdDate = createdDate; }
    }
}