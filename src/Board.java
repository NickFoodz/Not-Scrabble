/*
Represents the Board in a game of scrabble, with 15 rows and columns
 */

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Position[][] board;

    public Board() {
        board = new Position[15][15];
        initializeBoard();
    }

    //Initialize board with empty spaces
    private void initializeBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = new Position(i, j); // each position is empty initially
            }
        }
    }

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

    public Position getPosition(int row, int col) {
        if (row < 0 || row >= 15 || col < 0 || col >= 15) {
            return null;
        }
        return board[row][col];
    }

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

    public Position parsePosition(String positionString) {
        positionString = positionString.trim().toUpperCase();

        // ensure provided string has at least two characters
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
