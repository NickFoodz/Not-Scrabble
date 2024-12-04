import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main GUI class for a game of Scrabble. Contains the Frame with the necessary components to play
 *
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleView extends JFrame implements Serializable {
    private JFrame frame; // main frame for the game
    private ScrabbleBoardPanel boardPanel; // panel for the board
    private Tile selectedTile; // tile the player currently has selected
    private PlayerRackPanel playerRackPanel; // the players' rack
    private ScrabbleModel game; // the game instance
    private HashMap<Player, JLabel> playerScoreLabel = new HashMap<>() {
    };
    private JPanel scores = new JPanel();
    private HashMap<Integer, Character> blankRedo; // stores the letter chosen for a blank tile before undoing it
    private Integer numBlanks; // number of blanks played in a turn

    /**
     * Constructor for class ScrabbleView
     */
    public ScrabbleView() {
        // initiate variables for blank undo/redo information
        blankRedo = new HashMap<>();
        numBlanks = 0;

        // partially set up frame
        frame = new JFrame("Not Scrabble"); // create JFrame with title of main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);

        //Get the number of players, reject non-integer inputs
        int numPlayers = 0;
        while (numPlayers <= 0 | numPlayers > 4) {
            String getPlayers = (JOptionPane.showInputDialog(frame, "Enter the number of human players"));
            try {
                numPlayers = Integer.parseInt(getPlayers);
                if (numPlayers > 4) {
                    JOptionPane.showMessageDialog(frame, "Only 4 players or fewer can play at a time");
                }
            } catch (NumberFormatException e) {
                if (getPlayers == null) {
                    System.exit(0);
                }
                JOptionPane.showMessageDialog(frame, "Value must be an integer");
            }
        }

        int numAI = 0;
        boolean retry = false;
        String getNumAI;
        do {
            getNumAI = (JOptionPane.showInputDialog(frame, "Enter the number of AI Players?"));
            try {
                numAI = Integer.parseInt(getNumAI);
                if (numAI + numPlayers > 4) {
                    JOptionPane.showMessageDialog(frame, "Only 4 players or fewer can play at a time");
                    retry = true;
                } else {
                    retry = false;
                }
            } catch (NumberFormatException e) {
                if (getNumAI == null) {
                    System.exit(0);
                }
                JOptionPane.showMessageDialog(frame, "Value must be an integer");
                retry = true;
            }
        } while ((numPlayers + numAI) > 4 | retry);

        // initialize game with specified number of players
        this.game = new ScrabbleModel(numPlayers, this, numAI);


        // initialize player rack, score, and board panels
        playerRackPanel = new PlayerRackPanel(game.getCurrentPlayer().getRack(), this);
        boardPanel = new ScrabbleBoardPanel(playerRackPanel, this);

        //Player scores panel
        scores.setLayout(new GridLayout(game.getPlayers().size(), 0));
        for (Player player : game.getPlayers()) {
            JLabel playerScore = new JLabel(player.getName() + " score: " + player.getScore());
            playerScoreLabel.put(player, playerScore);
            scores.add(playerScore);
        }

        JPanel saveLoad = new JPanel();
        JButton save = new JButton("Save Game");
        JButton load = new JButton("Load Game");
        save.addActionListener(e -> {
            try {
                saveGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        load.addActionListener(e -> {
            try {
                loadGame();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        saveLoad.add(save);
        saveLoad.add(load);

        // set up rest of frame
        frame.add(saveLoad, BorderLayout.WEST);
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
        playerRackPanel.clearActionsPerformed(); // clears actions performed for next turn
        game.getCurrentPlayer().clearActionsPerformed(); // clears actions performed for next turn
        clearBlankRedo(); // clears information for blanks played this turn in prep for next turn

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
        //Handle AI at the end of the turn after the player is switched
        while (game.getCurrentPlayer().checkAIPlayer()) {
            game.handleAI(game.getCurrentPlayer());
            playerRackPanel.clearExchangePanel(); // clear exchange panel
            updateViewForCurrentPlayer();  // refresh display after pass
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
        playerRackPanel.clearActionsPerformed(); // clears actions performed for next turn
        game.getCurrentPlayer().clearActionsPerformed(); // clears actions performed for next turn
        clearBlankRedo(); // clears information for blanks played this turn in prep for next turn

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
        //Handle AI at the end of the turn after the player is switched
        while (game.getCurrentPlayer().checkAIPlayer()) {
            game.handleAI(game.getCurrentPlayer());
            playerRackPanel.clearExchangePanel(); // clear exchange panel
            updateViewForCurrentPlayer();  // refresh display after pass
        }
    }

    /**
     * Handle Exchange
     */
    public void handleSwapAction() {
        boardPanel.revertTiles(game.getCurrentPlayer()); // clear any tiles that were placed on board before swap

        // get tiles to exchange
        ArrayList<String> tilesToExchange = new ArrayList<>();
        for (Tile tile : playerRackPanel.getSelectedTilesForExchange().values()) {
            tilesToExchange.add(String.valueOf(tile.getLetter()));
        }
        // set tiles to exchange
        game.getCurrentPlayer().setTilesToExchange(tilesToExchange);

        // handle exchange
        game.handleExchange(game.getCurrentPlayer());

        playerRackPanel.clearExchangePanel(); // clear exchange panel
        updateViewForCurrentPlayer();  // refresh display after swap
        playerRackPanel.clearActionsPerformed(); // clears actions performed for next turn
        game.getCurrentPlayer().clearActionsPerformed(); // clears actions performed for next turn
        clearBlankRedo(); // clears information for blanks played this turn in prep for next turn

        // check if game is over
        if (game.isGameOver()) {
            displayWinnerScreen();
        }
        //Handle AI at the end of the turn after the player is switched
        while (game.getCurrentPlayer().checkAIPlayer()) {
            game.handleAI(game.getCurrentPlayer());
            playerRackPanel.clearExchangePanel(); // clear exchange panel
            updateViewForCurrentPlayer();  // refresh display after pass
        }
    }

    /**
     * Handle Undo
     */
    public void handleUndoAction() {
        if (game.getCurrentPlayer().getActionsPerformed() != null && game.getCurrentPlayer().getActionCounter() != 0) {

            HashMap<Integer, HashMap<Tile, Boolean>> actionMap = game.getCurrentPlayer().getActionsPerformed();
            Tile tile = actionMap.get(game.getCurrentPlayer().getActionCounter()).keySet().iterator().next();
            Boolean isExchange = actionMap.get(game.getCurrentPlayer().getActionCounter()).get(tile);

            if (isExchange) {
                int index = playerRackPanel.getSelectedTilesForExchange().lastEntry().getKey();
                if (index != -1) {
                    playerRackPanel.getExchangeButtons().get(index).revertExchangeTile();
                    playerRackPanel.getSelectedTilesForExchange().remove(index);
                    game.getCurrentPlayer().getTilesToExchange().remove(String.valueOf(tile.getLetter()));
                }
            } else {
                // Undo action was on the board
                Position position = game.getCurrentPlayer().getTilesPlayed().lastEntry().getKey();
                if (position != null) {
                    int row = position.getRow();
                    int col = position.getCol();
                    boardPanel.getBoardButtons()[row][col].revertTile();
                    game.getCurrentPlayer().getTilesPlayed().remove(position);
                    if (tile.isBlank()){
                        numBlanks++;
                        blankRedo.put(numBlanks, tile.getLetter());
                        tile.setLetter(' ');
                    }
                }
            }
            // Re-enable the corresponding button on the player's rack
            HashMap<Integer, JButton> rackButtonActions = playerRackPanel.getActionsPerformed();
            JButton buttonToEnable = rackButtonActions.get(playerRackPanel.getActionCounter());
            buttonToEnable.setEnabled(true);

            game.getCurrentPlayer().decrementActionCounter();
            playerRackPanel.decrementActionCounter();
        }
    }

    /**
     * Handle Redo
     */
    public void handleRedoAction() {
        if (game.getCurrentPlayer().getActionsPerformed() != null && game.getCurrentPlayer().getActionsPerformed().containsKey(game.getCurrentPlayer().getActionCounter() + 1)) {

            HashMap<Integer, HashMap<Tile, Boolean>> actionMap = game.getCurrentPlayer().getActionsPerformed();
            Tile tile = actionMap.get(game.getCurrentPlayer().getActionCounter() + 1).keySet().iterator().next();
            Boolean isExchange = actionMap.get(game.getCurrentPlayer().getActionCounter() + 1).get(tile);

            if (isExchange) {
                int index = -1;
                for (ScrabbleButton currentButton : playerRackPanel.getExchangeButtons()) {
                    if (currentButton.isEmpty()) {
                        index = playerRackPanel.getExchangeButtons().indexOf(currentButton);
                        playerRackPanel.getExchangeButtons().get(index).placeTile(tile, true);
                        break;
                    }
                }
                if (index != -1) {
                    playerRackPanel.getSelectedTilesForExchange().put(index, tile);
                    game.getCurrentPlayer().getTilesToExchange().add(String.valueOf(tile.getLetter()));
                }
            } else {
                // Redo action was on the board
                HashMap<Integer, Position> actionPosition = game.getCurrentPlayer().getActionsPerformedPositions();
                Position position = actionPosition.get(game.getCurrentPlayer().getActionCounter() + 1);
                if (position != null) {
                    int row = position.getRow();
                    int col = position.getCol();
                    if (tile.isBlank()){
                        tile.setLetter(blankRedo.get(numBlanks));
                        numBlanks--;
                    }
                    boardPanel.getBoardButtons()[row][col].placeTile(tile, false);
                    game.getCurrentPlayer().getTilesPlayed().put(position, tile);
                }
            }
            // Re-disable the corresponding button on the player's rack
            HashMap<Integer, JButton> rackButtonActions = playerRackPanel.getActionsPerformed();
            JButton buttonToDisable = rackButtonActions.get(playerRackPanel.getActionCounter() + 1);
            buttonToDisable.setEnabled(false);

            game.getCurrentPlayer().incrementActionCounter();
            playerRackPanel.incrementActionCounter();
        }
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

    /**
     * Called when the game is over, a window showing each players' score as well the winner is displayed
     * Quit will exit the program
     */
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

    /**
     * Panel to get input on if the user wishes to continue the game
     *
     * @param scorelessTurns the number of consecutive scoreless turns
     * @return 1 if they wish to continue, 0 otherwise
     */
    public int showGameOverDialog(int scorelessTurns) {
        // Create a custom Yes/No confirmation dialog
        return JOptionPane.showConfirmDialog(
                this, scorelessTurns + " scoreless turns have passed, would you like to continue the game?", "Game Over Confirmation",
                JOptionPane.YES_NO_OPTION
        );
    }

    /**
     * Loads the game from a java serialization using the filename
     *
     * @throws IOException            if file is not found
     * @throws ClassNotFoundException if Class is not found
     */
    private void loadGame() throws IOException, ClassNotFoundException {
        String fileName = JOptionPane.showInputDialog("Please enter the name of the save game file.");
        game.loadGame(fileName);
        updateFromLoad();
    }

    /**
     * Saves the game using the model's serialization method
     *
     * @throws IOException
     */
    private void saveGame() throws IOException {
        String fileName = JOptionPane.showInputDialog("Please enter a name for the Save");
        game.saveGame(fileName);
    }

    /**
     * Updates the GUI from a load.
     */
    public void updateFromLoad() {
        boardPanel.updateBoard();
        updatePlayerScoreLabel();
        updateViewForCurrentPlayer();
        frame.repaint();
        //Need to implement a method to update the GUI
    }

    /**
     * Updates the scores panel
     */
    private void updatePlayerScoreLabel() {
        scores.removeAll();
        for (Player player : game.getPlayers()) {
            JLabel playerScore = new JLabel(player.getName() + " score: " + player.getScore());
            playerScoreLabel.put(player, playerScore);
            scores.add(playerScore);
        }
        scores.repaint();
    }

    /**
     * method to reset the info regarding blank tiles played for undo redo
     */
    private void clearBlankRedo(){
        blankRedo.clear();
        numBlanks = 0;
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
