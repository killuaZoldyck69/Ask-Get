/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * FXML Controller class for My Recommendations (Comments) View
 *
 * @author Tanver
 */
public class MyRecommendationsController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label totalCommentsLabel;
    @FXML
    private ScrollPane commentsScrollPane;
    @FXML
    private VBox commentsContainer;
    @FXML
    private VBox noCommentsMessage;
    @FXML
    private Button refreshBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        loadMyComments();
    }

    private void setupUI() {
        // Set title with user name
        String userName = SessionManager.getLoggedInName();
        if (userName != null) {
            titleLabel.setText("My Recommendations (" + userName + ")");
        } else {
            titleLabel.setText("My Recommendations");
        }

        // Setup scroll pane
        if (commentsScrollPane != null) {
            commentsScrollPane.setFitToWidth(true);
            commentsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            commentsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }

    /**
     * Load all comments made by the logged-in user
     */
    private void loadMyComments() {
        if (commentsContainer == null) return;

        // Check if user is logged in
        String userEmail = SessionManager.getLoggedInEmail();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            showNoCommentsMessage("Please login to view your recommendations.");
            return;
        }

        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> commentsCollection = database.getCollection("comments");
            MongoCollection<Document> queriesCollection = database.getCollection("queries");

            // Find all comments by this user
            Document filter = new Document("userEmail", userEmail);
            FindIterable<Document> comments = commentsCollection
                .find(filter)
                .sort(new Document("createdAt", -1)); // Sort by newest first

            commentsContainer.getChildren().clear();
            int commentCount = 0;

            for (Document comment : comments) {
                // Get the related query information
                ObjectId queryId = comment.getObjectId("queryId");
                Document query = null;
                if (queryId != null) {
                    query = queriesCollection.find(new Document("_id", queryId)).first();
                }

                createCommentCard(comment, query);
                commentCount++;
            }

            // Update total comments count
            if (totalCommentsLabel != null) {
                totalCommentsLabel.setText("Total: " + commentCount + " recommendations");
            }

            // Show/hide no comments message
            if (commentCount == 0) {
                showNoCommentsMessage("You haven't made any recommendations yet.");
            } else {
                if (noCommentsMessage != null) {
                    noCommentsMessage.setVisible(false);
                    noCommentsMessage.setManaged(false);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load your recommendations: " + e.getMessage());
        }
    }

    /**
     * Create a comment card UI with query information
     */
    private void createCommentCard(Document comment, Document query) {
        // Main comment card container
        VBox commentCard = new VBox();
        commentCard.setSpacing(12);
        commentCard.setStyle("-fx-background-color: white; -fx-border-color: #E5E7EB; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
        commentCard.setPadding(new Insets(20));
        VBox.setMargin(commentCard, new Insets(0, 0, 16, 0));

        // Query information header
        VBox queryInfoSection = new VBox();
        queryInfoSection.setSpacing(8);

        if (query != null) {
            // Query title and status
            HBox queryHeaderBox = new HBox();
            queryHeaderBox.setSpacing(12);
            queryHeaderBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            String queryTitle = query.getString("title");
            Label queryTitleLabel = new Label(queryTitle != null ? queryTitle : "Untitled Query");
            queryTitleLabel.setStyle("-fx-text-fill: #1F2937; -fx-font-weight: bold; -fx-font-size: 16px;");
            queryTitleLabel.setWrapText(true);

            // Status badge
            Boolean solved = query.getBoolean("solved");
            if (solved != null) {
                Label statusBadge = new Label(solved ? "✅ Solved" : "❓ Unsolved");
                String statusColor = solved ? "#10B981" : "#F59E0B";
                statusBadge.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 11px; -fx-padding: 4 10 4 10;");
                queryHeaderBox.getChildren().addAll(queryTitleLabel, statusBadge);
            } else {
                queryHeaderBox.getChildren().add(queryTitleLabel);
            }

            // Query metadata
            HBox queryMetaBox = new HBox();
            queryMetaBox.setSpacing(16);
            queryMetaBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            String category = query.getString("category");
            if (category != null && !category.trim().isEmpty()) {
                Label categoryLabel = new Label("📂 " + category);
                categoryLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
                queryMetaBox.getChildren().add(categoryLabel);
            }

            String priority = query.getString("priority");
            if (priority != null && !priority.trim().isEmpty()) {
                Label priorityLabel = new Label(getPriorityIcon(priority) + " " + priority.toUpperCase());
                priorityLabel.setStyle("-fx-text-fill: " + getPriorityColor(priority) + "; -fx-font-size: 12px; -fx-font-weight: bold;");
                queryMetaBox.getChildren().add(priorityLabel);
            }

            String queryUserName = query.getString("userName");
            if (queryUserName != null && !queryUserName.trim().isEmpty()) {
                Label authorLabel = new Label("👤 by " + queryUserName);
                authorLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
                queryMetaBox.getChildren().add(authorLabel);
            }

            queryInfoSection.getChildren().addAll(queryHeaderBox, queryMetaBox);
        } else {
            Label noQueryLabel = new Label("❌ Original query not found");
            noQueryLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 14px; -fx-font-weight: bold;");
            queryInfoSection.getChildren().add(noQueryLabel);
        }

        // Separator line
        Region separator = new Region();
        separator.setStyle("-fx-background-color: #E5E7EB;");
        separator.setPrefHeight(1);
        separator.setMaxHeight(1);

        // Comment section
        VBox commentSection = new VBox();
        commentSection.setSpacing(8);

        // Comment header
        HBox commentHeaderBox = new HBox();
        commentHeaderBox.setSpacing(12);
        commentHeaderBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label commentLabel = new Label("💬 Your Recommendation:");
        commentLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        String commentDate = comment.getString("createdAt");
        Label dateLabel = new Label(formatTimestamp(commentDate));
        dateLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");

        commentHeaderBox.getChildren().addAll(commentLabel, spacer, dateLabel);

        // Comment text
        String commentText = comment.getString("text");
        if (commentText == null) commentText = "No comment text";

        Label commentTextLabel = new Label(commentText);
        commentTextLabel.setWrapText(true);
        commentTextLabel.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px; -fx-line-spacing: 2px; -fx-background-color: #F9FAFB; -fx-background-radius: 8; -fx-padding: 12;");

        commentSection.getChildren().addAll(commentHeaderBox, commentTextLabel);

        // Actions section
        HBox actionsBox = new HBox();
        actionsBox.setSpacing(10);
        actionsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // View Query button
        if (query != null) {
            Button viewQueryBtn = new Button("👁️ View Query");
            viewQueryBtn.setStyle("-fx-background-color: #4F46E5; -fx-text-fill: white; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 6 12 6 12;");
            ObjectId queryId = comment.getObjectId("queryId");
            viewQueryBtn.setOnAction(e -> viewQueryDetails(queryId));
            actionsBox.getChildren().add(viewQueryBtn);
        }

        // Delete comment button
        Button deleteBtn = new Button("🗑️ Delete");
        deleteBtn.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-cursor: hand; -fx-background-radius: 6; -fx-border-radius: 6; -fx-padding: 6 12 6 12;");
        ObjectId commentId = comment.getObjectId("_id");
        deleteBtn.setOnAction(e -> deleteComment(commentId));
        actionsBox.getChildren().add(deleteBtn);

        // Assemble the complete card
        commentCard.getChildren().addAll(queryInfoSection, separator, commentSection, actionsBox);
        commentsContainer.getChildren().add(commentCard);
    }

    /**
     * View query details
     */
    private void viewQueryDetails(ObjectId queryId) {
        if (queryId == null) return;

        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> queriesCollection = database.getCollection("queries");
            
            Document query = queriesCollection.find(new Document("_id", queryId)).first();
            if (query != null) {
                // Load QueryDetails view
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/com/productrecommendation/fxml/QueryDetails.fxml"));
                Parent root = loader.load();

                // Pass the query document to the controller
                QueryDetailsController controller = loader.getController();
                controller.setQueryDocument(query);

                // Show the scene
                Scene scene = new Scene(root);
                Stage stage = (Stage) commentsContainer.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Query Details - " + query.getString("title"));
            } else {
                showError("Error", "Query not found. It may have been deleted.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load query details: " + e.getMessage());
        }
    }

    /**
     * Delete a comment
     */
    private void deleteComment(ObjectId commentId) {
        if (commentId == null) return;

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Recommendation");
        confirmAlert.setHeaderText("Are you sure you want to delete this recommendation?");
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
                    showInfo("Success", "Recommendation deleted successfully!");
                    loadMyComments(); // Reload the comments
                } else {
                    showError("Error", "Failed to delete recommendation. You can only delete your own recommendations.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                showError("Error", "Failed to delete recommendation: " + e.getMessage());
            }
        }
    }

    /**
     * Refresh button handler
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadMyComments();
    }

    /**
     * Show no comments message
     */
    private void showNoCommentsMessage(String message) {
        commentsContainer.getChildren().clear();
        if (totalCommentsLabel != null) {
            totalCommentsLabel.setText("Total: 0 recommendations");
        }
        
        if (noCommentsMessage != null) {
            // Update the message
            for (javafx.scene.Node node : noCommentsMessage.getChildren()) {
                if (node instanceof Label) {
                    ((Label) node).setText(message);
                    break;
                }
            }
            noCommentsMessage.setVisible(true);
            noCommentsMessage.setManaged(true);
        }
    }

    // Helper methods
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