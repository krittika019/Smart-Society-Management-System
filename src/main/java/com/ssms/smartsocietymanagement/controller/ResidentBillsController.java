package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.model.Bill;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.Date;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResidentBillsController {

    @FXML
    private TableView<Bill> billsTableView;

    @FXML
    private TableColumn<Bill, String> idColumn;

    @FXML
    private TableColumn<Bill, String> billTypeColumn;

    @FXML
    private TableColumn<Bill, Double> amountColumn;

    @FXML
    private TableColumn<Bill, String> dueDateColumn;

    @FXML
    private TableColumn<Bill, String> statusColumn;

    @FXML
    private TableColumn<Bill, String> paidDateColumn;
    
    @FXML
    private ComboBox<String> monthComboBox;
    
    @FXML
    private ComboBox<String> yearComboBox;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button clearFilterButton;

    private Resident currentResident;
    private String blockName;
    private String flatNumber;
    private DatabaseHandler dbHandler;
    
    private ObservableList<Bill> billsList = FXCollections.observableArrayList();

    public void initData(Resident resident, String blockName, String flatNumber) {
        this.currentResident = resident;
        this.blockName = blockName;
        this.flatNumber = flatNumber;
        this.dbHandler = new DatabaseHandler();
        
        // Initialize month and year combo boxes
        initializeMonthComboBox();
        initializeYearComboBox();
        
        // Initialize columns
        initializeTableColumns();
        
        // Load bills data
        loadBillsData();
    }
    
    private void initializeMonthComboBox() {
        ObservableList<String> months = FXCollections.observableArrayList();
        for (Month month : Month.values()) {
            months.add(month.toString());
        }
        monthComboBox.setItems(months);
    }
    
    private void initializeYearComboBox() {
        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            years.add(String.valueOf(i));
        }
        yearComboBox.setItems(years);
    }
    
    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        billTypeColumn.setCellValueFactory(new PropertyValueFactory<>("billType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paidDateColumn.setCellValueFactory(new PropertyValueFactory<>("paidDate"));
        
        // Set custom cell factory to style the status column
        statusColumn.setCellFactory(new Callback<TableColumn<Bill, String>, TableCell<Bill, String>>() {
            @Override
            public TableCell<Bill, String> call(TableColumn<Bill, String> param) {
                return new TableCell<Bill, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            setText(item);
                            if (item.equals("PAID")) {
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            } else {
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            }
                        }
                    }
                };
            }
        });
    }
    
    // private void loadBillsData() {
    //     try {
    //         List<Map<String, Object>> bills = dbHandler.getBillsByFlat(blockName, flatNumber);
    //         billsList.clear();
            
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
    //         for (Map<String, Object> bill : bills) {
    //             String billId = (String) bill.get("billId");
    //             String billType = (String) bill.get("billType");
    //             Double amount = (Double) bill.get("amount");
                
    //             Timestamp dueDate = (Timestamp) bill.get("dueDate");
    //             String formattedDueDate = dueDate != null ? 
    //                     LocalDate.parse(dueDate.toString().substring(0, 10)).format(formatter) : "-";
                
    //             String status = (String) bill.get("status");
                
    //             Timestamp paidDate = (Timestamp) bill.get("paidDate");
    //             String formattedPaidDate = paidDate != null ? 
    //                     LocalDate.parse(paidDate.toString().substring(0, 10)).format(formatter) : "-";
                
    //             billsList.add(new Bill(billId, billType, amount, formattedDueDate, status, formattedPaidDate));
    //         }
            
    //         billsTableView.setItems(billsList);
    //     } catch (SQLException e) {
    //         showAlert(Alert.AlertType.ERROR, "Error", "Failed to load bills: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    private void loadBillsData() {
        try {
            List<Map<String, Object>> bills = dbHandler.getBillsByFlat(blockName, flatNumber);
            billsList.clear();
    
            for (Map<String, Object> bill : bills) {
                String billId = (String) bill.get("billId");
                String billType = (String) bill.get("billType");
                Double amount = (Double) bill.get("amount");
    
                Timestamp dueDateTS = (Timestamp) bill.get("dueDate");
                Date dueDate = dueDateTS != null ? new Date(dueDateTS.getTime()) : null;
    
                Timestamp paidDateTS = (Timestamp) bill.get("paidDate");
                Date paidDate = paidDateTS != null ? new Date(paidDateTS.getTime()) : null;
    
                String status = bill.get("status") != null ? (String) bill.get("status") : "PENDING";
    
                billsList.add(new Bill(billId, blockName, flatNumber, billType, amount, dueDate, paidDate, status));
            }
    
            billsTableView.setItems(billsList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load bills: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    // @FXML
    // private void handleSearchButton(ActionEvent event) {
    //     String selectedMonth = monthComboBox.getValue();
    //     String selectedYear = yearComboBox.getValue();
        
    //     if (selectedMonth == null || selectedYear == null) {
    //         showAlert(Alert.AlertType.WARNING, "Warning", "Please select both month and year");
    //         return;
    //     }
        
    //     try {
    //         // Convert month name to month number (1-12)
    //         int monthNumber = Month.valueOf(selectedMonth).getValue();
    //         int yearNumber = Integer.parseInt(selectedYear);
            
    //         List<Map<String, Object>> filteredBills = dbHandler.filterBillsByDate(
    //                 blockName, flatNumber, monthNumber, yearNumber);
            
    //         billsList.clear();
            
    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
    //         for (Map<String, Object> bill : filteredBills) {
    //             String billId = (String) bill.get("id");
    //             String billType = (String) bill.get("billType");
    //             Double amount = (Double) bill.get("amount");
                
    //             Timestamp dueDate = (Timestamp) bill.get("dueDate");
    //             String formattedDueDate = dueDate != null ? 
    //                     LocalDate.parse(dueDate.toString().substring(0, 10)).format(formatter) : "-";
                
    //             String status = (String) bill.get("status");
                
    //             Timestamp paidDate = (Timestamp) bill.get("paidDate");
    //             String formattedPaidDate = paidDate != null ? 
    //                     LocalDate.parse(paidDate.toString().substring(0, 10)).format(formatter) : "-";
                
    //             billsList.add(new Bill(billId, billType, amount, formattedDueDate, status, formattedPaidDate));
    //         }
            
    //         billsTableView.setItems(billsList);
            
    //     } catch (SQLException e) {
    //         showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter bills: " + e.getMessage());
    //         e.printStackTrace();
    //     }
    // }

    @FXML
private void handleSearchButton(ActionEvent event) {
    String selectedMonth = monthComboBox.getValue();
    String selectedYear = yearComboBox.getValue();
    
    if (selectedMonth == null || selectedYear == null) {
        showAlert(Alert.AlertType.WARNING, "Warning", "Please select both month and year");
        return;
    }
    
    try {
        int monthNumber = Month.valueOf(selectedMonth.toUpperCase()).getValue(); // Ensure uppercase
        int yearNumber = Integer.parseInt(selectedYear);
        
        List<Map<String, Object>> filteredBills = dbHandler.filterBillsByDate(
                blockName, flatNumber, monthNumber, yearNumber);
        
        billsList.clear();
        
        for (Map<String, Object> bill : filteredBills) {
            String billId = (String) bill.get("id");
            String billType = (String) bill.get("billType");
            Double amount = (Double) bill.get("amount");

            Timestamp dueDateTS = (Timestamp) bill.get("dueDate");
            Date dueDate = dueDateTS != null ? new Date(dueDateTS.getTime()) : null;

            Timestamp paidDateTS = (Timestamp) bill.get("paidDate");
            Date paidDate = paidDateTS != null ? new Date(paidDateTS.getTime()) : null;

            String status = bill.get("status") != null ? (String) bill.get("status") : "PENDING";

            billsList.add(new Bill(billId, blockName, flatNumber, billType, amount, dueDate, paidDate, status));
        }
        
        billsTableView.setItems(billsList);
        
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter bills: " + e.getMessage());
        e.printStackTrace();
    }
}

    
    @FXML
    private void handleClearFilterButton(ActionEvent event) {
        monthComboBox.setValue(null);
        yearComboBox.setValue(null);
        loadBillsData();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
}