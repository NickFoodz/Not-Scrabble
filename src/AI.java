import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AI extends Player {
    private ArrayList<String> dictionary;
    private Board board;
    private ScrabbleModel model;

    public AI(ScrabbleModel scrabble, String name, ArrayList<String> dictionary){
        super(name);
        this.model = scrabble;
        this.dictionary = dictionary;
        this.board = new Board();
    }
    /*For testing purposes.
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
        AIT.add(new Tile('D', 2));
        setRack(AIT);

    }
     */

    /*Just for testing the make word combinations method
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
     */

    @Override
    public boolean checkAIPlayer(){
        return true;
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

