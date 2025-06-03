package org.example.database;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class ScoreService {
     class InsertRecordHandler extends DbUtils implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JSONObject json;
            try {
                json = new JSONObject(body);
            } catch (Exception e) {
                sendResponse(exchange, 400, "Niepoprawny JSON");
                return;
            }

            int idGry = json.optInt("gameID", -1);
            String nick = json.optString("nick");
            int wynik = json.optInt("score", -1);

            if (idGry < 0 || wynik < 0 || nick.isEmpty()) {
                sendResponse(exchange, 400, "Brakuje danych: idGry, nick lub wynik");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
                String sql = "INSERT INTO scores (gameID, nick, score, date) VALUES (?, ?, ?, CURDATE())";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, idGry);
                    stmt.setString(2, nick);
                    stmt.setInt(3, wynik);
                    stmt.executeUpdate();
                    sendResponse(exchange, 200, "Rekord zapisany");
                } catch (SQLException e) {
                    if (e.getSQLState().startsWith("23")) {
                        sendResponse(exchange, 409, "Rekord z tym idGry i nickiem już istnieje");
                    } else {
                        throw e;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Błąd serwera: " + e.getMessage());
            }
        }
    }

    class GetBestRecordsHandler extends DbUtils implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            try (Connection conn = DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
                String sql = "SELECT gameID, nick, score, date FROM scores ORDER BY score DESC LIMIT 10";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    org.json.JSONArray results = new org.json.JSONArray();

                    while (rs.next()) {
                        JSONObject record = new JSONObject();
                        record.put("gameID", rs.getInt("gameID"));
                        record.put("nick", rs.getString("nick"));
                        record.put("score", rs.getInt("score"));
                        record.put("date", rs.getDate("date").toString());
                        results.put(record);
                    }

                    byte[] response = results.toString().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();

                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Błąd serwera: " + e.getMessage());
            }
        }
    }
}
