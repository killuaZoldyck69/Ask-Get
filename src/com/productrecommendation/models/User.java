/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.models;

public class User {
    private String name;
    private String email;
    private String joinDate;
    private boolean verified;
    private int queriesPosted;
    private int recommendationsGiven;
    private int solvedQueries;
    private int helpfulnessRating;

    // Constructor, Getters, and Setters
    public User(String name, String email, String joinDate, boolean verified,
                int queriesPosted, int recommendationsGiven, int solvedQueries, int helpfulnessRating) {
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
        this.verified = verified;
        this.queriesPosted = queriesPosted;
        this.recommendationsGiven = recommendationsGiven;
        this.solvedQueries = solvedQueries;
        this.helpfulnessRating = helpfulnessRating;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getJoinDate() { return joinDate; }
    public boolean isVerified() { return verified; }
    public int getQueriesPosted() { return queriesPosted; }
    public int getRecommendationsGiven() { return recommendationsGiven; }
    public int getSolvedQueries() { return solvedQueries; }
    public int getHelpfulnessRating() { return helpfulnessRating; }
}

