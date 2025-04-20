package com.ssms.smartsocietymanagement.model;

import java.util.Date;

public class Amenity {
    private String amenityId;
    private String name;
    private String description;
    private String location;
    private String openingHours;
    private String status;
    private String createdBy;
    private Date createdDate;
    private String createdByName; // For display purposes

    public Amenity(String amenityId, String name, String description, String location, 
                   String openingHours, String status, String createdBy, Date createdDate) {
        this.amenityId = amenityId;
        this.name = name;
        this.description = description;
        this.location = location;
        this.openingHours = openingHours;
        this.status = status;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    // Constructor with creator name
    public Amenity(String amenityId, String name, String description, String location, 
                   String openingHours, String status, String createdBy, Date createdDate, 
                   String createdByName) {
        this(amenityId, name, description, location, openingHours, status, createdBy, createdDate);
        this.createdByName = createdByName;
    }

    // Getters and setters
    public String getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(String amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }
}