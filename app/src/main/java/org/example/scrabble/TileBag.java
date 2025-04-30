package org.example.scrabble;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TileBag {
    private Map<Character, Integer> tileCounts = new HashMap<>();
    private Random random = new Random();

    public TileBag() {
        // fill map with Scrabble letter distribution
    }

    public char drawTile() {
        // randomly select and decrement a tile
        return 'A';
    }

    public boolean isEmpty() {
        return tileCounts.values().stream().allMatch(count -> count == 0);
    }
}
