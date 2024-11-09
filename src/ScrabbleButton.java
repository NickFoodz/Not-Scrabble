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
    private int row;
    private int column;
    private int score;
    private Tile tile;
    private Color tileColor = new Color(227,207,170);

    public ScrabbleButton(int rw, int col){
        super();
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        score = 0;


    }

    /**
     * Sets the row of the button
     * @param y the row number, an integer
     */
    public void setRow(int y){this.row =y;}

    /**
     * Gets the row of the button
     * @return the integer representing the current row
     */
    public int getRow(){return this.row;}

    /**
     * Sets the column of the button, done during initializing the board
     * @param x, the column integer
     */
    public void setColumn(int x){this.column =x;}

    /**
     * Returns the column of the button
     * @return column, the column of the button
     */
    public int getColumn(){return this.column;}

    /**
     * Allows the button object to have a color set. Useful when placing a tile. Useful in future for special tiles.
     * @param color the color to change the button to.
     */
    public void setColor(Color color){setBackground(color);}

    /**
     * Set the score of the button from a tile. Initializes to 0, but changes based on a tile being assigned
      * @param points
     */
    public void setScore(int points){this.score = points;}

    /**
     * Set the letter of the button, initializes as coordinate. Will change when tile is placed.
     * @param letter the letter to set the tile to.
     */
    public void setLetter(char letter){this.setText(String.valueOf(letter));}

    /**
     * Sets the tile that this button stores, so that it may be used to update the button
     */
    public void setTile(Tile t){
        //Stores Tile
        this.tile = t;
        //Sets letter of this button, and sets score
        this.setLetter(this.tile.getLetter());
        this.setScore(this.tile.getPointValue());
        this.setColor(tileColor);
    }







}
