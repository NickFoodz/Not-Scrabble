import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

import static java.util.Collections.min;

/**
 * AI class is an implementation of an AI player for the game of Scrabble
 *
 * @author Nick Fuda
 * @version 1
 */
public class AI extends Player implements Serializable {
    private ArrayList<String> dictionary;
    private Board board;
    private ScrabbleModel model;
    private boolean isTest; // true if test case, false otherwise

    /**
     * Constructor for AI class
     */
    public AI(ScrabbleModel scrabble, String name, ArrayList<String> dictionary, Board board) {
        super(name);
        this.model = scrabble;
        this.dictionary = dictionary;
        this.board = board;
        isTest = false;
    }

    // /* Testing methods
    public AI(String name) {
        super(name);
        this.dictionary = createDictionary();
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 1));
        AIT.add(new Tile('E', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile(' ', 2));
        setRack(AIT);
        isTest = true;
    }

    private ArrayList<String> createDictionary() {
        ArrayList<String> dictionary = new ArrayList<String>();
        File dictFile = new File("bigDictionary.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(dictFile);
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File \"bigDictionary.txt\" is missing");
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            dictionary.add(scanner.nextLine().toLowerCase());
        }
        return dictionary;
    }
    // end of testing methods */

    /**
     * Returns a String of what the AI wants to do. Calls methods to do the actions. The AI will prioritize exchanging
     * a blank tile, as the play logic does not allow for blanks. If there are no blanks, the AI will then try to
     * play a word, calling upon other methods to do so. If no words can be played, the AI will pass the turn.
     * Currently, the AI will not exchange letter tiles, as it can try to formulate words with all of them
     *
     * @return "exchange" to exchange blank tiles, "play" to play tilesPlayed, and "pass" to pass turn.
     * @author Nick Fuda, Andrew Roberts
     */
    @Override
    public String play() {
//        //Checks tiles letters for debugging
//        ArrayList<Character> tc = new ArrayList<>();
//        for (Tile t : getRack()) {
//            tc.add(t.getLetter());
//        }
//        System.out.println(tc);

        //First, check if there are blank tiles in rack. The logic is too complex to have the AI play that for now
        if (checkForBlankTile() > 0) {
            int numBlanks = checkForBlankTile();
            //String for exchange
            ArrayList<String> blankTile = new ArrayList<>();
            for (int i = 1; i <= numBlanks; i++) {
                blankTile.add(" ");
            }
            setTilesToExchange(blankTile);
            //Return exchange
            model.handleExchange(this);
            return "exchange";
        }

        //No blank tiles? Try playing a word
        ArrayList<String> highestScoringWords = getHighestScoringWordList();

        // If there are no words to play, exchange 3 tiles from rack
        if (highestScoringWords.isEmpty()) {
            System.out.println("Entered Exchange");
            for (int i = 0; i < Math.min(3, rack.size()); i++) {
                tilesToExchange.add(String.valueOf(rack.get(i).getLetter()));
            }
            if (model.handleExchange(this)) {
                return "exchange"; // exchange successful
            }
        }

        // attempt to place word on board starting from the highest scoring word
        for (String word : highestScoringWords) {
            for (int row = 0; row < 15; row++) {
                for (int col = 0; col < 15; col++) {
                    Position start = board.getPosition(row, col);

                    if (tryWordPlacement(word, start, true)) {
                        return "play";
                    }
                    // Attempt vertical placement
                    if (tryWordPlacement(word, start, false)) {
                        return "play";
                    }
                }
            }
        }
        // no other valid play, pass
        model.handlePass(this);
        return "pass";
    }

    /**
     * from a starting position and specified direction this function will attempt to play
     * a word from its rack
     *
     * @param word         the word to be played
     * @param start        the starting position
     * @param isHorizontal if the play is horizontal (true) or vertical (false)
     * @author Andrew Roberts
     * @return true if play is successful, false otherwise
     */
    private boolean tryWordPlacement(String word, Position start, boolean isHorizontal) {
        Map<Position, Tile> tilesToPlay = new LinkedHashMap<>();

        int row = start.getRow();
        int col = start.getCol();

        for (char letter : word.toCharArray()) {
            // Ensure position is within bounds nad not occupied
            if (row >= 15 || col >= 15 || row < 0 || col < 0) {
                return false;
            }
            if (board.getPosition(row, col).isOccupied()) {
                return false;
            }

            // Find the corresponding tile in the rack
            Tile tileToPlay = null;
            for (Tile tile : rack) {
                if (Character.toUpperCase(tile.getLetter()) == Character.toUpperCase(letter)) {
                    tileToPlay = tile;
                    break;
                }
            }

            if (tileToPlay == null) {
                return false; // Missing tile in rack
            }

            // remove tile from rack and add it to tiles that will be played
            tilesToPlay.put(board.getPosition(row, col), tileToPlay);

            // Move to the next position
            if (isHorizontal) {
                col++;
            } else {
                row++;
            }
        }

        // Attempt to play the word
        setTilesPlayed(tilesToPlay); // Assign tiles to the AI
        Map<Position, Tile> tempTilesToPlay = new HashMap<>(tilesToPlay); // stores copy of tiles to play for GUI update

        if (model.handlePlay(this)) {
            // updates the GUI
            if (!isTest) {
                for (Map.Entry<Position, Tile> entry : tempTilesToPlay.entrySet()) {
                    Position position = entry.getKey();
                    Tile tile = entry.getValue();

                    int guiRow = position.getRow();
                    int guiCol = position.getCol();

                    model.getView().getBoardPanel().getBoardButtons()[guiRow][guiCol].placeTile(tile);
                    model.getView().getBoardPanel().getBoardButtons()[guiRow][guiCol].setEnabled(false);
                    model.getView().getBoardPanel().repaint();
                }
            }
            return true; // Valid play
        }
        return false;
    }

    /**
     * Overrides parent method so that the model knows this player is AI and handles it accordingly
     *
     * @return true
     * @author Nick Fuda
     */
    @Override
    public boolean checkAIPlayer() {
        return true;
    }

    /**
     * Checks for a blank tile in the rack. If there is a blank tile, exchange it.
     *
     * @return number of blank tiles
     * @author Nick Fuda
     */
    public int checkForBlankTile() {
        ArrayList<Tile> rack = getRack();
        int numBlankTiles = 0;
        for (Tile tile : rack) {
            //If there is a blank tile
            if (tile.getLetter() == ' ') {
                numBlankTiles++;
            }
        }
        //If not found, return false
        return numBlankTiles;
    }


    /**
     * Creates a possible list of words from the tiles held
     *
     * @return an ArrayList of strings of all valid words with current hand ONLY
     * @author Nick Fuda
     */
    public ArrayList<String> getValidWordCombinations() {
        ArrayList<String> possibleWords = new ArrayList<>();
        ArrayList<Tile> tiles = getRack();
        ArrayList<Character> tileLetters = new ArrayList<>();

        //get a list of letters from the tiles on the rack
        for (Tile t : tiles) {
            tileLetters.add(t.getLetter());
        }

        for (String word : dictionary) {
            ArrayList<Character> lettersCopy = new ArrayList<Character>(tileLetters);
            char[] check = word.toUpperCase().toCharArray();

            for (int i = 0; i < word.length(); i++) {
                if (word.length() > 7 || word.length() < 2) {
                    break;
                }
                lettersCopy.remove(Character.valueOf(check[i]));
                if (lettersCopy.size() != tileLetters.size() - (i + 1)) {
                    break;
                } else if (i == word.length() - 1) {
                    possibleWords.add(word);
                }
            }
        }
        return possibleWords;
    }

    /**
     * Returns a list of strings of the highest scoring words that can be formed, in order from highest to lowest
     *
     * @return A list of strings for highest scoring words, in order of highest to lowest
     */
    public ArrayList<String> getHighestScoringWordList() {
        ArrayList<String> formableWords = getValidWordCombinations();
        ArrayList<String> sortedWords = new ArrayList<>();
        while (!formableWords.isEmpty()) {
            int maxPoints = 0;
            String currentBestWord = "";
            for (String word : formableWords) {
                int wordPoints = 0;
                char[] toArray = word.toCharArray();
                for (int i = 0; i < word.length(); i++) {
                    wordPoints += LetterPointValues.getPointValue(toArray[i]);
                }
                if (wordPoints > maxPoints) {
                    maxPoints = wordPoints;
                    currentBestWord = word;
                }

            }
            sortedWords.add(currentBestWord);
            formableWords.remove(currentBestWord);
        }

        return sortedWords;
    }

    public void setBoard(Board gameBoard) {
        this.board = gameBoard;
    }

    public void setModel(ScrabbleModel model) {
        this.model = model;
    }

}
