package com.ssms.smartsocietymanagement.controller;

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
import java.util.ResourceBundle;

public class ResidentVisitorsController implements Initializable {

    @FXML private TableView<Visitor> pendingVisitorsTable;
    @FXML private TableColumn<Visitor, String> pendingIdColumn;
    @FXML private TableColumn<Visitor, String> pendingNameColumn;
    @FXML private TableColumn<Visitor, String> pendingMobileColumn;
    @FXML private TableColumn<Visitor, String> pendingPurposeColumn;
    @FXML private TableColumn<Visitor, String> pendingEntryTimeColumn;

    @FXML private TableView<Visitor> visitorHistoryTable;
    @FXML private TableColumn<Visitor, String> historyIdColumn;
    @FXML private TableColumn<Visitor, String> historyNameColumn;
    @FXML private TableColumn<Visitor, String> historyMobileColumn;
    @FXML private TableColumn<Visitor, String> historyPurposeColumn;
    @FXML private TableColumn<Visitor, String> historyEntryTimeColumn;
    @FXML private TableColumn<Visitor, String> historyExitTimeColumn;
    @FXML private TableColumn<Visitor, String> historyStatusColumn;

    @FXML private Button approveVisitorBtn;
    @FXML private Button rejectVisitorBtn;
    @FXML private Button refreshPendingBtn;
    @FXML private Button refreshHistoryBtn;

    private ObservableList<Visitor> pendingVisitors = FXCollections.observableArrayList();

    private ObservableList<Visitor> allVisitors = FXCollections.observableArrayList();

    private Resident currentResident;
    private String flatBlock;
    private String flatNumber;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupPendingVisitorsTable();
        setupVisitorHistoryTable();

    }

    public void initData(Resident resident, String block, String flatNumber) {
        this.currentResident = resident;
        this.flatBlock = block;
        this.flatNumber = flatNumber;

        loadPendingVisitors();
        loadAllVisitors();
    }

    private void setupPendingVisitorsTable() {
        pendingIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pendingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pendingMobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        pendingPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        pendingEntryTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getEntryTime();
            return new SimpleStringProperty(timestamp != null ? dateFormat.format(timestamp) : "");
        });

        pendingIdColumn.setPrefWidth(100);
        pendingNameColumn.setPrefWidth(120);
        pendingMobileColumn.setPrefWidth(140);
        pendingPurposeColumn.setPrefWidth(180);
        pendingEntryTimeColumn.setPrefWidth(110);

        for (TableColumn<Visitor, ?> column : pendingVisitorsTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        pendingVisitorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        pendingVisitorsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            approveVisitorBtn.setDisable(!hasSelection);
            rejectVisitorBtn.setDisable(!hasSelection);
        });

        approveVisitorBtn.setDisable(true);
        rejectVisitorBtn.setDisable(true);

    }

    private void setupVisitorHistoryTable() {
        historyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        historyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        historyMobileColumn.setCellValueFactory(new PropertyValueFactory<>("mobileNumber"));
        historyPurposeColumn.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        historyEntryTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getEntryTime();
            return new SimpleStringProperty(timestamp != null ? dateFormat.format(timestamp) : "");
        });
        historyExitTimeColumn.setCellValueFactory(cellData -> {
            Timestamp timestamp = cellData.getValue().getExitTime();
            return new SimpleStringProperty(timestamp != null ? dateFormat.format(timestamp) : "-");
        });
        historyStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        historyIdColumn.setPrefWidth(100);
        historyNameColumn.setPrefWidth(120);
        historyMobileColumn.setPrefWidth(130);
        historyPurposeColumn.setPrefWidth(170);
        historyEntryTimeColumn.setPrefWidth(110);
        historyExitTimeColumn.setPrefWidth(110);
        historyStatusColumn.setPrefWidth(100);

        for (TableColumn<Visitor, ?> column : visitorHistoryTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        visitorHistoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    private void loadPendingVisitors() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            pendingVisitors.clear();
            pendingVisitors.addAll(dbHandler.getPendingVisitorsByFlat(flatBlock, flatNumber));
            pendingVisitorsTable.setItems(FXCollections.observableArrayList(pendingVisitors));
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load pending visitors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllVisitors() {
        try {
            DatabaseHandler dbHandler = new DatabaseHandler();
            allVisitors.clear();
            allVisitors.addAll(dbHandler.getVisitorsByFlat(flatBlock, flatNumber));
            visitorHistoryTable.setItems(FXCollections.observableArrayList(allVisitors));
            dbHandler.closeConnection();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load visitor history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleApproveVisitor(ActionEvent event) {
        Visitor selectedVisitor = pendingVisitorsTable.getSelectionModel().getSelectedItem();
        if (selectedVisitor != null) {
            try {
                DatabaseHandler dbHandler = new DatabaseHandler();
                boolean success = dbHandler.updateVisitorStatus(selectedVisitor.getId(), "APPROVED");
                dbHandler.closeConnection();

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Visitor request approved successfully.");
                    loadPendingVisitors();
                    loadAllVisitors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve visitor request.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error approving visitor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRejectVisitor(ActionEvent event) {
        Visitor selectedVisitor = pendingVisitorsTable.getSelectionModel().getSelectedItem();
        if (selectedVisitor != null) {
            try {
                DatabaseHandler dbHandler = new DatabaseHandler();
                boolean success = dbHandler.updateVisitorStatus(selectedVisitor.getId(), "REJECTED");
                dbHandler.closeConnection();

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Visitor request rejected successfully.");
                    loadPendingVisitors();
                    loadAllVisitors();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject visitor request.");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error rejecting visitor: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRefreshPending(ActionEvent event) {
        loadPendingVisitors();
    }

    @FXML
    private void handleRefreshHistory(ActionEvent event) {
        loadAllVisitors();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
