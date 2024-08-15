package com.example.qrscanner;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;

public class DatabaseConnector {

//    private static final String URL = "jdbc:mysql://localhost:3306/qr_database";
//    private static final String USER = "root";
//    private static final String PASSWORD = "password";
//
//    public static void sendToDatabase(String qrText) {
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            String query = "INSERT INTO qr_codes (code) VALUES (?)";
//            try (PreparedStatement stmt = conn.prepareStatement(query)) {
//                stmt.setString(1, qrText);
//                stmt.executeUpdate();
//                System.out.println("QR Code saved to database.");
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
}
