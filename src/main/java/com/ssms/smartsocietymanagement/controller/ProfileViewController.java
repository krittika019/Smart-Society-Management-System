package com.ssms.smartsocietymanagement.controller;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Resident;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileViewController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label apartmentLabel;

    @FXML
    private Label phoneLabel;

    public void initData(Resident resident) {
        nameLabel.setText(resident.getName());
        usernameLabel.setText(resident.getUsername());
        emailLabel.setText(resident.getEmail());
        apartmentLabel.setText(resident.getOwnership_status());
        phoneLabel.setText(resident.getPhoneNumber());
    }

    public void initAdminData(Admin admin) {
        nameLabel.setText(admin.getName());
        usernameLabel.setText(admin.getAd_username());
        emailLabel.setText(admin.getAd_email());
        apartmentLabel.setText("Admin Office");
        phoneLabel.setText("Admin Contact");
    }
}