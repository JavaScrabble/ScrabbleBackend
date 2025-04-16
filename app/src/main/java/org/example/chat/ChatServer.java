package org.example.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private ChatServer() {}

    public static void startServer(int port){
        new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.printf("[SERVER STARTED ON PORT %d]%n", port);

                while(serverSocket.isBound()) {
                    Socket socket = serverSocket.accept();
                    try {
                        new ChatClientHandler(socket);
                    }
                    catch (IOException | ClassNotFoundException e){
                        System.out.println("SERVER ERROR[Can't create ChatClientHandler!]");
                        e.printStackTrace();
                        socket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }).start();
    }
}
