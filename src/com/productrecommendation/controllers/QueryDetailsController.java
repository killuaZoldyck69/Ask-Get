package com.productrecommendation.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.productrecommendation.models.MongoDBConnection;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * FXML Controller class for Query Details with Comments
 *
 * @author Tanver
 */
public class QueryDetailsController implements Initializable {

    @FXML
    private Button backBtn;
    @FXML
    private Button editBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Label titleLabel;
    @FXML
    private Label statusBadge;
    @FXML
    private Label queryTypeBadge;
    @FXML
    private Label categoryBadge;
    @FXML
    private Label priorityBadge;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label userEmailLabel;
    @FXML
    private Label submissionDateLabel;
    @FXML
    private HBox budgetRow;
    @FXML
    private Label budgetLabel;
    @FXML
    private HBox locationRow;
    @FXML
    private Label locationLabel;
    @FXML
    private HBox timelineRow;
    @FXML
    private Label timelineLabel;
    @FXML
    private HBox recommendationsRow;
    @FXML
    private Label recommendationsLabel;
    @FXML
    private VBox tagsSection;
    @FXML
    private HBox tagsContainer;
    @FXML
    private VBox imagesSection;
    @FXML
    private VBox imagesContainer;
    
    // Comments Section - UPDATED FXML COMPONENTS
    @FXML
    private VBox commentsSection;
    @FXML
    private Label commentsCountLabel;
    @FXML
    private TextField commentAuthorField; // This will be removed from FXML
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button addCommentBtn;
    @FXML
    private VBox commentsContainer;
    @FXML
    private VBox noCommentsMessage;

    private Document queryDocument;
    private Stage previousStage;
    private ObjectId queryId;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Hide the comment author field since we'll use logged-in user info
        if (commentAuthorField != null) {
            commentAuthorField.setVisible(false);
            commentAuthorField.setManaged(false);
        }
        
