package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Resident;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainContentPane;

    private Resident currentResident;
    private Admin currentAdmin;
    private String userType;

    public void initData(Resident resident, String userType) {
        this.currentResident = resident;
        this.userType = userType;
        welcomeLabel.setText("Welcome, " + resident.getName() + "!");
        loadDefaultDashboardContent();
    }

    public void initAdminData(Admin admin) {
        this.currentAdmin = admin;
        this.userType = "admin";
        welcomeLabel.setText("Welcome, Admin " + admin.getName() + "!");
        loadDefaultDashboardContent();
    }

    private void loadDefaultDashboardContent() {
        try {
            Pane dashboardContent = FXMLLoader.load(getClass().getResource("/fxml/DashboardContent.fxml"));
            mainContentPane.setCenter(dashboardContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNoticesButton(ActionEvent event) {
        loadContentPane("/fxml/NoticesView.fxml");
    }

    @FXML
    private void handleComplaintsButton(ActionEvent event) {
        loadContentPane("/fxml/ComplaintsView.fxml");
    }

    @FXML
    private void handlePaymentsButton(ActionEvent event) {
        loadContentPane("/fxml/PaymentsView.fxml");
    }

    @FXML
    private void handleAmenitiesButton(ActionEvent event) {
        loadContentPane("/fxml/AmenitiesView.fxml");
    }

    @FXML
    private void handleProfileButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProfileView.fxml"));
            Pane profilePane = loader.load();

            ProfileViewController controller = loader.getController();
            if (userType.equals("resident")) {
                controller.initData(currentResident);
            } else {
                controller.initAdminData(currentAdmin);
            }

            mainContentPane.setCenter(profilePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/UserTypeSelection.fxml"));
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void loadContentPane(String fxmlPath) {
        try {
            Pane content = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContentPane.setCenter(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}