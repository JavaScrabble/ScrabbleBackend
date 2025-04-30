package org.example.dictionary;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DictionaryService dictionaryService = new DictionaryService();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println("Received: " + requestLine);

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) return;
            String path = parts[1];

            String response = handleRequest(path);
            out.write(response.getBytes());
            out.flush();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(String path) {
        if ("/hello".equals(path)) {
            return createResponse(200, "Hello, World!");
        } else if (path.startsWith("/doesexist/")) {
            String word = path.substring(11);
            boolean exists = dictionaryService.doesWordExist(word);
            if (exists) {
                return createResponse(200, "Word exists!");
            } else {
                return createResponse(404, "Word not found!");
            }
        } else {
            return createResponse(404, "Not Found");
        }
    }

    private String createResponse(int statusCode, String message) {
        String statusLine = "HTTP/1.1 " + statusCode + (statusCode == 200 ? " OK" : " Not Found");
        return statusLine + "\r\nContent-Type: text/plain\r\n\r\n" + message;
    }
}
