package com.ssms.smartsocietymanagement.model;

import java.sql.Timestamp;

public class Complaints {
    private String complaint_id;
    private String resident_id;
    private String subject;
    private String description;
    private Timestamp date_time;
    private String status; // "PENDING" or "RESOLVED"

    public Complaints(String complaint_id, String resident_id, String subject,
                     String description, Timestamp date_time, String status) {
        this.complaint_id = complaint_id;
        this.resident_id = resident_id;
        this.subject = subject;
        this.description = description;
        this.date_time = date_time;
        this.status = status;
    }

    // Getters and Setters
    public String getComplaint_id() {
        return complaint_id;
    }

    public void setComplaint_id(String complaint_id) {
        this.complaint_id = complaint_id;
    }

    public String getResident_id() {
        return resident_id;
    }

    public void setResident_id(String resident_id) {
        this.resident_id = resident_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate_time() {
        return date_time;
    }

    public void setDate_time(Timestamp date_time) {
        this.date_time = date_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
