import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AITest {

    @Test
    /**
     * Tests that having a blank tile in the rack makes the AI exchange the tile.
     */
    public void testCheckBlankTile() {
        ArrayList<Player> playerList = new ArrayList<Player>();
        AI ai = new AI("Tim");
        playerList.add(ai);
        //Create new game from the model's testing constructor
        ScrabbleModel game = new ScrabbleModel(playerList);

        ArrayList<Character> tc = new ArrayList<>();
        for (Tile t : ai.getRack()) {
            tc.add(t.getLetter());
        }
        System.out.println(ai.checkForBlankTile());
        assertEquals(ai.play(), "exchange");
        //assertEquals(ai.play(), "pass");
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 1));
        AIT.add(new Tile('E', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 2));
        ai.setRack(AIT);
        tc.clear();
        for (Tile t : ai.getRack()) {
            tc.add(t.getLetter());
        }
        System.out.println(tc);
        assertTrue(ai.getValidWordCombinations().contains("cheese"));
    }

    @Test
    public void testExchange() {
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("John");
        AI ai = new AI("AI");
        playerList.add(player1);
        playerList.add(ai);
        ScrabbleModel game = new ScrabbleModel(playerList);

        //First check and store the ai's tiles
        ArrayList<Character> tc = new ArrayList<>();
        for (Tile t : ai.getRack()) {
            tc.add(t.getLetter());
        }
        System.out.println(tc);
        //Check that AI has exactly 1 blank tile and exchanges it
        assertEquals(ai.checkForBlankTile(), 1);
        assertEquals(ai.play(), "exchange");
        //Now check that a full rack of blank tiles all gets exchanged
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile(' ', 2));
        AIT.add(new Tile(' ', 1));
        AIT.add(new Tile(' ', 2));
        AIT.add(new Tile(' ', 1));
        AIT.add(new Tile(' ', 2));
        AIT.add(new Tile(' ', 1));
        AIT.add(new Tile(' ', 2));
        ai.setRack(AIT);
        assertEquals(ai.checkForBlankTile(),7);
        assertEquals(ai.play(), "exchange");
        AIT.clear();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 1));
        AIT.add(new Tile('E', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 2));
        ai.setRack(AIT);
        tc.clear();
        for (Tile t : ai.getRack()) {
            tc.add(t.getLetter());
        }
        System.out.println(tc);
        //AI can play a word, does not need to exchange
        assertNotEquals(ai.play(), "exchange");
    }

    @Test
    public void testGetValidWords(){
        ArrayList<Player> playerList = new ArrayList<Player>();
        AI ai = new AI("Tim");
        playerList.add(ai);
        //Create new game from the model's testing constructor
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Set tiles to be known
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 1));
        AIT.add(new Tile('E', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 2));
        ai.setRack(AIT);
        //System.out.println(ai.getValidWordCombinations());
        //Words contained
        assertTrue(ai.getValidWordCombinations().contains("cheesy"));
        assertTrue(ai.getValidWordCombinations().contains("cheese"));
        assertTrue(ai.getValidWordCombinations().contains("cheese"));
        assertTrue(ai.getValidWordCombinations().contains("see"));
        assertTrue(ai.getValidWordCombinations().contains("he"));
        assertTrue(ai.getValidWordCombinations().contains("she"));
        assertTrue(ai.getValidWordCombinations().contains("eye"));
        assertTrue(ai.getValidWordCombinations().contains("eyes"));
        assertTrue(ai.getValidWordCombinations().contains("hey"));
        assertTrue(ai.getValidWordCombinations().contains("yes"));
        //Words not contained
        assertFalse(ai.getValidWordCombinations().contains("test"));
        assertFalse(ai.getValidWordCombinations().contains("apple"));
        assertFalse(ai.getValidWordCombinations().contains("chess"));
        assertFalse(ai.getValidWordCombinations().contains("peach"));
        assertFalse(ai.getValidWordCombinations().contains("lye"));

    }

    @Test

    public void getHighestScoringWordList(){
        ArrayList<Player> playerList = new ArrayList<Player>();
        AI ai = new AI("Tim");
        playerList.add(ai);
        //Create new game from the model's testing constructor
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Set tiles to be known
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 3));
        ai.setRack(AIT);

        ArrayList<String> rankedWords = ai.getHighestScoringWordList();
        //Longest, highest scoring word is cheesy. Words with same points ranked in order of appearance
        assertEquals(rankedWords.get(0), "cheesy");
        assertEquals(rankedWords.get(1), "sychee");
        assertEquals(rankedWords.get(5), "cheese");
    }


}