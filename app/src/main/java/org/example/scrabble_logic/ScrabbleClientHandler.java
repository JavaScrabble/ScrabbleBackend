package org.example.scrabble_logic;

import org.example.core.AbstractClientHandler;
import org.example.scrabble_game.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class ScrabbleClientHandler extends AbstractClientHandler {
    private static final Map<String, List<AbstractClientHandler>> scrabbleClients = new HashMap<>();
    private static final Map<String, GameEngine> games = new HashMap<>();

    private final List<AbstractClientHandler> playersInRoom;

    public ScrabbleClientHandler(Socket socket) throws IOException, ClassNotFoundException {
        super(socket);

        scrabbleClients.putIfAbsent(roomID, new ArrayList<>());
        playersInRoom = scrabbleClients.get(roomID);

        if (games.containsKey(roomID)) {
            sendToClient("FULL");
            socket.close();
            return;
        }

        sendToClient("PLAYERS " + String.join(" ", playersInRoom.stream().map(AbstractClientHandler::getNickname).toList()));

        playersInRoom.forEach(p -> {
            try {
                p.sendToClient("JOIN " + getNickname());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        playersInRoom.add(this);

        clientExecutor.submit(this);
        System.out.printf("SCRABBLE LOG[%s CONNECTED!]%n", nickname);
    }

    @Override
    public void run() {
        // Wait for 1st player to start the game
        while (!socket.isClosed()) {
            try {
                String text = (String) in.readObject();
                if (text.startsWith("SCRABBLE")) {
                    System.out.println("GAME START");

                    games.putIfAbsent(roomID, new GameEngine(playersInRoom.stream().map(AbstractClientHandler::getNickname).toList()));
                    GameEngine game = games.get(roomID);

                    for (AbstractClientHandler client : playersInRoom) {
                        client.sendToClient("START");
                    }

                    nextTurn(game);
                }

                GameEngine game = games.get(roomID); // Ensure we have the up-to-date game state

                if (!nickname.equals(game.getCurrentPlayer().getName())) {
                    sendToClient("NOT_YOUR_TURN");
                    continue;
                }

                if (text.startsWith("PLACE")) {
                    String[] moveText = text.split(" ");
                    Move move = new Move(moveText[1], moveText[2].charAt(0), Integer.parseInt(moveText[3]), moveText[4].charAt(0));

                    try {
                        game.getBoard().applyMove(move, game.getCurrentPlayer().getRack());

                        for (AbstractClientHandler client : playersInRoom) {
                            client.sendToClient(text);
                        }

                        nextTurn(game);
                    } catch (IllegalArgumentException e) {
                        sendToClient("INVALID");
                        System.out.printf("SCRABBLE LOG[%s makes an invalid move!]%n", nickname);

                    }
                }
                else if (text.startsWith("SKIP")) {
                    nextTurn(game);
                }
            } catch (SocketException e) {
                System.out.printf("SCRABBLE WARNING[%s, %s]%n", nickname, e.getMessage());
                break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // Clean up the connections
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.printf("SCRABBLE ERROR[%s, %s]%n", nickname, e.getMessage());
        }
        playersInRoom.remove(this); // Effectively logout
        System.out.printf("SCRABBLE INFO[session with %s closed]%n", nickname);

    }

    private void nextTurn(GameEngine game) throws IOException {
        game.nextTurn();

        for (AbstractClientHandler client : playersInRoom) {
            client.sendToClient("TURN " + game.getCurrentPlayer().getName());
        }

        Player player = game.getCurrentPlayer();
        AbstractClientHandler client = playersInRoom.stream().filter(e -> e.getNickname().equals(player.getName())).findFirst().orElse(null);
        client.sendToClient("RACK " + player.getRack());
    }
}
