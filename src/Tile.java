import java.io.Serializable;

/**
 * Class Tile represents the Tiles in a game of Scrabble
 * @author Andrew Roberts
 * @version 1
 */
public class Tile implements Serializable {
    private char letter; // the letter on the tile
    private int pointValue; // how much the tile is worth

    /**
     * Creates a new tile with the specified letter and point value
     * @param letter the letter for the tile
     * @param pointValue the letter's corresponding point value
     */
    public Tile(char letter, int pointValue) {
        this.letter = letter;
        this.pointValue = pointValue;
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
     * The String representation of a Tile
     * @return the String representing the Tile
     */
    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}
