/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.bson.Document;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.Region;

public class ProfileController implements Initializable {

    // 🟦 FXML label references (must match fx:id in FXML)
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label joinDateLabel;
    @FXML
    private Label queriesPostedLabel;
    @FXML
    private Label recommendationsGivenLabel;
    @FXML
    private Label solvedQueriesLabel;
    @FXML
    private Label helpfulnessRatingLabel;

    // 🔐 Logged-in user email (replace with dynamic value if you use login system)
    private final String loggedInEmail = SessionManager.loggedInEmail; // ✅ Dynamic way
    @FXML
    private Region profileImageRegion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Connect to MongoDB and fetch user data
        MongoCollection<Document> userCollection = getUserCollection();
        if (userCollection != null) {
            Document userDoc = userCollection.find(new Document("email", loggedInEmail)).first();
            if (userDoc != null) {
                // 🔁 Update labels with user data
                nameLabel.setText(userDoc.getString("display_name"));
                emailLabel.setText(userDoc.getString("email"));
                joinDateLabel.setText("Joined " + userDoc.getString("joinDate"));
                queriesPostedLabel.setText(String.valueOf(userDoc.getInteger("queriesPosted", 0)));
                recommendationsGivenLabel.setText(String.valueOf(userDoc.getInteger("recommendationsGiven", 0)));
                solvedQueriesLabel.setText(String.valueOf(userDoc.getInteger("solvedQueries", 0)));
                helpfulnessRatingLabel.setText(userDoc.getInteger("helpfulnessRating", 0) + "%");

                String imageUrl = userDoc.getString("photo_url");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    String style = "-fx-background-color: #4F46E5; "
                            + "-fx-background-radius: 40; "
                            + "-fx-background-image: url('" + imageUrl + "'); "
                            + "-fx-background-size: cover; "
                            + "-fx-background-position: center;";
                    profileImageRegion.setStyle(style);
                }
            } else {
                System.out.println("No user found with email: " + loggedInEmail);
            }
        }
    }

    // 🧩 MongoDB connection + collection access
    private MongoCollection<Document> getUserCollection() {
        try {
            String connectionString = "mongodb+srv://AskandGet:lu9qkEneDpgd6KxY@cluster0.2nua0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
            ConnectionString connString = new ConnectionString(connectionString);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .build();
            MongoClient mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase("Ask&Get");
            return database.getCollection("users");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
