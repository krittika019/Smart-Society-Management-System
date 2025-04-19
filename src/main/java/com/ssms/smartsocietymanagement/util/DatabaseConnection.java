package com.ssms.smartsocietymanagement.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/ssms?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    //private static final String PASSWORD = "Krittika1929!"; 

    private static final String PASSWORD = "Kritika@2004"; //kritika nair

    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
