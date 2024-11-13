import java.util.ArrayList;
import java.util.List;

/**
 * Class WordValidator checks if a word placement is valid on the board
 * @version 2
 * @author Andrew Roberts
 * @author Nick Fuda
 */
public class WordValidator {
    private Board board;
    private ArrayList<String> dictionary;

    /**
     * Constructor for class WordValidator
     * @param board the game board
     * @param gameDict the path to the dictionary (scrabblewords.txt)
     */
    public WordValidator(Board board, ArrayList<String> gameDict) {
        this.board = board;
        this.dictionary = gameDict;
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
     * Checks if placed tiles to be placed will be connected to other tiles to be placed
     * either directly or through other existing tiles.
     * @param positions
     * @return
     */
    public boolean isConnectedToOtherTilesInTurn(List<Position> positions){
        //Check if path between highest and lowest position is occupied
        //If more than one tile placed
        int max;
        int min;
        int sameRowOrColumn;
        if(positions.size() > 1){
            //same column, vertical turn
            if(positions.get(0).getCol() == positions.get(1).getCol()){
                //Save column
                sameRowOrColumn = positions.getFirst().getCol();
                //Set span of positions ot initialize to first item
                max = positions.getFirst().getRow();
                min = positions.getFirst().getRow();

                //Find min and max position
                for(Position position : positions){
                   if(position.getRow() > max){
                       max = position.getRow();
                   }else if(position.getRow() < min){
                       min = position.getRow();
                   }
                }
                //Check if there is a tile occupying each space between
                for(int i = min + 1; i < max; i++){
                    if(!board.getPosition(i,sameRowOrColumn).isOccupied()){
                        return false;
                    }
                }
            }

            //Same row, Horizontal turn
            if(positions.get(0).getRow() == positions.get(1).getRow()){
                //Save row
                sameRowOrColumn = positions.getFirst().getRow();
                //set span of positions to initialize first item
                max = positions.get(0).getCol();
                min = positions.get(0).getCol();
                //For each
                //Find min and max position
                for(Position position : positions){
                    if(position.getCol() > max){
                        max = position.getCol();
                    }else if(position.getCol() < min){
                        min = position.getCol();
                    }
                }
                //Check if there is a tile occupying each space between first and last tiles to place
                for(int i = min + 1; i < max; i++){
                    if(!board.getPosition(sameRowOrColumn, i).isOccupied()){
                        return false;
                    }
                }

            }
        }
        return true;
    }


    /**
     * Checks if the word is valid (calling parser class) by checking dictionary.
     * @param word the word to check
     * @return true if the word is valid, false otherwise
     */
    public boolean isValidWord(ArrayList<String> gameDictionary, String word) {
        if(gameDictionary.contains(word.trim().toLowerCase())){return true;}
        else{return false;}
    }
}