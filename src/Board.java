/*
Represents the Board in a game of scrabble, with 15 rows and columns
 */

public class Board {
    private char[][] board;
    private char letter;

    public Board(){
        board = new char[15][15];
        initializeBoard();
    }

    //Initialize board with empty spaces
    private void initializeBoard() {
        for(int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                board[i][j] = ' ';
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
                System.out.print("| " + board[i][j] + " "); // Print the cell with vertical borders
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
}
