package com.productrecommendation.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class QueryCardController {

    @FXML
    private ImageView productImage;
    @FXML
    private Label productName;
    @FXML
    private Label productBrand;
    @FXML
    private Label category;
    @FXML
    private Label queryTitle;
    @FXML
    private Label boycottReason;
    @FXML
    private Hyperlink readMore;
    @FXML
    private ImageView userPhoto;
    @FXML
    private Label postedBy;
    @FXML
    private Label postedDate;
    @FXML
    private Label likes;

//    public void setData(com.productrecommendation.models.Query query) {
//        productImage.setImage(new Image(query.getProductPhoto()));
//        productName.setText(query.getProductName());
//        productBrand.setText(query.getProductBrand());
//        category.setText(query.getCategory());
//        queryTitle.setText(query.getTitle());
//
//        String reason = query.getBoycottingReason();
//        if (reason.length() > 150) {
//            boycottReason.setText(reason.substring(0, 150) + "...");
//            readMore.setVisible(true);
//        } else {
//            boycottReason.setText(reason);
//            readMore.setVisible(false);
//        }
//
//        userPhoto.setImage(new Image(query.getUserPhoto()));
//        postedBy.setText(query.getPostedBy());
//        postedDate.setText(query.getPostedDate());
//        likes.setText("♥ " + query.getLikeCount());
//    }
}
