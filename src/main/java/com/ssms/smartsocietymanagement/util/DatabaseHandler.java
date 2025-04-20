package com.ssms.smartsocietymanagement.util;

import com.ssms.smartsocietymanagement.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ssms";
    private static final String USER = "root";

    // private static final String PASSWORD = "Pkg@121616";
    private static final String PASSWORD = "Kritika@2004";


    public DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addResident(Resident resident, Flat flat) throws SQLException {
        String query1 = "INSERT INTO residents (res_id, name, res_username, res_password, res_email, ownership_status, phonenumber, Approval_status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String query2 = "INSERT INTO flats (flat_id, block_name, flat_number) " +
                "VALUES (?, ?, ?)";
        String query3 = "INSERT INTO Resident_In_Flat (res_id, flat_id) " +
                "VALUES(?, ?)";

        try {
            PreparedStatement ps1 = connection.prepareStatement(query1);
            PreparedStatement ps2 = connection.prepareStatement(query2);
            PreparedStatement ps3 = connection.prepareStatement(query3);
            ps1.setString(1, resident.getId());
            ps1.setString(2, resident.getName());
            ps1.setString(3, resident.getUsername());
            ps1.setString(4, resident.getPassword());
            ps1.setString(5, resident.getEmail());
            ps1.setString(6, resident.getOwnership_status());
            ps1.setString(7, resident.getPhoneNumber());
            ps1.setString(8, resident.getApprovalStatus());
            ps2.setString(1, flat.getFlat_id());
            ps2.setString(2, flat.getBlock_name());
            ps2.setString(3, flat.getFlat_number());
            ps3.setString(1, resident.getId());
            ps3.setString(2, flat.getFlat_id());

            int result1 = ps1.executeUpdate();
            int result2 = ps2.executeUpdate();
            int result3 = ps3.executeUpdate();
            ps1.close();
            ps2.close();
            return result1 > 0 && result2 > 0 && result3 > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                return false;
            }
            throw e;
        }
    }

    public boolean validateResident(String username, String password) throws SQLException {
        String query = "SELECT * FROM residents WHERE res_username = ? AND res_password = ? AND approval_status = 'APPROVED'";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean valid = rs.next();

        rs.close();
        ps.close();
        return valid;
    }

    public boolean isResidentPendingApproval(String username, String password) throws SQLException {
        String query = "SELECT * FROM residents WHERE res_username = ? AND res_password = ? AND approval_status = 'PENDING'";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean isPending = rs.next();

        rs.close();
        ps.close();
        return isPending;
    }

    // New method to check if a resident is rejected
    public boolean isResidentRejected(String username, String password) throws SQLException {
        String query = "SELECT * FROM residents WHERE res_username = ? AND res_password = ? AND approval_status = 'REJECTED'";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean isRejected = rs.next();

        rs.close();
        ps.close();
        return isRejected;
    }

    public String getFlatIdByResidentId(String residentId) throws SQLException {
        String query = "SELECT flat_id FROM resident_in_flat WHERE res_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, residentId);

        ResultSet rs = ps.executeQuery();
        String flatId = null;

        if (rs.next()) {
            flatId = rs.getString("flat_id");
        }

        rs.close();
        ps.close();
        return flatId;
    }

    public boolean addVisitor(Visitor visitor) throws SQLException {
        String query = "INSERT INTO visitor (vis_id, name, phonenumber, purpose, entrytime, block_name, flat_number, approval_status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, visitor.getId());
            ps.setString(2, visitor.getName());
            ps.setString(3, visitor.getMobileNumber());
            ps.setString(4, visitor.getPurpose());
            ps.setTimestamp(5, visitor.getEntryTime());
            ps.setString(6, visitor.getBlock());
            ps.setString(7, visitor.getFlatNumber());
            ps.setString(8, visitor.getStatus());

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public boolean updateVisitorStatus(String visitorId, String status) throws SQLException {
        String query = "UPDATE visitor SET Approval_status = ? WHERE vis_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, visitorId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public boolean updateVisitorExitTime(String visitorId, Timestamp exitTime) throws SQLException {
        String query = "UPDATE visitor SET exittime = ? WHERE vis_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setTimestamp(1, exitTime);
            ps.setString(2, visitorId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public List<Visitor> getVisitorsByFlat(String block, String flatNumber) throws SQLException {
        String query = "SELECT * FROM visitor WHERE block_name = ? AND flat_number = ? ORDER BY entrytime DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, block);
            ps.setString(2, flatNumber);

            ResultSet rs = ps.executeQuery();
            List<Visitor> visitors = new ArrayList<>();

            while (rs.next()) {
                visitors.add(new Visitor(
                        rs.getString("vis_id"),
                        rs.getString("name"),
                        rs.getString("phonenumber"),
                        rs.getString("purpose"),
                        rs.getTimestamp("entrytime"),
                        rs.getTimestamp("exittime"),
                        rs.getString("block_name"),
                        rs.getString("flat_number"),
                        rs.getString("Approval_status")));
            }

            return visitors;
        }
    }

    public List<Visitor> getAllVisitors() throws SQLException {
        String query = "SELECT * FROM visitor ORDER BY entrytime DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            List<Visitor> visitors = new ArrayList<>();

            while (rs.next()) {
                visitors.add(new Visitor(
                        rs.getString("vis_id"),
                        rs.getString("name"),
                        rs.getString("phonenumber"),
                        rs.getString("purpose"),
                        rs.getTimestamp("entrytime"),
                        rs.getTimestamp("exittime"),
                        rs.getString("block_name"),
                        rs.getString("flat_number"),
                        rs.getString("Approval_status")));
            }

            return visitors;
        }
    }

    public List<Visitor> getPendingVisitorsByFlat(String block, String flatNumber) throws SQLException {
        String query = "SELECT * FROM visitor WHERE block_name = ? AND flat_number = ? AND Approval_status = 'PENDING' ORDER BY entrytime DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, block);
            ps.setString(2, flatNumber);

            ResultSet rs = ps.executeQuery();
            List<Visitor> visitors = new ArrayList<>();

            while (rs.next()) {
                visitors.add(new Visitor(
                        rs.getString("vis_id"),
                        rs.getString("name"),
                        rs.getString("phonenumber"),
                        rs.getString("purpose"),
                        rs.getTimestamp("entrytime"),
                        rs.getString("block_name"),
                        rs.getString("flat_number"),
                        rs.getString("Approval_status")));
            }

            return visitors;
        }
    }

    public Flat getFlatByResidentId(String residentId) throws SQLException {
        // First get the flat_id
        String flatId = getFlatIdByResidentId(residentId);
        if (flatId == null) {
            return null;
        }

        // Then get the flat details
        String query = "SELECT * FROM flats WHERE flat_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, flatId);

        ResultSet rs = ps.executeQuery();
        Flat flat = null;

        if (rs.next()) {
            flat = new Flat(
                    rs.getString("flat_id"),
                    rs.getString("block_name"),
                    rs.getString("flat_number"));
        }

        rs.close();
        ps.close();
        return flat;
    }

    public Visitor getVisitorById(String visitorId) throws SQLException {
        String query = "SELECT * FROM visitor WHERE vis_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, visitorId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Visitor(
                        rs.getString("vis_id"),
                        rs.getString("name"),
                        rs.getString("Phonenumber"),
                        rs.getString("purpose"),
                        rs.getTimestamp("entrytime"),
                        rs.getTimestamp("exittime"),
                        rs.getString("block_name"),
                        rs.getString("flat_number"),
                        rs.getString("Approval_status"));
            }
            return null;
        }
    }

    public List<Flat> getAllFlats() throws SQLException {
        String query = "SELECT * FROM flats ORDER BY block_name, flat_number";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<Flat> flats = new ArrayList<>();

        while (rs.next()) {
            flats.add(new Flat(
                    rs.getString("flat_id"),
                    rs.getString("block_name"),
                    rs.getString("flat_number")));
        }

        rs.close();
        stmt.close();
        return flats;
    }

    public boolean validateAdmin(String username, String password) throws SQLException {
        String query = "SELECT * FROM admins WHERE Ad_username = ? AND Ad_password = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean valid = rs.next();

        rs.close();
        ps.close();
        return valid;
    }

    public Resident getResidentByUsername(String username) throws SQLException {
        String query = "SELECT * FROM residents WHERE res_username = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        Resident resident = null;

        if (rs.next()) {
            resident = new Resident(
                    rs.getString("res_id"),
                    rs.getString("name"),
                    rs.getString("res_username"),
                    rs.getString("res_password"),
                    rs.getString("res_email"),
                    rs.getString("ownership_status"),
                    rs.getString("phonenumber"),
                    rs.getString("Approval_status"));
        }

        rs.close();
        ps.close();
        return resident;
    }

    public Admin getAdminByUsername(String username) throws SQLException {
        String query = "SELECT * FROM admins WHERE Ad_username = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        Admin admin = null;

        if (rs.next()) {
            admin = new Admin(
                    rs.getString("Ad_id"),
                    rs.getString("name"),
                    rs.getString("Ad_username"),
                    rs.getString("Ad_password"),
                    rs.getString("Ad_email"));
        }

        rs.close();
        ps.close();
        return admin;
    }

    public List<Resident> getPendingResidents() throws SQLException {
        String query = "SELECT * FROM residents WHERE Approval_status = 'PENDING'";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<Resident> pendingResidents = new ArrayList<>();

        while (rs.next()) {
            pendingResidents.add(new Resident(
                    rs.getString("res_id"),
                    rs.getString("name"),
                    rs.getString("res_username"),
                    rs.getString("res_password"),
                    rs.getString("res_email"),
                    rs.getString("ownership_status"),
                    rs.getString("phonenumber"),
                    rs.getString("approval_status")));
            System.out.println("res_id : " + rs.getString("res_id"));

        }

        rs.close();
        stmt.close();
        return pendingResidents;
    }

    public boolean resubmitResidentApplication(String residentId) throws SQLException {
        String query = "UPDATE residents SET approval_status = 'PENDING' WHERE res_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, residentId);

        int result = ps.executeUpdate();
        ps.close();
        return result > 0;
    }

    public boolean approveResident(String residentId) throws SQLException {
        String query = "UPDATE residents SET approval_status = 'APPROVED' WHERE res_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, residentId);

        int result = ps.executeUpdate();
        ps.close();
        return result > 0;
    }

    public boolean rejectResident(String residentId) throws SQLException {
        String query = "UPDATE residents SET approval_status = 'REJECTED' WHERE res_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, residentId);

        int result = ps.executeUpdate();
        ps.close();
        return result > 0;
    }

    // Add these methods to your DatabaseHandler.java class

    public String generateNoticeId() {
        // Get the count of notices to generate a new ID
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        return "NOC" + timestamp + random;

    }

    public boolean createNotice(String title, String description, String adminId) throws SQLException {
        String noticeId = generateNoticeId();

        String query = "INSERT INTO notices (notice_id, notice_title, created_date, description, created_by) " +
                "VALUES (?, ?, CURRENT_DATE, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, noticeId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, adminId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public List<Notice> getAllNotices() throws SQLException {
        String query = "SELECT n.*, a.name as admin_name " +
                "FROM notices n " +
                "JOIN admins a ON n.created_by = a.ad_id " +
                "ORDER BY n.created_date DESC";

        List<Notice> notices = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Notice notice = new Notice(
                        rs.getString("notice_id"),
                        rs.getString("notice_title"),
                        rs.getDate("created_date"),
                        rs.getString("description"),
                        rs.getString("created_by"),
                        rs.getString("admin_name")
                );
                notices.add(notice);
            }
        }

        return notices;
    }

    public boolean deleteNotice(String noticeId) throws SQLException {
        String query = "DELETE FROM notices WHERE notice_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, noticeId);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String encryptPassword(String password) {
        // Use a proper password hashing algorithm like BCrypt
        // For simplicity, using a basic hash here (NOT recommended for production)
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Not secure, only for demonstration
        }
    }

    public boolean registerComplaint(String residentId, String subject, String description) throws SQLException {
        String complaintId = generateComplaintId();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        String status = "PENDING";

        String query = "INSERT INTO complaints (complaint_id, resident_id, subject, description, date_time, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, complaintId);
            ps.setString(2, residentId);
            ps.setString(3, subject);
            ps.setString(4, description);
            ps.setTimestamp(5, currentTime);
            ps.setString(6, status);

            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    private String generateComplaintId()  {
        // Get the count of complaints to generate a new ID
        long timestamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        return "CON" + timestamp + random;
    }

    public boolean resolveComplaint(String complaintId) throws SQLException {
        String query = "UPDATE complaints SET status = 'RESOLVED' WHERE complaint_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, complaintId);
            int result = ps.executeUpdate();
            return result > 0;
        }
    }

    public List<Map<String, Object>> getComplaintsByResident(String residentId) throws SQLException {
        String query = "SELECT c.*, r.name as resident_name, f.block_name, f.flat_number " +
                "FROM complaints c " +
                "JOIN residents r ON c.resident_id = r.res_id " +
                "JOIN resident_in_flat rf ON r.res_id = rf.res_id " +
                "JOIN flats f ON rf.flat_id = f.flat_id " +
                "WHERE c.resident_id = ? " +
                "ORDER BY c.date_time DESC";

        List<Map<String, Object>> complaints = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, residentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> complaint = new HashMap<>();
                complaint.put("complaintId", rs.getString("complaint_id"));
                complaint.put("residentId", rs.getString("resident_id"));
                complaint.put("residentName", rs.getString("resident_name"));
                complaint.put("subject", rs.getString("subject"));
                complaint.put("description", rs.getString("description"));
                complaint.put("dateTime", rs.getTimestamp("date_time"));
                complaint.put("status", rs.getString("status"));
                complaint.put("blockName", rs.getString("block_name"));
                complaint.put("flatNumber", rs.getString("flat_number"));

                complaints.add(complaint);
            }
        }

        return complaints;
    }

    public List<Map<String, Object>> getAllComplaints() throws SQLException {
        String query = "SELECT c.*, r.name as resident_name, f.block_name, f.flat_number " +
                "FROM complaints c " +
                "JOIN residents r ON c.resident_id = r.res_id " +
                "JOIN resident_in_flat rf ON r.res_id = rf.res_id " +
                "JOIN flats f ON rf.flat_id = f.flat_id " +
                "ORDER BY c.date_time DESC";

        List<Map<String, Object>> complaints = new ArrayList<>();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Map<String, Object> complaint = new HashMap<>();
                complaint.put("complaintId", rs.getString("complaint_id"));
                complaint.put("residentId", rs.getString("resident_id"));
                complaint.put("residentName", rs.getString("resident_name"));
                complaint.put("subject", rs.getString("subject"));
                complaint.put("description", rs.getString("description"));
                complaint.put("dateTime", rs.getTimestamp("date_time"));
                complaint.put("status", rs.getString("status"));
                complaint.put("blockName", rs.getString("block_name"));
                complaint.put("flatNumber", rs.getString("flat_number"));

                complaints.add(complaint);
            }
        }

        return complaints;
    }

    public List<Map<String, Object>> getComplaintsByStatus(String status) throws SQLException {
        String query = "SELECT c.*, r.name as resident_name, f.block_name, f.flat_number " +
                "FROM complaints c " +
                "JOIN residents r ON c.resident_id = r.res_id " +
                "JOIN resident_in_flat rf ON r.res_id = rf.res_id " +
                "JOIN flats f ON rf.flat_id = f.flat_id " +
                "WHERE c.status = ? " +
                "ORDER BY c.date_time DESC";

        List<Map<String, Object>> complaints = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> complaint = new HashMap<>();
                complaint.put("complaintId", rs.getString("complaint_id"));
                complaint.put("residentId", rs.getString("resident_id"));
                complaint.put("residentName", rs.getString("resident_name"));
                complaint.put("subject", rs.getString("subject"));
                complaint.put("description", rs.getString("description"));
                complaint.put("dateTime", rs.getTimestamp("date_time"));
                complaint.put("status", rs.getString("status"));
                complaint.put("blockName", rs.getString("block_name"));
                complaint.put("flatNumber", rs.getString("flat_number"));

                complaints.add(complaint);
            }
        }

        return complaints;
    }



