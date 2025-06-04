package org.example;

import org.example.core.ClientConnectionDTO;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ScrabbleTestClient {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final Scanner scanner;

    public ScrabbleTestClient(String host, int port) {
        scanner = new Scanner(System.in);
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(new ClientConnectionDTO(scanner.nextLine(), "test"));
            out.flush();
            System.out.println("Connected to Scrabble server at " + host + ":" + port);

            // Start a thread to read messages from the server
            new Thread(this::readMessagesFromServer).start();

            // Send messages from console input
            sendMessagesToServer();

        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private void sendMessagesToServer() {
        while (!socket.isClosed()) {
            try {
                String message = scanner.nextLine();
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
                break;
            }
        }
    }

    private void readMessagesFromServer() {
        try {
            while (!socket.isClosed()) {
                String response = (String) in.readObject();
                System.out.println("[SERVER] " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Disconnected from server.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnections();
        }
    }

    private void closeConnections() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = "localhost"; // Replace with server IP if remote
        int port = 1123;          // Replace with actual server port
        new ScrabbleTestClient(host, port);
    }
}

