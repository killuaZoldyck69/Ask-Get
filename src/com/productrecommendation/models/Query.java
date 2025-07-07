/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.models;

/**
 *
 * @author Tasin Ahmed
 */
public class Query {

    private String title;
    private String productName;
    private String category;
    private String productBrand;
    private String productPhoto;
    private String boycottingReason;
    private String postedBy;
    private String postedEmail;
    private String userPhoto;
    private String postedDate;
    private int likeCount;

    public Query(String title, String productName, String category, String productBrand,
            String productPhoto, String boycottingReason,
            String postedBy, String postedEmail, String userPhoto,
            String postedDate, int likeCount) {
        this.title = title;
        this.productName = productName;
        this.category = category;
        this.productBrand = productBrand;
        this.productPhoto = productPhoto;
        this.boycottingReason = boycottingReason;
        this.postedBy = postedBy;
        this.postedEmail = postedEmail;
        this.userPhoto = userPhoto;
        this.postedDate = postedDate;
        this.likeCount = likeCount;
    }

    public String getTitle() {
        return title;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public String getBoycottingReason() {
        return boycottingReason;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getPostedEmail() {
        return postedEmail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public int getLikeCount() {
        return likeCount;
    }
}
