package org.example.scrabble_game;

/**
* Stores square coordinates -- used for knowing which squares were last changed
 */
public class SquareCoords {
    private int row;
    private int column;

    /**
     * Creates a new SquareCoords with (row, column).
     *
     * @param row    square row
     * @param column square column
     */
    public SquareCoords(int row, int column) {
        setRow(row);
        setColumn(column);
    }

    /**
     * Accessor for row.
     *
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * Mutator for row.
     *
     * @param row integer 0 - 14
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Accessor for column.
     *
     * @return column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Mutator for column.
     *
     * @param column integer 0 - 14
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Index equality checker.
     *
     * @param obj to be compared to calling Index object
     * @return {@code true} if two Index objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SquareCoords) {
            SquareCoords coords = (SquareCoords) obj;
            return coords.getRow() == getRow() && coords.getColumn() == getColumn();
        } else {
            return false;
        }
    }
}
