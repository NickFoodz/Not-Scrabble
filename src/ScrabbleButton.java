import javax.swing.*;
import java.awt.*;
import java.lang.String;

/**
 * Class Scrabble button represents the button the board to be replaced with tiles.
 * It allows the color, score, and coordinates to be stored in the button itself.
 *
 * @author Nick Fuda
 * @version 0.1
 */
public class ScrabbleButton extends JButton {
    private int row; //Row location of the tile
    private int column; //Column location of the tile
    private Position position;
    private int score; //Score of the tile
    private Tile tile; //Tile at this location (starts without one)
    private Color tileColor = new Color(227, 207, 170); // colour for placing a tile
    private boolean empty; // tracks if button is available
    private Color defultColor; // store default color

    /**
     * Default constructor for scrabble button
     */
    public ScrabbleButton() {
        //Button initialization
        super();
        initialize();

    }

    /**
     * Constructor for int and row to be assigned, used in ScrabbleBoardPanel
     *
     * @param rw  the row of the button
     * @param col the column of the button
     */
    public ScrabbleButton(int rw, int col) {
        //Button initialization
        super();
        //Field initialization
        this.row = rw;
        this.column = col;
        this.position = new Position(rw,col);
        initialize();
    }

    /**
     * initialize button
     */
    private void initialize() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setEnabled(true);
        this.score = 0;
        this.empty = true; // Active for play.
        this.tile = null; // Starts with no tile
        this.defultColor = null; // Default is no premium color
    }

    /**
     * Gets the row of the button
     *
     * @return the integer representing the current row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * Set the letter of the button, initializes as coordinate. Will change when tile is placed.
     *
     * @param letter the letter to set the tile to.
     */
    public void setLetter(char letter) {
        this.setText(String.valueOf(letter));
    }

    /**
     * Sets the tile that this button stores, so that it may be used to update the button
     */
    public void placeTile(Tile t) {
        if (empty) {
            //Stores Tile
            this.tile = t;
            //Code for blank tile letter selection (works but gives null pointer)
            if (this.tile.getLetter() == ' ') {
                char blankLetter = ' ';
                String blankInput;
                //Ask to enter a letter, continuously
                do {
                    blankInput = JOptionPane.showInputDialog("Please enter the letter you wish to play");

                    if (blankInput != null && blankInput.length() == 1) {
                        blankLetter = blankInput.toUpperCase().charAt(0);
                        break; // Valid input, exit the loop
                    } else {
                        JOptionPane.showMessageDialog(null, "Please enter only a single letter");
                    }
                } while (true);
                this.setLetter(blankLetter);
                this.score = 0;
                t.setLetter(blankLetter);
                t.setPointValue(0);
            } else {
                //Sets letter of this button, and sets score
                 this.setLetter(this.tile.getLetter());
                 this.score = tile.getPointValue();
            }
            this.setBackground(tileColor);
            this.empty = false;
        }
    }

    /**
     * Reverts the board tile to its default or premium state in the event of an invalid play or pass.
     */
    public void revertTile() {
        this.empty = true;
        setOpaque(true);

        // Reset to premium color if it exists; otherwise, default to white
        setBackground(defultColor != null ? defultColor : Color.WHITE);

        setForeground(Color.BLACK);
        setEnabled(true);

        // Reset text to default coordinates
        char ch = 'A';
        setText(Character.toString(ch + column) + (row + 1));
    }

    /**
     * reverts the exchange board tile to its default state in teh event of an invalid play or pass
     */
    public void revertExchangeTile() {
        this.empty = true;
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        setEnabled(true); // enable button for use
        setText("");
    }

    /**
     * Sets the color of the tile and remembers it as a premium color if applicable.
     * @param color the color to set
     */
    public void setColor(Color color) {
        this.setBackground(color);
        this.defultColor = color; // Save as the premium color
    }

    public Position getPosition(){
        return this.position;
    }
}
