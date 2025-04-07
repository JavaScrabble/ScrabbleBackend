package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;

// localhost:8082/doesexist/<word>
public class DictionarySocket {

    public static void initialize() throws IOException {
        int port = 8082;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept(); // waiting for the client
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

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

                String response;
                if ("/hello".equals(path)) {
                    response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nHello, World!";
                }
                else if (path.startsWith("/doesexist/")) {
                    String word = path.substring(11); // fetch <word> from URL
                    boolean exists = fetchWordDefinition(word);

                    if (exists) {
                        response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nWord exists!";
                    } else {
                        response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nWord not found!";
                    }
                } else {
                    response = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nNot Found";
                }

                // sending response
                out.write(response.getBytes());
                out.flush();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        // fetches data from the API
        private boolean fetchWordDefinition(String word) {
            try {
                URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                return responseCode == 200; // If 200 OK, then a word exists

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

    }
}