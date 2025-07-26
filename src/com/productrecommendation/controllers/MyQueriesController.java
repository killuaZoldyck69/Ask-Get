package com.productrecommendation.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.productrecommendation.models.MongoDBConnection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

// Query data model class
class QueryData {
    private String title;
    private String description;
    private String status;
    private String createdDate;
    private Document originalDocument; // Keep reference to original document
    
    public QueryData(String title, String description, String status, String createdDate, Document originalDocument) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.originalDocument = originalDocument;
        
        // Debug: Print when QueryData is created
        System.out.println("DEBUG: Creating QueryData - Title: '" + title + "', Description: '" + 
                          (description != null && description.length() > 20 ? description.substring(0, 20) + "..." : description) + 
                          "', Status: '" + status + "', Date: '" + createdDate + "'");
    }
    
    // Getters for TableView
    public String getTitle() { 
        System.out.println("DEBUG: getTitle() called, returning: " + title);
        return title; 
    }
    public String getDescription() { 
        System.out.println("DEBUG: getDescription() called, returning: " + (description != null && description.length() > 20 ? description.substring(0, 20) + "..." : description));
        return description; 
    }
    public String getStatus() { 
        System.out.println("DEBUG: getStatus() called, returning: " + status);
        return status; 
    }
    public String getCreatedDate() { 
        System.out.println("DEBUG: getCreatedDate() called, returning: " + createdDate);
        return createdDate; 
    }
    public Document getOriginalDocument() { return originalDocument; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}

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
    @FXML
    private TableView<QueryData> queriesTable;
    @FXML
    private TableColumn<QueryData, String> titleColumn;
    @FXML
    private TableColumn<QueryData, String> descriptionColumn;
    @FXML
    private TableColumn<QueryData, String> statusColumn;
    @FXML
    private TableColumn<QueryData, String> dateColumn;

    private ObservableList<QueryData> queryDataList = FXCollections.observableArrayList();
    
    // Get logged-in user email from SessionManager
    private String getLoggedInEmail() {
        return SessionManager.getLoggedInEmail();
    }

    // Simple helper method for backward compatibility
    private String getStringValue(Document doc, String field) {
        return getStringValueSafe(doc, field);
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
            System.err.println("DEBUG: Error getting field '" + field + "': " + e.getMessage());
            return null;
        }
    }
    
    // Safe method to create query cards
    private VBox createQueryCardSafe(Document doc) {
        try {
            String title = getStringValueSafe(doc, "title");
            String description = getStringValueSafe(doc, "description");
            String userName = getStringValueSafe(doc, "userName");
            String email = getStringValueSafe(doc, "userEmail");
            String status = getStringValueSafe(doc, "solved");
            Date postedAt = null;
            
            try {
                postedAt = doc.getDate("submissionDate");
            } catch (Exception e) {
                // Try alternative date fields
                try {
                    postedAt = doc.getDate("createdDate");
                } catch (Exception e2) {
                    // Ignore if no date found
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

            VBox card = new VBox(5);
            card.setStyle("-fx-background-color: #F2F2F2; -fx-padding: 10; -fx-background-radius: 10;");
            card.setPadding(new Insets(10));

            Label titleLabel = new Label("📌 " + (title != null ? title : "Untitled"));
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label descLabel = new Label(description != null ? description : "");
            Label nameLabel = new Label("👤 " + (userName != null ? userName : ""));
            Label emailLabel = new Label("📧 " + (email != null ? email : ""));
            Label statusLabel = new Label("Status: " + (status != null ? status : "Unsolved"));
            Label answerCountLabel = new Label("Answers: " + totalAnswers);
            Label dateLabel = new Label("Posted: " + formattedDate);

            card.getChildren().addAll(titleLabel, descLabel, nameLabel, emailLabel, dateLabel, answerCountLabel, statusLabel);

            return card;
            
        } catch (Exception e) {
            System.err.println("DEBUG: Error creating card: " + e.getMessage());
            e.printStackTrace();
            
            // Return a simple error card
            VBox errorCard = new VBox(5);
            errorCard.setStyle("-fx-background-color: #FFE6E6; -fx-padding: 10; -fx-background-radius: 10;");
            errorCard.setPadding(new Insets(10));
            
            Label errorLabel = new Label("⚠️ Error loading query data");
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            errorCard.getChildren().add(errorLabel);
            
            return errorCard;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadUserQueries();
    }
    
    private void setupTableColumns() {
        // Set up cell value factories for each column
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        
        // Set the data to the table
        queriesTable.setItems(queryDataList);
        
        // Optional: Set column widths
        titleColumn.setPrefWidth(200);
        descriptionColumn.setPrefWidth(300);
        statusColumn.setPrefWidth(100);
        dateColumn.setPrefWidth(150);
        
        // Debug: Add listener to check when items are added
        queryDataList.addListener((javafx.collections.ListChangeListener<QueryData>) change -> {
            System.out.println("DEBUG: QueryDataList changed. New size: " + queryDataList.size());
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("DEBUG: Added " + change.getAddedSize() + " items to queryDataList");
                }
            }
        });
    }

    private void loadUserQueries() {
        try {
            // Clear existing data
            myQueryContainer.getChildren().clear();
            queryDataList.clear();

            // Get current logged-in user email
            String loggedInEmail = getLoggedInEmail();
            
            // Debug: Print logged in email
            System.out.println("DEBUG: Logged in email: " + loggedInEmail);
            
            if (loggedInEmail == null || loggedInEmail.trim().isEmpty()) {
                System.out.println("DEBUG: No user logged in!");
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
            System.out.println("DEBUG: Total documents in queries collection: " + totalDocs);
            
            // First, let's see what field names exist in the first document
            Document firstDoc = queriesCollection.find().first();
            if (firstDoc != null) {
                System.out.println("DEBUG: Sample document structure: " + firstDoc.toJson());
                System.out.println("DEBUG: Available fields in first document: " + firstDoc.keySet());
                
                // Debug field types to identify the problematic Boolean field
                for (String key : firstDoc.keySet()) {
                    Object value = firstDoc.get(key);
                    String type = value != null ? value.getClass().getSimpleName() : "null";
                    System.out.println("DEBUG: Field '" + key + "' is of type: " + type + ", value: " + value);
                }
            } else {
                System.out.println("DEBUG: No documents found in collection!");
                totalQueriesLabel.setText("Total Queries: 0 - Collection is empty");
                return;
            }
            
            // Try different possible email field names
            FindIterable<Document> currentUserQueries = null;
            String emailField = null;
            
            // Check which email field exists and has matching data
            String[] possibleEmailFields = {"email", "userEmail", "user_email", "emailAddress"};
            
            for (String field : possibleEmailFields) {
                try {
                    long count = queriesCollection.countDocuments(new Document(field, loggedInEmail));
                    System.out.println("DEBUG: Documents with " + field + " = '" + loggedInEmail + "': " + count);
                    
                    if (count > 0) {
                        emailField = field;
                        currentUserQueries = queriesCollection.find(new Document(field, loggedInEmail));
                        break;
                    }
                } catch (Exception fieldError) {
                    System.out.println("DEBUG: Error checking field " + field + ": " + fieldError.getMessage());
                }
            }
            
            if (currentUserQueries == null) {
                System.out.println("DEBUG: No documents found for user: " + loggedInEmail);
                System.out.println("DEBUG: Trying to load all documents to check email formats...");
                
                // Show all unique email values to help debug
                try {
                    FindIterable<Document> allDocs = queriesCollection.find().limit(5); // Limit to first 5 docs
                    int docCount = 0;
                    for (Document doc : allDocs) {
                        docCount++;
                        System.out.println("DEBUG: Document " + docCount + " fields:");
                        for (String field : possibleEmailFields) {
                            try {
                                Object emailValue = doc.get(field);
                                if (emailValue != null) {
                                    System.out.println("DEBUG: Found " + field + ": '" + emailValue + "' (type: " + emailValue.getClass().getSimpleName() + ")");
                                }
                            } catch (Exception e) {
                                System.out.println("DEBUG: Error reading field " + field + ": " + e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG: Error reading documents: " + e.getMessage());
                }
                
                totalQueriesLabel.setText("Total Queries: 0 - No queries found for current user");
                return;
            }

            System.out.println("DEBUG: Using email field: " + emailField);

            int queryCount = 0;
            for (Document doc : currentUserQueries) {
                try {
                    System.out.println("DEBUG: Processing document " + (queryCount + 1));
                    
                    // Create card for VBox container with better error handling
                    VBox card = createQueryCardSafe(doc);
                    HBox cardWrapper = new HBox(card);
                    cardWrapper.setPadding(new Insets(10));
                    myQueryContainer.getChildren().add(cardWrapper);
                    
                    // Create data for TableView - Handle different data types safely
                    String title = getStringValueSafe(doc, "title");
                    String description = getStringValueSafe(doc, "description");
                    String status = getStringValueSafe(doc, "solved");
                    Date postedAt = null;
                    
                    try {
                        postedAt = doc.getDate("submissionDate");
                    } catch (Exception dateError) {
                        System.out.println("DEBUG: Error getting date: " + dateError.getMessage());
                        // Try alternative date field names
                        try {
                            postedAt = doc.getDate("createdDate");
                        } catch (Exception e2) {
                            try {
                                postedAt = doc.getDate("date");
                            } catch (Exception e3) {
                                System.out.println("DEBUG: No valid date field found");
                            }
                        }
                    }
                    
                    // Debug output
                    System.out.println("DEBUG: Title: " + title);
                    System.out.println("DEBUG: Description: " + (description != null ? description.substring(0, Math.min(50, description.length())) : "null"));
                    System.out.println("DEBUG: Status: " + status);
                    System.out.println("DEBUG: Posted at: " + postedAt);
                    
                    String formattedDate = postedAt != null
                            ? new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(postedAt)
                            : "Unknown date";
                    
                    // Truncate description for table display
                    String truncatedDescription = description != null && description.length() > 50 
                            ? description.substring(0, 50) + "..." 
                            : (description != null ? description : "");
                    
                    QueryData queryData = new QueryData(
                        title != null ? title : "Untitled",
                        truncatedDescription,
                        status != null ? status : "Unsolved",
                        formattedDate,
                        doc
                    );
                    
                    queryDataList.add(queryData);
                    queryCount++;
                    
                    // Debug: Check if item was added to list
                    System.out.println("DEBUG: Added QueryData to list. Current list size: " + queryDataList.size());
                    System.out.println("DEBUG: QueryData - Title: " + queryData.getTitle() + ", Status: " + queryData.getStatus());
                    
                } catch (Exception docError) {
                    System.err.println("DEBUG: Error processing document " + (queryCount + 1) + ": " + docError.getMessage());
                    docError.printStackTrace();
                    // Continue with next document instead of failing completely
                }
            }

            System.out.println("DEBUG: Total queries loaded: " + queryCount);
            System.out.println("DEBUG: QueryDataList size: " + queryDataList.size());
            System.out.println("DEBUG: TableView items size: " + queriesTable.getItems().size());
            
            // Force refresh the TableView
            queriesTable.refresh();
            
            // Double-check the binding
            if (queriesTable.getItems() != queryDataList) {
                System.out.println("DEBUG: TableView items not properly bound! Re-binding...");
                queriesTable.setItems(queryDataList);
            }
            
            totalQueriesLabel.setText("Total Queries: " + queryCount);

        } catch (Exception e) {
            System.err.println("Error loading queries: " + e.getMessage());
            e.printStackTrace();
            
            // Show error dialog
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to load queries");
            alert.setContentText("Error: " + e.getMessage() + "\n\nCheck console for detailed debug information.");
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

        // Clear containers
        myQueryContainer.getChildren().clear();
        queryDataList.clear();
        
        String loggedInEmail = getLoggedInEmail();
        if (loggedInEmail == null) {
            System.out.println("DEBUG: No user logged in for search");
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
                String title = getStringValue(doc, "title");
                String description = getStringValue(doc, "description");
                
                // Check if title or description contains search term
                boolean matchesTitle = title != null && title.toLowerCase().contains(searchTerm);
                boolean matchesDescription = description != null && description.toLowerCase().contains(searchTerm);
                
                if (matchesTitle || matchesDescription) {
                    // Add to card view
                    VBox card = createQueryCardSafe(doc);
                    HBox cardWrapper = new HBox(card);
                    cardWrapper.setPadding(new Insets(10));
                    myQueryContainer.getChildren().add(cardWrapper);
                    
                    // Add to table view
                    String status = getStringValueSafe(doc, "solved");
                    Date postedAt = null;
                    try {
                        postedAt = doc.getDate("submissionDate");
                    } catch (Exception e) {
                        // Try alternative date fields
                        try {
                            postedAt = doc.getDate("createdDate");
                        } catch (Exception e2) {
                            // Ignore if no date found
                        }
                    }
                    
                    String formattedDate = postedAt != null
                            ? new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(postedAt)
                            : "Unknown date";
                    
                    String truncatedDescription = description != null && description.length() > 50 
                            ? description.substring(0, 50) + "..." 
                            : (description != null ? description : "");
                    
                    QueryData queryData = new QueryData(
                        title != null ? title : "Untitled",
                        truncatedDescription,
                        status != null ? status : "Unsolved",
                        formattedDate,
                        doc
                    );
                    
                    queryDataList.add(queryData);
                    matchedCount++;
                }
            }

            totalQueriesLabel.setText("Matched Queries: " + matchedCount);
            
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



    @FXML
    private void viewQueryDetails(ActionEvent event) {
        QueryData selectedQuery = queriesTable.getSelectionModel().getSelectedItem();
        if (selectedQuery != null) {
            // Implement view details functionality
            System.out.println("Viewing details for: " + selectedQuery.getTitle());
            // You can access the full document with: selectedQuery.getOriginalDocument()
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No query selected");
            alert.setContentText("Please select a query from the table to view details.");
            alert.showAndWait();
        }
    }

    @FXML
    private void editQuery(ActionEvent event) {
        QueryData selectedQuery = queriesTable.getSelectionModel().getSelectedItem();
        if (selectedQuery != null) {
            // Implement edit functionality
            System.out.println("Editing: " + selectedQuery.getTitle());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No query selected");
            alert.setContentText("Please select a query from the table to edit.");
            alert.showAndWait();
        }
    }

    @FXML
    private void deleteQuery(ActionEvent event) {
        QueryData selectedQuery = queriesTable.getSelectionModel().getSelectedItem();
        if (selectedQuery != null) {
            // Show confirmation dialog
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Delete Confirmation");
            confirmAlert.setHeaderText("Delete Query");
            confirmAlert.setContentText("Are you sure you want to delete this query: " + selectedQuery.getTitle() + "?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                // Implement delete functionality
                System.out.println("Deleting: " + selectedQuery.getTitle());
                // You can access the full document with: selectedQuery.getOriginalDocument()
                // Then delete from MongoDB and refresh the view
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No query selected");
            alert.setContentText("Please select a query from the table to delete.");
            alert.showAndWait();
        }
    }
}