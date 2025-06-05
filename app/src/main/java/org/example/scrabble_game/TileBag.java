package org.example.scrabble_game;

import java.util.ArrayList;
import java.util.Random;

public class TileBag {
    private ArrayList<Tile> tiles = new ArrayList<>(); // Tile pool
    private Random random = new Random();

    /**
    *Makes new Tile pool based on standard Scrabble distributions
     */
    public TileBag() {
        // fill array with Scrabble letter distribution
        tiles = new ArrayList<>();

        // For each group of tiles
        for (int i = 0; i < Tile.TYPES_ARRAY.length; i++) {
            // Add their number of tiles
            for (int j = 0; j < Tile.NUM_ARRAY[i]; j++) {
                addTiles(Tile.TYPES_ARRAY[i]);
            }
        }
    }

    /**
    * Add given group of tiles to the pool
     */
    public void addTiles(String tileGroup){
        for (char ch : tileGroup.toCharArray()) {
            tiles.add(Tile.makeTile(ch));
        }
    }

    /**
    * Draw random tile from the tiles list
     */
    public Tile drawTile() throws IllegalStateException {
        if (isEmpty()) {
            throw new IllegalStateException("Tile bag is empty!");
        }

        // Return a random index between 0 and tiles.size()-1
        int index = random.nextInt(tiles.size());

        // Get tile and return it
        Tile t = tiles.get(index);
        tiles.remove(index);
        return t;
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public int size() {
        return tiles.size();
    }

    // Getters
    public ArrayList<Tile> getTiles() {
        return tiles;
    }
}
