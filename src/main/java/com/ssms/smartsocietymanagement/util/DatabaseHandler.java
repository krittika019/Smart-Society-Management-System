package com.ssms.smartsocietymanagement.util;

import com.ssms.smartsocietymanagement.model.Admin;
import com.ssms.smartsocietymanagement.model.Resident;

import java.sql.*;

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

    private void createDefaultAdmin() throws SQLException {
        String checkAdmin = "SELECT COUNT(*) FROM admins WHERE username = 'admin'";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(checkAdmin);
        rs.next();
        int count = rs.getInt(1);

        if (count == 0) {
            String insertAdmin = "INSERT INTO admins (name, username, password, email, role) " +
                    "VALUES ('Administrator', 'admin', 'admin123', 'admin@society.com', 'Super Admin')";
            stmt.execute(insertAdmin);
        }

        stmt.close();
    }

    public boolean addResident(Resident resident) throws SQLException {
        String query = "INSERT INTO residents (name, username, password, email, apartment_number, phone_number) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, resident.getName());
            ps.setString(2, resident.getUsername());
            ps.setString(3, resident.getPassword());
            ps.setString(4, resident.getEmail());
            ps.setString(5, resident.getApartmentNumber());
            ps.setString(6, resident.getPhoneNumber());

            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error
                return false;
            }
            throw e;
        }
    }

    public boolean validateResident(String username, String password) throws SQLException {
        String query = "SELECT * FROM residents WHERE username = ? AND password = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);

        ResultSet rs = ps.executeQuery();
        boolean valid = rs.next();

        rs.close();
        ps.close();
        return valid;
    }

    public boolean validateAdmin(String username, String password) throws SQLException {
        String query = "SELECT * FROM admins WHERE username = ? AND password = ?";
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
        String query = "SELECT * FROM residents WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        Resident resident = null;

        if (rs.next()) {
            resident = new Resident(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("apartment_number"),
                    rs.getString("phone_number")
            );
        }

        rs.close();
        ps.close();
        return resident;
    }

    public Admin getAdminByUsername(String username) throws SQLException {
        String query = "SELECT * FROM admins WHERE username = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        Admin admin = null;

        if (rs.next()) {
            admin = new Admin(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("role")
            );
        }

        rs.close();
        ps.close();
        return admin;
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
}