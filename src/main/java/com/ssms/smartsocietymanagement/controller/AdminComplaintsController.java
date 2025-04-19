package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminComplaintsController implements Initializable {

    @FXML
    private TableView<Map<String, Object>> complaintsTable;

    @FXML
    private TableColumn<Map<String, Object>, String> dateTimeColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> residentNameColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> flatInfoColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> subjectColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> descriptionColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> statusColumn;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private Button refreshButton;

    @FXML
    private Button resolveButton;

    private Admin currentAdmin;
    private ObservableList<Map<String, Object>> complaintsData = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure status filter
        statusFilterComboBox.setItems(FXCollections.observableArrayList("All", "PENDING", "RESOLVED"));
        statusFilterComboBox.getSelectionModel().selectFirst();
        statusFilterComboBox.setOnAction(event -> filterComplaints());

        // Configure table columns
        dateTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = (Timestamp) cellData.getValue().get("dateTime");
            return new SimpleStringProperty(timestamp.toLocalDateTime().format(dateFormatter));
        });

        residentNameColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty((String) cellData.getValue().get("residentName")));

        flatInfoColumn.setCellValueFactory(cellData -> {
            String blockName = (String) cellData.getValue().get("blockName");
            String flatNumber = (String) cellData.getValue().get("flatNumber");
            return new SimpleStringProperty(blockName + "-" + flatNumber);
        });

        subjectColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty((String) cellData.getValue().get("subject")));

        descriptionColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty((String) cellData.getValue().get("description")));

        statusColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty((String) cellData.getValue().get("status")));

        // Style the status column
        statusColumn.setCellFactory(column -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("RESOLVED")) {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-font-weight: bold");
                    } else if (item.equals("PENDING")) {
                        setTextFill(Color.ORANGE);
                        setStyle("-fx-font-weight: bold");
                    } else {
                        setTextFill(Color.BLACK);
                        setStyle("");
                    }
                }
            }
        });

        // Set up table selection listener for the resolve button
        complaintsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String status = (String) newSelection.get("status");
                resolveButton.setDisable(status.equals("RESOLVED"));
            } else {
                resolveButton.setDisable(true);
            }
        });

        complaintsTable.setItems(complaintsData);
    }

    public void initData(Admin admin) {
        this.currentAdmin = admin;
        loadAllComplaints();
    }

    private void loadAllComplaints() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            List<Map<String, Object>> complaints = dbHandler.getAllComplaints();
            complaintsData.clear();
            complaintsData.addAll(complaints);
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load complaints: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void filterComplaints() {
        String selectedStatus = statusFilterComboBox.getValue();
        
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            List<Map<String, Object>> complaints;
            
            if (selectedStatus.equals("All")) {
                complaints = dbHandler.getAllComplaints();
            } else {
                complaints = dbHandler.getComplaintsByStatus(selectedStatus);
            }
            
            complaintsData.clear();
            complaintsData.addAll(complaints);
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to filter complaints: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResolveButton(ActionEvent event) {
        Map<String, Object> selectedComplaint = complaintsTable.getSelectionModel().getSelectedItem();
        
        if (selectedComplaint != null) {
            String complaintId = (String) selectedComplaint.get("complaintId");
            
            try {
                DatabaseHandler dbHandler = new DatabaseHandler();
                boolean success = dbHandler.resolveComplaint(complaintId);
                dbHandler.closeConnection();
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Complaint has been marked as resolved.");
                    // Refresh the table
                    filterComplaints();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Action Failed", "Failed to mark complaint as resolved.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error resolving complaint: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        filterComplaints();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}