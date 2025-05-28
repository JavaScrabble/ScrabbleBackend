package org.example.database;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DbController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/scrabble";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/users", new UserHandler());
        server.setExecutor(null); // domyślny executor
        server.start();
        System.out.println("Serwer HTTP uruchomiony na porcie 8000...");
    }

    static class UserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
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

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO users (nick, password) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nick);
                    stmt.setString(2, password);
                    stmt.executeUpdate();
                }
                sendResponse(exchange, 200, "Użytkownik dodany");
            } catch (SQLException e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Błąd serwera: " + e.getMessage());
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] response = message.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}
