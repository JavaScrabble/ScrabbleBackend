package org.example;

import org.example.chat.ChatServer;
import org.example.dictionary.DictionarySocket;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {
        // System.out.println(new App().getGreeting());
        DictionarySocket.initialize();
        ChatServer.startServer(1122);
    }
}
