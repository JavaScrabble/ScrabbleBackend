package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {
    public static void testdb() {
        String baseUrl = "jdbc:mysql://localhost:3306/";
        String dbName = "testdb";
        String fullUrl = baseUrl + dbName;
        String user = "root";
        String password = "";

        // tworzenie bazy danych jeśli nie istnieje
        try (Connection conn = DriverManager.getConnection(baseUrl, user, password)) {
            String createDb = "CREATE DATABASE IF NOT EXISTS " + dbName;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createDb);
                System.out.println("Baza danych została utworzona (lub już istnieje).");
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
            return;
        }

        // połączenie z istniejącą bazą danych
        try (Connection conn = DriverManager.getConnection(fullUrl, user, password)) {
            System.out.println("Połączono z bazą danych!");

            // Tworzenie tabeli
            String createTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL
                    );
                    """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }

            // INSERT
            String insertSql = "INSERT INTO users (name) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, "Jan Kowalski");
                pstmt.executeUpdate();
                System.out.println("Dodano użytkownika!");
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas pracy z bazą danych:");
            e.printStackTrace();
        }
    }
}
