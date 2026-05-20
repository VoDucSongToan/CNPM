package com.quanlygiay.demo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS01;databaseName=QuanLyGiay;user=sa;password=123;encrypt=true;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        
        return DriverManager.getConnection(URL);
    }
}