/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.productrecommendation.controllers;

import com.productrecommendation.models.Query;
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
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
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

    @FXML
    public void initialize() {
        System.out.println("allQueriesSection = " + allQueriesSection);
    }

    @FXML
    private ObservableList<Query> getSampleQueries() {
        ObservableList<Query> queries = FXCollections.observableArrayList();

        queries.add(new Query(
                "Coca-Cola: Quenching Israel’s genocidal soldiers’ thirst",
                "Coca-Cola",
                "Soft Drinks",
                "The Coca-Cola Company",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/27/Coca_Cola_Flasche_-_Original_Taste.jpg/1200px-Coca_Cola_Flasche_-_Original_Taste.jpg",
                "Because Coca-Cola is implicated in Israeli war crimes. According to research by WhoProfits, the Central Beverage Company, known as Coca-Cola Israel, which is the exclusive franchisee of the Coca-Cola Company in Israel, operates a regional distribution center and cooling houses in the Atarot Settlement Industrial Zone...",
                "Nahid Hasan",
                "nh694225@gmail.com",
                "https://lh3.googleusercontent.com/a/ACg8ocLph06TzFGSCFfsuwtR8fvToQN3wUcXkCEmRnjCrVVjPT9OBrj4=s96-c",
                "2024-12-27",
                1
        ));

        queries.add(new Query(
                "Hasina sued for genocide, crimes against humanity",
                "Sheikh Hasina",
                "Prime Minister",
                " Awami League",
                "https://pbs.twimg.com/media/GUSKNgxWUAA9TWL.jpg",
                "A complaint was filed with the investigation agency of the International Crimes Tribunal yesterday accusing former prime minister Sheikh Hasina and eight others of committing crimes against humanity and genocide between July 15 and August 5.\n\nSupreme Court lawyer Gazi MH Tamim filed the complaint on behalf of Bulbul Kabir...",
                "Nahid Hasan",
                "nh694225@gmail.com",
                "https://lh3.googleusercontent.com/a/ACg8ocLph06TzFGSCFfsuwtR8fvToQN3wUcXkCEmRnjCrVVjPT9OBrj4=s96-c",
                "2024-12-27",
                4
        ));

        return queries;
    }

    @FXML
    private void showAllQueries() {
        hideAllSections();  // Hide all other sections
        allQueriesSection.setVisible(true);  // Show the "All Queries" section

        // Ensure the container is not null
        if (queryCardContainer == null) {
            System.err.println("queryCardContainer is null. Check your FXML and fx:id.");
            return;
        }

        queryCardContainer.getChildren().clear();  // Clear previous cards

        for (Query q : getSampleQueries()) {
            try {
                // Load QueryCard.fxml
                URL fxmlLocation = getClass().getResource("/resources/com/productrecommendation/fxml/QueryCard.fxml");

                if (fxmlLocation == null) {
                    System.err.println("QueryCard.fxml not found! Check the path.");
                    continue;
                }

                FXMLLoader loader = new FXMLLoader(fxmlLocation);
                Node card = loader.load();

                // Get the controller and set query data
                QueryCardController controller = loader.getController();
                controller.setData(q);

                // Add the card to the FlowPane
                queryCardContainer.getChildren().add(card);

            } catch (IOException e) {
                System.err.println("Failed to load QueryCard.fxml:");
                e.printStackTrace();
            }
        }
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

}
