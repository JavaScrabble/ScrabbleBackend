package org.example.chat;

import org.example.core.AbstractClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClientHandler extends AbstractClientHandler {
    private static final Logger LOGGER = Logger.getLogger(ChatClientHandler.class.getName());
    private static final Map<String, List<AbstractClientHandler>> chatClients = new HashMap<>();
    private final List<AbstractClientHandler> clientsInRoom;

    static {
        try {
            FileHandler fh = new FileHandler("chat.log");
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ChatClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        super(socket);
        chatClients.putIfAbsent(roomID, new ArrayList<>());
        clientsInRoom = chatClients.get(roomID);
        clientsInRoom.add(this);

        clientExecutor.submit(this);
        LOGGER.info("%s connected to room %s".formatted(nickname, roomID));
    }

    @Override
    public void run() {
        while(!socket.isClosed()) {
            try{
                String text = in.readUTF();
                LOGGER.fine("Received message from %s: %s".formatted(nickname, text));

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
                LOGGER.warning(ERROR_TEMPLATE.formatted(nickname, e.getMessage()));
                break;
            }
            catch(IOException e){
                LOGGER.severe(ERROR_TEMPLATE.formatted(nickname, e.getMessage()));
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
            LOGGER.severe(ERROR_TEMPLATE.formatted(nickname, e.getMessage()));
        }

        clientsInRoom.remove(this); // Effectively logout
        LOGGER.info("%s disconnected from room %s".formatted(nickname, roomID));
    }

    @Override
    public void sendToClient(Object msg) throws IOException {
        LOGGER.fine("Sending message to %s: %s".formatted(nickname, msg));
        super.sendToClient(msg);
    }
}
