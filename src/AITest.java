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
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);

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
    public void testBlankExchange() {
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("John");
        AI ai = new AI("AI");
        playerList.add(player1);
        playerList.add(ai);
        ScrabbleModel game = new ScrabbleModel(playerList);
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);
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

    @Test
    public void testAIPlayOptimalWord() {
        ArrayList<Player> playerList = new ArrayList<>();
        AI ai = new AI("AI");
        playerList.add(ai);

        ScrabbleModel game = new ScrabbleModel(playerList);
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);

        // Set the rack to contain tiles for a valid word
        ArrayList<Tile> AIT = new ArrayList<>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 4));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 1));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 4));
        ai.setRack(AIT);

        // AI should play a valid word (e.g., "CHEESY")
        assertEquals("play", ai.play());
        assertTrue(game.getWordsInPlay().contains("CHEESY"));
    }

    @Test
    public void testAIPlayExchange() {
        ArrayList<Player> playerList = new ArrayList<>();
        AI ai = new AI("AI");
        playerList.add(ai);

        ScrabbleModel game = new ScrabbleModel(playerList);
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);

        // Set the rack to contain tiles that cannot form a word
        ArrayList<Tile> AIT = new ArrayList<>();
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        ai.setRack(AIT);

        // AI should pass
        assertEquals("exchange", ai.play());
    }

    @Test
    public void testAIPlayPassFromExchange() {
        ArrayList<Player> playerList = new ArrayList<>();
        AI ai = new AI("AI");
        playerList.add(ai);

        ScrabbleModel game = new ScrabbleModel(playerList);
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);
        game.getGameBag().emptyBag();

        assertTrue(game.getGameBag().isEmpty());

        // Set the rack to contain tiles that cannot form a word
        ArrayList<Tile> AIT = new ArrayList<>();
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        AIT.add(new Tile('Q', 10));
        ai.setRack(AIT);

        // AI should pass as bag is empty
        assertEquals("pass", ai.play());
    }

    @Test
    public void testAIPlayPassFromPlay() {
        ArrayList<Player> playerList = new ArrayList<>();
        AI ai = new AI("AI");
        playerList.add(ai);

        ScrabbleModel game = new ScrabbleModel(playerList);
        ai.setBoard(game.getGameBoard());
        ai.setModel(game);

        game.getGameBoard().setAllPositionsOccupied();

        // Set the rack to contain tiles that cannot form a word
        ArrayList<Tile> AIT = new ArrayList<>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 4));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 1));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 4));
        ai.setRack(AIT);

        // AI should pass as it can form a word it just can't place it this turn
        assertEquals("pass", ai.play());
    }
}