package org.example.scrabble_logic;

import org.example.core.AbstractClientHandler;
import org.example.scrabble_game.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.scrabble_game.MoveScoring.calculateScore;

public class ScrabbleClientHandler extends AbstractClientHandler {
    private static final Logger LOGGER = Logger.getLogger(ScrabbleClientHandler.class.getName());
    private static final Map<String, List<AbstractClientHandler>> scrabbleClients = new HashMap<>();
    private static final Map<String, GameEngine> games = new HashMap<>();

    private final List<AbstractClientHandler> playersInRoom;

    static {
        try {
            FileHandler fh = new FileHandler("game.log");
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        LOGGER.info("%s connected to room %s".formatted(nickname, roomID));
    }

    @Override
    public void run() {
        // Wait for 1st player to start the game
        while (!socket.isClosed()) {
            try {
                String text = (String) in.readObject();
                LOGGER.fine("Received command from %s: %s".formatted(nickname, text));

                if (text.startsWith("SCRABBLE")) {
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

                       int score = calculateScore(move, game.getBoard());
                       game.getCurrentPlayer().increaseScore(score);

                        for (AbstractClientHandler client : playersInRoom) {
                            client.sendToClient(text);
                            client.sendToClient("SCORED " + score + " points");
                        }

                        if (game.getCurrentPlayer().getRack().isEmpty()) {
                            String results = "FINAL RESULTS:\n" + game.getFinalScores();
                            for (AbstractClientHandler client : playersInRoom) {
                                client.sendToClient("GAME END!\n" +  game.getWinner().getName() + "WON!\n"+results);
                            }
                        } else {
                            nextTurn(game);
                        }
                    } catch (IllegalArgumentException e) {
                        sendToClient("INVALID");
                    }
                }
                else if (text.startsWith("SKIP")) {
                    nextTurn(game);
                }
            } catch (SocketException e) {
                LOGGER.warning("%s, %s".formatted(nickname, e.getMessage()));
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
            LOGGER.severe(ERROR_TEMPLATE.formatted(nickname, e.getMessage()));
        }
        playersInRoom.remove(this); // Effectively logout
        LOGGER.info("%s disconnected from room %s".formatted(nickname, roomID));

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

    @Override
    public void sendToClient(Object msg) throws IOException {
        LOGGER.fine("Sending command to %s: %s".formatted(nickname, msg));
        super.sendToClient(msg);
    }
}
