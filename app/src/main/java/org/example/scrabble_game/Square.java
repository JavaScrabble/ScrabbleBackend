package org.example.scrabble_game;

public class Square {
    private final Multiplier multiplier;
    private Tile tile = null; // tile occupying the square

    public Square(Multiplier multiplier) {
        this.multiplier = multiplier;
    }

    // Getters
    /**
    * Returns multiplier as its Multiplier enum type
     */
    public Multiplier getMultiplier() {
        return multiplier;
    }

    /**
    * Returns square's WORD multiplier as int
     */
    public int getWordMultiplier(){
        switch (multiplier) {
            case DOUBLE_W:
                return 2;
            case TRIPLE_W:
                return 3;
            default:
                return 1;
        }
    }

    /**
     * Returns square's LETTER multiplier as int
     */
    public int getLetterMultiplier(){
        switch (multiplier) {
            case DOUBLE_L:
                return 2;
            case TRIPLE_L:
                return 3;
            default:
                return 1;
        }
    }

    public Tile getTile() {
        return tile;
    }

    // Setters
    /**
    * Place tile on square
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    /**
     * Checks if square is vacant
    */
    public boolean isEmpty() {
        return tile == null;
    }

    /**
     * Check if Square of given index exists
     *
     * @param col -- int between 0 and SIZE-1
     * @param row -- int between 0 and SIZE-1
     * @return {@code true} if specified square index is valid
     */
    public static boolean doesExist(int col, int row) {
        return col >= 0 && col < Board.SIZE &&
                row >= 0 && row < Board.SIZE;
    }

    /**
     * Returns String representation of a Square.
     */
    @Override
    public String toString() {
        // if there is no tile, return multiplier/swuare type
        if (tile == null) {
            switch (multiplier) {
                case DOUBLE_L:
                    return "2L";
                case TRIPLE_L:
                    return "3L";
                case DOUBLE_W:
                    return "2W";
                case TRIPLE_W:
                    return "3W";
                case CENTRE:
                    return "*";
                default: // NORMAL
                    return " ";
            }
        }
        // else return tile character
        return tile.toString();
    }

    /** Enum type representing types of squares
     */
    public enum Multiplier {DOUBLE_L, TRIPLE_L, DOUBLE_W, TRIPLE_W, NORMAL, CENTRE};
}
