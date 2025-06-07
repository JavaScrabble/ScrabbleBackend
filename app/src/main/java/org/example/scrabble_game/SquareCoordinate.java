package org.example.scrabble_game;

// Class representing the coordinates of a square on the board
public class SquareCoordinate {
    private int row;
    private int column;

    public SquareCoordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SquareCoordinate) {
            SquareCoordinate coordinate = (SquareCoordinate) obj;
            return coordinate.getRow() == getRow() && coordinate.getColumn() == getColumn();
        } else {
            return false;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
