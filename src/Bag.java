import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents the game bag containing all tiles in a game of "Not Scrabble"
 * @version 1
 * @author Andrew Roberts
 */
public class Bag {
    private List<Tile> tiles; // a list containing all the available tiles
    private static final Map<Character, Integer> letterFrequencies = new HashMap<>(); // map containing the letters and corresponding frequencies
    private static final Map<Character, Integer> letterPoints = new HashMap<>(); // map containing the letters and corresponding point value

    /**
     * creates new bag object that contains all tiles available for play
     */
    public Bag(boolean isTest) {
        tiles = new ArrayList<>(); // initialize the tiles list
        // initialize letter frequencies and points
        if(!isTest) {
            initializeLetterFrequencies();
        }
        else if(isTest){
            testLetterFrequencies();
        }
        initializeLetterPoints();

        // add tiles to the bag based on frequencies
        for (Map.Entry<Character, Integer> entry : letterFrequencies.entrySet()) {
            char letter = entry.getKey();
            int count = entry.getValue();
            int points = letterPoints.get(letter);

            for (int i = 0; i < count; i++) {
                tiles.add(new Tile(letter, points));
            }
        }
        Collections.shuffle(tiles); //  randomize tile order
    }

    /**
     * Initialize each letters' frequency
     */
    private void initializeLetterFrequencies() {
        letterFrequencies.put('E', 12);
        letterFrequencies.put('A', 9);
        letterFrequencies.put('I', 9);
        letterFrequencies.put('O', 8);
        letterFrequencies.put('N', 6);
        letterFrequencies.put('R', 6);
        letterFrequencies.put('T', 6);
        letterFrequencies.put('L', 4);
        letterFrequencies.put('S', 4);
        letterFrequencies.put('U', 4);
        letterFrequencies.put('D', 4);
        letterFrequencies.put('G', 3);
        letterFrequencies.put('B', 2);
        letterFrequencies.put('C', 2);
        letterFrequencies.put('M', 2);
        letterFrequencies.put('P', 2);
        letterFrequencies.put('F', 2);
        letterFrequencies.put('H', 2);
        letterFrequencies.put('V', 2);
        letterFrequencies.put('W', 2);
        letterFrequencies.put('Y', 2);
        letterFrequencies.put('K', 1);
        letterFrequencies.put('J', 1);
        letterFrequencies.put('X', 1);
        letterFrequencies.put('Q', 1);
        letterFrequencies.put('Z', 1);
        //blank tile
        letterFrequencies.put(' ',2);
    }

    /**
     * Initialize each letters' point value
     */
    private void initializeLetterPoints() {
        letterPoints.put('E', 1);
        letterPoints.put('A', 1);
        letterPoints.put('I', 1);
        letterPoints.put('O', 1);
        letterPoints.put('N', 1);
        letterPoints.put('R', 1);
        letterPoints.put('T', 1);
        letterPoints.put('L', 1);
        letterPoints.put('S', 1);
        letterPoints.put('U', 1);
        letterPoints.put('D', 2);
        letterPoints.put('G', 2);
        letterPoints.put('B', 3);
        letterPoints.put('C', 3);
        letterPoints.put('M', 3);
        letterPoints.put('P', 3);
        letterPoints.put('F', 4);
        letterPoints.put('H', 4);
        letterPoints.put('V', 4);
        letterPoints.put('W', 4);
        letterPoints.put('Y', 4);
        letterPoints.put('K', 5);
        letterPoints.put('J', 8);
        letterPoints.put('X', 8);
        letterPoints.put('Q', 10);
        letterPoints.put('Z', 10);
        //Blank tile points
        letterPoints.put(' ',0);
    }

    /**
     * Method to draw a single tile, removing it from the bag
     *
     * @return the tile that was drawn
     */
    public Tile drawTile() {
        if (!tiles.isEmpty()) {
            return tiles.remove(0); //Tile is removed from bag
        }
        return null;
    }

    /**
     * Method to check if the bag is empty
     *
     * @return true if bag is empty, false otherwise
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Initialize each letters' frequency for a test
     */
    private void testLetterFrequencies() {
        //Need only one of each letter to not fail exchange test
        int oneOfEach = 1;
        letterFrequencies.put('E', oneOfEach);
        letterFrequencies.put('A', oneOfEach);
        letterFrequencies.put('I', oneOfEach);
        letterFrequencies.put('O', oneOfEach);
        letterFrequencies.put('N', oneOfEach);
        letterFrequencies.put('R', oneOfEach);
        letterFrequencies.put('T', oneOfEach);
        letterFrequencies.put('L', oneOfEach);
        letterFrequencies.put('S', oneOfEach);
        letterFrequencies.put('U', oneOfEach);
        letterFrequencies.put('D', oneOfEach);
        letterFrequencies.put('G', oneOfEach);
        letterFrequencies.put('B', oneOfEach);
        letterFrequencies.put('C', oneOfEach);
        letterFrequencies.put('M', oneOfEach);
        letterFrequencies.put('P', oneOfEach);
        letterFrequencies.put('F', oneOfEach);
        letterFrequencies.put('H', oneOfEach);
        letterFrequencies.put('V', oneOfEach);
        letterFrequencies.put('W', oneOfEach);
        letterFrequencies.put('Y', oneOfEach);
        letterFrequencies.put('K', oneOfEach);
        letterFrequencies.put('J', oneOfEach);
        letterFrequencies.put('X', oneOfEach);
        letterFrequencies.put('Q', oneOfEach);
        letterFrequencies.put('Z', oneOfEach);
    }

    /**
     * Empties the bag, removing all tiles.
     */
    public void emptyBag() {
        tiles.clear();  // Removes all tiles from the list
    }
}
