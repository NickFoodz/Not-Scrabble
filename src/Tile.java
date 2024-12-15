import java.io.Serializable;

/**
 * Class Tile represents the Tiles in a game of Scrabble
 * @version 1
 */
public class Tile implements Serializable {
    private char letter; // the letter on the tile
    private int pointValue; // how much the tile is worth
    private boolean isBlank; // true if tile is a blank tile, false otherwise

    /**
     * Creates a new tile with the specified letter and point value
     *
     * @param letter     the letter for the tile
     * @param pointValue the letter's corresponding point value
     * @param isBlank
     */
    public Tile(char letter, int pointValue, boolean isBlank) {
        this.letter = letter;
        this.pointValue = pointValue;
        this.isBlank = isBlank;
    }

    /**
     * Getter to return the letter on the tile
     * @return the tiles letter
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Gets a tile's point value
     * @return pointValue of tile
     */
    public int getPointValue(){return pointValue;}

    /**
     * Sets the letter of the Tile
     * @param newLetter the letter the tile will take
     */
    public void setLetter(char newLetter){
        this.letter = newLetter;
    }

    /**
     * Set the point value of a tile
     * @param pointValue the pointValue a tile will take
     */
    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

    /**
     * Getter to return if a tile is a blank tile or not
     * @return true if tile is blank tile, false otherwise
     */
    public boolean isBlank() {
        return isBlank;
    }

    /**
     * The String representation of a Tile
     * @return the String representing the Tile
     */
    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}
