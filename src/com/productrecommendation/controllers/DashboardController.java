/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

//import com.productrecommendation.models.Query;
//import com.productrecommendation.models.QueryApiService;
import java.io.IOException;
import java.net.URL;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Tasin Ahmed
 */
public class DashboardController {

    @FXML
    private FlowPane queryCardContainer;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button logoutButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button allQueriesButton;
    @FXML
    private Button myQueriesButton;
    @FXML
    private Button myRecommendationsButton;
    @FXML
    private Button recommendationsForMeButton;
    @FXML
    private Label totalQueriesLabel;
    @FXML
    private Label totalRecommendationsLabel;
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox welcomeScreen;
    @FXML
    private ScrollPane profileSection;
    private TextField usernameField;
    @FXML
    private TextField emailField;
    private TextField fullNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea bioArea;

    @FXML
    private ScrollPane allQueriesSection;
//    @FXML
////    private TableView<Query> allQueriesTable;
////
////    @FXML
////    private TableColumn<Query, String> queryTitleColumn;
////    @FXML
////    private TableColumn<Query, String> productNameColumn;
////    @FXML
////    private TableColumn<Query, String> categoryColumn;
////    @FXML
////    private TableColumn<Query, String> postedByColumn;
////    @FXML
////    private TableColumn<Query, String> postedDateColumn;
    @FXML
    private VBox myQueriesSection;
    @FXML
    private TableView<?> myQueriesTable;
    @FXML
    private TableColumn<?, ?> myQueryIdColumn;
    @FXML
    private TableColumn<?, ?> myQueryTextColumn;
    @FXML
    private TableColumn<?, ?> myQueryDateColumn;
    @FXML
    private TableColumn<?, ?> myQueryStatusColumn;
    @FXML
    private TableColumn<?, ?> myQueryActionsColumn;
    @FXML
    private VBox myRecommendationsSection;
    @FXML
    private VBox myRecommendationsContainer;
    @FXML
    private VBox recommendationsForMeSection;
    @FXML
    private VBox recommendationsForMeContainer;
    @FXML
    private TextField displayNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField registrationField;

    @FXML
    private void handleLogout(ActionEvent event) {
    }

    @FXML
    private void showProfile() {
        hideAllSections();
        profileSection.setVisible(true);

        // Fill with user data (example)
        usernameField.setText("nahid123");
        emailField.setText("nahid@example.com");
        fullNameField.setText("Nahid Hasan");
        phoneField.setText("017XXXXXXXX");
        bioArea.setText("CSE student. Passionate about tech.");
    }

    @FXML
    private void saveProfile() {
        // Save logic (you can add file/db saving here)
        System.out.println("Saved: " + fullNameField.getText());
    }

    @FXML
    private void cancelProfileEdit() {
        showProfile(); // Reset values (or use hideAllSections if needed)
    }

    private void hideAllSections() {
        welcomeScreen.setVisible(false);
        profileSection.setVisible(false);
        allQueriesSection.setVisible(false);
        myQueriesSection.setVisible(false);
        myRecommendationsSection.setVisible(false);
        recommendationsForMeSection.setVisible(false);
    }

    public void initialize() {
        System.out.println("allQueriesSection = " + allQueriesSection);
    }

//    @FXML
// private ObservableList<Query> fetchLiveQueries() {
//    return FXCollections.observableArrayList(QueryApiService.fetchAllQueries());
//}
//
//
//@FXML
    @FXML
    private void showAllQueries() {
//    hideAllSections();
//    allQueriesSection.setVisible(true);
//
//    if (queryCardContainer == null) {
//        System.err.println("queryCardContainer is null. Check your FXML.");
//        return;
//    }
//
//    queryCardContainer.getChildren().clear();
//
//    // 🔁 Use real data from backend
//    ObservableList<Query> liveQueries = fetchLiveQueries();
//
//    for (Query q : liveQueries) {
//        try {
//            URL fxmlLocation = getClass().getResource("/resources/com/productrecommendation/fxml/QueryCard.fxml");
//
//            if (fxmlLocation == null) {
//                System.err.println("QueryCard.fxml not found.");
//                continue;
//            }
//
//            FXMLLoader loader = new FXMLLoader(fxmlLocation);
//            Node card = loader.load();
//
//            QueryCardController controller = loader.getController();
//            controller.setData(q);
//
//            queryCardContainer.getChildren().add(card);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}


    @FXML
    private void showMyQueries(ActionEvent event) {
    }

    @FXML
    private void showMyRecommendations(ActionEvent event) {
    }

    @FXML
    private void showRecommendationsForMe(ActionEvent event) {
    }

    @FXML
    private void createNewQuery(ActionEvent event) {
    }

    @FXML
    private void refreshRecommendations(ActionEvent event) {
    }

    @FXML
    private void resetProfile(ActionEvent event) {
    }

}
