package org.example.scrabble_game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private Rack rack;
    private int score;
    private boolean isTurn;




    public Player(Rack rack) {
        this.rack = rack;
        score = 0;
    }


    /**
     * Increases the player score by given value
     *
     */
    public void increaseScore(int value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IllegalArgumentException("Score increase value must be positive");
        }
        score += value;
    }

    /**
    * Sets the player's turn
     */
    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }

    public Rack getRack() {
        return rack;
    }
}
