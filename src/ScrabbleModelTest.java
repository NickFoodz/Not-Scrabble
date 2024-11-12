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
        ArrayList<Tile> hand = game.getCurrentPlayer().getRack();
        //game.handleExchange(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player2);
        //Switch back to player 1
        game.handlePass(game.getCurrentPlayer());
        //Check if rack is different
        assertNotEquals(hand, game.getCurrentPlayer().getRack());
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
    }

    @Test
    public void getGameBoard() {
        //base of each test
        Board board = new Board();
        ArrayList<Player> playerList = new ArrayList<Player>();
        Player player1 = new Player("Andrew");
        Player player2 = new Player("Nick");
        playerList.add(player1);
        playerList.add(player2);
        ScrabbleModel game = new ScrabbleModel(playerList);
        //Assert that the game board is an empty board
        //Check each position of each board and assert they are empty
        //Cannot test as this method is static, cannot use class Board methods to check tiles

    }
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