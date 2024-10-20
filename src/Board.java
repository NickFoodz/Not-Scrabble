/**
 * Represents the Board in a game of scrabble, with 15 rows and columns
 * @version 1
 * @author Nick Fuda
 * @author Andrew Roberts
 */

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Position[][] board;

    public Board() {
        board = new Position[15][15];
        initializeBoard();
    }

    /**
     * Initialize the board with empty spaces and an array for tile positions
     */
    private void initializeBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Position(i, j); // each position is empty initially
            }
        }
    }

    /**
     *  Display the board in the console
     */
    public void displayBoard() {
        System.out.print("\t  "); // Initial spacing for column headers
        for (char j = 'A'; j < 'P'; j++) {
            System.out.print(j + "\t  "); // Print column numbers
        }
        System.out.println(); // New line after column headers
        System.out.print("\t-");
        for (int j = 0; j < 15; j++) {
            System.out.print("----"); // Print top horizontal border
        }
        System.out.println();

        for (int i = 0; i < 15; i++) {
            System.out.print(i + 1 + "\t"); // Print row numbers
            for (int j = 0; j < 15; j++) {
                char tile = board[i][j].isOccupied() ? board[i][j].getTile().getLetter() : ' ';
                System.out.print("| " + tile + " "); // Print the cell with vertical borders
            }
            System.out.println("|"); // End of row with a vertical border

            // Print horizontal border
            System.out.print("\t"); // Initial spacing for row border
            for (int j = 0; j < 15; j++) {
                System.out.print("----"); // Print horizontal borders between cells
            }
            System.out.println("-"); // End of row with a horizontal border
        }
    }

    /**
     * Get position of the tile
     * @param row the row of the tile
     * @param col the column of the tile
     * @return the position
     */
    public Position getPosition(int row, int col) {
        if (row < 0 || row >= 15 || col < 0 || col >= 15) {
            return null;
        }
        return board[row][col];
    }

    /**
     * Place a tile in the position given
     * @param tile the tile to place
     * @param row the row to place in
     * @param col the column to place in
     * @return true if placed, false if not
     */
    public boolean placeTile(Tile tile, int row, int col) {
        // checks if row and column provided are within board boundaries
        if (row < 0 || row >= 15 || col < 0 || col >= 15) {
            System.out.println("invalid position: out of bounds");
            return false;
        }
        Position pos = board[row][col];
        // checks if position is occupied, adding the tile to the board if it isn't
        if (!pos.isOccupied()) {
            pos.setTile(tile);
            return true;
        }
        // position occupied
        else {
            System.out.println("position already occupied");
            return false;
        }
    }

    //I think this is where we are getting index out of bounds exceptions when trying to play a character

    /**
     * Converts the position from a string to a place on the array
     * @param positionString the position a player enters on the board (e.g. M:A12)
     * @return the position on the board in the 2D array
     */
    public Position parsePosition(String positionString) {
        positionString = positionString.trim().toUpperCase();

        // ensure provided string has at least two characters (e.g. A8 or A14
        if (positionString.length() < 2 || positionString.length() > 3) {
            return null;
        }

        // validate column is between A and O, and that row is between 1 and 15
        char colChar = positionString.charAt(0);
        String rowString = positionString.substring(1);

        // convert row and column to zero-indexed
        int row, col;
        row = Integer.parseInt(rowString) - 1;
        col = colChar - 'A';

        // check if row and column are within bounds
        if (row < 0 || row >= 15 || col < 0 || col >= 15) {
            return null;
        }
        return board[row][col];
    }

    /**
     * Gathers the words on the board
     * @return a List of Strings of the words on the board
     */
    public List<String> gatherWordsOnBoard() {
        List<String> words = new ArrayList<>();

        // check horizontal words
        for (int row = 0; row < 15; row++) {
            StringBuilder currentWord = new StringBuilder();
            for (int col = 0; col < 15; col++) {
                Position position = this.getPosition(row, col);
                if (position.isOccupied()) {
                    currentWord.append(position.getTile().getLetter());
                } else {
                    if (currentWord.length() > 1) {
                        words.add(currentWord.toString());
                    }
                    currentWord.setLength(0); // reset word
                }
            }
            if (currentWord.length() > 1) {
                words.add(currentWord.toString());
            }
        }
        // check vertical words
        for (int col = 0; col < 15; col++) {
            StringBuilder currentWord = new StringBuilder();
            for (int row = 0; row < 15; row++) {
                Position position = this.getPosition(row, col);
                if (position.isOccupied()) {
                    currentWord.append(position.getTile().getLetter());
                } else {
                    if (currentWord.length() > 1) {
                        words.add(currentWord.toString());
                    }
                    currentWord.setLength(0); // reset word
                }
            }
            if (currentWord.length() > 1) {
                words.add(currentWord.toString());
            }
        }
        return words;
    }
}
