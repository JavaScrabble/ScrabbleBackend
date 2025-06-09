package org.example.scrabble_game;

import org.example.dictionary.DictionaryProvider;
import org.example.dictionary.DictionaryService;

public class Board {
    public static final int SIZE = 15;
    private static final DictionaryProvider dictionary = new DictionaryService();

    /**
    * First 8 rows, after that it's mirrored
     */
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
    private Square[][] board = new Square[SIZE][SIZE];
    private boolean isFirstMove;

    /**
     * Creates a new empty Scrabble board
     */
    public Board() {
        board = new Square[SIZE][SIZE];
        setFirstMove(true);
        // Initialise multiplier squares on board
        String[] row;
        for (int i = 0; i < SIZE; i++) {
            row = (i <= 7) ? LAYOUT[i].split(",") : LAYOUT[SIZE - i - 1].split(",");
            for (int j = 0; j < SIZE; j++) {
                switch (row[j]) {
                    case "*":
                        board[i][j] = new Square(Square.Multiplier.CENTRE);
                        break;
                    case "2L":
                        board[i][j] = new Square(Square.Multiplier.DOUBLE_L);
                        break;
                    case "3L":
                        board[i][j] = new Square(Square.Multiplier.TRIPLE_L);
                        break;
                    case "2W":
                        board[i][j] = new Square(Square.Multiplier.DOUBLE_W);
                        break;
                    case "3W":
                        board[i][j] = new Square(Square.Multiplier.TRIPLE_W);
                        break;
                    default: // N
                        board[i][j] = new Square(Square.Multiplier.NORMAL);
                        break;
                }
            }
        }
    }

    /**
    * Sets isFirstMove to given value
     */
    private void setFirstMove(boolean value) {
        isFirstMove = value;
    }

    /**
     * Returns board
     */
    public Square[][] getBoard() {
        return board;
    }

    /**
     * Check if board has no tiles
     *
     * @return {@code true} if board is empty
     */
    public boolean isEmpty() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
    * Places tile on given square
     */
    public void placeTile(char column, int row, Tile tile) {
        board[row - 1][column - 'A'].setTile(tile);
        MoveScoring.NEW_SQUARES_USED.add(new SquareCoordinate(row - 1, column - 'A'));
    }

    public String getHorizontalWord(int row, int startColumn, int endColumn) {
        StringBuilder word = new StringBuilder();
        for (int i = startColumn; i <= endColumn; i++) {
            word.append(board[row][i].getTile().getLetter());
        }
        return word.toString();
    }

    public String getVerticalWord(int column, int startRow, int endRow) {
        StringBuilder word = new StringBuilder();
        for (int i = startRow; i <= endRow; i++) {
            word.append(board[i][column].getTile().getLetter());
        }
        return word.toString();
    }

    public void applyMove(Move move, Rack rack) throws IllegalArgumentException {
        if (!isValidMove(move, rack)) {
            throw new IllegalArgumentException("Invalid move placement");
        }
        MoveScoring.reset();
        setFirstMove(false);
        int row, column;
        for (int i = 0; i < move.wordLength(); i++) {
            char ch = move.wordCharAt(i);
            if (move.isHorizontal()) {
                // Column index increases for word placed horizontally
                column = move.getStartCol() + i;
                row = move.getStartRow();
            } else {
                // Row index increases for word placed vertically
                row = move.getStartRow() + i;
                column = move.getStartCol();
            }
            // Ignore filled squares, place tiles on the remaining squares
            if (board[row][column].isEmpty()) {
                if (rack.contains(ch)) {
                    // Place tile on the board and remove it from the frame
                    placeTile((char) (column + 'A'), row + 1, rack.getTile(ch));
                    rack.remove(ch);
                } else {
                    // Convert blank tile to a given letter if letter is not in the frame
                    Tile blankTile = new Tile(ch, 0);
                    placeTile((char) (column + 'A'), row + 1, blankTile);
                    rack.remove('-');
                }
            }
        }
    }

    /*
    * Check if move placement is valid
     */
    public boolean isValidMove(Move move, Rack rack) {
        // Check position, alignment, connectivity, tile availability
        if (!Square.doesExist(move.getStartCol(), move.getStartRow()) ||
                !(move.isHorizontal() || move.isVertical()) ||
                move.wordLength() < 2 || !move.wordIsAlphaString() ||
                rack == null) {
            return false;
        }
        // Checks if word length exceeds the size of the board
        if (isOverflowed(move)) {
            // komunikat np. Word goes beyond the board;
            //System.out.println("LOG: Word overflow");
            return false;
        }
        // Checks if it conflicts with another word
        if (doesWordConflict(move)) {
            // komunikat np. Word conflicts;
            //System.out.println("LOG: Word conflicts");
            return false;
        }

        // Checks if rack contains the required tiles for the move
        if (!doesRackHaveTiles(move, rack)) {
            // komunikat
            //System.out.println("LOG: rack doesn't have tiles");
            return false;
        }
        // Checks whether the placement uses at least one letter from frame
        if (!isRackUsed(move, rack)) {
            // komunikat
            return false;
        }

        if(!doesWordExist(move))
            return false;

        if(!doExtraWordsExist(move)) {
            System.out.println("LOG: extra words conflict");
            return false;
        }

        // If first move, checks if it covers the centre square
        if (isFirstMove) {
            boolean isCentreCovered = doesMoveCoverCentre(move);
            if (!isCentreCovered) {
                // Komunikat
            }
            return isCentreCovered;
        } else {
            // If not first move, checks if word connects with an existing word on board
            return isWordJoined(move);
        }
    }

