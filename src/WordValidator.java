import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WordValidator {
    private Board board;
    private String dictionaryFilePath;

    public WordValidator(Board board, String dictionaryFilePath) {
        this.board = board;
        this.dictionaryFilePath = dictionaryFilePath;
    }

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