package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Flat;
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
import java.util.Optional;

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
    private ComboBox<String> signupOwnershipStatus;

    @FXML
    private ComboBox<String> signupBlockName;

    @FXML
    private ComboBox<String> signupFlatNumber;

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
                // User is approved, proceed to dashboard
                Resident resident = dbHandler.getResidentByUsername(username);
                navigateToDashboard(event, resident, "resident");
            } else if (dbHandler.isResidentPendingApproval(username, password)) {
                // User exists but is pending approval
                showAlert(Alert.AlertType.WARNING, "Account Pending",
                        "Your account is awaiting approval by the administrator. Please try again later.");
            } else if (dbHandler.isResidentRejected(username, password)) {
                // User exists but was rejected
//                showAlert(Alert.AlertType.ERROR, "Account Rejected",
//                        "Your registration has been rejected by the administrator. Please contact the society office for assistance.");
                showResubmitDialog(username, password);
            } else {
                // Invalid credentials
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password");
            }
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }
    private void showResubmitDialog(String username, String password) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Account Rejected");
        alert.setHeaderText("Your registration has been rejected by the administrator.");
        alert.setContentText("Would you like to resubmit your application?");

        ButtonType resubmitButton = new ButtonType("Resubmit Application");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(resubmitButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == resubmitButton) {
            try {
                Resident resident = dbHandler.getResidentByUsername(username);
                if (resident != null) {
                    boolean success = dbHandler.resubmitResidentApplication(resident.getId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Application Resubmitted",
                                "Your application has been resubmitted for approval. You will be able to log in once an administrator approves your account.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Resubmission Failed",
                                "Failed to resubmit your application. Please contact the society office.");
                    }
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        }
    }


    @FXML
    private void handleSignup(ActionEvent event) {
        String name = signupName.getText();
        String username = signupUsername.getText();
        String password = signupPassword.getText();
        String email = signupEmail.getText();
        String ownership_status = signupOwnershipStatus.getValue();
        String blockname = signupBlockName.getValue() ;
        String flatnumber = signupFlatNumber.getValue() ;
        String phone = signupPhone.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() ||
                email.isEmpty() || ownership_status.isEmpty() || blockname.isEmpty() || flatnumber.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Signup Error", "Please fill all fields");
            return;
        }
        String res_id = generateResidentId() ;
        String flat_id = generateFlatId() ;
        Resident resident = new Resident(res_id, name, username, password, email, ownership_status, phone, "Pending");
        Flat flat = new Flat(flat_id, blockname, flatnumber) ;

        try {
            if (dbHandler.addResident(resident, flat)) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Submitted",
                        "Your registration has been submitted for approval. You will be able to log in once an administrator approves your account.");
                // Clear the form fields
                signupName.clear();
                signupUsername.clear();
                signupPassword.clear();
                signupEmail.clear();
                signupPhone.clear();
                signupOwnershipStatus.getSelectionModel().clearSelection();
                signupBlockName.getSelectionModel().clearSelection();
                signupFlatNumber.getSelectionModel().clearSelection();

            } else {
                showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username or email may already exist");
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
    private String generateResidentId() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        return "RES" + timestamp + random;
    }
    private String generateFlatId() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        return "FA" + timestamp + random;
    }

}