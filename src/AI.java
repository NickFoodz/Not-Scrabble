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
    public AI(ScrabbleModel scrabble, String name, ArrayList<String> dictionary){
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
     * Returns a String of what the AI wants to do. Calls methods to do the actions.
     * @return exchange to exchange blank tiles, play to play tilesPlayed, and pass to pass turn.
     */
    @Override
    public String play(){
        //First, check if there are blank tiles in rack. The logic is too complex to have the AI play that for now
        if(checkForBlankTile()){
            //set up tile for exchange
//            ArrayList<Tile> rack = getRack();
//            for(Tile t : rack)
//            setTilesToExchange(" ");
            //Return exchange
            return "exchange";
        }
        //No blank tile? Try playing a word
        //method for play
        //return play

        //Can't play a turn? Pass
        return "pass";

    }

    /**
     * Overrides parent method so that the model knows this player is AI and handles it accordingly
     * @return true
     */
    @Override
    public boolean checkAIPlayer(){
        return true;
    }

    /**
     * Checks for a blank tile in the rack. If there is a blank tile, exchange it.
     * @return true if there is a blank tile, false otherwise
     */
    private boolean checkForBlankTile(){
        ArrayList<Tile> rack = getRack();
        for(Tile tile : rack){
            //If there is a blank tile
            if(tile.getLetter() == ' '){
                return true;
            }
        }
        //If not found, return false
        return false;
    }


    /**
     * Creates a possible list of words from the tiles held
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


}

