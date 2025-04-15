package com.ssms.smartsocietymanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserTypeSelectionController {

    @FXML
    private void handleResidentButton(ActionEvent event) throws IOException {
        loadScene(event, "/com/ssms/smartsocietymanagement/view/ResidentLoginSignup.fxml");
    }

    @FXML
    private void handleAdminButton(ActionEvent event) throws IOException {
        loadScene(event, "/com/ssms/smartsocietymanagement/view/AdminLogin.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}