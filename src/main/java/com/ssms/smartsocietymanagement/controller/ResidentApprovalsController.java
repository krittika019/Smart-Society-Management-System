package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ResidentApprovalsController implements Initializable {

    @FXML
    private TableView<Resident> pendingResidentsTable;

    @FXML
    private TableColumn<Resident, String> idColumn;

    @FXML
    private TableColumn<Resident, String> nameColumn;

    @FXML
    private TableColumn<Resident, String> usernameColumn;

    @FXML
    private TableColumn<Resident, String> emailColumn;

    @FXML
    private TableColumn<Resident, String> ownershipstatusColumn;

    @FXML
    private TableColumn<Resident, String> phonenumberColumn;

    @FXML
    private Button approveButton;

    @FXML
    private Button rejectButton;

    private DatabaseHandler dbHandler = new DatabaseHandler();
    private ObservableList<Resident> pendingResidents = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ownershipstatusColumn.setCellValueFactory(new PropertyValueFactory<>("ownership_status"));
        phonenumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Set preferred widths for columns
        idColumn.setPrefWidth(150);
        nameColumn.setPrefWidth(120);
        usernameColumn.setPrefWidth(120);
        emailColumn.setPrefWidth(150);
        ownershipstatusColumn.setPrefWidth(150);
        phonenumberColumn.setPrefWidth(120);

        for (TableColumn<Resident, ?> column : pendingResidentsTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        pendingResidentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Load pending residents
        loadPendingResidents();

        // Disable buttons when no row is selected
        approveButton.setDisable(true);
        rejectButton.setDisable(true);

        // Enable buttons when a row is selected
        pendingResidentsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean rowSelected = newSelection != null;
            approveButton.setDisable(!rowSelected);
            rejectButton.setDisable(!rowSelected);
        });

        System.out.println("ResidentApprovalsController loaded.");

    }

    private void loadPendingResidents() {
        try {
            pendingResidents.clear();
            pendingResidents.addAll(dbHandler.getPendingResidents());
            System.out.println("Number of pending residents loaded: " + pendingResidents.size());
            pendingResidentsTable.setItems(pendingResidents);
            System.out.println("Residents loaded: " + pendingResidents.size());

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load pending residents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApproveButton(ActionEvent event) {
        Resident selectedResident = pendingResidentsTable.getSelectionModel().getSelectedItem();
        if (selectedResident != null) {
            try {
                if (dbHandler.approveResident(selectedResident.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Resident approved successfully");
                    loadPendingResidents(); // Refresh the table
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve resident");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to approve resident: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRejectButton(ActionEvent event) {
        Resident selectedResident = pendingResidentsTable.getSelectionModel().getSelectedItem();
        if (selectedResident != null) {
            try {
                if (dbHandler.rejectResident(selectedResident.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Resident rejected");
                    loadPendingResidents(); // Refresh the table
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject resident");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to reject resident: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        loadPendingResidents();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