        // Check if user is logged in
        if (SessionManager.getLoggedInName() == null || SessionManager.getLoggedInEmail() == null) {
            // Disable commenting if user is not logged in
            if (addCommentBtn != null) {
                addCommentBtn.setText("Login Required to Comment");
                addCommentBtn.setDisable(true);
                addCommentBtn.setStyle("-fx-background-color: #D1D5DB; -fx-text-fill: #6B7280; -fx-background-radius: 6; -fx-border-radius: 6;");
            }
            if (commentTextArea != null) {
                commentTextArea.setDisable(true);
                commentTextArea.setPromptText("Please login to add comments...");
            }
        }
    }

    /**
     * Set the query document to display
     */
    public void setQueryDocument(Document queryDocument) {
        this.queryDocument = queryDocument;
        if (queryDocument != null) {
            this.queryId = queryDocument.getObjectId("_id");
        }
        populateQueryDetails();
        loadComments();
    }

    /**
     * Set the previous stage for navigation
     */
    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    private void populateQueryDetails() {
        if (queryDocument == null) return;

        // Set title
        String title = queryDocument.getString("title");
        titleLabel.setText(title != null ? title : "Untitled Query");

        // Set status badge
        Boolean solved = queryDocument.getBoolean("solved");
        if (solved != null) {
            String statusText = solved ? "✅ Solved" : "❓ Unsolved";
            String statusColor = solved ? "#10B981" : "#F59E0B";
            statusBadge.setText(statusText);
            statusBadge.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-padding: 5 12 5 12;");
        } else {
            statusBadge.setVisible(false);
        }

        // Set query type badge
        String queryType = queryDocument.getString("queryType");
        if (queryType != null && !queryType.trim().isEmpty()) {
            queryTypeBadge.setText("📋 " + queryType);
        } else {
            queryTypeBadge.setVisible(false);
        }

        // Set category badge
        String category = queryDocument.getString("category");
        if (category != null && !category.trim().isEmpty()) {
            categoryBadge.setText("📂 " + category);
        } else {
            categoryBadge.setVisible(false);
        }

        // Set priority badge
        String priority = queryDocument.getString("priority");
        if (priority != null && !priority.trim().isEmpty()) {
            String priorityIcon = getPriorityIcon(priority);
            String priorityColor = getPriorityColor(priority);
            priorityBadge.setText(priorityIcon + " " + priority.toUpperCase());
            priorityBadge.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 12px; -fx-padding: 5 12 5 12;");
        } else {
            priorityBadge.setVisible(false);
        }

        // Set description
        String description = queryDocument.getString("description");
        descriptionLabel.setText(description != null && !description.trim().isEmpty() ? description : "No description provided.");

        // Set user information
        String userName = queryDocument.getString("userName");
        userNameLabel.setText(userName != null ? userName : "Unknown User");

        String userEmail = queryDocument.getString("userEmail");
        userEmailLabel.setText(userEmail != null ? userEmail : "No email provided");

        String submissionDate = queryDocument.getString("submissionDate");
        submissionDateLabel.setText(formatTimestamp(submissionDate));

        // Set budget information
        setupBudgetInfo();

        // Set location
        setupLocationInfo();

        // Set timeline
        setupTimelineInfo();

        // Set recommendations count
        setupRecommendationsInfo();

        // Set tags
        setupTagsInfo();

        // Set images (if any)
        setupImagesInfo();
    }

    // UPDATED METHOD: Load comments from MongoDB with user info
    private void loadComments() {
        if (queryId == null || commentsContainer == null) return;
        
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> commentsCollection = database.getCollection("comments");
            
            // Find all comments for this query
            Document filter = new Document("queryId", queryId);
            FindIterable<Document> comments = commentsCollection
                .find(filter)
                .sort(new Document("createdAt", -1)); // Sort by newest first
            
            commentsContainer.getChildren().clear();
            int commentCount = 0;
            
            for (Document comment : comments) {
                createCommentUI(comment);
                commentCount++;
            }
            
            // Update comments count
            if (commentsCountLabel != null) {
                commentsCountLabel.setText("(" + commentCount + ")");
            }
            
            // Show/hide no comments message
            if (noCommentsMessage != null) {
                if (commentCount == 0) {
                    noCommentsMessage.setVisible(true);
                    noCommentsMessage.setManaged(true);
                } else {
                    noCommentsMessage.setVisible(false);
                    noCommentsMessage.setManaged(false);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load comments: " + e.getMessage());
        }
    }
    
    // UPDATED METHOD: Create UI for individual comment with user info
    private void createCommentUI(Document comment) {
        // Main comment container
        VBox commentBox = new VBox();
        commentBox.setSpacing(10);
        commentBox.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        commentBox.setPadding(new Insets(15));
        
        // Comment header with user info
        HBox headerBox = new HBox();
        headerBox.setSpacing(12);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // User avatar/photo placeholder
        String photoUrl = comment.getString("userPhotoUrl");
        Label avatarLabel;
        if (photoUrl != null && !photoUrl.trim().isEmpty() && !photoUrl.equals("default_avatar.png")) {
            // If you have actual image loading capability, you can use ImageView here
            avatarLabel = new Label("🖼️");
            avatarLabel.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 50%; -fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-alignment: center; -fx-font-size: 16px;");
        } else {
            avatarLabel = new Label("👤");
            avatarLabel.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 50%; -fx-min-width: 32; -fx-min-height: 32; -fx-max-width: 32; -fx-max-height: 32; -fx-alignment: center; -fx-font-size: 16px;");
        }
        
        // User info container
        VBox userInfoBox = new VBox();
        userInfoBox.setSpacing(2);
        
        String userName = comment.getString("userName");
        if (userName == null || userName.trim().isEmpty()) {
            userName = "Unknown User";
        }
        
        Label userNameLabel = new Label(userName);
        userNameLabel.setStyle("-fx-text-fill: #1F2937; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        String userEmail = comment.getString("userEmail");
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            Label userEmailLabel = new Label(userEmail);
            userEmailLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
            userInfoBox.getChildren().addAll(userNameLabel, userEmailLabel);
        } else {
            userInfoBox.getChildren().add(userNameLabel);
        }
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Label dateLabel = new Label(formatTimestamp(comment.getString("createdAt")));
        dateLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        
        headerBox.getChildren().addAll(avatarLabel, userInfoBox, spacer, dateLabel);
        
        // Comment text
        String commentText = comment.getString("text");
        if (commentText == null) commentText = "";
        
        Label commentTextLabel = new Label(commentText);
        commentTextLabel.setWrapText(true);
        commentTextLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-line-spacing: 2px; -fx-padding: 8 0 0 44;"); // Left padding to align with user info
        
        // Actions box (only show for comment owner or admin)
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(5);
        actionsBox.setStyle("-fx-padding: 0 0 0 44;"); // Align with comment text
        
        String commentUserEmail = comment.getString("userEmail");
        String currentUserEmail = SessionManager.getLoggedInEmail();
        
        // Only show delete button if it's the user's own comment
        if (currentUserEmail != null && commentUserEmail != null && currentUserEmail.equals(commentUserEmail)) {
            Button deleteCommentBtn = new Button("🗑️ Delete");
            deleteCommentBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 4; -fx-border-radius: 4; -fx-padding: 4 8 4 8;");
            deleteCommentBtn.setOnAction(e -> deleteComment(comment.getObjectId("_id")));
            actionsBox.getChildren().add(deleteCommentBtn);
        }
        
        actionsBox.setVisible(false); // Hidden by default
        
        commentBox.getChildren().addAll(headerBox, commentTextLabel);
        
        // Only add actions box if it has children
        if (!actionsBox.getChildren().isEmpty()) {
            commentBox.getChildren().add(actionsBox);
            
            // Add hover effect to show actions
            commentBox.setOnMouseEntered(e -> actionsBox.setVisible(true));
            commentBox.setOnMouseExited(e -> actionsBox.setVisible(false));
        }
        
        commentsContainer.getChildren().add(commentBox);
    }
    
    // UPDATED METHOD: Handle adding new comment with logged-in user info
    @FXML
    private void handleAddComment(ActionEvent event) {
        // Check if user is logged in
        if (SessionManager.getLoggedInName() == null || SessionManager.getLoggedInEmail() == null) {
            showError("Authentication Required", "Please login to add comments.");
            return;
        }
        
        if (commentTextArea == null) return;
        
        String text = commentTextArea.getText().trim();
        
        // Validation
        if (text.isEmpty()) {
            showError("Validation Error", "Please enter a comment.");
            commentTextArea.requestFocus();
            return;
        }
        
        if (text.length() > 1000) {
            showError("Validation Error", "Comment is too long. Maximum 1000 characters allowed.");
            commentTextArea.requestFocus();
            return;
        }
        
        if (queryId == null) {
            showError("Error", "Query ID not found. Cannot add comment.");
            return;
        }
        
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> commentsCollection = database.getCollection("comments");
            
            // Get user info from session
            String userName = SessionManager.getLoggedInName();
            String userEmail = SessionManager.getLoggedInEmail();
            
            // Create comment document with user info
            Document commentDoc = new Document()
                .append("queryId", queryId)
                .append("userName", userName)
                .append("userEmail", userEmail)
                .append("userPhotoUrl", "default_avatar.png") // You can implement actual photo URL logic
                .append("text", text)
                .append("createdAt", LocalDateTime.now().toString())
                .append("ipAddress", "127.0.0.1") // You can implement IP tracking if needed
                .append("status", "active");
            
            // Insert comment
            commentsCollection.insertOne(commentDoc);
            
            // Clear form
            commentTextArea.clear();
            
            // Reload comments
            loadComments();
            
            showInfo("Success", "Comment added successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to add comment: " + e.getMessage());
        }
    }
    
    // UPDATED METHOD: Delete comment with ownership check
    private void deleteComment(ObjectId commentId) {
        if (commentId == null) return;
        
        // Check if user is logged in
        if (SessionManager.getLoggedInEmail() == null) {
            showError("Authentication Required", "Please login to delete comments.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Comment");
        confirmAlert.setHeaderText("Are you sure you want to delete this comment?");
        confirmAlert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                MongoDatabase database = MongoDBConnection.getDatabase();
                MongoCollection<Document> commentsCollection = database.getCollection("comments");
                
                // Only allow deletion if it's the user's own comment
                Document filter = new Document("_id", commentId)
                    .append("userEmail", SessionManager.getLoggedInEmail());
                
                long deletedCount = commentsCollection.deleteOne(filter).getDeletedCount();
                
                if (deletedCount > 0) {
                    loadComments(); // Reload comments
                    showInfo("Success", "Comment deleted successfully!");
                } else {
                    showError("Error", "Failed to delete comment. You can only delete your own comments.");
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Failed to delete comment: " + e.getMessage());
            }
        }
    }

    // EXISTING METHODS (unchanged)
    private void setupBudgetInfo() {
        String minBudget = queryDocument.getString("minBudget");
        String maxBudget = queryDocument.getString("maxBudget");
        String currency = queryDocument.getString("currency");

        if ((minBudget != null && !minBudget.trim().isEmpty()) || 
            (maxBudget != null && !maxBudget.trim().isEmpty())) {
            
            StringBuilder budgetText = new StringBuilder();
            if (minBudget != null && !minBudget.trim().isEmpty()) {
                budgetText.append(minBudget);
            }
            if (maxBudget != null && !maxBudget.trim().isEmpty()) {
                if (minBudget != null && !minBudget.trim().isEmpty()) {
                    budgetText.append(" - ");
                }
                budgetText.append(maxBudget);
            }
            if (currency != null && !currency.trim().isEmpty()) {
                budgetText.append(" ").append(currency);
            }
            
            budgetLabel.setText(budgetText.toString());
        } else {
            budgetRow.setVisible(false);
            budgetRow.setManaged(false);
        }
    }

    private void setupLocationInfo() {
        String location = queryDocument.getString("location");
        if (location != null && !location.trim().isEmpty()) {
            locationLabel.setText(location);
        } else {
            locationRow.setVisible(false);
            locationRow.setManaged(false);
        }
    }

    private void setupTimelineInfo() {
        String startDate = queryDocument.getString("startDate");
        String endDate = queryDocument.getString("endDate");
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            String timelineText = startDate;
            if (endDate != null && !endDate.trim().isEmpty()) {
                timelineText += " to " + endDate;
            }
            timelineLabel.setText(timelineText);
        } else {
            timelineRow.setVisible(false);
            timelineRow.setManaged(false);
        }
    }

    private void setupRecommendationsInfo() {
        Object totalRecommendations = queryDocument.get("totalRecommendations");
        int recCount = 0;
        
        if (totalRecommendations != null) {
            if (totalRecommendations instanceof Integer) {
                recCount = (Integer) totalRecommendations;
            } else if (totalRecommendations instanceof String) {
                try {
                    recCount = Integer.parseInt((String) totalRecommendations);
                } catch (NumberFormatException e) {
                    recCount = 0;
                }
            }
        }
        
        recommendationsLabel.setText(recCount + " recommendations received");
        String recColor = recCount > 0 ? "#059669" : "#6B7280";
        recommendationsLabel.setStyle("-fx-text-fill: " + recColor + "; -fx-font-size: 14px;");
    }

    @SuppressWarnings("unchecked")
    private void setupTagsInfo() {
        try {
            List<String> tags = (List<String>) queryDocument.get("tags");
            if (tags != null && !tags.isEmpty()) {
                tagsContainer.getChildren().clear();
                for (String tag : tags) {
                    if (tag != null && !tag.trim().isEmpty()) {
                        Label tagLabel = new Label("#" + tag.trim());
                        tagLabel.setStyle("-fx-background-color: #F3F4F6; -fx-text-fill: #374151; -fx-background-radius: 12; -fx-font-size: 12px;");
                        tagLabel.setPadding(new Insets(4, 10, 4, 10));
                        tagsContainer.getChildren().add(tagLabel);
                    }
                }
            } else {
                tagsSection.setVisible(false);
                tagsSection.setManaged(false);
            }
        } catch (Exception e) {
            tagsSection.setVisible(false);
            tagsSection.setManaged(false);
        }
    }

    @SuppressWarnings("unchecked")
    private void setupImagesInfo() {
        try {
            List<String> imageUrls = (List<String>) queryDocument.get("imageUrls");
            if (imageUrls != null && !imageUrls.isEmpty()) {
                imagesContainer.getChildren().clear();
                for (String imageUrl : imageUrls) {
                    if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                        Label imageLabel = new Label("🖼️ " + imageUrl);
                        imageLabel.setStyle("-fx-text-fill: #4F46E5; -fx-font-size: 12px;");
                        imageLabel.setWrapText(true);
                        imagesContainer.getChildren().add(imageLabel);
                    }
                }
            } else {
                imagesSection.setVisible(false);
                imagesSection.setManaged(false);
            }
        } catch (Exception e) {
            imagesSection.setVisible(false);
            imagesSection.setManaged(false);
        }
    }

    private String getPriorityIcon(String priority) {
        if (priority == null) return "⚡";
        switch (priority.toLowerCase()) {
            case "low": return "🟢";
            case "medium": return "🟡";
            case "high": return "🟠";
            case "urgent": return "🔴";
            default: return "⚡";
        }
    }

    private String getPriorityColor(String priority) {
        if (priority == null) return "#6B7280";
        switch (priority.toLowerCase()) {
            case "low": return "#10B981";
            case "medium": return "#F59E0B";
            case "high": return "#F97316";
            case "urgent": return "#EF4444";
            default: return "#6B7280";
        }
    }

    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return "Unknown date";
        }
        
        try {
            // Try different date formats
            DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
            };
            
            for (DateTimeFormatter formatter : formatters) {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);
                    return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm"));
                } catch (DateTimeParseException e) {
                    // Try next formatter
                }
            }
            
            // If no formatter works, return as is
            return timestamp;
        } catch (Exception e) {
            return timestamp;
        }
    }

    // FXML Event Handlers (unchanged)
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            // Load the AllQueries view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) backBtn.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("All Queries - Product Recommendation System");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load All Queries view: " + e.getMessage());
        }
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        // TODO: Implement edit functionality
        showInfo("Edit Query", "Edit functionality will be implemented in future updates.");
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Query");
        confirmAlert.setHeaderText("Are you sure you want to delete this query?");
        confirmAlert.setContentText("This action cannot be undone. The query and all associated data will be permanently deleted.");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteQuery();
        }
    }

    private void deleteQuery() {
        if (queryDocument == null) return;
        
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> collection = database.getCollection("queries");
            
            ObjectId queryId = queryDocument.getObjectId("_id");
            Document filter = new Document("_id", queryId);
            
            long deletedCount = collection.deleteOne(filter).getDeletedCount();
            
            if (deletedCount > 0) {
                // Also delete associated comments
                MongoCollection<Document> commentsCollection = database.getCollection("comments");
                Document commentFilter = new Document("queryId", queryId);
                commentsCollection.deleteMany(commentFilter);
                
                showInfo("Success", "Query and associated comments deleted successfully!");
                // Navigate back to AllQueries view
                handleBack(null);
            } else {
                showError("Error", "Failed to delete query. Query not found.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Database Error", "Error deleting query: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}