import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScrabbleView extends JFrame{
    private JFrame frame;
    private ScrabbleBoardPanel boardPanel;
    private Tile selectedTile;
    private PlayerRackPanel playerRackPanel;
    private ScrabbleModel game;
    private HashMap<Player, JLabel> playerScoreLabel = new HashMap<>() {
    };

    public ScrabbleView(){
        // partially set up frame
        frame = new JFrame("Not Scrabble"); // create JFrame with title of main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);

        int numPlayers = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the number of players for the game"));

        // initialize game with specified number of players
        this.game = new ScrabbleModel(numPlayers, this);

        // initialize player rack, score, and board panels
        playerRackPanel = new PlayerRackPanel(game.getCurrentPlayer().getRack(), this);
        boardPanel = new ScrabbleBoardPanel(playerRackPanel, this);

        //Player scores panel
        JPanel scores = new JPanel();
        scores.setLayout(new GridLayout(game.getPlayers().size(), 0));
        for(Player player : game.getPlayers()){
            JLabel playerScore = new JLabel(player.getName()+" score: " + player.getScore());
            playerScoreLabel.put(player, playerScore);
            scores.add(playerScore);
        }


        // set up rest of frame
        frame.add(scores, BorderLayout.EAST);
        frame.add(playerRackPanel, BorderLayout.SOUTH);
        frame.add(boardPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // setter for selected tile
    public void setSelectedTile(Tile tile){
        this.selectedTile =  tile;
    }

    // getter for selected tile
    public Tile getSelectedTile(){
        return selectedTile;
    }

    // view updater for player
    private void updateViewForCurrentPlayer(){
        playerRackPanel.updateRack(game.getCurrentPlayer().getRack());
        playerScoreLabel.get(game.getCurrentPlayer()).setText(game.getCurrentPlayer().getName()+" score: " + game.getCurrentPlayer().getScore());
        frame.repaint();
    }

    // ends turn and moves to next player
    public void endTurn(){
        game.nextPlayerTurn();
        updateViewForCurrentPlayer();
    }

    // getter for current game
    public ScrabbleModel getGame() {
        return game;
    }

    // temp method for handling a play
    public void handlePlayAction() {
        game.handlePlay(game.getCurrentPlayer());
        updateViewForCurrentPlayer();  // refresh display after play
    }

    // temp method for handling a pass
    public void handlePassAction() {
        game.handlePass(game.getCurrentPlayer());
        updateViewForCurrentPlayer();  // refresh display after pass
    }

    // temp method for handling an exchange
    public void handleSwapAction() {
        game.handleExchange(game.getCurrentPlayer());
        updateViewForCurrentPlayer();  // refresh display after swapa
    }

    // method for getting the frame
    public Component getFrame() {
        return frame;
    }

    // method for getting board panel
    public ScrabbleBoardPanel getBoardPanel() {
        return boardPanel;
    }

    public static void main(String[] args){
        new ScrabbleView();
    }
}
