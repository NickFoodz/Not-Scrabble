import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main GUI class for a game of Scrabble. Contains the Frame with the necessary components to play
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleView extends JFrame{
    private JFrame frame;
    private ScrabbleBoardPanel boardPanel;
    private Tile selectedTile;
    private PlayerRackPanel playerRackPanel;
    private ScrabbleModel game;
    private HashMap<Player, JLabel> playerScoreLabel = new HashMap<>() {};

    /**
     * Constructor for class ScrabbleView
     */
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

    /**
     * Setter for a selected tile
     * @param tile the tile to place
     */
    public void setSelectedTile(Tile tile){
        this.selectedTile =  tile;
    }

    /**
     * Gets the tile you wish to get in order to place
     * @return the selected tile
     */
    public Tile getSelectedTile(){
        return selectedTile;
    }

    /**
     * Updates the view for the current player (shows their rack, updates scores)
     */
    private void updateViewForCurrentPlayer(){
        playerRackPanel.updateRack(game.getCurrentPlayer().getRack());
        playerScoreLabel.get(game.getCurrentPlayer()).setText(game.getCurrentPlayer().getName()+" score: " + game.getCurrentPlayer().getScore());
        frame.repaint();
    }

    /**
     * Getter for current game instance
     * @return game
     */
    public ScrabbleModel getGame() {
        return game;
    }

    /**
     * Getter for player rack panel
     * @return playerRackPanel
     */
    public PlayerRackPanel getPlayerRackPanel() {
        return playerRackPanel;
    }

    /**
     * Handle Play
     */
    public void handlePlayAction() {
        game.handlePlay(game.getCurrentPlayer());
        playerRackPanel.clearExchangePanel();
        updateViewForCurrentPlayer();  // refresh display after play
    }

    /**
     * Handle Pass
     */
    public void handlePassAction() {
        game.handlePass(game.getCurrentPlayer());
        playerRackPanel.clearExchangePanel();
        updateViewForCurrentPlayer();  // refresh display after pass
    }

    /**
     * Handle Exchange
     */
    public void handleSwapAction() {
        game.handleExchange(game.getCurrentPlayer());
        playerRackPanel.clearExchangePanel();
        updateViewForCurrentPlayer();  // refresh display after swap
    }

    /**
     * Handle Undo
     */
    public void handleUndoAction() {
        return;
    }
    /**
     * Getter for frame
     * @return the frame of this ScrabbleView instance
     */
    public Component getFrame() {
        return frame;
    }

    /**
     * Getter for board GUI
     * @return boardPanel
     */
    public ScrabbleBoardPanel getBoardPanel() {
        return boardPanel;
    }

    /**
     * Main method to run and test a game
     * @param args none
     */
    public static void main(String[] args){
        new ScrabbleView();
    }
}
