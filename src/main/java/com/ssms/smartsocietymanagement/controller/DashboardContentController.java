package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.*;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;
import com.ssms.smartsocietymanagement.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardContentController implements Initializable {

    // Common database handler
    private DatabaseHandler dbHandler = new DatabaseHandler();

    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    // Session management
    private String currentUsername;
    private String userType;
    private Resident currentResident;
    private Admin currentAdmin;
    private Flat currentFlat;

    // FXML components - Resident Dashboard
    @FXML private VBox residentDashboard;
    @FXML private Label residentPendingBillsCount;
    @FXML private Label residentPendingComplaintsCount;
    @FXML private Label residentVisitorRequestsCount;
    @FXML private Label residentNextPaymentDue;

    // Resident Notice Table
    @FXML private TableView<Notice> residentNoticesTable;
    @FXML private TableColumn<Notice, Date> residentNoticeDate;
    @FXML private TableColumn<Notice, String> residentNoticeTitle;
    @FXML private TableColumn<Notice, String> residentNoticeBy;

    // Resident Visitors Table
    @FXML private TableView<Visitor> residentVisitorsTable;
    @FXML private TableColumn<Visitor, Timestamp> residentVisitorDate;
    @FXML private TableColumn<Visitor, String> residentVisitorName;
    @FXML private TableColumn<Visitor, String> residentVisitorPurpose;
    @FXML private TableColumn<Visitor, String> residentVisitorStatus;


    // FXML components - Admin Dashboard
    @FXML private VBox adminDashboard;
    @FXML private Label adminPendingApprovalsCount;
    @FXML private Label adminOpenComplaintsCount;
    @FXML private Label adminVisitorsInsideCount;
    @FXML private Label adminPendingBillsCount;

    // Admin Complaints Table
    @FXML private TableView<Map<String, Object>> adminComplaintsTable;
    @FXML private TableColumn<Map<String, Object>, Timestamp> adminComplaintDate;
    @FXML private TableColumn<Map<String, Object>, String> adminComplaintSubject;
    @FXML private TableColumn<Map<String, Object>, String> adminComplaintResident;
    @FXML private TableColumn<Map<String, Object>, String> adminComplaintStatus;


    // Admin Approvals Table
    @FXML private TableView<ResidentApprovalViewModel> adminApprovalsTable;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalName;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalEmail;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalPhone;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalBlock;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalFlat;
    @FXML private TableColumn<ResidentApprovalViewModel, String> adminApprovalOwnership;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String currentUsername = SessionManager.getCurrentUserId();
        String userType = SessionManager.getCurrentUserType();

        try {
            if ("resident".equals(userType)) {
                // Initialize for resident
                currentResident = dbHandler.getResidentByUsername(currentUsername);
                if (currentResident != null) {
                    currentFlat = dbHandler.getFlatByResidentId(currentResident.getId());
                    if (currentFlat != null) {
                        initializeResidentDashboard();
                        residentDashboard.setVisible(true);
                    }
                }
            } else if ("admin".equals(userType)) {
                // Initialize for admin
                currentAdmin = dbHandler.getAdminByUsername(currentUsername);
                if (currentAdmin != null) {
                    initializeAdminDashboard();
                    adminDashboard.setVisible(true);
                }
            }
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Initialize Resident Dashboard
    private void initializeResidentDashboard() throws SQLException {
        if (currentFlat == null) return;

        // Initialize tables
        initializeResidentNoticesTable();
        initializeResidentVisitorsTable();

        // Load data
        loadResidentNotices();
        loadResidentVisitors();

        // Update summary counts
        updateResidentSummaries();
    }

    // Initialize Admin Dashboard
    private void initializeAdminDashboard() throws SQLException {
        // Initialize tables
        initializeAdminComplaintsTable();
        initializeAdminApprovalsTable();


        loadAdminComplaints();
        loadAdminApprovals();

        // Update summary counts
        updateAdminSummaries();
    }

    // Initialize Resident Tables
    private void initializeResidentNoticesTable() {
        residentNoticeDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        residentNoticeTitle.setCellValueFactory(new PropertyValueFactory<>("noticeTitle"));
        residentNoticeBy.setCellValueFactory(new PropertyValueFactory<>("adminName"));

        residentNoticeDate.setPrefWidth(100);
        residentNoticeTitle.setPrefWidth(160);
        residentNoticeBy.setPrefWidth(120);

        for (TableColumn<Notice, ?> column : residentNoticesTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        residentNoticesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Format date column
        residentNoticeDate.setCellFactory(column -> new TableCell<Notice, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });
    }

    private void initializeResidentVisitorsTable() {
        residentVisitorDate.setCellValueFactory(new PropertyValueFactory<>("entryTime"));
        residentVisitorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        residentVisitorPurpose.setCellValueFactory(new PropertyValueFactory<>("purpose"));
        residentVisitorStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        residentVisitorDate.setPrefWidth(110);
        residentVisitorName.setPrefWidth(120);
        residentVisitorPurpose.setPrefWidth(180);
        residentVisitorStatus.setPrefWidth(100);

        for (TableColumn<Visitor, ?> column : residentVisitorsTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        residentVisitorsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Format date column
        residentVisitorDate.setCellFactory(column -> new TableCell<Visitor, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(dateTimeFormat.format(item));
                }
            }
        });

        // Format status column with colors
        residentVisitorStatus.setCellFactory(column -> new TableCell<Visitor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("APPROVED".equals(item)) {
                        setStyle("-fx-text-fill: green;");
                    } else if ("PENDING".equals(item)) {
                        setStyle("-fx-text-fill: orange;");
                    } else if ("REJECTED".equals(item)) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void initializeAdminComplaintsTable() {
        adminComplaintDate.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>((Timestamp) cellData.getValue().get("dateTime")));
        adminComplaintSubject.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("subject")));
        adminComplaintResident.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("residentName")));
        adminComplaintStatus.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty((String) cellData.getValue().get("status")));
        adminComplaintDate.setPrefWidth(80);
        adminComplaintSubject.setPrefWidth(80);
        adminComplaintResident.setPrefWidth(110);
        adminComplaintStatus.setPrefWidth(110);

        for (TableColumn<Map<String, Object>, ?> column : adminComplaintSubject.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        adminComplaintsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Format date column
        adminComplaintDate.setCellFactory(column -> new TableCell<Map<String, Object>, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Format status column with colors
        adminComplaintStatus.setCellFactory(column -> new TableCell<Map<String, Object>, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("RESOLVED".equals(item)) {
                        setStyle("-fx-text-fill: green;");
                    } else if ("PENDING".equals(item)) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }


    private void initializeAdminApprovalsTable() {
        adminApprovalName.setCellValueFactory(new PropertyValueFactory<>("name"));
        adminApprovalEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        adminApprovalPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        adminApprovalBlock.setCellValueFactory(new PropertyValueFactory<>("blockName"));
        adminApprovalFlat.setCellValueFactory(new PropertyValueFactory<>("flatNumber"));
        adminApprovalOwnership.setCellValueFactory(new PropertyValueFactory<>("ownershipStatus"));

        adminApprovalName.setPrefWidth(120);
        adminApprovalEmail.setPrefWidth(130);
        adminApprovalPhone.setPrefWidth(120);
        adminApprovalBlock.setPrefWidth(80);
        adminApprovalFlat.setPrefWidth(80);
        adminApprovalOwnership.setPrefWidth(120);

        for (TableColumn<ResidentApprovalViewModel, ?> column : adminApprovalsTable.getColumns()) {
            column.setStyle("-fx-alignment: CENTER; -fx-text-alignment: CENTER;");
        }
        // Set column resize policy
        adminApprovalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Load Resident Data
    private void loadResidentNotices() throws SQLException {
        List<Notice> notices = dbHandler.getAllNotices();
        // Show only top 3 notices
        if (notices.size() > 3) {
            notices = notices.subList(0, 3);
        }
        residentNoticesTable.setItems(FXCollections.observableArrayList(notices));
    }

    private void loadResidentVisitors() throws SQLException {
        if (currentFlat != null) {
            List<Visitor> visitors = dbHandler.getVisitorsByFlat(currentFlat.getBlock_name(), currentFlat.getFlat_number());
            // Show only top 3 recent visitors
            if (visitors.size() > 3) {
                visitors = visitors.subList(0, 3);
            }
            residentVisitorsTable.setItems(FXCollections.observableArrayList(visitors));
        }
    }

    private void updateResidentSummaries() throws SQLException {
        if (currentFlat == null || currentResident == null) return;

        // Count pending bills
        List<Map<String, Object>> bills = dbHandler.getBillsByFlat(currentFlat.getBlock_name(), currentFlat.getFlat_number());
        long pendingBillsCount = bills.stream()
                .filter(bill -> "PENDING".equals(bill.get("status")))
                .count();
        residentPendingBillsCount.setText(String.valueOf(pendingBillsCount));

        // Count pending complaints
        List<Map<String, Object>> complaints = dbHandler.getComplaintsByResident(currentResident.getId());
        long pendingComplaintsCount = complaints.stream()
                .filter(complaint -> "PENDING".equals(complaint.get("status")))
                .count();
        residentPendingComplaintsCount.setText(String.valueOf(pendingComplaintsCount));

        // Count pending visitor requests
        List<Visitor> visitors = dbHandler.getPendingVisitorsByFlat(currentFlat.getBlock_name(), currentFlat.getFlat_number());
        residentVisitorRequestsCount.setText(String.valueOf(visitors.size()));

        // Check for next payment due
        bills.stream()
                .filter(bill -> "PENDING".equals(bill.get("status")))
                .min(Comparator.comparing(bill -> (Timestamp) bill.get("dueDate")))
                .ifPresentOrElse(
                        nextBill -> residentNextPaymentDue.setText(dateFormat.format((Timestamp) nextBill.get("dueDate"))),
                        () -> residentNextPaymentDue.setText("No dues")
                );
    }

    private void loadAdminComplaints() throws SQLException {
        List<Map<String, Object>> complaints = dbHandler.getComplaintsByStatus("PENDING");
        // Show only top 5 recent complaints
        if (complaints.size() > 5) {
            complaints = complaints.subList(0, 5);
        }
        adminComplaintsTable.setItems(FXCollections.observableArrayList(complaints));
    }

    private void loadAdminApprovals() throws SQLException {
        List<Resident> pendingResidents = dbHandler.getPendingResidents();
        List<ResidentApprovalViewModel> viewModels = new ArrayList<>();

        for (Resident resident : pendingResidents) {
            String residentId = resident.getId();
            Flat flat = dbHandler.getFlatByResidentId(residentId);

            if (flat != null) {
                ResidentApprovalViewModel viewModel = new ResidentApprovalViewModel(
                        resident.getName(),
                        resident.getEmail(),
                        resident.getPhoneNumber(),
                        flat.getBlock_name(),
                        flat.getFlat_number(),
                        resident.getOwnership_status()
                );
                viewModels.add(viewModel);
            }
        }

        adminApprovalsTable.setItems(FXCollections.observableArrayList(viewModels));
    }

    private void updateAdminSummaries() throws SQLException {
        // Count pending approvals
        List<Resident> pendingResidents = dbHandler.getPendingResidents();
        adminPendingApprovalsCount.setText(String.valueOf(pendingResidents.size()));

        // Count open complaints
        List<Map<String, Object>> openComplaints = dbHandler.getComplaintsByStatus("PENDING");
        adminOpenComplaintsCount.setText(String.valueOf(openComplaints.size()));

        // Count visitors inside
        List<Visitor> allVisitors = dbHandler.getAllVisitors();
        long visitorsInside = allVisitors.stream()
                .filter(visitor -> "APPROVED".equals(visitor.getStatus()) && visitor.getExitTime() == null)
                .count();
        adminVisitorsInsideCount.setText(String.valueOf(visitorsInside));

        // Count pending bills
        List<Map<String, Object>> allBills = dbHandler.getAllBills();
        long pendingBills = allBills.stream()
                .filter(bill -> "PENDING".equals(bill.get("status")))
                .count();
        adminPendingBillsCount.setText(String.valueOf(pendingBills));
    }

    // Helper methods
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // View model for pending resident approvals
    public static class ResidentApprovalViewModel {
        private String name;
        private String email;
        private String phoneNumber;
        private String blockName;
        private String flatNumber;
        private String ownershipStatus;

        public ResidentApprovalViewModel(String name, String email, String phoneNumber,
                                         String blockName, String flatNumber, String ownershipStatus) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.blockName = blockName;
            this.flatNumber = flatNumber;
            this.ownershipStatus = ownershipStatus;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getBlockName() { return blockName; }
        public void setBlockName(String blockName) { this.blockName = blockName; }

        public String getFlatNumber() { return flatNumber; }
        public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

        public String getOwnershipStatus() { return ownershipStatus; }
        public void setOwnershipStatus(String ownershipStatus) { this.ownershipStatus = ownershipStatus; }
    }
}
