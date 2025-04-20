package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Amenity;
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
import java.util.List;
import java.util.ResourceBundle;

public class AdminAmenitiesController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField locationField;
    @FXML private TextField hoursField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity, String> nameColumn;
    @FXML private TableColumn<Amenity, String> descriptionColumn;
    @FXML private TableColumn<Amenity, String> locationColumn;
    @FXML private TableColumn<Amenity, String> hoursColumn;
    @FXML private TableColumn<Amenity, String> statusColumn;

    private Admin currentAdmin;
    private ObservableList<Amenity> amenitiesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the status combo box
        statusComboBox.getItems().addAll("AVAILABLE", "UNDER MAINTENANCE", "CLOSED");
        statusComboBox.setValue("AVAILABLE");
        
        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        amenitiesTable.setItems(amenitiesList);
    }

    public void initData(Admin admin) {
        this.currentAdmin = admin;
        loadAmenities();
    }

    private void loadAmenities() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            List<Amenity> amenities = dbHandler.getAllAmenities();
            amenitiesList.clear();
            amenitiesList.addAll(amenities);
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load amenities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddAmenity(ActionEvent event) {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String location = locationField.getText().trim();
        String hours = hoursField.getText().trim();
        String status = statusComboBox.getValue();
        
        if (name.isEmpty() || location.isEmpty() || hours.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name, Location and Opening Hours are required.");
            return;
        }
        
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            boolean success = dbHandler.addAmenity(name, description, location, hours, status, currentAdmin.getId());
            dbHandler.closeConnection();
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Amenity added successfully.");
                clearFields();
                loadAmenities();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add amenity.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add amenity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        descriptionArea.clear();
        locationField.clear();
        hoursField.clear();
        statusComboBox.setValue("AVAILABLE");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}