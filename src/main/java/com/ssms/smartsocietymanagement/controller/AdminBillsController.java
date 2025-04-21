package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Bill;
import com.ssms.smartsocietymanagement.model.Flat;
import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.io.IOException;
import java.sql.Date;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Map;

public class AdminBillsController {

    @FXML
    private TableView<Bill> billsTableView;

    @FXML
    private TableColumn<Bill, String> idColumn;

    @FXML
    private TableColumn<Bill, String> blockColumn;

    @FXML
    private TableColumn<Bill, String> flatNumberColumn;

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
    private TableColumn<Bill, Void> actionColumn;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private ComboBox<String> yearComboBox;

    @FXML
    private ComboBox<String> blockComboBox;

    @FXML
    private ComboBox<String> flatComboBox;

    @FXML
    private BorderPane mainContentPane;

    @FXML
    private Button searchButton;

    @FXML
    private Button clearFilterButton;

    @FXML
    private ComboBox<String> billTypeName;

    @FXML
    private TextField amountTextField;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private ComboBox<String> createBillBlockComboBox;

    @FXML
    private ComboBox<String> createBillFlatComboBox;

    @FXML
    private Label totalBillsLabel;
    
    @FXML
    private Label pendingBillsLabel;
    
    @FXML
    private Label paidBillsLabel;

    private Admin currentAdmin;
    private DatabaseHandler dbHandler;
    private String userType;

    private ObservableList<Bill> billsList = FXCollections.observableArrayList();
    private ObservableList<String> blocksList = FXCollections.observableArrayList();

    public void initData(Admin admin) {
        this.currentAdmin = admin;
        this.userType = "admin";
        this.dbHandler = new DatabaseHandler();

        // Initialize month and year combo boxes
        initializeMonthYearComboBoxes();

        // Initialize block and flat combo boxes
        initializeBlockFlatComboBoxes();

        // Initialize columns
        initializeTableColumns();

        // Load bills data
        loadAllBillsData();

        // Add listener to block combo box for filtering flats
        addBlockComboBoxListeners();
    }

