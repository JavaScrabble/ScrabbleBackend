package org.example.scrabble_game;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameEngine {
    private boolean skippedBefore = false;
    private Board board;
    private TileBag tileBag;
    private Player currentPlayer;
    private Queue<Player> turnQueue;

    public GameEngine(List<String> nicks) {
        this.board = new Board();
        this.tileBag = new TileBag();

        List<Player> players = nicks.stream().map(e -> new Player(e, new Rack(tileBag))).toList();

        this.turnQueue = new LinkedList<>(players);

        for (Player p : players) {
            p.getRack().refillBag();
        }

        this.currentPlayer = turnQueue.poll();
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Queue<Player> getTurnQueue() {
        return turnQueue;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isSkippedBefore() {
        return skippedBefore;
    }

    public void setSkippedBefore(boolean skippedBefore) {
        this.skippedBefore = skippedBefore;
    }

    public void nextTurn(){
        turnQueue.offer(currentPlayer);
        currentPlayer = turnQueue.poll();
        currentPlayer.getRack().refillBag();
    }

    public Player getWinner() {
        int maxScore = getCurrentPlayer().getScore();
        Player winner = getCurrentPlayer();

        for (Player p : turnQueue) {
            if (p.getScore() > maxScore) {
                maxScore = p.getScore();
                winner = p;
            }
        }
        return winner;
    }

    public String getScores() {
        String scores = "";
        scores += getCurrentPlayer().getName() + " " + getCurrentPlayer().getScore() + "|\t";
        for (Player p : turnQueue) {
            scores += p.getName() + " " + p.getScore() + "|\t";
        }
        return scores;
    }
}
