package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ResidentLoginSignupController {

    @FXML
    private TextField loginUsername;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField signupName;

    @FXML
    private TextField signupUsername;

    @FXML
    private PasswordField signupPassword;

    @FXML
    private TextField signupEmail;

    @FXML
    private TextField signupApartment;

    @FXML
    private TextField signupPhone;

    private DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password");
            return;
        }

        try {
            if (dbHandler.validateResident(username, password)) {
                Resident resident = dbHandler.getResidentByUsername(username);
                navigateToDashboard(event, resident, "resident");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password");
            }
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    private void handleSignup(ActionEvent event) {
        String name = signupName.getText();
        String username = signupUsername.getText();
        String password = signupPassword.getText();
        String email = signupEmail.getText();
        String apartment = signupApartment.getText();
        String phone = signupPhone.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() ||
                email.isEmpty() || apartment.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Signup Error", "Please fill all fields");
            return;
        }

        Resident resident = new Resident(0, name, username, password, email, apartment, phone);

        try {
            if (dbHandler.addResident(resident)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Please login.");
                // Switch to login tab
            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username may already exist");
            }
        } catch (SQLException e) {
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

    private void navigateToDashboard(ActionEvent event, Resident resident, String userType) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ssms/smartsocietymanagement/view/Dashboard.fxml"));
        Parent root = loader.load();

        DashboardController controller = loader.getController();
        controller.initData(resident, userType);

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