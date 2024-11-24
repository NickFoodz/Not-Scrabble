import java.util.*;

/**
 * Class Player represents a player in "Not Scrabble" games. Deals with actions
 * such as drawing tiles
 *
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 2
 */
public class Player {
    protected String name; // the player's name
    protected ArrayList<Tile> rack; //the tiles the player currently holds
    protected int score; //the players score
    protected Map<Position, Tile> tilesPlayed; // tiles played in current turn
    protected ArrayList<String> tilesToExchange; // tiles the play wishes to exchange
    protected LinkedHashMap<Tile, Boolean> actionsPerformed; // actions the play took before click play, pass or swap, boolean indicates whether its in exchange panel (0) or board (1)

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
        tilesToExchange = new ArrayList<>();
        actionsPerformed = new LinkedHashMap<>();
    }

    /**
     * method for drawing a tile from the bag
     *
     * @param bag      the bag to be drawn from
     * @param numTiles the number of tiles the player wishes to draw
     * @param gameRef  reference to the game instance
     */
    public boolean drawTiles(Bag bag, int numTiles, ScrabbleModel gameRef) {
        for (int i = 0; i < numTiles; i++) {
            if (!bag.isEmpty() && (rack.size() < 7)) {
                Tile drawnTile = bag.drawTile();
                rack.add(drawnTile);
            } else if (bag.isEmpty()) {
                gameRef.showMessage("there are no more tiles in the bag");
                return false;
            } else if (rack.size() == 7) {
                gameRef.showMessage("you already have 7 tiles");
                return false;
            }
        }
        return true;
    }

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

    /**
     * method for setting the score of the play
     *
     * @param score the score of the player's turn
     */
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
     * Removes tile from rack, during exchange or play
     *
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
     *
     * @param coordinate the row and column
     * @param tile       the tile played
     */
    public void addTilesPlayed(Tile tile, Position coordinate) {
        tilesPlayed.put(coordinate, tile);
    }

    /**
     * Method for getting the tiles played during this turn
     *
     * @return Map with the tiles and position for the turn
     */
    public Map<Position, Tile> getTilesPlayed() {
        return tilesPlayed;
    }

    /**
     * Method for clearing the tiles played this turn in preparation of next turn
     */
    public void clearTilesPlayed() {
        tilesPlayed.clear();
    }

    /**
     * Sets player's rack for testing purposes
     *
     * @param tiles the tiles you want the player to have
     */
    public void setRack(ArrayList<Tile> tiles) {
        this.rack = tiles;
    }

    /**
     * Sets tiles played for player; for testing purposes
     */
    public void setTilesPlayed(Map<Position, Tile> playTiles) {
        tilesPlayed = playTiles;
    }

    /**
     * getter for tiles to exchange
     *
     * @return the tiles the player wishes to exchange
     */
    public ArrayList<String> getTilesToExchange() {
        return tilesToExchange;
    }

    /**
     * Sets tiles to exchange
     *
     * @param tilesToExchange the tiles the player is exchanging
     */
    public void setTilesToExchange(ArrayList<String> tilesToExchange) {
        this.tilesToExchange = tilesToExchange;
    }

    /**
     * gets the map of actions played this turn
     *
     * @return hashmap of actions
     */
    public LinkedHashMap<Tile, Boolean> getActionsPerformed() {
        return actionsPerformed;
    }

    /**
     * adds an action to the linked hashmap
     *
     * @param tilePlaced  the tile placed
     * @param isExecution true if action was an execution, false otherwise
     */
    public void addActionPerformed(Tile tilePlaced, Boolean isExecution) {
        actionsPerformed.put(tilePlaced, isExecution);
    }

    /**
     * Method to be overwritten by child, AI. Returning false for this player has no bearing on the game
     * @return false, as this class is for human players
     */
    public boolean checkAIPlayer(){
        return false;
    }

    /**
     * Removes last action in linked hash map
     */
    public void removeLastAction() {
        actionsPerformed.remove(actionsPerformed.lastEntry().getKey());
    }

    /**
     * Included to be overridden by subclass AI
     *
     * @return null, as this will not be used and will only be used by AI player
     */
    public String play() {
        return null;
    }
}
