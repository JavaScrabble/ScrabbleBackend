package org.example.scrabble_game;

import java.util.Map;

public class Move {
    int startRow;
    int startCol;
    char direction; // H -- horizontal or V -- vertical
    String word;

    // constructor
    /**
     * Construct a word with given letters, starting at a specified
     * position (row, column), and having a certain orientation.
     */
    public Move(String word, char column, int row, char direction) {
        this.word = word.toUpperCase().trim();
        // Convert column and row to real board indices (0 - 14)
        this.startRow = row - 1;
        this.startCol = column - 'A';
        this.direction = Character.toUpperCase(direction);
    }

    /*
    * Parses move from 1 string
     */
    public static Move parseMove(String move) {
        String[] inputArguments = move.split("\\s+");
        char column = inputArguments[0].charAt(0);
        int row = Integer.parseInt(inputArguments[0].substring(1));
        char orientation = inputArguments[1].charAt(0);
        String word = inputArguments[2];
        return new Move(word, column, row, orientation);
    }

    public boolean isHorizontal() {
        return direction == 'H';
    }

    public boolean isVertical() {
        return direction == 'V';
    }

    public char wordCharAt(int index) {
        return word.charAt(index);
    }

    /**
     * Checks if the word contains alphabets only.
     *
     * @return {@code true}, if the word is strictly alphabetic.
     */
    public boolean wordIsAlphaString() {
        return word.matches("[A-Za-z]+");
    }

    /**
    * Getters
     */
    public char getDirection() {
        return direction;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getStartRow() {
        return startRow;
    }

    public String getWord() {
        return word;
    }

    public int wordLength() {
        return word.length();
    }
}