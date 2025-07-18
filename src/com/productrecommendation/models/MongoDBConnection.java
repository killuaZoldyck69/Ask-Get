/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.models;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.FindIterable;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
/**
 *
 * @author Tanver
 */
public class MongoDBConnection {
    
      private static final String CONNECTION_STRING = 
        "mongodb+srv://AskandGet:lu9qkEneDpgd6KxY@cluster0.2nua0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    // Connect to MongoDB
    public static void connect() {
        ConnectionString connString = new ConnectionString(CONNECTION_STRING);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("Ask&Get");
        System.out.println("Connected to MongoDB.");
    }
    
    // Get the database object
    public static MongoDatabase getDatabase() {
        if (database == null) {
            connect();
        }
        return database;
    }
    
    // Close the connection when app exits
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }
}
