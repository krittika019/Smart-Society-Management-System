package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Flat;
import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import com.ssms.smartsocietymanagement.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainContentPane;

    @FXML
    private Button approvalsButton;

    @FXML
    private Button visitorsButton;

    @FXML
    private Button billsButton;

    private Resident currentResident;
    private Admin currentAdmin;
    private String userType;
    private String flatId;

    @FXML
    private void handleResidentApprovalsButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentApprovals.fxml"));
            Pane approvalsView = loader.load();

            mainContentPane.setCenter(approvalsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initData(Resident resident, String userType) {
        this.currentResident = resident;
        this.userType = userType;
        welcomeLabel.setText("Welcome, " + resident.getName() + "!");
        // Get the resident's flat ID
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            this.flatId = dbHandler.getFlatIdByResidentId(resident.getId());
            dbHandler.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadDefaultDashboardContent();
        if (approvalsButton != null) {
            approvalsButton.setVisible(false);
        }
        if (visitorsButton != null) {
            visitorsButton.setText("Visitor Requests");
        }

        if (billsButton != null) {
            billsButton.setText("Bill Payments");
        }
    }

    public void initAdminData(Admin admin) {
        this.currentAdmin = admin;
        this.userType = "admin";
        welcomeLabel.setText("Welcome, Admin " + admin.getName() + "!");
        loadDefaultDashboardContent();
        if (approvalsButton != null) {
            approvalsButton.setVisible(true);
        }
    }

    private void loadDefaultDashboardContent() {
        try {
            Pane dashboardContent = FXMLLoader
                    .load(getClass().getResource("/com/ssms/smartsocietymanagement/view/DashboardContent.fxml"));
            mainContentPane.setCenter(dashboardContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDashboardButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/DashboardContent.fxml"));
            Pane approvalsView = loader.load();

            mainContentPane.setCenter(approvalsView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleComplaintsButton(ActionEvent event) {
        try {
            if (userType.equals("resident")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentComplaintsView.fxml"));
                Pane complaintsPane = loader.load();

                ResidentComplaintsController controller = loader.getController();
                controller.initData(currentResident);

                mainContentPane.setCenter(complaintsPane);
            } else {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/AdminComplaintsView.fxml"));
                Pane complaintsPane = loader.load();

                AdminComplaintsController controller = loader.getController();
                controller.initData(currentAdmin);

                mainContentPane.setCenter(complaintsPane);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load complaints view: " + e.getMessage());
        }
    }

    @FXML
private void handleBillsButton(ActionEvent event) {
    try {
        if (userType.equals("resident")) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentBillsView.fxml"));
            Pane billsPane = loader.load();

            ResidentBillsController controller = loader.getController();
            
            // Get the resident's flat information
            DatabaseHandler dbHandler = new DatabaseHandler();
            Flat residentFlat = dbHandler.getFlatByResidentId(currentResident.getId());
            dbHandler.closeConnection();

            if (residentFlat != null) {
                controller.initData(currentResident, residentFlat.getBlock_name(), residentFlat.getFlat_number());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not find flat information for this resident.");
                return;
            }

            mainContentPane.setCenter(billsPane);
        } else {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/AdminBillsView.fxml"));
            Pane billsPane = loader.load();

            AdminBillsController controller = loader.getController();
            controller.initData(currentAdmin);

            mainContentPane.setCenter(billsPane);
        }
    } catch (IOException | SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load bills view: " + e.getMessage());
    }
}

    // @FXML
    // private void handlePaymentsButton(ActionEvent event) {
    //     loadContentPane("/fxml/PaymentsView.fxml");
    // }

    @FXML
private void handleAmenitiesButton(ActionEvent event) {
    try {
        if (userType.equals("resident")) {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentAmenitiesView.fxml"));
            Pane amenitiesPane = loader.load();

            ResidentAmenitiesController controller = loader.getController();
            controller.initData(currentResident);

            mainContentPane.setCenter(amenitiesPane);
        } else {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ssms/smartsocietymanagement/view/AdminAmenitiesView.fxml"));
            Pane amenitiesPane = loader.load();

            AdminAmenitiesController controller = loader.getController();
            controller.initData(currentAdmin);

            mainContentPane.setCenter(amenitiesPane);
        }
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load amenities view: " + e.getMessage());
    }
}



    @FXML
    private void handleVisitorsButton(ActionEvent event) {
        try {
            if (userType.equals("resident")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentVisitorsView.fxml"));
                Pane visitorsPane = loader.load();

                ResidentVisitorsController controller = loader.getController();

                // Get the resident's flat information
                DatabaseHandler dbHandler = new DatabaseHandler();
                Flat residentFlat = dbHandler.getFlatByResidentId(currentResident.getId());
                dbHandler.closeConnection();

                if (residentFlat != null) {
                    controller.initData(currentResident, residentFlat.getBlock_name(), residentFlat.getFlat_number());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not find flat information for this resident.");
                    return;
                }

                mainContentPane.setCenter(visitorsPane);
            } else {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/AdminVisitorsView.fxml"));
                Pane visitorsPane = loader.load();

                AdminVisitorsController controller = loader.getController();
                controller.initData(currentAdmin);

                mainContentPane.setCenter(visitorsPane);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load visitors view: " + e.getMessage());
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader
                .load(getClass().getResource("/com/ssms/smartsocietymanagement/view/UserTypeSelection.fxml"));
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

    // Add this method to your DashboardController.java class

    @FXML
    private void handleNoticesButton(ActionEvent event) {
        try {
            if (userType.equals("resident")) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/ResidentNoticesView.fxml"));
                Pane noticesPane = loader.load();

                ResidentNoticesController controller = loader.getController();
                controller.initData(currentResident);

                mainContentPane.setCenter(noticesPane);
            } else {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/ssms/smartsocietymanagement/view/AdminNoticesView.fxml"));
                Pane noticesPane = loader.load();

                AdminNoticesController controller = loader.getController();
                controller.initData(currentAdmin);

                mainContentPane.setCenter(noticesPane);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load notices view: " + e.getMessage());
        }
    }

// Replace your existing handleNoticesButton method with this one



}