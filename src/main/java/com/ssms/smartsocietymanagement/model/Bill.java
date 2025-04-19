package com.ssms.smartsocietymanagement.model;

import java.sql.Date;
import java.time.LocalDate;

public class Bill {
    private String id;
    private String block ;
    private String flatNumber;
    private String billType; // MAINTENANCE, WATER, ELECTRICITY, OTHERS
    private double amount;
    private Date dueDate;
    private Date paidDate;
    private String status; // PENDING, PAID

    public Bill(String id, String block, String flatNumber, String billType, double amount, Date dueDate,
                String status) {
        this.id = id;
        this.block = block ;
        this.flatNumber = flatNumber;
        this.billType = billType;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Bill(String id, String block, String flatNumber, String billType, double amount, Date dueDate, Date paidDate, String status) {
        this.id = id;
        this.block = block;
        this.flatNumber = flatNumber;
        this.billType = billType;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paidDate = paidDate;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBlock() {return block;}
    public void setBlock(String block) {this.block = block;}

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getPaidDate() { return paidDate; }
    public void setPaidDate(Date paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
