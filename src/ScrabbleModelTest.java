import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
public class ScrabbleModelTest {

    @Test
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
    }

    @Test
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
        game.handlePass(game.getCurrentPlayer());
        assertEquals(game.getCurrentPlayer(), player2);
    }

    @Test
    public void handlePlay() {
    }

    @Test
    public void nextPlayerTurn() {
    }

    @Test
    public void getCurrentPlayer() {
    }

    @Test
    public void getPlayers() {
    }

    @Test
    public void getGameBoard() {
    }

    @Test
    public void showMessage() {
    }
}



///**
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