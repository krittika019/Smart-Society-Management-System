package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Flat;
import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.model.Visitor;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminVisitorsController implements Initializable {

    @FXML private TextField visitorNameField;
    @FXML private TextField visitorMobileField;
    @FXML private TextField visitorPurposeField;
    @FXML private ComboBox<String> blockComboBox;
    @FXML private ComboBox<String> flatNumberComboBox;

    @FXML private TableView<Visitor> visitorsTable;
    @FXML private TableColumn<Visitor, String> idColumn;
    @FXML private TableColumn<Visitor, String> nameColumn;
    @FXML private TableColumn<Visitor, String> mobileColumn;
    @FXML private TableColumn<Visitor, String> purposeColumn;
    @FXML private TableColumn<Visitor, String> blockColumn;
    @FXML private TableColumn<Visitor, String> flatNumberColumn;
    @FXML private TableColumn<Visitor, String> entryTimeColumn;
    @FXML private TableColumn<Visitor, String> exitTimeColumn;
    @FXML private TableColumn<Visitor, String> statusColumn;

    @FXML private Button refreshBtn;
    @FXML private Button recordExitBtn;
    @FXML private Button registerVisitorBtn;
    @FXML private Button clearFormBtn;

    private ObservableList<Visitor> allVisitors = FXCollections.observableArrayList();
    private FilteredList<Visitor> filteredVisitors;

    private Admin currentAdmin;
    private Map<String, List<String>> blockToFlatsMap = new HashMap<>();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupVisitorsTable();

        filteredVisitors = new FilteredList<>(allVisitors, p -> true);
        visitorsTable.setItems(filteredVisitors);

        recordExitBtn.setDisable(true);

        // Setup block selection change listener
        blockComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                loadFlatNumbers(newValue);
            }
        });
    }

    public void initData(Admin admin) {
        this.currentAdmin = admin;
        loadAllVisitors();
        loadBlocks();
    }

    private void setupVisitorsTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        mobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        purposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        blockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        flatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("flatNumber"));
        entryTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getEntryTime();
            return new SimpleStringProperty(timestamp != null ? dateFormat.format(timestamp) : "");
        });
        exitTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getExitTime();
            return new SimpleStringProperty(timestamp != null ? dateFormat.format(timestamp) : "-");
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        idColumn.setPrefWidth(100);
        nameColumn.setPrefWidth(120);
        mobileColumn.setPrefWidth(140);
        purposeColumn.setPrefWidth(180);
        blockColumn.setPrefWidth(80);
        flatNumberColumn.setPrefWidth(80);
        entryTimeColumn.setPrefWidth(110);
        exitTimeColumn.setPrefWidth(110);

        for (TableColumn<Visitor, ?> column : visitorsTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        visitorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add color coding for status
        statusColumn.setCellFactory(column -> {
            return new TableCell<Visitor, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if ("APPROVED".equals(item)) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else if ("REJECTED".equals(item)) {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        } else if ("PENDING".equals(item)) {
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
        });

        visitorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                boolean canRecordExit = "APPROVED".equals(newSelection.getStatus()) && newSelection.getExitTime() == null;
                recordExitBtn.setDisable(!canRecordExit);
            } else {
                recordExitBtn.setDisable(true);
            }
        });
    }


    private void loadAllVisitors() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            allVisitors.clear();
            allVisitors.addAll(dbHandler.getAllVisitors());
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load visitors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBlocks() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            List<Flat> allFlats = dbHandler.getAllFlats();
            dbHandler.closeConnection();

            Set<String> blocks = new HashSet<>();
            blockToFlatsMap.clear();

            for (Flat flat : allFlats) {
                blocks.add(flat.getBlock_name());

                if (!blockToFlatsMap.containsKey(flat.getBlock_name())) {
                    blockToFlatsMap.put(flat.getBlock_name(), new ArrayList<>());
                }
                blockToFlatsMap.get(flat.getBlock_name()).add(flat.getFlat_number());
            }

            ObservableList<String> blocksList = FXCollections.observableArrayList(new ArrayList<>(blocks));
            FXCollections.sort(blocksList);
            blockComboBox.setItems(blocksList);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load blocks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadFlatNumbers(String selectedBlock) {
        if (blockToFlatsMap.containsKey(selectedBlock)) {
            List<String> flatNumbers = blockToFlatsMap.get(selectedBlock);
            Collections.sort(flatNumbers);
            flatNumberComboBox.setItems(FXCollections.observableArrayList(flatNumbers));
            flatNumberComboBox.getSelectionModel().selectFirst();
        } else {
            flatNumberComboBox.getItems().clear();
        }
    }

    @FXML
    private void handleRegisterVisitor(ActionEvent event) {
        if (validateVisitorForm()) {
            try {
                String visitorName = visitorNameField.getText().trim();
                String visitorMobile = visitorMobileField.getText().trim();
                String visitorPurpose = visitorPurposeField.getText().trim();
                String selectedBlock = blockComboBox.getValue();
                String selectedFlatNumber = flatNumberComboBox.getValue();

                if(!validateName(visitorName)){
                    showAlert(Alert.AlertType.ERROR, "Signup Error", "Please fill correct name");
                    return;
                }
                if(!validatePhonenumber(visitorMobile)){
                    showAlert(Alert.AlertType.ERROR, "Signup Error", "Please fill correct phonenumber");
                    return;
                }

                // Generate unique ID
                String visitorId = generatevisitorId();

                // Create visitor object
                Visitor newVisitor = new Visitor(
                        visitorId,
                        visitorName,
                        visitorMobile,
                        visitorPurpose,
                        new Timestamp(System.currentTimeMillis()),
                        selectedBlock,
                        selectedFlatNumber,
                        "PENDING"
                );

                DatabaseHandler dbHandler = new DatabaseHandler();
                boolean success = dbHandler.addVisitor(newVisitor);
                dbHandler.closeConnection();

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Visitor registered successfully! Waiting for resident approval.");
                    clearForm();
                    loadAllVisitors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to register visitor.");
                }

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error registering visitor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        visitorNameField.clear();
        visitorMobileField.clear();
        visitorPurposeField.clear();
        blockComboBox.getSelectionModel().clearSelection();
        flatNumberComboBox.getItems().clear();
    }

    private boolean validateVisitorForm() {
        StringBuilder errorMsg = new StringBuilder();

        if (visitorNameField.getText().trim().isEmpty()) {
            errorMsg.append("- Visitor name is required\n");
        }

        if (visitorMobileField.getText().trim().isEmpty()) {
            errorMsg.append("- Mobile number is required\n");
        } else if (!visitorMobileField.getText().trim().matches("\\d+")) {
            errorMsg.append("- Mobile number should contain only digits\n");
        }

        if (visitorPurposeField.getText().trim().isEmpty()) {
            errorMsg.append("- Purpose of visit is required\n");
        }

        if (blockComboBox.getValue() == null) {
            errorMsg.append("- Block selection is required\n");
        }

        if (flatNumberComboBox.getValue() == null) {
            errorMsg.append("- Flat number selection is required\n");
        }

        if (errorMsg.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please correct the following errors:\n" + errorMsg.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleRecordExit(ActionEvent event) {
        Visitor selectedVisitor = visitorsTable.getSelectionModel().getSelectedItem();
        if (selectedVisitor != null && "APPROVED".equals(selectedVisitor.getStatus()) && selectedVisitor.getExitTime() == null) {
            try {
                DatabaseHandler dbHandler = new DatabaseHandler();
                boolean success = dbHandler.updateVisitorExitTime(selectedVisitor.getId(), new Timestamp(System.currentTimeMillis()));
                dbHandler.closeConnection();

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Visitor exit time recorded successfully.");
                    loadAllVisitors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to record visitor exit time.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error recording exit time: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllVisitors();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private String generatevisitorId() {
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        return "VIS" + timestamp + random;
    }

    private boolean validatePhonenumber(String phonenumber) {
        return phonenumber.matches("\\d{10}");
    }

    private boolean validateName(String name) {
        return name.matches("[a-zA-Z ]+");
    }
}