// Generate unique bill ID
private String generateBillId() throws SQLException {
    String query = "SELECT COUNT(*) as count FROM bill";
    try (Statement stmt = connection.createStatement()) {
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            int count = rs.getInt("count") + 1;
            return "BILL" + String.format("%04d", count);
        }
        return "BILL0001";
    }
}

// Create new bill by admin
public boolean createBill(String blockName, String flatNumber, String billType,
        double amount, Timestamp dueDate) throws SQLException {
    String billId = generateBillId();
    String status = "PENDING";

    String query = "INSERT INTO bill (bill_id, block_name, flat_number, billtype, amount, duedate, approval_status) "
            +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, billId);
        ps.setString(2, blockName);
        ps.setString(3, flatNumber);
        ps.setString(4, billType);
        ps.setDouble(5, amount);
        ps.setTimestamp(6, dueDate);
        ps.setString(7, status);

        int result = ps.executeUpdate();
        return result > 0;
    }
}

// Update bill status to paid
public boolean updateBillStatus(String billId, Timestamp paidDate) throws SQLException {
    String query = "UPDATE bill SET approval_status = 'PAID', paidDate = ? WHERE bill_id = ?";

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setTimestamp(1, paidDate);
        ps.setString(2, billId);

        int result = ps.executeUpdate();
        return result > 0;
    }
}

