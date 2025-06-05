package org.example.scrabble_game;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Rack {
    private final TileBag bag;
    private final ArrayList<Tile> rack;
    public static final int TILE_LIMIT = 7;

    public Rack(TileBag bag) throws IllegalArgumentException {
        if (bag == null) {
            throw new IllegalArgumentException("TileBag cannot be null");
        }
        rack = new ArrayList<>();
        this.bag = bag;
        refillBag();
    }

    /**
     * Draws tile into tack until the limit is met or until the bag is empty
     */
    public void refillBag() {
        while (rack.size() < TILE_LIMIT  && !bag.isEmpty()) {
            rack.add(bag.drawTile());
        }
    }

    /**
    * Removes tile of given letter
     */
    public void remove(char letter) throws NoSuchElementException {
        if (contains(letter)) {
            rack.remove(getLetterIndex(letter));
        } else {
            throw new NoSuchElementException("> Letter can't be removed. Not in frame!");
        }
    }

    /**
     * Accessor for letters in the frame.
     *
     */
    public Tile getTile(char letter) throws NoSuchElementException {
        if (contains(letter)) {
            return getTile(getLetterIndex(letter));
        } else {
            throw new NoSuchElementException("> Letter can't be accessed. Not in frame!");
        }
    }

    /**
     * Returns the Tile at a given index in the frame.
     *
    */
    public Tile getTile(int index) throws IllegalArgumentException {
        if (index < 0 || index >= rack.size()) {
            throw new IllegalArgumentException("Index out of bounds!");
        }
        return rack.get(index);
    }

    public ArrayList<Tile> getRack() {
        return rack;
    }

    public boolean contains(char letter) {
        return getLetterIndex(letter) != -1;
    }

    private int getLetterIndex(char letter) {
        for (int i = 0; i < rack.size(); i++) {
            if (getTile(i).getLetter() == letter) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if frame is empty.
     *
     * @return {@code true} if frame is empty
     */
    public boolean isEmpty() {
        return rack.isEmpty();
    }


    @Override
    public String toString() {
        return rack.stream().map(Tile::getLetter).map(String::valueOf).collect(Collectors.joining());
    }
}
