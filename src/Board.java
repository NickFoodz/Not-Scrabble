/**
 * Represents the Board in a game of scrabble, with 15 rows and columns
 * @version 1
 * @author Nick Fuda
 * @author Andrew Roberts
 */

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Board models a board in a game of scrabble
 * @author Nick Fuda
 * @author Andrew Roberts
 * @version 2
 */
public class Board implements Serializable {
    private Position[][] board;
    private HashMap<String, Integer> premiumPositions = new HashMap<>();
    private boolean customBoard;

    public Board() {
        board = new Position[15][15];
        initializeBoard();
        initDefaultPremiums();
    }

    /**
     *  Constructor for custom board
     * @param customName the name of the file where the board XML is stored.
     */
    public Board(String customName){
        board = new Position[15][15];
        initializeBoard();
        importCustomBoardXML(customName);
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
     *  Display the board in the console, used mostly for debugging
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
    public Map<Map<Position, Tile>, String> gatherWordsOnBoard() {
        Map<Map<Position, Tile>, String> wordsToTiles = new HashMap<>();

        // check horizontal words
        for (int row = 0; row < 15; row++) {
            StringBuilder currentWord = new StringBuilder();
            Map<Position, Tile> currentTiles = new HashMap<>();

            for (int col = 0; col < 15; col++) {
                Position position = this.getPosition(row, col);
                if (position.isOccupied()) {
                    currentWord.append(position.getTile().getLetter());
                    currentTiles.put(position, position.getTile());
                } else {
                    if (currentWord.length() > 1) {
                        wordsToTiles.put(new HashMap<>(currentTiles), currentWord.toString());
                    }
                    currentWord.setLength(0); // reset word
                    currentTiles.clear(); // reset tile list
                }
            }
            if (currentWord.length() > 1) {
                wordsToTiles.put(new HashMap<>(currentTiles), currentWord.toString());
            }
        }
        // check vertical words
        for (int col = 0; col < 15; col++) {
            StringBuilder currentWord = new StringBuilder();
            Map<Position, Tile> currentTiles = new HashMap<>();

            for (int row = 0; row < 15; row++) {
                Position position = this.getPosition(row, col);
                if (position.isOccupied()) {
                    currentWord.append(position.getTile().getLetter());
                    currentTiles.put(position, position.getTile());
                } else {
                    if (currentWord.length() > 1) {
                        wordsToTiles.put(new HashMap<>(currentTiles), currentWord.toString());
                    }
                    currentWord.setLength(0); // reset word
                    currentTiles.clear(); // reset tile list
                }
            }
            if (currentWord.length() > 1) {
                wordsToTiles.put(new HashMap<>(currentTiles), currentWord.toString());
            }
        }
        return wordsToTiles;
    }

    /**
     * method to set all board positions to occupied (for testing)
     */
    public void setAllPositionsOccupied() {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Position position = getPosition(row, col);
                position.setOccupied(true);
            }
        }
    }

    /**
     * method to initiate all premium positions
     */
    private void initDefaultPremiums(){
        //2LS (double letter square)
        int doubleLetterScore = 2;
        Position a4 = new Position(3,0);
        premiumPositions.put(a4.toString(),doubleLetterScore);
        Position a12 = new Position(11,0);
        premiumPositions.put(a12.toString(),doubleLetterScore);
        Position c7 = new Position(6,2);
        premiumPositions.put(c7.toString(),doubleLetterScore);
        Position c9 = new Position(8,2);
        premiumPositions.put(c9.toString(),doubleLetterScore);
        Position d1= new Position(0,3);
        premiumPositions.put(d1.toString(),doubleLetterScore);
        Position d8 = new Position(7,3);
        premiumPositions.put(d8.toString(),doubleLetterScore);
        Position d15 = new Position(14,3);
        premiumPositions.put(d15.toString(),doubleLetterScore);
        Position g3 = new Position(2,6);
        premiumPositions.put(g3.toString(),doubleLetterScore);
        Position g7 = new Position(6,6);
        premiumPositions.put(g7.toString(),doubleLetterScore);
        Position g9 = new Position(8,6);
        premiumPositions.put(g9.toString(),doubleLetterScore);
        Position g13 = new Position(12,6);
        premiumPositions.put(g13.toString(),doubleLetterScore);
        Position h4 = new Position(3,7);
        premiumPositions.put(h4.toString(),doubleLetterScore);
        Position h12 = new Position(11,7);
        premiumPositions.put(h12.toString(),doubleLetterScore);
        Position i3 = new Position(2,8);
        premiumPositions.put(i3.toString(),doubleLetterScore);
        Position i7 = new Position(6,8);
        premiumPositions.put(i7.toString(),doubleLetterScore);
        Position i9 = new Position(8,8);
        premiumPositions.put(i9.toString(),doubleLetterScore);
        Position i13 = new Position(12,8);
        premiumPositions.put(i13.toString(),doubleLetterScore);
        Position l1 = new Position(0,11);
        premiumPositions.put(l1.toString(),doubleLetterScore);
        Position l8 = new Position(7,11);
        premiumPositions.put(l8.toString(),doubleLetterScore);
        Position l15 = new Position(14,11);
        premiumPositions.put(l15.toString(),doubleLetterScore);
        Position m7= new Position(6,12);
        premiumPositions.put(m7.toString(),doubleLetterScore);
        Position m9= new Position(8,12);
        premiumPositions.put(m9.toString(),doubleLetterScore);
        Position o4= new Position(3,14);
        premiumPositions.put(o4.toString(),doubleLetterScore);
        Position o12= new Position(11,14);
        premiumPositions.put(o12.toString(),doubleLetterScore);

        //2WS (double word square)
        int doubleWordSquare = 3;
        Position b2= new Position(1,1);
        premiumPositions.put(b2.toString(),doubleWordSquare);
        Position b14= new Position(13,1);
        premiumPositions.put(b14.toString(),doubleWordSquare);
        Position c3= new Position(2,2);
        premiumPositions.put(c3.toString(),doubleWordSquare);
        Position c13= new Position(12,2);
        premiumPositions.put(c13.toString(),doubleWordSquare);
        Position d4= new Position(3,3);
        premiumPositions.put(d4.toString(),doubleWordSquare);
        Position d12= new Position(11,3);
        premiumPositions.put(d12.toString(),doubleWordSquare);
        Position e5= new Position(4,4);
        premiumPositions.put(e5.toString(),doubleWordSquare);
        Position e11= new Position(10,4);
        premiumPositions.put(e11.toString(),doubleWordSquare);
        Position h8 = new Position(7,7);
        premiumPositions.put(h8.toString(), doubleWordSquare);
        Position k5= new Position(4,10);
        premiumPositions.put(k5.toString(),doubleWordSquare);
        Position k11= new Position(10,10);
        premiumPositions.put(k11.toString(),doubleWordSquare);
        Position l4= new Position(3,11);
        premiumPositions.put(l4.toString(),doubleWordSquare);
        Position l12= new Position(11,11);
        premiumPositions.put(l12.toString(),doubleWordSquare);
        Position m3= new Position(2,12);
        premiumPositions.put(m3.toString(),doubleWordSquare);
        Position m13= new Position(12,12);
        premiumPositions.put(m13.toString(),doubleWordSquare);
        Position n2= new Position(1,13);
        premiumPositions.put(n2.toString(),doubleWordSquare);
        Position n14= new Position(13,13);
        premiumPositions.put(n14.toString(),doubleWordSquare);

        //3LS (triple letter square)
        int tripleLetterSquare = 4;
        Position b6 = new Position(5,1);
        premiumPositions.put(b6.toString(),tripleLetterSquare);
        Position b10= new Position(9,1);
        premiumPositions.put(b10.toString(),tripleLetterSquare);
        Position f2 = new Position(1,5);
        premiumPositions.put(f2.toString(),tripleLetterSquare);
        Position f6 = new Position(5,5);
        premiumPositions.put(f6.toString(),tripleLetterSquare);
        Position f10= new Position(9,5);
        premiumPositions.put(f10.toString(),tripleLetterSquare);
        Position f14= new Position(13,5);
        premiumPositions.put(f14.toString(),tripleLetterSquare);
        Position j2 = new Position(1,9);
        premiumPositions.put(j2.toString(),tripleLetterSquare);
        Position j6 = new Position(5,9);
        premiumPositions.put(j6.toString(),tripleLetterSquare);
        Position j10= new Position(9,9);
        premiumPositions.put(j10.toString(),tripleLetterSquare);
        Position j14= new Position(13,9);
        premiumPositions.put(j14.toString(),tripleLetterSquare);
        Position n6 = new Position(5,13);
        premiumPositions.put(n6.toString(),tripleLetterSquare);
        Position n10= new Position(9,13);
        premiumPositions.put(n10.toString(),tripleLetterSquare);

        //3WS (triple word square)
        int tripleWordSquare = 5;
        Position a1= new Position(0,0);
        premiumPositions.put(a1.toString(),tripleWordSquare);
        Position a8= new Position(7,0);
        premiumPositions.put(a8.toString(),tripleWordSquare);
        Position a15= new Position(14,0);
        premiumPositions.put(a15.toString(),tripleWordSquare);
        Position h1= new Position(0,7);
        premiumPositions.put(h1.toString(),tripleWordSquare);
        Position h15= new Position(14,7);
        premiumPositions.put(h15.toString(),tripleWordSquare);
        Position o1= new Position(0,14);
        premiumPositions.put(o1.toString(),tripleWordSquare);
        Position o8= new Position(7,14);
        premiumPositions.put(o8.toString(),tripleWordSquare);
        Position o15= new Position(14,14);
        premiumPositions.put(o15.toString(),tripleWordSquare);
    }

    /**
     * getter for premium positions
     * @return premium positions
     */
    public HashMap<String,Integer> getPremiumPositions(){
        return premiumPositions;
    }

    private void setPremiumPositions(HashMap<String,Integer> customBoard){
        premiumPositions = customBoard;
    }

    /**
     * method to remove position from premium position map once it's been used
     * @param key the position to remove
     */
    public void removePremiumPosition(String key){
        premiumPositions.remove(key);
    }

    public void importCustomBoardXML(String fileName){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            //Parse the XML file using a custom handler
            saxParser.parse(new File(fileName), new BoardHandler(this));


        } catch (IOException e){
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static class BoardHandler extends DefaultHandler {
        private Board customBoard;
        private String position;
        private int type;
        private String currentElement;

        public BoardHandler(Board board) {this.customBoard = board;}

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes){
            currentElement = qName;
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException{
            if ("square".equalsIgnoreCase(qName)){
                customBoard.premiumPositions.put(position, type);
                position = null;
                type = 0;
            }
            currentElement = null;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException{
            String content = new String(ch, start, length).trim();
            if (currentElement != null & !content.isEmpty()){
                switch (currentElement.toLowerCase()){
                    case "pos": position = content;
                    System.out.println(position);
                        break;
                    case "type": type = Integer.parseInt(content);
                    System.out.println(type);
                        break;
                }
            }
        }
    }

}
