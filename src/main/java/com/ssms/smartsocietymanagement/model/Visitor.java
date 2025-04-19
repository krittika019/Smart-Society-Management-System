package com.ssms.smartsocietymanagement.model;

import java.sql.Timestamp;


public class Visitor {
    private String id;
    private String name;
    private String mobileNumber;
    private String purpose;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private String block ;
    private String flatNumber;
    private String status; // PENDING, APPROVED, REJECTED

    public Visitor(String id, String name, String mobileNumber, String purpose, Timestamp entryTime, String block,
                   String flatNumber, String status) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.purpose = purpose;
        this.entryTime = entryTime;
        this.block = block;
        this.flatNumber = flatNumber;
        this.status = status;
    }

    public Visitor(String id, String name, String mobileNumber, String purpose, Timestamp entryTime, Timestamp exitTime, String block, String flatNumber, String status) {
        this.id = id;
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.purpose = purpose;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.block = block;
        this.flatNumber = flatNumber;
        this.status = status;
    }

    public String getBlock() {return block;}
    public void setBlock(String block) {this.block = block;}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public Timestamp getEntryTime() { return entryTime; }
    public void setEntryTime(Timestamp entryTime) { this.entryTime = entryTime; }

    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
