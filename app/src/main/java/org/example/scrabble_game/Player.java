package org.example.scrabble_game;

public class Player {
    private String name;
    private Rack rack;
    private int score;

    public Player(String name, Rack rack) {
        this.name = name;
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

    public Rack getRack() {
        return rack;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
