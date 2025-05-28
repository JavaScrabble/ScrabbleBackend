package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbManager {
        private static String baseUrl = "jdbc:mysql://localhost:3306/";
        private static String dbName = "scrabble";
        private static String fullUrl = baseUrl + dbName;
        private static String user = "root";
        private static String password = "";

    public static void dbInit() {

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

        try{
            DbController.startServer();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
