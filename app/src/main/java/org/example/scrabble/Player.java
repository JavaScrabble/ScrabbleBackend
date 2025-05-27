package org.example.scrabble;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private List<Tile> rack = new ArrayList<>();
    private int score;
    private boolean isTurn;

    public void drawTiles(TileBag bag, int n) {
        while (rack.size() < 7 && !bag.isEmpty()) {
            rack.add(bag.drawTile());
        }
    }

    // getters/setters
    public void setTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}
