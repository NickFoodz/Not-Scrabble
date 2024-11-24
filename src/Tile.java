/**
 * Class Tile represents the Tiles in a game of Scrabble
 * @author Andrew Roberts
 * @version 1
 */
public class Tile {
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

    public int getPointValue(){return pointValue;}

    public void setLetter(char newLetter){
        this.letter = newLetter;
    }

    public void setPointValue(int pointValue) {
        this.pointValue = pointValue;
    }

    @Override
    public String toString() {
        return String.valueOf(letter);
    }
}
