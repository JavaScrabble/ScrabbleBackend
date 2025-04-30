package org.example.scrabble;

public class Board {
    public static final int SIZE = 15;
    public static final String[] LAYOUT = {
            "3W,N,N,2L,N,N,N,3W,N,N,N,2L,N,N,3W",
            "N,2W,N,N,N,3L,N,N,N,3L,N,N,N,2W,N",
            "N,N,2W,N,N,N,2L,N,2L,N,N,N,2W,N,N",
            "2L,N,N,2W,N,N,N,2L,N,N,N,2W,N,N,2L",
            "N,N,N,N,2W,N,N,N,N,N,2W,N,N,N,N",
            "N,3L,N,N,N,3L,N,N,N,3L,N,N,N,3L,N",
            "N,N,2L,N,N,N,2L,N,2L,N,N,N,2L,N,N",
            "3W,N,N,2L,N,N,N,*,N,N,N,2L,N,N,3W",
    };
    private Square[][] grid = new Square[SIZE][SIZE];

    public Board() {
        // initialize empty board with bonus squares
    }

    public boolean isValidMove(Move move, Player player) {
        // Check position, alignment, connectivity, tile availability
        // Check words formed
        return true;
    }

    public int applyMove(Move move, Player player) {
        // Place tiles, calculate score, update board
        return 0; // return points scored
    }
}