    private void initializeMonthYearComboBoxes() {
        ObservableList<String> months = FXCollections.observableArrayList();
        for (Month month : Month.values()) {
            months.add(month.toString());
        }
        monthComboBox.setItems(months);

        ObservableList<String> years = FXCollections.observableArrayList();
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            years.add(String.valueOf(i));
        }
        yearComboBox.setItems(years);
    }

    private void initializeBlockFlatComboBoxes() {
        try {
            List<Flat> flats = dbHandler.getAllFlats();

            // Collect unique block names
            for (Flat flat : flats) {
                String block = flat.getBlock_name();
                if (!blocksList.contains(block)) {
                    blocksList.add(block);
                }
            }

            blockComboBox.setItems(blocksList);
            createBillBlockComboBox.setItems(blocksList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load blocks and flats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addBlockComboBoxListeners() {
        blockComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateFlatComboBox(newVal, flatComboBox);
            }
        });

        createBillBlockComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateFlatComboBox(newVal, createBillFlatComboBox);
            }
        });
    }

    private void updateFlatComboBox(String selectedBlock, ComboBox<String> flatComboBox) {
        try {
            List<Flat> flats = dbHandler.getAllFlats();

            ObservableList<String> filteredFlats = FXCollections.observableArrayList();

            for (Flat flat : flats) {
                if (flat.getBlock_name().equals(selectedBlock)) {
                    filteredFlats.add(flat.getFlat_number());
                }
            }

            flatComboBox.setItems(filteredFlats);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load flats: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        blockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        flatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flatNumber"));
        billTypeColumn.setCellValueFactory(new PropertyValueFactory<>("billType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paidDateColumn.setCellValueFactory(new PropertyValueFactory<>("paidDate"));

        // Set preferred widths for columns
        idColumn.setPrefWidth(120);
        blockColumn.setPrefWidth(90);
        flatNumberColumn.setPrefWidth(100);
        billTypeColumn.setPrefWidth(140);
        amountColumn.setPrefWidth(120);
        dueDateColumn.setPrefWidth(120);
        statusColumn.setPrefWidth(100);
        paidDateColumn.setPrefWidth(120);

        for (TableColumn<Bill, ?> column : billsTableView.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        billsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        // Add action button for marking bills as paid
        actionColumn.setCellFactory(param -> new TableCell<Bill, Void>() {
            private final Button markPaidButton = new Button("Mark Paid");

            {
                markPaidButton.setOnAction(event -> {
                    Bill bill = getTableView().getItems().get(getIndex());
                    if (bill.getStatus().equals("PENDING")) {
                        try {
                            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                            boolean success = dbHandler.updateBillStatus(bill.getId(), currentTime);

                            if (success) {
                                showAlert(Alert.AlertType.INFORMATION, "Success", "Bill marked as paid successfully!");
                                loadAllBillsData(); // Refresh the table
                                updateBillStatistics();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to mark bill as paid.");
                            }
                        } catch (SQLException e) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        showAlert(Alert.AlertType.INFORMATION, "Info", "This bill is already paid.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Bill bill = getTableView().getItems().get(getIndex());
                    if (bill.getStatus().equals("PENDING")) {
                        setGraphic(markPaidButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void loadAllBillsData() {
        try {
            List<Map<String, Object>> bills = dbHandler.getAllBills();
            updateBillsList(bills);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load bills: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBillsList(List<Map<String, Object>> bills) {
        billsList.clear();

        for (Map<String, Object> bill : bills) {
            String billId = (String) bill.get("id");
            String block = (String) bill.get("block");
            String flatNumber = (String) bill.get("flatNumber");
            String billType = (String) bill.get("billType");
            Double amount = (Double) bill.get("amount");
            String status = (String) bill.get("status");

            Timestamp dueDate = (Timestamp) bill.get("dueDate");
            Date sqlDueDate = dueDate != null ? new Date(dueDate.getTime()) : null;

            Timestamp paidDate = (Timestamp) bill.get("paidDate");
            Date sqlPaidDate = paidDate != null ? new Date(paidDate.getTime()) : null;

            billsList.add(new Bill(billId, block, flatNumber, billType, amount, sqlDueDate, sqlPaidDate, status));
        }

        billsTableView.setItems(billsList);
        updateBillStatistics();
    }

    private void updateBillStatistics() {
        int totalBills = billsList.size();
        int pendingBills = 0;
        int paidBills = 0;
        
        for (Bill bill : billsList) {
            if (bill.getStatus().equals("PENDING")) {
                pendingBills++;
            } else if (bill.getStatus().equals("PAID")) {
                paidBills++;
            }
        }
        
        totalBillsLabel.setText("Total Bills: " + totalBills);
        pendingBillsLabel.setText("Pending Bills: " + pendingBills);
        paidBillsLabel.setText("Paid Bills: " + paidBills);
    }


    @FXML
public void handleFilterButton(ActionEvent event) {
    // This should be the same implementation as your handleSearchButton
    String selectedMonth = monthComboBox.getValue();
    String selectedYear = yearComboBox.getValue();
    String selectedBlock = blockComboBox.getValue();
    String selectedFlat = flatComboBox.getValue();
    
    try {
        List<Map<String, Object>> filteredBills;
        
        // Based on which filters are applied, call the appropriate method
        if (selectedMonth != null && selectedYear != null) {
            int monthNumber = Month.valueOf(selectedMonth).getValue();
            int yearNumber = Integer.parseInt(selectedYear);
            
            if (selectedBlock != null && selectedFlat != null) {
                // Filter by month, year, block, and flat
                filteredBills = dbHandler.filterBillsByDate(selectedBlock, selectedFlat, monthNumber, yearNumber);
            } else {
                // Filter by month and year only
                filteredBills = dbHandler.filterAllBillsByDate(monthNumber, yearNumber);
            }
        } else if (selectedBlock != null && selectedFlat != null) {
            // Filter by block and flat only
            filteredBills = dbHandler.filterBillsByFlat(selectedBlock, selectedFlat);
        } else {
            // No filters applied or incomplete filters
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select valid filter criteria");
            return;
        }
        
        updateBillsList(filteredBills);
        
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to filter bills: " + e.getMessage());
        e.printStackTrace();
    }
}

@FXML 
public void handleResetFilterButton(ActionEvent event) {
    // Clear all filter selections
    monthComboBox.setValue(null);
    yearComboBox.setValue(null);
    blockComboBox.setValue(null);
    flatComboBox.setValue(null);
    
    // Reload all bills
    loadAllBillsData();
}


    @FXML
    private void handleCreateBillButton(ActionEvent event) {
        String billType = billTypeName.getValue();
        String amountText = amountTextField.getText().trim();
        LocalDate dueDate = dueDatePicker.getValue();
        String block = createBillBlockComboBox.getValue();
        String flatNumber = createBillFlatComboBox.getValue();

        // Validate input
        if (billType.isEmpty() || amountText.isEmpty() || dueDate == null ||
                block == null || flatNumber == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all fields");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Amount must be greater than zero");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter a valid amount");
            return;
        }

        try {
            // Convert LocalDate to Timestamp
            Timestamp dueDateTimestamp = Timestamp.valueOf(dueDate.atStartOfDay());

            boolean success = dbHandler.createBill(block, flatNumber, billType, amount, dueDateTimestamp);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bill created successfully!");

                // Clear input fields
                billTypeName.setValue(null);
                amountTextField.clear();
                dueDatePicker.setValue(null);
                createBillBlockComboBox.setValue(null);
                createBillFlatComboBox.setValue(null);

                // Refresh the table
                loadAllBillsData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create bill");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleClearButton(ActionEvent event) {
        // Clear input fields without reloading the entire view
        billTypeName.setValue(null);
        amountTextField.clear();
        dueDatePicker.setValue(null);
        createBillBlockComboBox.setValue(null);
        createBillFlatComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}