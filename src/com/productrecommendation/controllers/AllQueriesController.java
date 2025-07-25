/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.productrecommendation.controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.productrecommendation.models.MongoDBConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.bson.Document;

/**
 * FXML Controller class
 *
 * @author Tanver
 */
public class AllQueriesController implements Initializable {

    @FXML
    private Button refreshBtn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private Label totalQueriesLabel;
    @FXML
    private StackPane loadingPane;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private ScrollPane queriesScrollPane;
    @FXML
    private VBox queriesContainer;
    @FXML
    private VBox emptyStatePane;

    private List<Document> allQueries = new ArrayList<>();
    private List<Document> filteredQueries = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupComboBoxes();
        loadQueries();
    }

    private void setupComboBoxes() {
        // Setup sort combo box
        sortComboBox.getItems().addAll(
            "Newest First",
            "Oldest First",
            "Most Popular",
            "Category A-Z"
        );
        sortComboBox.setValue("Newest First");
        
        // Setup category filter
        categoryFilter.getItems().add("All Categories");
        categoryFilter.setValue("All Categories");
    }

    private void loadQueries() {
        showLoading(true);
        
        Task<List<Document>> task = new Task<List<Document>>() {
            @Override
            protected List<Document> call() throws Exception {
                List<Document> queries = new ArrayList<>();
                
                try {
                    MongoDatabase database = MongoDBConnection.getDatabase();
                    MongoCollection<Document> collection = database.getCollection("queries");
                    
                    FindIterable<Document> iterable = collection.find();
                    for (Document doc : iterable) {
                        queries.add(doc);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showError("Error loading queries: " + e.getMessage()));
                }
                
                return queries;
            }
        };
        
        task.setOnSucceeded(e -> {
            allQueries = task.getValue();
            filteredQueries = new ArrayList<>(allQueries);
            updateCategoryFilter();
            sortQueries();
            displayQueries();
            updateTotalLabel();
            showLoading(false);
        });
        
        task.setOnFailed(e -> {
            showLoading(false);
            showError("Failed to load queries from database");
        });
        
        new Thread(task).start();
    }

    private void updateCategoryFilter() {
        Set<String> categories = new HashSet<>();
        for (Document query : allQueries) {
            String category = query.getString("category");
            if (category != null && !category.trim().isEmpty()) {
                categories.add(category);
            }
        }
        
        String currentSelection = categoryFilter.getValue();
        categoryFilter.getItems().clear();
        categoryFilter.getItems().add("All Categories");
        categoryFilter.getItems().addAll(categories.stream().sorted().toArray(String[]::new));
        categoryFilter.setValue(currentSelection != null ? currentSelection : "All Categories");
    }

    private void displayQueries() {
        queriesContainer.getChildren().clear();
        
        if (filteredQueries.isEmpty()) {
            showEmptyState(true);
            return;
        }
        
        showEmptyState(false);
        
        for (Document query : filteredQueries) {
            VBox queryCard = createQueryCard(query);
            queriesContainer.getChildren().add(queryCard);
        }
    }

    private VBox createQueryCard(Document query) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #E5E7EB; -fx-border-width: 1;");
        card.setPadding(new Insets(20));
        
        // Header with title and query type
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(query.getString("title"));
        titleLabel.setStyle("-fx-text-fill: #1F2937; -fx-font-weight: bold; -fx-font-size: 16px;");
        titleLabel.setWrapText(true);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Query Type Badge
        String queryType = query.getString("queryType");
        if (queryType != null && !queryType.trim().isEmpty()) {
            Label typeLabel = new Label(queryType);
            typeLabel.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; -fx-background-radius: 12; -fx-font-size: 10px; -fx-padding: 3 8 3 8;");
            header.getChildren().add(typeLabel);
        }
        
        header.getChildren().addAll(titleLabel, spacer);
        
        // Query description
        String description = query.getString("description");
        if (description != null && !description.trim().isEmpty()) {
            Label descLabel = new Label(description.length() > 150 ? description.substring(0, 150) + "..." : description);
            descLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 14px;");
            descLabel.setWrapText(true);
            card.getChildren().add(descLabel);
        }
        
        // Tags and metadata row 1
        HBox metaInfo1 = new HBox(15);
        metaInfo1.setAlignment(Pos.CENTER_LEFT);
        
        // Category tag
        String category = query.getString("category");
        if (category != null && !category.trim().isEmpty()) {
            Label categoryTag = new Label("📂 " + category);
            categoryTag.setStyle("-fx-background-color: #EEF2FF; -fx-text-fill: #4F46E5; -fx-background-radius: 12; -fx-font-size: 12px;");
            categoryTag.setPadding(new Insets(4, 8, 4, 8));
            metaInfo1.getChildren().add(categoryTag);
        }
        
        // Priority
        String priority = query.getString("priority");
        if (priority != null && !priority.trim().isEmpty()) {
            String priorityIcon = getPriorityIcon(priority);
            String priorityColor = getPriorityColor(priority);
            Label priorityLabel = new Label(priorityIcon + " " + priority.toUpperCase());
            priorityLabel.setStyle("-fx-background-color: " + priorityColor + "; -fx-text-fill: white; -fx-background-radius: 12; -fx-font-size: 10px;");
            priorityLabel.setPadding(new Insets(3, 8, 3, 8));
            metaInfo1.getChildren().add(priorityLabel);
        }
        
        // Public/Private indicator
        Boolean isPublic = query.getBoolean("isPublic");
        if (isPublic != null) {
            String visibility = isPublic ? "🌐 Public" : "🔒 Private";
            Label visibilityLabel = new Label(visibility);
            visibilityLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 11px;");
            metaInfo1.getChildren().add(visibilityLabel);
        }
        
        // Budget info row 2 (if available)
        HBox metaInfo2 = new HBox(15);
        metaInfo2.setAlignment(Pos.CENTER_LEFT);
        
        String minBudget = query.getString("minBudget");
        String maxBudget = query.getString("maxBudget");
        String currency = query.getString("currency");
        
        if ((minBudget != null && !minBudget.trim().isEmpty()) || 
            (maxBudget != null && !maxBudget.trim().isEmpty())) {
            StringBuilder budgetText = new StringBuilder("💰 Budget: ");
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
            
            Label budgetLabel = new Label(budgetText.toString());
            budgetLabel.setStyle("-fx-text-fill: #059669; -fx-font-size: 12px;");
            metaInfo2.getChildren().add(budgetLabel);
        }
        
        // Location
        String location = query.getString("location");
        if (location != null && !location.trim().isEmpty()) {
            Label locationLabel = new Label("📍 " + location);
            locationLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
            metaInfo2.getChildren().add(locationLabel);
        }
        
        // Timeline
        String startDate = query.getString("startDate");
        String endDate = query.getString("endDate");
        if (startDate != null && !startDate.trim().isEmpty()) {
            String timelineText = "📅 " + startDate;
            if (endDate != null && !endDate.trim().isEmpty()) {
                timelineText += " to " + endDate;
            }
            Label timelineLabel = new Label(timelineText);
            timelineLabel.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
            metaInfo2.getChildren().add(timelineLabel);
        }
        
        card.getChildren().addAll(header, metaInfo1);
        if (!metaInfo2.getChildren().isEmpty()) {
            card.getChildren().add(metaInfo2);
        }
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #CBD5E1; -fx-border-width: 1; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #E5E7EB; -fx-border-width: 1;"));
        
        return card;
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
            return "Unknown";
        }
        
        try {
            // If timestamp is in ISO format, parse it
            if (timestamp.contains("T")) {
                LocalDateTime dateTime = LocalDateTime.parse(timestamp.replace("Z", ""));
                return dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
            } else {
                return timestamp; // Return as is if not in expected format
            }
        } catch (Exception e) {
            return timestamp; // Return original if parsing fails
        }
    }

    private void showLoading(boolean show) {
        loadingPane.setVisible(show);
        queriesScrollPane.setVisible(!show);
    }

    private void showEmptyState(boolean show) {
        emptyStatePane.setVisible(show);
        queriesScrollPane.setVisible(!show);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Database Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTotalLabel() {
        totalQueriesLabel.setText("Total: " + filteredQueries.size() + " queries");
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();
        
        filteredQueries = allQueries.stream()
            .filter(query -> {
                // Search filter
                if (!searchText.isEmpty()) {
                    String title = query.getString("title");
                    String description = query.getString("description");
                    String category = query.getString("category");
                    String queryType = query.getString("queryType");
                    String location = query.getString("location");
                    
                    boolean matches = (title != null && title.toLowerCase().contains(searchText)) ||
                                    (description != null && description.toLowerCase().contains(searchText)) ||
                                    (category != null && category.toLowerCase().contains(searchText)) ||
                                    (queryType != null && queryType.toLowerCase().contains(searchText)) ||
                                    (location != null && location.toLowerCase().contains(searchText));
                    if (!matches) return false;
                }
                
                // Category filter
                if (!"All Categories".equals(selectedCategory)) {
                    String queryCategory = query.getString("category");
                    if (!selectedCategory.equals(queryCategory)) return false;
                }
                
                return true;
            })
            .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
        
        sortQueries();
        displayQueries();
        updateTotalLabel();
    }

    private void sortQueries() {
        String sortOption = sortComboBox.getValue();
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Newest First":
                filteredQueries.sort((a, b) -> {
                    // Since your AddQuery doesn't add timestamp, we'll use MongoDB's _id for ordering
                    String idA = a.getObjectId("_id").toString();
                    String idB = b.getObjectId("_id").toString();
                    return idB.compareTo(idA); // Newer ObjectIds are greater
                });
                break;
            case "Oldest First":
                filteredQueries.sort((a, b) -> {
                    String idA = a.getObjectId("_id").toString();
                    String idB = b.getObjectId("_id").toString();
                    return idA.compareTo(idB);
                });
                break;
            case "Category A-Z":
                filteredQueries.sort((a, b) -> {
                    String catA = a.getString("category");
                    String catB = b.getString("category");
                    if (catA == null && catB == null) return 0;
                    if (catA == null) return 1;
                    if (catB == null) return -1;
                    return catA.compareToIgnoreCase(catB);
                });
                break;
            case "Most Popular":
                // For now, sort by priority (urgent first)
                filteredQueries.sort((a, b) -> {
                    String priorityA = a.getString("priority");
                    String priorityB = b.getString("priority");
                    return getPriorityOrder(priorityB) - getPriorityOrder(priorityA);
                });
                break;
        }
    }
    
    private int getPriorityOrder(String priority) {
        if (priority == null) return 0;
        switch (priority.toLowerCase()) {
            case "urgent": return 4;
            case "high": return 3;
            case "medium": return 2;
            case "low": return 1;
            default: return 0;
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadQueries();
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        applyFilters();
    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void handleSort(ActionEvent event) {
        sortQueries();
        displayQueries();
    }
}