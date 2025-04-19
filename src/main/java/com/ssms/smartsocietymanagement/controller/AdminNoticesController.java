package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Notice;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.ActionEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class AdminNoticesController {

    @FXML
    private TextField noticeTitleField;

    @FXML
    private TextArea noticeDescriptionArea;

    @FXML
    private VBox noticesLogContainer;

    private Admin currentAdmin;
    private DatabaseHandler dbHandler;

    @FXML
    public void initialize() {
        dbHandler = new DatabaseHandler();
    }

    public void initData(Admin admin) {
        this.currentAdmin = admin;
        loadNoticesLog();
    }

    @FXML
    private void handleCreateNoticeButton(ActionEvent event) {
        String title = noticeTitleField.getText().trim();
        String description = noticeDescriptionArea.getText().trim();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Notice title cannot be empty.");
            return;
        }

        if (description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Notice description cannot be empty.");
            return;
        }

        try {
            boolean success = dbHandler.createNotice(title, description, currentAdmin.getId());

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Notice has been posted successfully.");
                noticeTitleField.clear();
                noticeDescriptionArea.clear();
                loadNoticesLog();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to post notice.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearButton(ActionEvent event) {
        noticeTitleField.clear();
        noticeDescriptionArea.clear();
    }

    @FXML
    private void handleRefreshLogButton(ActionEvent event) {
        loadNoticesLog();
    }

    private void loadNoticesLog() {
        try {
            List<Notice> notices = dbHandler.getAllNotices();
            noticesLogContainer.getChildren().clear();

            if (notices.isEmpty()) {
                Label noNoticesLabel = new Label("No notices available.");
                noNoticesLabel.setFont(new Font(14));
                noticesLogContainer.getChildren().add(noNoticesLabel);
                return;
            }

            for (Notice notice : notices) {
                noticesLogContainer.getChildren().add(createNoticeLogCard(notice));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load notices log: " + e.getMessage());
        }
    }

    private VBox createNoticeLogCard(Notice notice) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        card.setSpacing(5);

        HBox headerBox = new HBox();
        headerBox.setSpacing(10);

        Label titleLabel = new Label(notice.getNoticeTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff5252; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> handleDeleteNotice(notice.getNoticeId()));

        headerBox.getChildren().addAll(titleLabel, spacer, deleteButton);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = dateFormat.format(notice.getCreatedDate());

        Label dateLabel = new Label("Posted on: " + formattedDate + " by " + notice.getAdminName());
        dateLabel.setStyle("-fx-text-fill: #666;");

        TextArea descriptionArea = new TextArea(notice.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(4);
        descriptionArea.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: #eee;");
        VBox.setVgrow(descriptionArea, Priority.ALWAYS);

        card.getChildren().addAll(headerBox, dateLabel, descriptionArea);

        return card;
    }

    private void handleDeleteNotice(String noticeId) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this notice? This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = dbHandler.deleteNotice(noticeId);

                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Notice has been deleted successfully.");
                    loadNoticesLog();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete notice.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onClose() {
        if (dbHandler != null) {
            dbHandler.closeConnection();
        }
    }
}