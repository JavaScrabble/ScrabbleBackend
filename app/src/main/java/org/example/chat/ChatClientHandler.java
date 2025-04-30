package org.example.chat;

import org.example.core.AbstractClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClientHandler extends AbstractClientHandler {
    private static final Map<String, List<AbstractClientHandler>> chatClients = new HashMap<>();
    private final List<AbstractClientHandler> clientsInRoom;

    public ChatClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        super(socket);
        chatClients.putIfAbsent(roomID, new ArrayList<>());
        clientsInRoom = chatClients.get(roomID);
        clientsInRoom.add(this);

        clientExecutor.submit(this);
        System.out.printf("SERVER LOG[%s CONNECTED!]%n", nickname);
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try{
                String text = in.readUTF();
                System.out.printf("SERVER LOG[%s: %s]%n", nickname, text);

                clientsInRoom.forEach(peer -> {
                    if(!peer.getNickname().equals(nickname)) {
                        try {
                            peer.sendToClient(new ChatMessage(nickname, text));
                        }
                        // Catches if impossible to forward the message to another client
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            // Catches stuff like disconnects etc.
            catch(SocketException e){
                System.out.printf("SERVER WARNING[%s, %s]%n", nickname, e.getMessage());
                break;
            }
            catch(IOException e){
                System.out.printf("SERVER ERROR[%s, %s]%n", nickname, e.getMessage());
                break;
            }
        }

        // Clean up the connections
        try{
            in.close();
            out.close();
            socket.close();
        }
        catch(IOException e){
            System.out.printf("SERVER ERROR[%s, %s]%n", nickname, e.getMessage());
        }

        clientsInRoom.remove(this); // Effectively logout
        System.out.printf("SERVER INFO[session with %s closed]%n", nickname);
    }
}
