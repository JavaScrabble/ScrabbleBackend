package org.example.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private ChatServer() {}


    public static void startServer(int port){
        executor.submit(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.printf("[CHAT SERVER STARTED ON PORT %d]%n", port);

                while(serverSocket.isBound()) {
                    Socket socket = serverSocket.accept();
                    try {
                        new ChatClientHandler(socket);
                    }
                    catch (IOException | ClassNotFoundException e){
                        System.out.println("CHAT SERVER ERROR[Can't create ChatClientHandler!]");
                        e.printStackTrace();
                        socket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
