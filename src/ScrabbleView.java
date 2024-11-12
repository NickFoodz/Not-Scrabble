import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The main GUI class for a game of Scrabble. Contains the Frame with the necessary components to play
 *
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleView extends JFrame {
    private JFrame frame;
    private ScrabbleBoardPanel boardPanel;
    private Tile selectedTile;
    private PlayerRackPanel playerRackPanel;
    private ScrabbleModel game;
    private HashMap<Player, JLabel> playerScoreLabel = new HashMap<>() {
    };

    /**
     * Constructor for class ScrabbleView
     */
    public ScrabbleView() {
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
        for (Player player : game.getPlayers()) {
            JLabel playerScore = new JLabel(player.getName() + " score: " + player.getScore());
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
     *
     * @param tile the tile to place
     */
    public void setSelectedTile(Tile tile) {
        this.selectedTile = tile;
    }

    /**
     * Gets the tile you wish to get in order to place
     *
     * @return the selected tile
     */
    public Tile getSelectedTile() {
        return selectedTile;
    }

    /**
     * Updates the view for the current player (shows their rack, updates scores)
     */
    private void updateViewForCurrentPlayer() {
        playerRackPanel.updateRack(game.getCurrentPlayer().getRack());
        playerScoreLabel.get(game.getLastPlayer()).setText(game.getLastPlayer().getName() + " score: " + game.getLastPlayer().getScore());
        frame.repaint();
    }

    /**
     * Getter for current game instance
     *
     * @return game
     */
    public ScrabbleModel getGame() {
        return game;
    }

    /**
     * Getter for player rack panel
     *
     * @return playerRackPanel
     */
    public PlayerRackPanel getPlayerRackPanel() {
        return playerRackPanel;
    }

    /**
     * Handle Play
     */
    public void handlePlayAction() {
        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
        game.handlePlay(game.getCurrentPlayer());
        playerRackPanel.clearExchangePanel();
        updateViewForCurrentPlayer();  // refresh display after play

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
    }

    /**
     * Handle Pass
     */
    public void handlePassAction() {
        boardPanel.revertTiles(game.getCurrentPlayer()); // clear any tiles that were placed on board before pass
        game.handlePass(game.getCurrentPlayer());
        playerRackPanel.clearExchangePanel(); // clear exchange panel
        updateViewForCurrentPlayer();  // refresh display after pass

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
    }

    /**
     * Handle Exchange
     */
    public void handleSwapAction() {
        boardPanel.revertTiles(game.getCurrentPlayer()); // clear any tiles that were placed on board before swap

        // get tiles to exchange
        ArrayList<String> tilesToExchange = new ArrayList<>();
        for (Tile tile : playerRackPanel.getSelectedTilesForExchange()) {
            tilesToExchange.add(String.valueOf(tile.getLetter()));
        }
        // set tiles to exchange
        game.getCurrentPlayer().setTilesToExchange(tilesToExchange);

        // handle exchange
        game.handleExchange(game.getCurrentPlayer());

        playerRackPanel.clearExchangePanel(); // clear exchange panel
        updateViewForCurrentPlayer();  // refresh display after swap

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
    }

    /**
     * Handle Undo
     */
    public void handleUndoAction() {
        return;
    }

    /**
     * Getter for frame
     *
     * @return the frame of this ScrabbleView instance
     */
    public Component getFrame() {
        return frame;
    }

    /**
     * Getter for board GUI
     *
     * @return boardPanel
     */
    public ScrabbleBoardPanel getBoardPanel() {
        return boardPanel;
    }

    private void displayWinnerScreen() {
        JDialog winnerDialog = new JDialog((Frame) null, "Game Over", true);
        winnerDialog.setLayout(new BorderLayout());
        winnerDialog.setSize(400, 300);

        // Create a panel to display scores
        JPanel scorePanel = new JPanel(new GridLayout(game.getPlayers().size() + 1, 1));
        JLabel winnerLabel = new JLabel("Congratulations to the winner!");

        int highestScore = -1;
        Player winner = null;
        for (Player player : game.getPlayers()) {
            JLabel scoreLabel = new JLabel(player.getName() + ": " + player.getScore());
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
                winner = player;
            }
            scorePanel.add(scoreLabel);
        }

        winnerLabel.setText("Winner: " + winner.getName() + " with " + highestScore + " points!");
        scorePanel.add(winnerLabel);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton quitButton = new JButton("Quit");

        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(quitButton);

        winnerDialog.add(scorePanel, BorderLayout.CENTER);
        winnerDialog.add(buttonPanel, BorderLayout.SOUTH);
        winnerDialog.setVisible(true);
    }

    public int showGameOverDialog(int scorelessTurns) {
        // Create a custom Yes/No confirmation dialog
        return JOptionPane.showConfirmDialog(
                this, scorelessTurns + " scoreless turns have passed, would you like to continue the game?", "Game Over Confirmation",
                JOptionPane.YES_NO_OPTION
        );
    }

    /**
     * Main method to run and test a game
     *
     * @param args none
     */
    public static void main(String[] args) {
        new ScrabbleView();
    }
}
