import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests main methods in ScrabbleModel
 *
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleModelTest {

    @Test
    /**
     * Tests the handlePass method
     */
    public void handlePass() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);

        //Create new game from the model's testing constructor
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Check that its player 1, then pass
        assertEquals(game.getCurrentPlayer(), (player1));
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player2);
        //Pass back to player 1
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player1);
        //Confirm that the racks switched too
        assertNotEquals(game.getCurrentPlayer().getRack(), player2.getRack());
        System.out.println("handlePass test successful\n");
    }

    @Test
    /**
     * Tests the handleExchange Method
     */
    public void handleExchange() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);

        //Create new game from the model's testing constructor
        ScrabbleModel game = new ScrabbleModel(playerList);
        assertEquals(game.getCurrentPlayer(), (player1));

        //Save current rack before exchange to check if it is different from next
        ArrayList<Tile> hand = new ArrayList<>();
        hand.addAll(game.getCurrentPlayer().getRack());
        //for(Tile tile : hand){System.out.print(tile.getLetter()+ "|");}
        //System.out.println();
        ArrayList<String> toExchange = new ArrayList<>();
        //Set two tiles to exchange
        toExchange.add(String.valueOf(game.getCurrentPlayer().getRack().get(0).getLetter()));

        //Prints letter to be exchanged
        //for(String str: toExchange){System.out.println(str);}

        //Set tiles to exchange and use turn to exchange
        game.getCurrentPlayer().setTilesToExchange(toExchange);
        game.handleExchange(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player2);
        //Switch back to player 1
        game.handlePass(game.getCurrentPlayer());

        //Prints the new rack
        //System.out.println("Rack from game:");
        //for(Tile tile : game.getCurrentPlayer().getRack()){System.out.print(tile.getLetter()+ "|");}
        //System.out.println("\nRack stored from before exchange:");
        //for(Tile tile : hand){System.out.print(tile.getLetter()+ "|");}

        boolean sameRack = true;
        for (Tile tile : hand) {
            if (!game.getCurrentPlayer().getRack().contains(tile)) {
                sameRack = false;
            }
        }
        //Check if rack is different
        assertFalse(sameRack);
        System.out.println("handleExchange test successful\n");
    }

    @Test
    /**
     * Tests the handlePlay method
     */
    public void handlePlay() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);

        //Create new game
        ScrabbleModel game = new ScrabbleModel(playerList);
        //confirm player 1 is the first player
        assertEquals(game.getCurrentPlayer(), (player1));
        Tile h = new Tile('H', 2);
        Tile i = new Tile('I', 1);
        Map<Position, Tile> map = new HashMap<>();
        Position h8 = new Position(7, 7);
        Position i8 = new Position(7, 8);
        Position i9 = new Position(8, 8);
        //First test invalid move
        map.put(i8, h);
        map.put(i9, i);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertFalse(game.handlePlay(game.getCurrentPlayer()));
        //Now test valid move
        map.clear();
        map.put(h8, h);
        map.put(i8, i);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));
        System.out.println("handlePlay test successful\n");

    }

    @Test
    /**
     * Tests that tile placement is accurate
     */
    public void testPlacements() {

        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);

        //Create new game
        ScrabbleModel game = new ScrabbleModel(playerList);
        //confirm player 1 is the first player
        assertEquals(game.getCurrentPlayer(), (player1));
        Tile h = new Tile('H', 2);
        Tile i = new Tile('I', 1);
        Tile t = new Tile('T', 3);
        Map<Position, Tile> map = new HashMap<>();

        //Positions for top left corner of board
        //vertical word (won't work first turn and if no tiles attached)
        Position a1 = new Position(0, 0);
        Position a2 = new Position(1, 0);
        Position a3 = new Position(2, 0);
        //Test for placement for first turn needing to be centered
        map.put(a1, h);
        map.put(a2, i);
        map.put(a3, t);
        game.getCurrentPlayer().setTilesPlayed(map);
        //Should be invalid, as first turn must be H8
        assertFalse(game.handlePlay(game.getCurrentPlayer()));


        //valid for horizontal word in center of board (first turn)
        Position h8 = new Position(7, 7);
        Position i8 = new Position(7, 8);
        Position j8 = new Position(7, 9);

        //to check that tiles can only be placed in same row or column
        Position h9 = new Position(8, 7);
        //Second test invalid move (not in line)
        map.put(h8, h);
        map.put(h9, i);
        map.put(j8, t);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertFalse(game.handlePlay(game.getCurrentPlayer()));


        //Now test valid move
        map.clear();
        map.put(h8, h);
        map.put(i8, i);
        map.put(j8, t);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));

        //now let second player try to play in corner
        map.clear();
        assertEquals(game.getCurrentPlayer(), player2);
        //Try to play same word in corner
        map.put(a1, h);
        map.put(a2, i);
        map.put(a3, t);
        game.getCurrentPlayer().setTilesPlayed(map);
        //Should be invalid, as no tiles are connected
        assertFalse(game.handlePlay(game.getCurrentPlayer()));

        //Let player 2 play same word, vertically, only needs to place h and t at i7 and i9
        Position i7 = new Position(6, 8);
        Position i9 = new Position(6, 8);
        Tile h2 = new Tile('H', 2);
        Tile t2 = new Tile('T', 3);
        map.clear();
        map.put(i7, h2);
        map.put(i9, t2);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));


        System.out.println("testPlacements test successful\n");
    }


    @Test
    public void testPremiumScoring() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Test that current player is player 1
        assertEquals(game.getCurrentPlayer(), (player1));
        //Map to set current player's tiles to
        Map<Position, Tile> map = new HashMap<>();
        Tile t1 = new Tile('T', 2);
        Tile e = new Tile('E', 1);
        Tile s = new Tile('S', 2);
        Tile t2 = new Tile('T', 2);
        Tile s2 = new Tile('S', 2);
        //Writes out "Tests" horizontally from h8 to l8
        Position h8 = new Position(7, 7);
        Position i8 = new Position(7, 8);
        Position j8 = new Position(7, 9);
        Position k8 = new Position(7, 10);
        Position l8 = new Position(7, 11);
        //Place in map
        map.put(h8, t1);
        map.put(i8, e);
        map.put(j8, s);
        map.put(k8, t2);
        map.put(l8, s2);
        game.getCurrentPlayer().setTilesPlayed(map);
        game.handlePlay(game.getCurrentPlayer());
        //We are now on player 2. Player 2 will turn "TESTS" into "ATTESTS"
        map.clear();
        Position g8 = new Position(7, 6);
        Position f8 = new Position(7, 5);
        Tile a = new Tile('A', 1);
        Tile t3 = new Tile('T', 2);
        map.put(f8, a);
        map.put(g8, t3);
        game.getCurrentPlayer().setTilesPlayed(map);
        game.handlePlay(game.getCurrentPlayer());
        map.clear();
        //Player 1 should get score of 22 [(T:2 + E:1: + S:2 + T:2 + S:2*2)*2] = 11*2 = 22
        assertEquals(game.getCurrentPlayer().getScore(), 22);
        game.handlePass(game.getCurrentPlayer());
        //Player 2 should have score for entire word "attests" but without premium
        //11 from tests + 1 from A, 2 from new T = 14
        assertEquals(game.getCurrentPlayer().getScore(), 12);
        game.handlePass(game.getCurrentPlayer());
        //Back to player one, who will play the word Fists through 2 triple letters
        Tile f = new Tile('F', 3);
        Tile i = new Tile('I', 1);
        Tile s3 = new Tile('S', 2);
        Tile t4 = new Tile('T', 2);
        //Positions in order
        Position j6 = new Position(5, 9);
        Position j7 = new Position(6, 9);
        Position j9 = new Position(8, 9);
        Position j10 = new Position(9, 9);
        //Tiles F and S4 will be over triple letter squares.
        map.put(j6, f);
        map.put(j7, i);
        map.put(j9, t4);
        map.put(j10, s3);
        game.getCurrentPlayer().setTilesPlayed(map);
        game.handlePlay(game.getCurrentPlayer());
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer().getScore(), 42);
        game.handlePass(game.getCurrentPlayer());
        //clear map and give player tiles to Play "SLEEPY" from J10->O10 (I am sleepy its 3:33 a.m.)
        map.clear();
        assertEquals(game.getCurrentPlayer(), player2);
        Tile l = new Tile('L', 3);
        Tile e2 = new Tile('E', 1);
        Tile e3 = new Tile('E', 1);
        Tile p = new Tile('P', 2);
        Tile y = new Tile('Y', 4);
        Position k10 = new Position(9, 10);
        Position l10 = new Position(9, 11);
        Position m10 = new Position(9, 12);
        Position n10 = new Position(9, 13);
        Position o10 = new Position(9, 14);
        map.put(k10, l);
        map.put(l10, e2);
        map.put(m10, e3);
        map.put(n10, p);
        map.put(o10, y);
        //Should add score of 2 + 3 + 1 + 1 + 3*2 + 4 =17 for total of 17 + 12 = 29
        game.getCurrentPlayer().setTilesPlayed(map);
        game.handlePlay(game.getCurrentPlayer());
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer().getScore(), 29);
        game.handlePass(game.getCurrentPlayer());
        //Back to player 1, will play the word "TRY" from O8 to O10, to use a triple word multiplier
        map.clear();
        Position o8 = new Position(7, 14);
        Position o9 = new Position(8, 14);
        Tile t5 = new Tile('T', 2);
        Tile r = new Tile('R', 3);
        map.put(o8, t5);
        map.put(o9, r);
        game.getCurrentPlayer().setTilesPlayed(map);
        game.handlePlay(game.getCurrentPlayer());
        game.handlePass(game.getCurrentPlayer());
        //Score should be 69; 42 + (2+3+4)*3 = 69
        assertEquals(game.getCurrentPlayer().getScore(), 69);
    }

    @Test
    /**
     * Tests that turn order functions correctly
     */
    public void nextPlayerTurn() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Test that current player is player 1
        assertEquals(game.getCurrentPlayer(), (player1));
        //Switches player and should be player 2
        game.nextPlayerTurn();
        assertEquals(game.getCurrentPlayer(), player2);
        System.out.println("nextPlayerTurn test successful\n");
    }

    @Test
    /**
     * Tests that current player is accurate
     */
    public void getCurrentPlayer() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Test that current player is player 1
        assertEquals(game.getCurrentPlayer(), (player1));
        System.out.println("getCurrentPlayer test successful\n");

    }

    @Test
    /**
     * Tests that list of active players is accurate
     */
    public void getPlayers() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Test that current game players list has same players as the one we made
        assertEquals(game.getPlayers(), playerList);
        System.out.println("getPlayers test successful\n");
    }
}