// Get bills for a specific flat
public List<Map<String, Object>> getBillsByFlat(String blockName, String flatNumber) throws SQLException {
    String query = "SELECT * FROM bill WHERE block_name = ? AND flat_number = ? ORDER BY duedate DESC";

    List<Map<String, Object>> bills = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, blockName);
        ps.setString(2, flatNumber);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, Object> bill = new HashMap<>();
            bill.put("billId", rs.getString("bill_id"));
            bill.put("blockName", rs.getString("block_name"));
            bill.put("flatNumber", rs.getString("flat_number"));
            bill.put("billType", rs.getString("billtype"));
            bill.put("amount", rs.getDouble("amount"));
            bill.put("dueDate", rs.getTimestamp("duedate"));
            bill.put("paidDate", rs.getTimestamp("paiddate"));
            bill.put("status", rs.getString("approval_status"));

            bills.add(bill);
        }
    }

    return bills;
}

// Get all bills (for admin)
public List<Map<String, Object>> getAllBills() throws SQLException {
    String query = "SELECT * FROM bill ORDER BY duedate DESC";

    List<Map<String, Object>> bills = new ArrayList<>();

    try (Statement stmt = connection.createStatement()) {
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Map<String, Object> bill = new HashMap<>();
            bill.put("id", rs.getString("bill_id"));
            bill.put("block", rs.getString("block_name"));
            bill.put("flatNumber", rs.getString("flat_number"));
            bill.put("billType", rs.getString("billtype"));
            bill.put("amount", rs.getDouble("amount"));
            bill.put("dueDate", rs.getTimestamp("duedate"));
            bill.put("paidDate", rs.getTimestamp("paiddate"));
            bill.put("status", rs.getString("approval_status"));

            bills.add(bill);
        }
    }

    return bills;
}

