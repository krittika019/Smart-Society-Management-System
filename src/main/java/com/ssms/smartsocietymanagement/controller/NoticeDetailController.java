package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Notice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;

public class NoticeDetailController {

    @FXML
    private Label noticeIdLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label descriptionLabel;

    private Notice notice;

    public void setNotice(Notice notice) {
        this.notice = notice;
        displayNoticeDetails();
    }

    private void displayNoticeDetails() {
        if (notice != null) {
            noticeIdLabel.setText(notice.getNoticeId());

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            dateLabel.setText(formatter.format(notice.getCreatedDate()));

            titleLabel.setText(notice.getNoticeTitle());
            descriptionLabel.setText(notice.getDescription());
        }
    }

    @FXML
    private void handleBackButton() {
        // Close the window
        Stage stage = (Stage) noticeIdLabel.getScene().getWindow();
        stage.close();
    }
}