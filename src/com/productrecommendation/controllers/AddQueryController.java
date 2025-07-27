package com.productrecommendation.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.productrecommendation.controllers.SessionManager.loggedInName;
import com.productrecommendation.models.MongoDBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import org.bson.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class AddQueryController implements Initializable {

    // Header Controls
    @FXML
    private Button backBtn;

    // Form Controls
    @FXML
    private ComboBox<String> queryTypeComboBox;
    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> categoryComboBox;

    // Conditional Sections
    @FXML
    private HBox budgetSection;
    @FXML
    private TextField minBudgetField;
    @FXML
    private TextField maxBudgetField;
    @FXML
    private ComboBox<String> currencyComboBox;

    @FXML
    private VBox locationSection;
    @FXML
    private TextField locationField;

    @FXML
    private HBox timelineSection;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;

    // Priority Selection
    @FXML
    private RadioButton lowPriorityRadio;
    @FXML
    private RadioButton mediumPriorityRadio;
    @FXML
    private RadioButton highPriorityRadio;
    @FXML
    private RadioButton urgentPriorityRadio;
    @FXML
    private ToggleGroup priorityGroup;

    // Description and Content
    @FXML
    private TextArea descriptionArea;

    // Image Upload
    @FXML
    private TextField imageUrlField;
    @FXML
    private Button addImageBtn;
    @FXML
    private ScrollPane imagePreviewScrollPane;
    @FXML
    private HBox imagePreviewContainer;

    // Tags and Preferences
    @FXML
    private TextField tagsField;
    private CheckBox allowPublicCheckBox;
    private CheckBox emailNotificationCheckBox;
    private CheckBox allowCommentsCheckBox;
    private CheckBox allowAnonymousCheckBox;

    // Action Buttons
    @FXML
    private Button previewBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button submitBtn;
    @FXML
    private Label statusLabel;

    // Data Storage
    private List<String> imageUrls = new ArrayList<>();
    private QueryData queryData = new QueryData();
    private final String loggedInEmail = SessionManager.loggedInEmail; // ✅ Dynamic way
    private final String currentUserName = SessionManager.loggedInName;

    // Reference to parent dashboard controller
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupPriorityGroup();
        setupComboBoxes();
        setupValidation();
        setupDatePickers();
        setupNumericFields();
    }

    private void setupPriorityGroup() {
        priorityGroup = new ToggleGroup();
        lowPriorityRadio.setToggleGroup(priorityGroup);
        mediumPriorityRadio.setToggleGroup(priorityGroup);
        highPriorityRadio.setToggleGroup(priorityGroup);
        urgentPriorityRadio.setToggleGroup(priorityGroup);
    }

    private void setupComboBoxes() {
        // Query Types
        ObservableList<String> queryTypes = FXCollections.observableArrayList(
                "Product Recommendation",
                "Service Request",
                "Technical Support",
                "General Question",
                "Comparison Request",
                "Troubleshooting",
                "Consultation"
        );
        queryTypeComboBox.setItems(queryTypes);

        // Categories
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Electronics & Gadgets",
                "Home & Garden",
                "Fashion & Beauty",
                "Health & Fitness",
                "Automotive",
                "Books & Education",
                "Food & Beverages",
                "Travel & Tourism",
                "Business & Finance",
                "Software & Technology",
                "Entertainment & Media",
                "Sports & Recreation",
                "Other"
        );
        categoryComboBox.setItems(categories);

        // Currencies
        ObservableList<String> currencies = FXCollections.observableArrayList(
                "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR", "BRL"
        );
        currencyComboBox.setItems(currencies);
        currencyComboBox.setValue("USD");
    }

    private void setupValidation() {
        // Add text limiters
        titleField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 200) {
                titleField.setText(oldText);
            }
        });

        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 5000) {
                descriptionArea.setText(oldText);
            }
        });

        tagsField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 500) {
                tagsField.setText(oldText);
            }
        });
    }

    private void setupDatePickers() {
        // Set date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        startDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
            }
        });

        endDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, formatter) : null;
            }
        });

        // Set minimum date to today
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusWeeks(2));
    }

    private void setupNumericFields() {
        // Allow only numeric input for budget fields
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

    @FXML
    private void handleQueryTypeChange() {
        String selectedType = queryTypeComboBox.getValue();
        if (selectedType != null) {
            updateConditionalSections(selectedType);
        }
    }

    private void updateConditionalSections(String queryType) {
        // Show/hide sections based on query type
        switch (queryType) {
            case "Product Recommendation":
            case "Comparison Request":
                budgetSection.setVisible(true);
                budgetSection.setManaged(true);
                locationSection.setVisible(false);
                locationSection.setManaged(false);
                timelineSection.setVisible(false);
                timelineSection.setManaged(false);
                break;

            case "Service Request":
            case "Consultation":
                budgetSection.setVisible(true);
                budgetSection.setManaged(true);
                locationSection.setVisible(true);
                locationSection.setManaged(true);
                timelineSection.setVisible(true);
                timelineSection.setManaged(true);
                break;

            case "Technical Support":
            case "Troubleshooting":
                budgetSection.setVisible(false);
                budgetSection.setManaged(false);
                locationSection.setVisible(false);
                locationSection.setManaged(false);
                timelineSection.setVisible(false);
                timelineSection.setManaged(false);
                break;

            default:
                budgetSection.setVisible(false);
                budgetSection.setManaged(false);
                locationSection.setVisible(true);
                locationSection.setManaged(true);
                timelineSection.setVisible(false);
                timelineSection.setManaged(false);
                break;
        }
    }

    @FXML
    private void handleAddImage() {
        String imageUrl = imageUrlField.getText().trim();

        if (imageUrl.isEmpty()) {
            showStatus("Please enter an image URL", "error");
            return;
        }

        if (!isValidImageUrl(imageUrl)) {
            showStatus("Please enter a valid image URL (jpg, png, gif)", "error");
            return;
        }

        if (imageUrls.size() >= 5) {
            showStatus("Maximum 5 images allowed", "error");
            return;
        }

        try {
            addImagePreview(imageUrl);
            imageUrls.add(imageUrl);
            imageUrlField.clear();
            showStatus("Image added successfully", "success");
        } catch (Exception e) {
            showStatus("Failed to load image. Please check the URL", "error");
        }
    }

    private boolean isValidImageUrl(String url) {
        return url.toLowerCase().matches("^https?://.*\\.(jpg|jpeg|png|gif)(\\?.*)?$");
    }

    private void addImagePreview(String imageUrl) {
        VBox imageContainer = new VBox(5);
        imageContainer.setStyle("-fx-alignment: center; -fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-padding: 5;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        try {
            Image image = new Image(imageUrl, true);
            imageView.setImage(image);
        } catch (Exception e) {
            // Handle image loading error
            Label errorLabel = new Label("Failed to load");
            errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 10px;");
            imageContainer.getChildren().add(errorLabel);
        }

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6 2 6;");
        removeBtn.setOnAction(e -> removeImagePreview(imageContainer, imageUrl));

        imageContainer.getChildren().addAll(imageView, removeBtn);
        imagePreviewContainer.getChildren().add(imageContainer);

        imagePreviewScrollPane.setVisible(true);
        imagePreviewScrollPane.setManaged(true);
    }

    private void removeImagePreview(VBox imageContainer, String imageUrl) {
        imagePreviewContainer.getChildren().remove(imageContainer);
        imageUrls.remove(imageUrl);

        if (imageUrls.isEmpty()) {
            imagePreviewScrollPane.setVisible(false);
            imagePreviewScrollPane.setManaged(false);
        }
    }

    private void navigateBack() {
        if (dashboardController != null) {
            try {
                dashboardController.getClass().getMethod("showMainDashboard").invoke(dashboardController);
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback: close window
                Stage stage = (Stage) backBtn.getScene().getWindow();
                if (stage != null) {
                    stage.close();
                }
            }
        } else {
            // Fallback: close window
            Stage stage = (Stage) backBtn.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }

    @FXML
    private void handleBack() {
        if (hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Unsaved Changes");
            alert.setHeaderText("You have unsaved changes");
            alert.setContentText("Are you sure you want to go back? All changes will be lost.");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                navigateBack();
            }
        } else {
            navigateBack();
        }
    }

    private void handleSaveAsDraft() {
        if (validateMinimumFields()) {
            collectFormData();
            // TODO: Implement draft saving logic
            showStatus("Query saved as draft", "success");
        }
    }

    @FXML
    private void handlePreview() {
        if (validateMinimumFields()) {
            collectFormData();
            // TODO: Open preview dialog
            showPreviewDialog();
        }
    }

    @FXML
    private void handleCancel() {
        handleBack();
    }

    @FXML
    private void handleSubmit() {
        if (validateForm()) {
            collectFormData();

            // TODO: Implement actual submission logic
            submitQuery();
        }
    }

    private boolean validateForm() {
        List<String> errors = new ArrayList<>();

        if (queryTypeComboBox.getValue() == null || queryTypeComboBox.getValue().isEmpty()) {
            errors.add("Query type is required");
        }

        if (titleField.getText().trim().isEmpty()) {
            errors.add("Query title is required");
        }

        if (categoryComboBox.getValue() == null || categoryComboBox.getValue().isEmpty()) {
            errors.add("Category is required");
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            errors.add("Description is required");
        }

        // Validate budget fields if visible
        if (budgetSection.isVisible()) {
            String minBudget = minBudgetField.getText().trim();
            String maxBudget = maxBudgetField.getText().trim();

            if (!minBudget.isEmpty() && !maxBudget.isEmpty()) {
                try {
                    double min = Double.parseDouble(minBudget);
                    double max = Double.parseDouble(maxBudget);
                    if (min > max) {
                        errors.add("Minimum budget cannot be greater than maximum budget");
                    }
                } catch (NumberFormatException e) {
                    errors.add("Invalid budget values");
                }
            }
        }

        // Validate dates if visible
        if (timelineSection.isVisible()) {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                errors.add("Start date cannot be after end date");
            }
        }

        if (!errors.isEmpty()) {
            showValidationErrors(errors);
            return false;
        }

        return true;
    }

    private boolean validateMinimumFields() {
        return !titleField.getText().trim().isEmpty() || !descriptionArea.getText().trim().isEmpty();
    }

    private void collectFormData() {
        queryData.setQueryType(queryTypeComboBox.getValue());
        queryData.setTitle(titleField.getText().trim());
        queryData.setCategory(categoryComboBox.getValue());
        queryData.setDescription(descriptionArea.getText().trim());

        // Priority
        RadioButton selectedPriority = (RadioButton) priorityGroup.getSelectedToggle();
        if (selectedPriority != null) {
            queryData.setPriority(selectedPriority.getText().toLowerCase());
        }

        // Budget
        if (budgetSection.isVisible()) {
            queryData.setMinBudget(minBudgetField.getText().trim());
            queryData.setMaxBudget(maxBudgetField.getText().trim());
            queryData.setCurrency(currencyComboBox.getValue());
        }

        // Location
        if (locationSection.isVisible()) {
            queryData.setLocation(locationField.getText().trim());
        }

        // Timeline
        if (timelineSection.isVisible()) {
            queryData.setStartDate(startDatePicker.getValue());
            queryData.setEndDate(endDatePicker.getValue());
        }

        // Tags
        String tagsText = tagsField.getText().trim();
        if (!tagsText.isEmpty()) {
            queryData.setTags(Arrays.asList(tagsText.split(",")));
        }

        // Images
        queryData.setImageUrls(new ArrayList<>(imageUrls));

        // Preferences
        queryData.setPublic(allowPublicCheckBox.isSelected());
        queryData.setEmailNotifications(emailNotificationCheckBox.isSelected());
        queryData.setAllowComments(allowCommentsCheckBox.isSelected());
        queryData.setAllowAnonymous(allowAnonymousCheckBox.isSelected());
    }

    private void submitQuery() {
        try {
            // Connect to the MongoDB
            MongoDatabase db = MongoDBConnection.getDatabase();
            MongoCollection<Document> collection = db.getCollection("queries");

            // Create a MongoDB Document from QueryData
            Document doc = new Document("queryType", queryData.getQueryType())
                    .append("title", queryData.getTitle())
                    .append("category", queryData.getCategory())
                    .append("description", queryData.getDescription())
                    .append("priority", queryData.getPriority())
                    .append("minBudget", queryData.getMinBudget())
                    .append("maxBudget", queryData.getMaxBudget())
                    .append("currency", queryData.getCurrency())
                    .append("location", queryData.getLocation())
                    .append("startDate", queryData.getStartDate() != null ? queryData.getStartDate().toString() : null)
                    .append("endDate", queryData.getEndDate() != null ? queryData.getEndDate().toString() : null)
                    .append("tags", queryData.getTags())
                    .append("imageUrls", queryData.getImageUrls())
                    .append("isPublic", queryData.isPublic())
                    .append("emailNotifications", queryData.isEmailNotifications())
                    .append("allowComments", queryData.isAllowComments())
                    .append("allowAnonymous", queryData.isAllowAnonymous())
                    // ✅ Additional Fields
                    .append("userName", currentUserName)
                    .append("userEmail", loggedInEmail)
                    .append("solved", false)
                    .append("totalRecommendations", 0)
                    .append("submissionDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Insert the document
            collection.insertOne(doc);

            // Show success status
            showStatus("Query submitted successfully!", "success");

            // Show alert after short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("Query Submitted");
                        alert.setContentText("Your query has been submitted successfully!");
                        alert.showAndWait();

                        // Navigate back after submission
                        navigateBack();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showStatus("Failed to submit query: " + e.getMessage(), "error");
        }
    }

    private void showPreviewDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Query Preview");
        alert.setHeaderText(queryData.getTitle());

        StringBuilder content = new StringBuilder();
        content.append("Type: ").append(queryData.getQueryType()).append("\n");
        content.append("Category: ").append(queryData.getCategory()).append("\n");
        content.append("Priority: ").append(queryData.getPriority()).append("\n\n");
        content.append("Description:\n").append(queryData.getDescription());

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void showValidationErrors(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following errors:");
        alert.setContentText(String.join("\n• ", errors));
        alert.showAndWait();
    }

    private void showStatus(String message, String type) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);

        String color = type.equals("error") ? "#EF4444" : "#10B981";
        statusLabel.setStyle("-fx-text-fill: " + color + ";");

        // Hide status after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> statusLabel.setVisible(false));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private boolean hasUnsavedChanges() {
        return !titleField.getText().trim().isEmpty()
                || !descriptionArea.getText().trim().isEmpty()
                || queryTypeComboBox.getValue() != null
                || categoryComboBox.getValue() != null;
    }

    // Method to set reference to dashboard controller
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
    }

    // Method for dashboard to call when returning to main view
    public void cleanup() {
        // Clean up any resources if needed
        imageUrls.clear();
        queryData = new QueryData();
    }

    // Data class to hold query information
    public static class QueryData {

        private String queryType;
        private String title;
        private String category;
        private String description;
        private String priority = "medium";
        private String minBudget;
        private String maxBudget;
        private String currency;
        private String location;
        private LocalDate startDate;
        private LocalDate endDate;
        private List<String> tags = new ArrayList<>();
        private List<String> imageUrls = new ArrayList<>();
        private boolean isPublic = true;
        private boolean emailNotifications = true;
        private boolean allowComments = true;
        private boolean allowAnonymous = false;

        // Getters and setters
        public String getQueryType() {
            return queryType;
        }

        public void setQueryType(String queryType) {
            this.queryType = queryType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getMinBudget() {
            return minBudget;
        }

        public void setMinBudget(String minBudget) {
            this.minBudget = minBudget;
        }

        public String getMaxBudget() {
            return maxBudget;
        }

        public void setMaxBudget(String maxBudget) {
            this.maxBudget = maxBudget;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }

        public boolean isEmailNotifications() {
            return emailNotifications;
        }

        public void setEmailNotifications(boolean emailNotifications) {
            this.emailNotifications = emailNotifications;
        }

        public boolean isAllowComments() {
            return allowComments;
        }

        public void setAllowComments(boolean allowComments) {
            this.allowComments = allowComments;
        }

        public boolean isAllowAnonymous() {
            return allowAnonymous;
        }

        public void setAllowAnonymous(boolean allowAnonymous) {
            this.allowAnonymous = allowAnonymous;
        }
    }
}
