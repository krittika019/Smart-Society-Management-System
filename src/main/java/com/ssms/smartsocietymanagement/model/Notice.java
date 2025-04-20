package com.ssms.smartsocietymanagement.model;

import java.sql.Date;

public class Notice {
    private String noticeId;
    private String noticeTitle;
    private Date createdDate;
    private String description;
    private String createdBy;
    private String adminName; // To store the name of the admin who created the notice

    public Notice(String noticeId, String noticeTitle, Date createdDate, String description, String createdBy) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.createdDate = createdDate;
        this.description = description;
        this.createdBy = createdBy;
    }

    public Notice(String noticeId, String noticeTitle, Date createdDate, String description, String createdBy, String adminName) {
        this(noticeId, noticeTitle, createdDate, description, createdBy);
        this.adminName = adminName;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
}