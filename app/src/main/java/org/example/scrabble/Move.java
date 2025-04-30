package org.example.scrabble;

import java.util.Map;

public class Move {
    int startRow;
    int startCol;
    String direction; // HORIZONTAL or VERTICAL
    String word;
    Map<Integer, Character> tilesFromRack; // Position → Letter placed

    // constructor
}