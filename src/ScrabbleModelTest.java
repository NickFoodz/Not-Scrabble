import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests main methods in ScrabbleModel
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
        assertEquals(game.getCurrentPlayer(), (player1));
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player2);
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player1);
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
        for(Tile tile : hand){
            if(!game.getCurrentPlayer().getRack().contains(tile)){sameRack = false;}
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
        Tile h = new Tile('H',2);
        Tile i = new Tile('I',1);
        Map<Tile,Position> map = new HashMap<>();
        Position h8 = new Position(7,7);
        Position i8= new Position(7,8);
        Position i9 = new Position(8,8);
        //First test invalid move
        map.put(h, i8);
        map.put(i,i9);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertFalse(game.handlePlay(game.getCurrentPlayer()));
        //Now test valid move
        map.clear();
        map.put(h, h8);
        map.put(i, i8);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));
        System.out.println("handlePlay test successful\n");

    }

    @Test
    public void testPlacements(){

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
        Tile h = new Tile('H',2);
        Tile i = new Tile('I',1);
        Tile t = new Tile('T', 3);
        Map<Tile,Position> map = new HashMap<>();

        //Positions for top left corner of board
        //vertical word (won't work first turn and if no tiles attached)
        Position a1 = new Position(0,0);
        Position a2 = new Position(1,0);
        Position a3 = new Position(2,0);
        //Test for placement for first turn needing to be centered
        map.put(h, a1);
        map.put(i, a2);
        map.put(t, a3);
        game.getCurrentPlayer().setTilesPlayed(map);
        //Should be invalid, as first turn must be H8
        assertFalse(game.handlePlay(game.getCurrentPlayer()));


        //valid for horizontal word in center of board (first turn)
        Position h8 = new Position(7,7);
        Position i8= new Position(7,8);
        Position j8 = new Position(7,9);

        //to check that tiles can only be placed in same row or column
        Position h9 = new Position(8,7);
        //Second test invalid move (not in line)
        map.put(h, h8);
        map.put(i,h9);
        map.put(t,j8);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertFalse(game.handlePlay(game.getCurrentPlayer()));


        //Now test valid move
        map.clear();
        map.put(h, h8);
        map.put(i, i8);
        map.put(t, j8);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));

        //now let second player try to play in corner
        map.clear();
        assertEquals(game.getCurrentPlayer(), player2);
        //Try to play same word in corner
        map.put(h, a1);
        map.put(i, a2);
        map.put(t, a3);
        game.getCurrentPlayer().setTilesPlayed(map);
        //Should be invalid, as no tiles are connected
        assertFalse(game.handlePlay(game.getCurrentPlayer()));

        //Let player 2 play same word, vertically, only needs to place h and t at i7 and i9
        Position i7 = new Position(6,8);
        Position i9 = new Position(6,8);
        Tile h2 = new Tile('H',2);
        Tile t2 = new Tile('T',3);
        map.clear();
        map.put(h2, i7);
        map.put(t2, i9);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));



        System.out.println("testPlacements test successful\n");
    }

    @Test
    public void testScoring(){
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);

        //Create new game
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Check that both player's scores are 0 at start
        assertEquals(player1.getScore(),0);
        assertEquals(player2.getScore(),0);

        //confirm player 1 is the first player
        assertEquals(game.getCurrentPlayer(), (player1));
        Tile h = new Tile('H',2);
        Tile i = new Tile('I',1);
        Tile t = new Tile('T', 3);
        Map<Tile,Position> map = new HashMap<>();

        //valid for horizontal word in center of board (first turn)
        Position h8 = new Position(7,7);
        Position i8= new Position(7,8);
        Position j8 = new Position(7,9);

        //Now test valid move
        map.clear();
        map.put(h, h8);
        map.put(i, i8);
        map.put(t, j8);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));
        //Tests that word hit gave 6 points
        assertEquals(player1.getScore(), 6);

        assertEquals(game.getCurrentPlayer(), player2);
        assertEquals(game.getCurrentPlayer().getScore(), 0);
        game.handlePass(game.getCurrentPlayer());
        //Pass doesn't give points
        assertEquals(player2.getScore(), 0);
        assertEquals(game.getCurrentPlayer(), player1);
        //Let player 1 play same word, vertically, only needs to place h and t at i7 and i9
        Position i7 = new Position(6,8);
        Position i9 = new Position(6,8);
        Tile h2 = new Tile('H',2);
        Tile t2 = new Tile('T',3);
        map.clear();
        map.put(h2, i7);
        map.put(t2, i9);
        game.getCurrentPlayer().setTilesPlayed(map);
        assertTrue(game.handlePlay(game.getCurrentPlayer()));
        //Check that player's score added correctly (6 from before, played a 2+3 = 5. Should be 11)
        assertEquals(player1.getScore(), 11);
        System.out.println("testScoring test successful\n");

    }



    @Test
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
    public void getPlayers() {
        //base of each test
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Test that current game players list has same players as the one we made
        assertEquals(game.getPlayers(),playerList);
        System.out.println("getPlayers test successful\n");
    }

    //    @Test - Nothing to test