// Filter bills by month and year
public List<Map<String, Object>> filterBillsByDate(String blockName, String flatNumber,
        int month, int year) throws SQLException {
    String query = "SELECT * FROM bill WHERE block_name = ? AND flat_number = ? " +
            "AND MONTH(duedate) = ? AND YEAR(duedate) = ? ORDER BY duedate DESC";

    List<Map<String, Object>> bills = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, blockName);
        ps.setString(2, flatNumber);
        ps.setInt(3, month);
        ps.setInt(4, year);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, Object> bill = new HashMap<>();
            bill.put("id", rs.getString("bill_id"));
            bill.put("block", rs.getString("block_name"));
            bill.put("flatNumber", rs.getString("flat_number"));
            bill.put("billType", rs.getString("billtype"));
            bill.put("amount", rs.getDouble("amount"));
            bill.put("dueDate", rs.getTimestamp("duedate"));
            bill.put("paidDate", rs.getTimestamp("paiddate"));
            bill.put("status", rs.getString("approval_status"));

            bills.add(bill);
        }
    }

    return bills;
}

// Filter all bills by date (for admin)
public List<Map<String, Object>> filterAllBillsByDate(int month, int year) throws SQLException {
    String query = "SELECT * FROM bill WHERE MONTH(duedate) = ? AND YEAR(duedate) = ? ORDER BY duedate DESC";

    List<Map<String, Object>> bills = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setInt(1, month);
        ps.setInt(2, year);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, Object> bill = new HashMap<>();
            bill.put("id", rs.getString("bill_id"));
            bill.put("block", rs.getString("block_name"));
            bill.put("flatNumber", rs.getString("flat_number"));
            bill.put("billType", rs.getString("billtype"));
            bill.put("amount", rs.getDouble("amount"));
            bill.put("dueDate", rs.getTimestamp("duedate"));
            bill.put("paidDate", rs.getTimestamp("paiddate"));
            bill.put("status", rs.getString("approval_status"));

            bills.add(bill);
        }
    }

    return bills;
}

// Filter bills by block and flat (for admin)
public List<Map<String, Object>> filterBillsByFlat(String blockName, String flatNumber) throws SQLException {
    String query = "SELECT * FROM bill WHERE block_name = ? AND flat_number = ? ORDER BY duedate DESC";

    List<Map<String, Object>> bills = new ArrayList<>();

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, blockName);
        ps.setString(2, flatNumber);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Map<String, Object> bill = new HashMap<>();
            bill.put("id", rs.getString("bill_id"));
            bill.put("block", rs.getString("block_name"));
            bill.put("flatNumber", rs.getString("flat_number"));
            bill.put("billType", rs.getString("billtype"));
            bill.put("amount", rs.getDouble("amount"));
            bill.put("dueDate", rs.getTimestamp("duedate"));
            bill.put("paidDate", rs.getTimestamp("paiddate"));
            bill.put("status", rs.getString("approval_status"));

            bills.add(bill);
        }
    }

    return bills;
}


}