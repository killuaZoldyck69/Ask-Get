package com.productrecommendation.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.productrecommendation.models.MongoDBConnection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.bson.Document;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

public class MyQueriesController implements Initializable {

    @FXML
    private VBox myQueryContainer;
    @FXML
    private Label totalQueriesLabel;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private Button refreshBtn;

    // Get logged-in user email from SessionManager
    private String getLoggedInEmail() {
        return SessionManager.getLoggedInEmail();
    }

    // Enhanced helper method to safely get string values from document
    private String getStringValueSafe(Document doc, String field) {
        try {
            Object value = doc.get(field);
            if (value == null) {
                return null;
            }
            
            // Handle different data types
            if (value instanceof String) {
                return (String) value;
            } else if (value instanceof Boolean) {
                return ((Boolean) value) ? "Solved" : "Unsolved";
            } else if (value instanceof Number) {
                return value.toString();
            } else {
                return value.toString();
            }
        } catch (Exception e) {
            System.err.println("Error getting field '" + field + "': " + e.getMessage());
            return null;
        }
    }
    
    // Create query card with better styling and error handling
    private VBox createQueryCard(Document doc) {
        try {
            String title = getStringValueSafe(doc, "title");
            String description = getStringValueSafe(doc, "description");
            String userName = getStringValueSafe(doc, "userName");
            String email = getStringValueSafe(doc, "userEmail");
            
            // Handle status field correctly (it's a Boolean in MongoDB)
            String status;
            Object solvedValue = doc.get("solved");
            if (solvedValue instanceof Boolean) {
                status = ((Boolean) solvedValue) ? "Solved" : "Unsolved";
            } else if (solvedValue instanceof String) {
                status = (String) solvedValue;
            } else {
                status = "Unsolved";
            }
            
            Date postedAt = null;
            try {
                postedAt = doc.getDate("submissionDate");
            } catch (Exception e) {
                // Try alternative date fields
                try {
                    postedAt = doc.getDate("createdDate");
                } catch (Exception e2) {
                    try {
                        postedAt = doc.getDate("date");
                    } catch (Exception e3) {
                        // No date found
                    }
                }
            }
            
            // Handle totalRecommendations safely
            Integer totalAnswers = 0;
            try {
                totalAnswers = doc.getInteger("totalRecommendations");
                if (totalAnswers == null) {
                    totalAnswers = 0;
                }
            } catch (Exception e) {
                // Try as string or other type
                Object answersObj = doc.get("totalRecommendations");
                if (answersObj != null) {
                    try {
                        totalAnswers = Integer.parseInt(answersObj.toString());
                    } catch (NumberFormatException nfe) {
                        totalAnswers = 0;
                    }
                }
            }

            String formattedDate = postedAt != null
                    ? new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(postedAt)
                    : "Unknown date";

            // Create main card container
            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; " +
                         "-fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 12; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
            card.setPadding(new Insets(20));

            // Title
            Label titleLabel = new Label("📌 " + (title != null ? title : "Untitled"));
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
            titleLabel.setWrapText(true);

            // Description
            Label descLabel = new Label(description != null ? description : "No description");
            descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #4b5563; -fx-wrap-text: true;");
            descLabel.setWrapText(true);
            descLabel.setMaxWidth(Double.MAX_VALUE);

            // User info section
            HBox userInfoBox = new HBox(20);
            Label nameLabel = new Label("👤 " + (userName != null ? userName : "Unknown"));
            nameLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
            
            Label emailLabel = new Label("📧 " + (email != null ? email : "No email"));
            emailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
            
            userInfoBox.getChildren().addAll(nameLabel, emailLabel);

            // Status and info section
            HBox statusInfoBox = new HBox(20);
            
            // Status with color coding
            Label statusLabel = new Label("Status: " + status);
            if ("Solved".equals(status)) {
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #059669; -fx-font-weight: bold;");
            } else {
                statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
            }
            
            Label answerCountLabel = new Label("💬 Answers: " + totalAnswers);
            answerCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");
            
            Label dateLabel = new Label("📅 Posted: " + formattedDate);
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6b7280;");

            statusInfoBox.getChildren().addAll(statusLabel, answerCountLabel, dateLabel);

            // Action buttons
            HBox buttonBox = new HBox(10);
            
            Button viewBtn = new Button("View Details");
            viewBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-background-radius: 6; " +
                           "-fx-border-radius: 6; -fx-font-size: 11px; -fx-padding: 5 10;");
            
            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 6; " +
                           "-fx-border-radius: 6; -fx-font-size: 11px; -fx-padding: 5 10;");
            
            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-background-radius: 6; " +
                             "-fx-border-radius: 6; -fx-font-size: 11px; -fx-padding: 5 10;");

            // Add button actions (you can implement these later)
            viewBtn.setOnAction(e -> System.out.println("View details for: " + title));
            editBtn.setOnAction(e -> System.out.println("Edit: " + title));
            deleteBtn.setOnAction(e -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Delete Confirmation");
                confirmAlert.setHeaderText("Delete Query");
                confirmAlert.setContentText("Are you sure you want to delete: " + title + "?");
                
                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    System.out.println("Delete: " + title);
                    // Implement delete functionality here
                }
            });

            buttonBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

            // Add all elements to card
            card.getChildren().addAll(titleLabel, descLabel, userInfoBox, statusInfoBox, buttonBox);

            return card;
            
        } catch (Exception e) {
            System.err.println("Error creating card: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple error card
            VBox errorCard = new VBox(5);
            errorCard.setStyle("-fx-background-color: #fef2f2; -fx-padding: 15; -fx-background-radius: 8; " +
                              "-fx-border-color: #fecaca; -fx-border-width: 1; -fx-border-radius: 8;");
            errorCard.setPadding(new Insets(15));
            
            Label errorLabel = new Label("⚠️ Error loading query data");
            errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc2626; -fx-font-weight: bold;");
            errorCard.getChildren().add(errorLabel);
            
            return errorCard;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserQueries();
    }

    private void loadUserQueries() {
        try {
            // Clear existing data
            myQueryContainer.getChildren().clear();

            // Get current logged-in user email
            String loggedInEmail = getLoggedInEmail();
            
            if (loggedInEmail == null || loggedInEmail.trim().isEmpty()) {
                System.out.println("No user logged in!");
                totalQueriesLabel.setText("Total Queries: 0 - No user logged in");
                
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No User Session");
                alert.setHeaderText("User not logged in");
                alert.setContentText("Please log in to view your queries.");
                alert.showAndWait();
                return;
            }

            MongoDBConnection.connect();
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> queriesCollection = database.getCollection("queries");

            // Debug: Check total documents in collection
            long totalDocs = queriesCollection.countDocuments();
            System.out.println("Total documents in queries collection: " + totalDocs);
            
            // Try different possible email field names
            FindIterable<Document> currentUserQueries = null;
            String emailField = null;
            
            // Check which email field exists and has matching data
            String[] possibleEmailFields = {"email", "userEmail", "user_email", "emailAddress"};
            
            for (String field : possibleEmailFields) {
                try {
                    long count = queriesCollection.countDocuments(new Document(field, loggedInEmail));
                    System.out.println("Documents with " + field + " = '" + loggedInEmail + "': " + count);
                    
                    if (count > 0) {
                        emailField = field;
                        currentUserQueries = queriesCollection.find(new Document(field, loggedInEmail));
                        break;
                    }
                } catch (Exception fieldError) {
                    System.out.println("Error checking field " + field + ": " + fieldError.getMessage());
                }
            }
            
            if (currentUserQueries == null) {
                System.out.println("No documents found for user: " + loggedInEmail);
                totalQueriesLabel.setText("Total Queries: 0 - No queries found for current user");
                
                // Show a message card
                VBox messageCard = new VBox(10);
                messageCard.setStyle("-fx-background-color: #f3f4f6; -fx-padding: 30; -fx-background-radius: 12; " +
                                   "-fx-border-color: #d1d5db; -fx-border-width: 1; -fx-border-radius: 12;");
                messageCard.setPadding(new Insets(30));
                
                Label messageLabel = new Label("📝 No queries found");
                messageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #6b7280;");
                
                Label subMessageLabel = new Label("You haven't submitted any queries yet. Start by asking a question!");
                subMessageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
                subMessageLabel.setWrapText(true);
                
                messageCard.getChildren().addAll(messageLabel, subMessageLabel);
                myQueryContainer.getChildren().add(messageCard);
                return;
            }

            System.out.println("Using email field: " + emailField);

            int queryCount = 0;
            for (Document doc : currentUserQueries) {
                try {
                    // Create card
                    VBox card = createQueryCard(doc);
                    myQueryContainer.getChildren().add(card);
                    queryCount++;
                    
                } catch (Exception docError) {
                    System.err.println("Error processing document " + (queryCount + 1) + ": " + docError.getMessage());
                    docError.printStackTrace();
                }
            }

            System.out.println("Total queries loaded: " + queryCount);
            totalQueriesLabel.setText("Total Queries: " + queryCount);

        } catch (Exception e) {
            System.err.println("Error loading queries: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to load queries");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
            
        } finally {
            MongoDBConnection.close();
        }
    }

    @FXML
    private void performSearch(ActionEvent event) {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadUserQueries();
            return;
        }

        // Clear container
        myQueryContainer.getChildren().clear();
        
        String loggedInEmail = getLoggedInEmail();
        if (loggedInEmail == null) {
            System.out.println("No user logged in for search");
            return;
        }
        
        try {
            MongoDBConnection.connect();
            MongoDatabase database = MongoDBConnection.getDatabase();
            MongoCollection<Document> queriesCollection = database.getCollection("queries");

            // Use the same email field detection logic as in loadUserQueries
            FindIterable<Document> currentUserQueries = null;
            String[] possibleEmailFields = {"email", "userEmail", "user_email", "emailAddress"};
            
            for (String field : possibleEmailFields) {
                long count = queriesCollection.countDocuments(new Document(field, loggedInEmail));
                if (count > 0) {
                    currentUserQueries = queriesCollection.find(new Document(field, loggedInEmail));
                    break;
                }
            }
            
            if (currentUserQueries == null) {
                totalQueriesLabel.setText("Matched Queries: 0 - No queries found");
                return;
            }

            int matchedCount = 0;

            for (Document doc : currentUserQueries) {
                String title = getStringValueSafe(doc, "title");
                String description = getStringValueSafe(doc, "description");
                
                // Check if title or description contains search term
                boolean matchesTitle = title != null && title.toLowerCase().contains(searchTerm);
                boolean matchesDescription = description != null && description.toLowerCase().contains(searchTerm);
                
                if (matchesTitle || matchesDescription) {
                    VBox card = createQueryCard(doc);
                    myQueryContainer.getChildren().add(card);
                    matchedCount++;
                }
            }

            totalQueriesLabel.setText("Matched Queries: " + matchedCount);
            
            if (matchedCount == 0) {
                // Show no results message
                VBox messageCard = new VBox(10);
                messageCard.setStyle("-fx-background-color: #fef3c7; -fx-padding: 20; -fx-background-radius: 8; " +
                                   "-fx-border-color: #fbbf24; -fx-border-width: 1; -fx-border-radius: 8;");
                messageCard.setPadding(new Insets(20));
                
                Label messageLabel = new Label("🔍 No matching queries found");
                messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #92400e;");
                
                Label subMessageLabel = new Label("Try different search terms or clear the search to see all queries.");
                subMessageLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #b45309;");
                
                messageCard.getChildren().addAll(messageLabel, subMessageLabel);
                myQueryContainer.getChildren().add(messageCard);
            }
            
        } catch (Exception e) {
            System.err.println("Error searching queries: " + e.getMessage());
            e.printStackTrace();
        } finally {
            MongoDBConnection.close();
        }
    }

    @FXML
    private void refreshQueries(ActionEvent event) {
        searchField.clear();
        loadUserQueries();
    }
}