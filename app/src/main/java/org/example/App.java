package org.example;

import org.example.chat.ChatServer;
import org.example.database.DbManager;
import org.example.dictionary.DictionarySocket;
import org.example.scrabble_logic.ScrabbleServer;

import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {
        // System.out.println(new App().getGreeting());
        DictionarySocket.initialize(8082);
        ChatServer.startServer(1122);
        ScrabbleServer.startServer(1123);
        DbManager.dbInit();
    }
}