//    public void getGameBoard() {
//        //base of each test
//        Board board = new Board();
//        ArrayList<Player> playerList = new ArrayList<Player>();
//        Player player1 = new Player("Andrew");
//        Player player2 = new Player("Nick");
//        playerList.add(player1);
//        playerList.add(player2);
//        ScrabbleModel game = new ScrabbleModel(playerList);
//        //Assert that the game board is an empty board
//        //Check each position of each board and assert they are empty
//        //Cannot test as this method is static, cannot use class Board methods to check tiles
//
//    }
}






// * Constructor primarily used for test-cases. Does not use GUI, just tests model.
// */
//public ScrabbleModel(ArrayList<Player> playerList){
//    gameBoard = new Board();
//    gameBag = new Bag()
//    players = playerList;
//    gameOver = false;
//    successiveScorelessTurns = 0;
//    turnNumber = 0;
//    this.wordsInPlay = new ArrayList<>();
//    dictionary = new ArrayList<String>();
//    dictionary = createDictionary();
//
//    for(Player player : players){
//        player.drawTiles(gameBag, 7);
//    }
//    currentPlayerIndex = 0;
//
//}

//private void testLetterFrequencies() {
//    //Need only one of each letter to not fail exchange test
//    int oneOfEach = 1;
//    letterFrequencies.put('E', oneOfEach);
//    letterFrequencies.put('A', oneOfEach);
//    letterFrequencies.put('I', oneOfEach);
//    letterFrequencies.put('O', oneOfEach);
//    letterFrequencies.put('N', oneOfEach);
//    letterFrequencies.put('R', oneOfEach);
//    letterFrequencies.put('T', oneOfEach);
//    letterFrequencies.put('L', oneOfEach);
//    letterFrequencies.put('S', oneOfEach);
//    letterFrequencies.put('U', oneOfEach);
//    letterFrequencies.put('D', oneOfEach);
//    letterFrequencies.put('G', oneOfEach);
//    letterFrequencies.put('B', oneOfEach);
//    letterFrequencies.put('C', oneOfEach);
//    letterFrequencies.put('M', oneOfEach);
//    letterFrequencies.put('P', oneOfEach);
//    letterFrequencies.put('F', oneOfEach);
//    letterFrequencies.put('H', oneOfEach);
//    letterFrequencies.put('V', oneOfEach);
//    letterFrequencies.put('W', oneOfEach);
//    letterFrequencies.put('Y', oneOfEach);
//    letterFrequencies.put('K', oneOfEach);
//    letterFrequencies.put('J', oneOfEach);
//    letterFrequencies.put('X', oneOfEach);
//    letterFrequencies.put('Q', oneOfEach);
//    letterFrequencies.put('Z', oneOfEach);
//}//