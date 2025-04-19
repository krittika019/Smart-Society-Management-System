package com.ssms.smartsocietymanagement.util;

import com.ssms.smartsocietymanagement.model.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DatabaseHandler {
    private Connection connection;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ssms";
    private static final String USER = "root";
    private static final String PASSWORD = "Krittika1929!";

    public DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addResident(Resident resident, Flat flat) throws SQLException {
        String query1 = "INSERT INTO residents (res_id, name, res_username, res_password, res_email, ownership_status, phonenumber, Approval_status) " +
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

    public List<Flat> getAllFlats() throws SQLException {
        String query = "SELECT * FROM flats ORDER BY block_name, flat_number";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        List<Flat> flats = new ArrayList<>();

        while (rs.next()) {
            flats.add(new Flat(
                    rs.getString("flat_id"),
                    rs.getString("block_name"),
                    rs.getString("flat_number")
            ));
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
                    rs.getString("Approval_status")
            );
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
                    rs.getString("Ad_email")
            );
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
                    rs.getString("approval_status")
            ));
            System.out.println("res_id : "+rs.getString("res_id")) ;

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
}