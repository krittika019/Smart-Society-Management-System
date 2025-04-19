package com.ssms.smartsocietymanagement.model;

public class Resident {
    private String res_id;
    private String name;
    private String res_username;
    private String res_password;
    private String res_email;
    private String ownership_status;
    private String phoneNumber;
    private String Approval_status;

    public Resident(String id, String name, String username, String password, String email, String ownership_status, String phoneNumber, String Approval_status) {
        this.res_id = id;
        this.name = name;
        this.res_username = username;
        this.res_password = password;
        this.res_email = email;
        this.ownership_status = ownership_status;
        this.phoneNumber = phoneNumber;
        this.Approval_status = Approval_status ;
    }

    // Getters and Setters
    public String getId() {
        return res_id;
    }

    public void setId(String id) {
        this.res_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return res_username;
    }

    public void setUsername(String username) {
        this.res_username = username;
    }

    public String getPassword() {
        return res_password;
    }

    public void setPassword(String password) {
        this.res_password = password;
    }

    public String getEmail() {
        return res_email;
    }

    public void setEmail(String email) {
        this.res_email = email;
    }

    public String getOwnership_status() {
        return ownership_status;
    }

    public String getApprovalStatus() {
        return Approval_status;
    }

    public void setApproval_status(String Approval_status) {
        this.Approval_status = Approval_status;
    }

    public void setOwnership_status(String ownership_status) {
        this.ownership_status = ownership_status;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}