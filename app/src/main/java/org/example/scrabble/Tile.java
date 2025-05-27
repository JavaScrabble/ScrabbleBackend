package org.example.scrabble;

public class Tile {
    /*Kinda of tiles ("-" represents the blank tile) grouped by their shared amount of points and amount */
    public static final String[] TYPES_ARRAY = {"-", "E", "AI", "O", "NRT", "LSU",
            "D", "G", "BCMP", "FHVWY", "K", "JX", "QZ"};
    // Number of tiles for each tile type in every group of tile types
    public static final int[] NUM_ARRAY = {2, 12, 9, 8, 6, 4, 4, 3, 2, 2, 1, 1, 1};
    // Points associated for each tile in each group
    public static final int[] POINTS_ARRAY = {0, 1, 1, 1, 1, 1, 2, 2, 3, 4, 5, 8, 10};

    // Data of specific tile
    private char letter;
    private int points;


    // constructor
    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
    }

    /** Creates and returns tile based on the letter
    */
    public static Tile makeTile(char letter) throws IllegalArgumentException {
        int points = -1; // initialise point value
        // Look for tile
        for (int i = 0; i < TYPES_ARRAY.length; i++) {
            if (TYPES_ARRAY[i].contains(letter + "")) {
                points = POINTS_ARRAY[i];
                break;
            }
        }
        // If points remain -1 (initial), that means there's an error with type
        if (points == -1) {
            throw new IllegalArgumentException("Invalid type of tile given");
        }
        return new Tile(letter, points);
    }

    // getters
    public char getLetter() {
        return letter;
    }

    public int getPoints() {
        return points;
    }

    /**
     * Tile equality checker.
     *
     * @param obj Object to be tested for equality against another Tile
     * @return {@code true} if Tiles are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile tile = (Tile) obj;
            return tile.getLetter() == getLetter() && tile.getPoints() == getPoints();
        } else {
            return false;
        }
    }

    /**
     * Each Tile is displayed as its letter
     */
    @Override
    public String toString() {
        return Character.toString(letter);
    }
}
