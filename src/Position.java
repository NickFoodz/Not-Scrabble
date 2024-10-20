/**
 * Position class is used for positional methods using the board in the game
 * @version 1
 * @author Andrew Roberts
 */

public class Position {
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
        this.row = row;
        this.col = col;
        this.tile = null;
        this.occupied = false;
    }

    /**
     * Get the row of the board
     * @return the row (0 through 14)
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the column of the board
     * @return the column (0-15)
     */
    public int getCol() {
        return col;
    }

    /**
     * Get a tile (scrabble letter)
     * @return
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Set a tile at a location, or checks if the location is occupied
     * @param tile
     */
    public void setTile(Tile tile) {
        this.tile = tile;
        this.occupied = (tile != null);
    }

    /**
     * Returns if the tile is occupied
     * @return true if occupied, false if not
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Displays the tile at a location
     * @return tile letter at location
     */
    public char displayTile() {
        return tile != null ? tile.getLetter() : ' ';
    }

    @Override
    public String toString() {
        char colLetter = (char) ('A' + col);
        return (row + 1) + "" + colLetter;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Position position = (Position) obj;
        return this.row == position.row && this.col == position.col;
    }
}
