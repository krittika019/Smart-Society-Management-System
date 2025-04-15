package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AdminLoginController {

    @FXML
    private TextField adminUsername;

    @FXML
    private PasswordField adminPassword;

    private DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = adminUsername.getText();
        String password = adminPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
            return;
        }

        try {
            if (dbHandler.validateAdmin(username, password)) {
                Admin admin = dbHandler.getAdminByUsername(username);
                navigateToDashboard(event, admin);
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid admin credentials");
            }
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/ssms/smartsocietymanagement/view/UserTypeSelection.fxml"));
        Scene scene = new Scene(root, 800, 600);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void navigateToDashboard(ActionEvent event, Admin admin) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ssms/smartsocietymanagement/view/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController controller = loader.getController();
        controller.initAdminData(admin);

        Scene scene = new Scene(root, 1000, 700);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}