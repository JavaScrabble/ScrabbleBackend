package org.example.dictionary;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DictionarySocket {
    private static final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private static final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private DictionarySocket(){}

    public static void initialize(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Dictionary server started on port: " + port);

        serverExecutor.submit(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientExecutor.submit(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
