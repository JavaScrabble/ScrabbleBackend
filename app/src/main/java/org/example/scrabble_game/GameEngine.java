package org.example.scrabble_game;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameEngine {
    private Board board;
    private TileBag tileBag;
    private Queue<Player> turnQueue;
    private boolean gameOver;

    public GameEngine(List<Player> players) {
        this.board = new Board();
        this.tileBag = new TileBag();
        this.turnQueue = new LinkedList<>(players);
        this.gameOver = false;

        for (Player p : players) {
            p.getRack().refillBag();
        }
    }

    public void startGame() {
        while (!gameOver) {
            Player current = turnQueue.poll();
            current.setTurn(true);

            // send board and rack to current player
            // receive move from player
            // validate and apply move
            // broadcast result

            current.setTurn(false);
            turnQueue.offer(current);
        }
    }
}
