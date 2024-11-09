import javax.swing.*;
import java.awt.*;
import java.lang.String;

/**
 * Class Scrabble button represents the button the board to be replaced with tiles.
 * It allows the color, score, and coordinates to be stored in the button itself.
 * @author Nick Fuda
 * @version 0.1
 */
public class ScrabbleButton extends JButton {
    private int row; //Row location of the tile
    private int column; //Column location of the tile
    private int score; //Score of the tile
    private Tile tile; //Tile at this location (starts without one)
    private Color tileColor = new Color(227,207,170);
    private boolean available;

    public ScrabbleButton(int rw, int col){
        //Button initialization
        super();
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        //Field initialization
        this.row=rw;
        this.column=col;
        this.score = 0;
        this.available= true; //Active for play.
        this.tile = null; //Starts with no tile

    }
    /**
     * Gets the row of the button
     * @return the integer representing the current row
     */
    public int getRow(){return this.row;}

    /**
     * Returns the column of the button
     * @return column, the column of the button
     */
    public int getColumn(){return this.column;}

    /**
     * Gets score from individual tile
     */
    public int getScore(){return this.score;}

    /**
     * Set the letter of the button, initializes as coordinate. Will change when tile is placed.
     * @param letter the letter to set the tile to.
     */
    public void setLetter(char letter){this.setText(String.valueOf(letter));}

    /**
     * Sets the tile that this button stores, so that it may be used to update the button
     */
    public void placeTile(Tile t){
        //Stores Tile
        this.tile = t;
        //Sets letter of this button, and sets score
        this.setLetter(this.tile.getLetter());
        this.score = tile.getPointValue();
        this.setBackground(tileColor);
        this.available = false;
    }

    /**
     *  Checks if tile is available. Should be called before any actions on the tile from other classes
     *  Can also be used to check an entire word (e.g. follow a row of tiles until empty to calculate score)
     */
    public boolean checkAvailable(){return available;}





}
