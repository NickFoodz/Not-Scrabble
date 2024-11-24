import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * AI class is an implementation of an AI player for the game of Scrabble
 * @author Nick Fuda
 * @version 1
 */
public class AI extends Player {
    private ArrayList<String> dictionary;
    private Board board;
    private ScrabbleModel model;

    /**
     * Constructor for AI class
     */
    public AI(ScrabbleModel scrabble, String name, ArrayList<String> dictionary, Board board){
        super(name);
        this.model = scrabble;
        this.dictionary = dictionary;
        this.board = new Board();
    }
    // /* Testing methods
    public AI(String name){
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
     * @author Nick Fuda
     * @return "exchange" to exchange blank tiles, "play" to play tilesPlayed, and "pass" to pass turn.
     */
    @Override
    public String play(){
        //First, check if there are blank tiles in rack. The logic is too complex to have the AI play that for now
        if(checkForBlankTile() > 0){
            int numBlanks = checkForBlankTile();
            //Checks tiles letters for debugging
            ArrayList<Character> tc = new ArrayList<>();
            for(Tile t : getRack()){
                tc.add(t.getLetter());
            }
            System.out.println(tc);
            //String for exchange
            ArrayList<String> blankTile = new ArrayList<>();
            for(int i = 1; i<=numBlanks; i++){
                blankTile.add(" ");
            }
            setTilesToExchange(blankTile);
            //Return exchange
            return "exchange";
        }

        //No blank tiles? Try playing a word
        //method for play
        //return play

        //Can't play a turn or exchange? Pass
        return "pass";

    }

    /**
     * Overrides parent method so that the model knows this player is AI and handles it accordingly
     * @author Nick Fuda
     * @return true
     */
    @Override
    public boolean checkAIPlayer(){
        return true;
    }

    /**
     * Checks for a blank tile in the rack. If there is a blank tile, exchange it.
     * @author Nick Fuda
     * @return number of blank tiles
     */
    public int checkForBlankTile(){
        ArrayList<Tile> rack = getRack();
        int numBlankTiles=0;
        for(Tile tile : rack){
            //If there is a blank tile
            if(tile.getLetter() == ' '){
                numBlankTiles ++;
            }
        }
        //If not found, return false
        return numBlankTiles;
    }


    /**
     * Creates a possible list of words from the tiles held
     * @author Nick Fuda
     * @return an ArrayList of strings of all valid words with current hand ONLY
     */
    public ArrayList<String> getValidWordCombinations() {
        ArrayList<String> possibleWords = new ArrayList<>();
        ArrayList<Tile> tiles= getRack();
        ArrayList<Character> tileLetters = new ArrayList<>();

        //get a list of letters from the tiles on the rack
        for(Tile t : tiles){
            tileLetters.add(t.getLetter());
        }

        for(String word : dictionary){
            ArrayList<Character> lettersCopy = new ArrayList<Character>(tileLetters);
            char[] check = word.toUpperCase().toCharArray();

            for(int i = 0; i < word.length(); i++){
                if(word.length() > 7){
                    break;
                }
                lettersCopy.remove(Character.valueOf(check[i]));
                if(lettersCopy.size() != tileLetters.size() - (i+1)){
                    break;
                } else if(i == word.length()-1){
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
    private ArrayList<String> getHighestScoringWordList(){
        ArrayList<String> formableWords = getValidWordCombinations();
        ArrayList<String> sortedWords = new ArrayList<>();
        while(!formableWords.isEmpty()){
            int maxPoints = 0;
            String currentBestWord = "";
            for(String word: formableWords){
                int wordPoints = 0;
                char[] toArray = word.toCharArray();
                for(int i=0; i < word.length(); i++){
                    wordPoints += LetterPointValues.getPointValue(toArray[i]);
                }
                if(wordPoints > maxPoints){
                    maxPoints = wordPoints;
                    currentBestWord = word;
                }

            }
            sortedWords.add(currentBestWord);
            formableWords.remove(currentBestWord);
        }

        return sortedWords;

    }


}

