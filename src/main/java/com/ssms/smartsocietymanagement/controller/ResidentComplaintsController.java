package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Resident;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ResidentComplaintsController implements Initializable {

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Button clearButton;

    @FXML
    private Button submitButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TableView<Map<String, Object>> complaintsTable;

    @FXML
    private TableColumn<Map<String, Object>, String> dateTimeColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> subjectColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> descriptionColumn;

    @FXML
    private TableColumn<Map<String, Object>, String> statusColumn;

    private Resident currentResident;
    private ObservableList<Map<String, Object>> complaintsData = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configure table columns
        dateTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = (Timestamp) cellData.getValue().get("dateTime");
            return new SimpleStringProperty(timestamp.toLocalDateTime().format(dateFormatter));
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

        complaintsTable.setItems(complaintsData);
    }

    public void initData(Resident resident) {
        this.currentResident = resident;
        loadComplaints();
    }

    private void loadComplaints() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            List<Map<String, Object>> complaints = dbHandler.getComplaintsByResident(currentResident.getId());
            complaintsData.clear();
            complaintsData.addAll(complaints);
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load complaints: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmitButton(ActionEvent event) {
        String subject = subjectField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (subject.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in both subject and description fields.");
            return;
        }

        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            boolean success = dbHandler.registerComplaint(currentResident.getId(), subject, description);
            dbHandler.closeConnection();

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Your complaint has been submitted successfully.");
                clearForm();
                loadComplaints();
            } else {
                showAlert(Alert.AlertType.ERROR, "Submission Failed", "Failed to submit complaint. Please try again.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error submitting complaint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearButton(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        subjectField.clear();
        descriptionArea.clear();
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        loadComplaints();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}