package org.example.scrabble_logic;

import org.example.chat.ChatClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ScrabbleServer {
    public static void startServer(int port){
        new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.printf("[SCRABBLE SERVER STARTED ON PORT %d]%n", port);

                while(serverSocket.isBound()) {
                    Socket socket = serverSocket.accept();
                    try {
                        new ScrabbleClientHandler(socket);
                    }
                    catch (IOException | ClassNotFoundException e){
                        System.out.println("SCRABBLE SERVER ERROR[Can't create ScrabbleClientHandler!]");
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
