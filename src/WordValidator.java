import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Class WordValidator checks if a word placement is valid on the board
 * @version 1
 * @author Andrew Roberts
 * @author Nick Fuda
 */
public class WordValidator {
    private Board board;
    private String dictionaryFilePath;

    /**
     * Constructor for class WordValidator
     * @param board the game board
     * @param dictionaryFilePath the path to the dictionary (scrabblewords.txt)
     */
    public WordValidator(Board board, String dictionaryFilePath) {
        this.board = board;
        this.dictionaryFilePath = dictionaryFilePath;
    }

    /**
     * Checks if word posiitons are beside or above/below each other
     * @param positions the positions to place the tiles
     * @return true if aligned, false if not
     */
    public boolean arePositionsAligned(List<Position> positions) {
        boolean sameRow = true;
        boolean sameCol = true;
        int row = positions.get(0).getRow();
        int col = positions.get(0).getCol();

        if (positions.size() == 1) {
            return true;
        }

        for (Position pos : positions) {
            if (pos.getRow() != row) {
                sameRow = false;
            }
            if (pos.getCol() != col) {
                sameCol = false;
            }
        }
        return sameRow || sameCol;
    }

    /**
     * Checks if letters are connected to other tiles (cannot be floating)
     * @param positions the positions of the tiles to be placed
     * @return true if connected, false if not
     */
    public boolean isConnectedToAdjacentTiles(List<Position> positions) {
        for (Position pos : positions) {
            int row = pos.getRow();
            int col = pos.getCol();

            if ((row > 0 && board.getPosition(row - 1, col).isOccupied()) || // above
                    (row < 14 && board.getPosition(row + 1, col).isOccupied()) || // below
                    (col > 0 && board.getPosition(row, col - 1).isOccupied()) || // left
                    (col < 14 && board.getPosition(row, col + 1).isOccupied())) { // right
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the word is valid (calling parser class) by checking dictionary.
     * @param wordList the word to check
     * @return true if the word is valid, false otherwise
     */
    public boolean isValidWord(List<String> wordList) {
        for (String word : wordList) {
            try {
                if (!Parser.validWord(dictionaryFilePath, word.toLowerCase())) {
                    return false;
                }
            } catch (IOException e) {
                System.err.println("Error reading dictionary: " + e.getMessage());
                return false; // in case of an error, treat word as invalid
            }
        }
        return true;
    }
}