package org.example.scrabble_game;

import java.util.ArrayList;

// Class used for scoring a move
public class MoveScoring {
    public static final ArrayList<SquareCoordinate> NEW_SQUARES_USED = new ArrayList<>();

    // Resets move data
    public static void reset() {
        NEW_SQUARES_USED.clear();
    }

    // Calculates score for the entire move, including the main word + extra formed words
    public static int calculateScore(Move move, Board board) {
        int score = scoreMainWord(move, board) + scoreExtraWords(move, board);
        return score;
    }

    // Scores main word
    private static int scoreMainWord(Move move, Board board) {
        int score = 0;
        int wordMultiplier = 1;
        int row = move.getStartRow();
        int column = move.getStartCol();

        for (int i = 0; i < move.wordLength(); i++) {
            Square square = board.getBoard()[row][column];
            if (isNewlyUsed(row, column)) {
                score += square.getTile().getPoints() * square.getLetterMultiplier();
                wordMultiplier *= square.getWordMultiplier();
            } else {
                score += square.getTile().getPoints();
            }

            if (move.isHorizontal()) {
                column++;
            } else {
                row++;
            }
        }

        return score*wordMultiplier;
    }

    private static int scoreExtraWords(Move move, Board board) {
        if (move.isHorizontal()) {
            return scoreVerticalExtraWords(move, board);
        } else {
            return scoreHorizontalExtraWords(move, board);
        }
    }

    private static int scoreVerticalExtraWords(Move move, Board board) {
        int score = 0;
        Square[][] b = board.getBoard();

        // For each newly filled square
        for (SquareCoordinate coord : NEW_SQUARES_USED) {
            int wordScore = 0;
            int wordMultiplier = 1;
            int startRow = coord.getRow();
            int endRow = coord.getRow();
            int column = coord.getColumn();

            while (Square.doesExist(column, startRow-1)  && !b[startRow - 1][column].isEmpty()) // Calculate the start row of perpindicular word
                startRow -= 1;
            while (Square.doesExist(column, startRow+1)  && !b[startRow + 1][column].isEmpty()) // Calculate the end row of perpindicular word
                endRow += 1;

            // Add word score to score if there is an extra word
            if (startRow != endRow) {
                for (int i = startRow; i <= endRow; i++) {
                    Square square = b[i][column];
                    if (i == coord.getRow()) {
                        wordScore += square.getTile().getPoints() * square.getLetterMultiplier();
                        wordMultiplier *= square.getWordMultiplier();
                    } else {
                        wordScore += square.getTile().getPoints();
                    }
                }
                score += wordScore;
            }
        }

        return score;
    }

    private static int scoreHorizontalExtraWords(Move move, Board board) {
        int score = 0;
        Square[][] b = board.getBoard();

        // For each newly filled square
        for (SquareCoordinate coord : NEW_SQUARES_USED) {
            int wordScore = 0;
            int wordMultiplier = 1;
            int startCol = coord.getColumn();
            int endCol = coord.getColumn();
            int row = coord.getRow();

            while (Square.doesExist(startCol-1, row)  && !b[row][startCol-1].isEmpty()) // Calculate the start row of perpinducular word
                startCol -= 1;
            while (Square.doesExist(startCol+1, row)  && !b[row][startCol+1].isEmpty()) // Calculate the end row of perpinducular word
                endCol += 1;

            // Add word score to score if there is an extra word
            if (startCol != endCol) {
                for (int i = startCol; i <= endCol; i++) {
                    Square square = b[row][i];
                    if (i == coord.getColumn()) {
                        wordScore += square.getTile().getPoints() * square.getLetterMultiplier();
                        wordMultiplier *= square.getWordMultiplier();
                    } else {
                        wordScore += square.getTile().getPoints();
                    }
                }
                score += wordScore;
            }
        }

        return score;
    }

    // Checks if given square was covered in this move
    private static boolean isNewlyUsed(int row, int col) {
        return NEW_SQUARES_USED.contains(new SquareCoordinate(row, col));
    }
}
