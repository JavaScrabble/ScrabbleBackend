package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbManager {

    public static void dbInit() {
        // Tworzenie bazy danych jeśli nie istnieje
        try (Connection conn = DriverManager.getConnection(DbConfig.BASE_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
            String createDb = "CREATE DATABASE IF NOT EXISTS " + DbConfig.DB_NAME;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createDb);
                System.out.println("Baza danych została utworzona (lub już istnieje).");
            }
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia bazy danych:");
            e.printStackTrace();
            return;
        }

        // Połączenie z istniejącą bazą danych
        try (Connection conn = DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
            System.out.println("Połączono z bazą danych!");

            String createTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        nick VARCHAR(100) NOT NULL,
                        password VARCHAR(100) NOT NULL
                    );
                    """;

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }

        } catch (SQLException e) {
            System.err.println("Błąd podczas pracy z bazą danych:");
            e.printStackTrace();
        }

        try {
            DbController.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