    // Checks if a word placement goes out of the board
    private boolean isOverflowed(Move move) {
        if (move.isHorizontal()) {
            return (move.getStartCol() + move.wordLength() - 1) >= SIZE;
        } else {
            return (move.getStartRow() + move.wordLength() - 1) >= SIZE;
        }
    }

    // Checks if the move conflicts with existing words on the board
    private boolean doesWordConflict(Move move) {
        char[] wordArray = move.getWord().toCharArray();
        int column = move.getStartCol();
        int row = move.getStartRow();
        // For horizontal move
        if (move.isHorizontal()) {
            // Check if the squares before and after the word are empty
            if (Square.doesExist(column - 1, row) &&
                    Square.doesExist(column + move.wordLength(), row)) {
                if (!board[row][column - 1].isEmpty() ||
                        !board[row][column + move.wordLength()].isEmpty()) {
                    return true;
                }
            }
            for (int i = 0; i < move.wordLength(); i++) {
                // Square is filled but tile does not match the letter in the placed word
                if (!board[row][column + i].isEmpty() &&
                        board[row][column + i].getTile().getLetter() != wordArray[i]) {
                    return true;
                }
            }
        } else { // For vertical move
            // Check if the squares before and after the word are empty
            if (Square.doesExist(column, row - 1) &&
                    Square.doesExist(column, row + move.wordLength())) {
                if (!board[row - 1][column].isEmpty() ||
                        !board[row + move.wordLength()][column].isEmpty()) {
                    return true;
                }
            }
            for (int i = 0; i < move.wordLength(); i++) {
                // Square is filled but tile does not match the letter in the placed word
                if (!board[row + i][column].isEmpty() &&
                        board[row + i][column].getTile().getLetter() != wordArray[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    // Checks if the frame contains the letters necessary for word placement
    private boolean doesRackHaveTiles(Move move, Rack rack) {
        StringBuilder sb = new StringBuilder();
        for (Tile t : rack.getRack()) {
            sb.append(t.getLetter());
        }
        String tilesInFrame = sb.toString();
        int row, column;
        for (int i = 0; i < move.wordLength(); i++) {
            if (move.isHorizontal()) {
                // Column index increases for word placed horizontally
                column = move.getStartCol() + i;
                row = move.getStartRow();
            } else {
                // Row index increases for word placed vertically
                column = move.getStartCol();
                row = move.getStartRow() + i;
            }
            String letter = Character.toString(move.wordCharAt(i));
            if (board[row][column].isEmpty()) {
                if (tilesInFrame.contains(letter)) {
                    // Check for the specified letter from the frame
                    tilesInFrame = tilesInFrame.replaceFirst(letter, "");
                } else if (tilesInFrame.contains("-")) {
                    // Check for any blank characters from the frame
                    tilesInFrame = tilesInFrame.replaceFirst("-", "");
                } else {
                    // Frame does not contain letter needed
                    return false;
                }
            }
        }
        return true;
    }

    // Checks that at least one letter from the frame is used
    private boolean isRackUsed(Move move, Rack rack) {
        int row, column;
        for (int i = 0; i < move.wordLength(); i++) {
            if (move.isHorizontal()) {
                // Column index increases for word placed horizontally
                column = move.getStartCol() + i;
                row = move.getStartRow();
            } else {
                // Row index increases for word placed vertically
                column = move.getStartCol();
                row = move.getStartRow()+1;
            }
            char letter = move.wordCharAt(i);
            // If square is empty, check if frame contains the required letter or a blank tile
            if (board[row][column].isEmpty()) {
                if (rack.contains(letter) || rack.contains('-')) {
                    return true;
                }
            }
        }
        return false;
    }

    // Checks if the word to be placed covers the centre square H8 (aka 7, 7)
    private boolean doesMoveCoverCentre(Move move) {
        int row = move.getStartRow();
        int column = move.getStartCol();
        if (move.isHorizontal()) {
            return row == SIZE / 2 && column <= SIZE / 2 &&
                    column + move.wordLength() - 1 >= SIZE / 2;
        } else {
            return column == SIZE / 2 && row <= SIZE / 2 &&
                    row + move.wordLength() - 1 >= SIZE / 2;
        }
    }

    // Checks if a word placement connects with another existing word on the board
    private boolean isWordJoined(Move move) {
        int row = move.getStartRow();
        int column = move.getStartCol();
        if (move.isHorizontal()) {
            for (int i = 0; i < move.wordLength(); i++) {
                // Check top
                if (Square.doesExist(column + i, row - 1)) {
                    if (!board[row - 1][column + i].isEmpty()) {
                        return true;
                    }
                }
                // Check bottom
                if (Square.doesExist(column + i, row + 1)) {
                    if (!board[row + 1][column + i].isEmpty()) {
                        return true;
                    }
                }
                // Check if the word contains tiles already on the board
                if (Square.doesExist(column + i, row)) {
                    if (!board[row][column + i].isEmpty()) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < move.wordLength(); i++) {
                // Check left
                if (Square.doesExist(column - 1, row + i)) {
                    if (!board[row + i][column - 1].isEmpty()) {
                        return true;
                    }
                }
                // Check right
                if (Square.doesExist(column + 1, row + i)) {
                    if (!board[row + i][column + 1].isEmpty()) {
                        return true;
                    }
                }
                // Check if the word contains letter already on the board
                if (Square.doesExist(column, row + i)) {
                    if (!board[row + i][column].isEmpty()) {
                        return true;
                    }
                }
            }
        }

        // Komunikat
        return false;
    }

    /**
     * Check if the main word of a move is a valid dictionary word
     */
    private boolean doesWordExist(Move move) {
        return dictionary.doesWordExist(move.getWord());
    }

    /**
     * Check if all extra words formed by a move are valid dictionary word
     */
    private boolean doExtraWordsExist(Move move) {
        if (move.isHorizontal()) {
            return doVerticalExtraWordsExist(move);
        } else {
            return doHorizontalExtraWordsExist(move);
        }
    }

    private boolean doVerticalExtraWordsExist(Move move) {
        int mainStartCol = move.getStartCol();
        int mainEndCol= mainStartCol + move.wordLength() - 1;
        int mainRow = move.getStartRow();

        // For each square in the word not previously used (currently empty)
        for (int i = mainStartCol; i <= mainEndCol; i++) {
            if (board[mainRow][i].isEmpty()) {
                // Calculate start and end rows of the perpinducular vertical word
                int startRow = mainRow;
                int endRow = mainRow;
                int column = i;

                while (Square.doesExist(column, startRow-1)  && !board[startRow - 1][column].isEmpty()) // Calculate the start row of perpinducular word
                    startRow -= 1;
                while (Square.doesExist(column, startRow+1)  && !board[startRow + 1][column].isEmpty()) // Calculate the end row of perpinducular word
                    endRow += 1;

                // if extra vertical word exists, read it and check
                if (startRow != endRow) {
                    String extraWord = "";
                    char c;
                    for (int j = startRow; j <= endRow; j++) {
                        Square square = board[j][column];
                        if (j == mainRow) {
                            c = move.getWord().charAt(column - move.getStartCol());
                        } else {
                            c = square.getTile().getLetter();
                        }
                        extraWord += c;
                    }
                    if (!dictionary.doesWordExist(extraWord))
                            return false;
                }
            }
        }
        return true;
    }

    private boolean doHorizontalExtraWordsExist(Move move) {
        int mainStartRow = move.getStartRow();
        int mainEndRow= mainStartRow + move.wordLength() - 1;
        int mainCol = move.getStartCol();

        // For each square in the word not previously used (currently empty)
        for (int i = mainStartRow; i <= mainEndRow; i++) {
            if (board[i][mainCol].isEmpty()) {
                // Calculate start and end rows of the perpinducular horizontal word
                int startCol = mainCol;
                int endCol = mainCol;
                int row = i;

                while (Square.doesExist(startCol-1, row)  && !board[row][startCol-1].isEmpty()) // Calculate the start col of perpinducular word
                    startCol -= 1;
                while (Square.doesExist(startCol+1, row)  && !board[row][startCol+1].isEmpty()) // Calculate the end col of perpinducular word
                    endCol += 1;

                // if extra vertical word exists, read it and check
                if (startCol != endCol) {
                    String extraWord = "";
                    char c;
                    for (int j = startCol; j <= endCol; j++) {
                        Square square = board[row][j];
                        if (j == mainCol) {
                            c = move.getWord().charAt(row - move.getStartRow());
                        } else {
                            c = square.getTile().getLetter();
                        }
                        extraWord += c;
                    }
                    if (!dictionary.doesWordExist(extraWord))
                        return false;
                }
            }
        }
        return true;
    }
}