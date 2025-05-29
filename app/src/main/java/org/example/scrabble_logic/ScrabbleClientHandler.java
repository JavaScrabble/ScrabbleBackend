package org.example.scrabble_logic;

import org.example.chat.ChatMessage;
import org.example.core.AbstractClientHandler;
import org.example.scrabble_game.Board;
import org.example.scrabble_game.GameEngine;
import org.example.scrabble_game.Player;
import org.example.scrabble_game.TileBag;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ScrabbleClientHandler extends AbstractClientHandler {
    private static final Map<String, List<AbstractClientHandler>> scrabbleClients = new HashMap<>();
    private final List<AbstractClientHandler> playersInRoom;

    private GameEngine game;

    public ScrabbleClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        super(socket);
        scrabbleClients.putIfAbsent(roomID, new ArrayList<>());
        playersInRoom = scrabbleClients.get(roomID);
        playersInRoom.add(this);

        clientExecutor.submit(this);
        System.out.printf("SERVER LOG[%s CONNECTED!]%n", nickname);
    }

    @Override
    public void run() {
        // Wait for 1st player to start the game
        while(!socket.isClosed()) {
            try {
                String text = in.readUTF();
                if (text.startsWith("SCRABBLE")) {

                }
            } catch(SocketException e){
                System.out.printf("SERVER WARNING[%s, %s]%n", nickname, e.getMessage());
                break;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
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
        playersInRoom.remove(this); // Effectively logout
        System.out.printf("SERVER INFO[session with %s closed]%n", nickname);

    }
}
