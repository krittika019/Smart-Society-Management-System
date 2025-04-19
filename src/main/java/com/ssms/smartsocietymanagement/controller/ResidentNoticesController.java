package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Notice;
import com.ssms.smartsocietymanagement.model.Resident;
import com.ssms.smartsocietymanagement.util.DatabaseHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ResidentNoticesController {

    @FXML
    private VBox noticesContainer;

    private Resident currentResident;
    private DatabaseHandler dbHandler;

    @FXML
    public void initialize() {
        dbHandler = new DatabaseHandler();
        loadNotices();
    }

    public void initData(Resident resident) {
        this.currentResident = resident;
    }

    @FXML
    private void handleRefreshButton() {
        loadNotices();
    }

    private void loadNotices() {
        try {
            List<Notice> notices = dbHandler.getAllNotices();
            noticesContainer.getChildren().clear();

            if (notices.isEmpty()) {
                Label noNoticesLabel = new Label("No notices available at the moment.");
                noNoticesLabel.setFont(new Font(14));
                noticesContainer.getChildren().add(noNoticesLabel);
                return;
            }

            for (Notice notice : notices) {
                noticesContainer.getChildren().add(createNoticeCard(notice));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load notices: " + e.getMessage());
        }
    }

    private VBox createNoticeCard(Notice notice) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        card.setSpacing(5);

        Label titleLabel = new Label(notice.getNoticeTitle());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = dateFormat.format(notice.getCreatedDate());

        Label dateLabel = new Label("Posted on: " + formattedDate + " by " + notice.getAdminName());
        dateLabel.setStyle("-fx-text-fill: #666;");

        TextArea descriptionArea = new TextArea(notice.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setEditable(false);
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setStyle("-fx-control-inner-background: #f8f8f8; -fx-border-color: #eee;");
        VBox.setVgrow(descriptionArea, Priority.ALWAYS);

        card.getChildren().addAll(titleLabel, dateLabel, descriptionArea);

        return card;
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