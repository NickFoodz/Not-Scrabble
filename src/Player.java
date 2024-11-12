import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class Player represents a player in "Not Scrabble" games. Deals with actions
 * such as drawing tiles
 * @version 2
 * @author Andrew Roberts
 */
public class Player {
    private String name; // the player's name
    private ArrayList<Tile> rack; //the tiles the player currently holds
    private int score; //the players score
    private Map<Tile, Position> tilesPlayed; // tiles played in current turn

    /**
     * Creates new player object with corresponding name
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        rack = new ArrayList<Tile>();
        score = 0;
        tilesPlayed = new HashMap<>();
    }

    /**
     * method for drawing a tile from the bag
     *
     * @param bag      the bag to be drawn from
     * @param numTiles the number of tiles the player wishes to draw
     * @param gameRef reference to the game instance
     */
    public void drawTiles(Bag bag, int numTiles, ScrabbleModel gameRef) {
        for (int i = 0; i < numTiles; i++) {
            if (!bag.isEmpty() && (rack.size() < 7)) {
                Tile drawnTile = bag.drawTile();
                rack.add(drawnTile);
            } else if (bag.isEmpty()) {
                gameRef.showMessage("there are no more tiles in the bag");
                break;
            } else if (rack.size() == 7) {
                gameRef.showMessage("you already have 7 tiles");
                break;
            }
        }
    }

    // Milestone 1
//    /**
//     * method for displaying the tiles of the player
//     */
//    public void showTiles() {
//        System.out.print(name + "'s tiles: ");
//        for (Tile tile : rack) {
//            System.out.print(tile.getLetter() + " ");
//        }
//        System.out.println();
//    }

    /**
     * getter for player's name
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for player's rack
     *
     * @return the player's rack
     */
    public ArrayList<Tile> getRack() {
        return rack;
    }

    /**
     * getter for the player's score
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * method to check if player's rack is empty
     *
     * @return true if player rack is empty, false otherwise
     */
    public boolean isRackEmpty() {
        return rack.isEmpty();
    }


    /**
     * get tile from rack
     * @param tileLetter the letter of the desired tile
     * @return the desired tile on the rack or null if not in rack
     */
    public Tile getTile(String tileLetter){
        Iterator<Tile> iterator = this.getRack().iterator();

        //Find tile in rack and return its properties
        while (iterator.hasNext()) {
            Tile currentTile = iterator.next();
            if (String.valueOf(currentTile.getLetter()).equalsIgnoreCase(tileLetter)) {
                return currentTile; // tile found
        }
    }

        return null; // tile not found
    }

    /**
     * Find a tile on the rack
     * @param tileLetter the letter of the tile to search for
     * @return true if the tile is on the rack, false otherwise
     */
    public boolean findTile(String tileLetter) {
        Iterator<Tile> iterator = this.getRack().iterator();

        //Find tile in rack iteratively
        while (iterator.hasNext()) {
            Tile currentTile = iterator.next();
            if (String.valueOf(currentTile.getLetter()).equalsIgnoreCase(tileLetter)) {
                return true; // tile found
            }
        }

        return false; // tile not found
    }

    /**
     * Removes tile from rack, during exchange or play
     * @param tileLetter the letter of the tile to remove
     * @return true if the tile was removed, false if not or not in rack.
     */
    public boolean removeTile(String tileLetter) {
        Iterator<Tile> iterator = this.getRack().iterator();

        //Find first instance of tile in rack and remove it
        while (iterator.hasNext()) {
            Tile currentTile = iterator.next();
            if (String.valueOf(currentTile.getLetter()).equalsIgnoreCase(tileLetter)) {
                iterator.remove();
                return true; // tile found and removed
            }
        }

        return false; // tile not found
    }

    /**
     * add tiles played this turn to the playedTiles field
     * @param coordinate the row and column
     * @param tile the tile played
     */
    public void addTilesPlayed(Tile tile, Position coordinate) {
        tilesPlayed.put(tile, coordinate);
    }

    public Map<Tile, Position> getTilesPlayed() {
        return tilesPlayed;
    }

    public void clearTilesPlayed() {
        tilesPlayed.clear();
    }

    /**
     * Sets player's rack for testing purposes
     * @param tiles the tiles you want the player to have
     */
    public void setRack(ArrayList<Tile> tiles){this.rack = tiles;}

    /**
     * Sets tiles played for player; for testing purposes
     */
    public void setTilesPlayed(Map<Tile,Position> playTiles){
        tilesPlayed = playTiles;
    }

}
