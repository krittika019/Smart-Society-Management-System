package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Amenity;
import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ResidentAmenitiesController implements Initializable {

    @FXML private TableView<Amenity> amenitiesTable;
    @FXML private TableColumn<Amenity, String> nameColumn;
    @FXML private TableColumn<Amenity, String> descriptionColumn;
    @FXML private TableColumn<Amenity, String> locationColumn;
    @FXML private TableColumn<Amenity, String> hoursColumn;
    @FXML private TableColumn<Amenity, String> statusColumn;
    @FXML private TableColumn<Amenity, String> createdByColumn;
    @FXML private TableColumn<Amenity, Date> createdDateColumn;

    private Resident currentResident;
    private ObservableList<Amenity> amenitiesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        hoursColumn.setCellValueFactory(new PropertyValueFactory<>("openingHours"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdByColumn.setCellValueFactory(new PropertyValueFactory<>("createdByName"));
        createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        
        amenitiesTable.setItems(amenitiesList);
    }

    public void initData(Resident resident) {
        this.currentResident = resident;
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}