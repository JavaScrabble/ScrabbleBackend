package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {
    public static void testdb() {
        // 👇 Zmień dane dostępowe do bazy:
        String url = "jdbc:mysql://localhost:3306/testdb"; // testdb = Twoja baza
        String user = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Połączono z bazą danych!");

            // 1. Tworzenie tabeli (jeśli nie istnieje)
            String createTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL
                    );
                    """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }

            // 2. INSERT - dodawanie danych
            String insertSql = "INSERT INTO users (name) VALUES (?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, "Jan Kowalski");
                pstmt.executeUpdate();
                System.out.println("Dodano użytkownika!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

