package org.example.database;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DbController {

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        UserService userService = new UserService();
        server.createContext("/register", userService.new RegisterHandler());
        server.createContext("/login", userService.new LoginHandler());

        ScoreService scoreService = new ScoreService();
        server.createContext("/scores", scoreService.new InsertRecordHandler());
        server.createContext("/best", scoreService.new GetBestRecordsHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Serwer HTTP uruchomiony na porcie 8000...");
    }
}
