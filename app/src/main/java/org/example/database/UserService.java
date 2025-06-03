package org.example.database;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class UserService {
    class RegisterHandler extends DbUtils implements HttpHandler {
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

            String nick = json.optString("nick");
            String password = json.optString("password");

            if (nick.isEmpty() || password.isEmpty()) {
                sendResponse(exchange, 400, "Brakuje nicka lub hasła");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
                String sql = "INSERT INTO users (nick, password) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nick);
                    stmt.setString(2, password);
                    stmt.executeUpdate();
                    sendResponse(exchange, 200, "Użytkownik dodany");
                } catch (SQLException e) {
                    if (e.getSQLState().startsWith("23")) {
                        sendResponse(exchange, 409, "Gracz o takim nicku juz istnieje");
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

    class LoginHandler extends DbUtils implements HttpHandler {
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

            String nick = json.optString("nick");
            String password = json.optString("password");

            if (nick.isEmpty() || password.isEmpty()) {
                sendResponse(exchange, 400, "Brakuje nicka lub hasła");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DbConfig.DB_URL, DbConfig.DB_USER, DbConfig.DB_PASS)) {
                String sql = "SELECT COUNT(*) FROM users WHERE nick = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nick);
                    stmt.setString(2, password);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            sendResponse(exchange, 200, "Zalogowano pomyślnie");
                        } else {
                            sendResponse(exchange, 401, "Nieprawidłowy nick lub hasło");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Błąd serwera: " + e.getMessage());
            }
        }
    }
}
