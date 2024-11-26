import java.io.Serializable;

/**
 * Position class is used for positional methods using the board in the game
 * @version 1
 * @author Andrew Roberts
 * @author Nick Fuda
 */

public class Position implements Serializable {
    private int row; // row index
    private int col; // column

    private Tile tile; // scrabble tile letter for representation
    private boolean occupied; // true if position is occupied, false otherwise

    /**
     * Constructor for position class
     * @param row the row of the board
     * @param col the column of the board
     */
    public Position(int row, int col) {
        this.row = row; //Row position
        this.col = col; //Column position
        this.tile = null; //No tile by default
        this.occupied = false; //not occupied by default
    }

    /**
     * Get the row of the board
     * @return the row (0 through 14)
     */
    public int getRow() {
        return row; //returns the row on the board
    }

    /**
     * Get the column of the board
     * @return the column (0-15)
     */
    public int getCol() {
        return col; //returns the column on the board
    }

    /**
     * Get a tile (scrabble letter)
     * @return the tile
     */
    public Tile getTile() {
        return tile; //returns the tile at the position
    }

    /**
     * Set a tile at a location, or checks if the location is occupied
     * @param tile the tile to set a location as
     */
    public void setTile(Tile tile) {
        this.tile = tile; //Sets tile in position to the one specified
        this.occupied = (tile != null); //Changes to being occupied if the tile is not empty
    }

    /**
     * method to set a position as occupied
     * @param occupied the position to be marked as occupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /**
     * Returns if the tile is occupied
     * @return true if occupied, false if not
     */
    public boolean isOccupied() {
        return occupied; //True if tile in position
    }

    /**
     * Overrides toString() method to represent the position as displayed
     * @return
     */
    @Override
    public String toString() {
        char colLetter = (char) ('A' + col);
        return (row + 1) + "" + colLetter;
    }

    /**
     * Overrides the equals() method to function with the position
     * @param obj
     * @return true if equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        // Check if the object is the same reference
        if (this == obj) {
            return true;
        }
        // Check if the object is null or not of the same class
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        // Cast the object to Position and compare fields
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
