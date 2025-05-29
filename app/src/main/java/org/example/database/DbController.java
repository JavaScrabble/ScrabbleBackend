package org.example.database;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DbController {

    public static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/register", new UserService.RegisterHandler());
        server.createContext("/login", new UserService.LoginHandler()); // nowy endpoint
        server.createContext("/scores", new ScoreService.InsertRecordHandler());
        server.createContext("/best", new ScoreService.GetBestRecordsHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Serwer HTTP uruchomiony na porcie 8000...");
    }
}
