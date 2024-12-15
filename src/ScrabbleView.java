import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

/**
 * The main GUI class for a game of Scrabble. Contains the Frame with the necessary components to play
 *
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
    private Timer turnTimer; // Swing Timer for countdown
    private int timeRemaining; // Time left for the current turn
    private JLabel timerLabel; // Label to display the timer
    private static int TURN_TIME_LIMIT = 0; // Time limit in seconds (adjustable)
    private boolean timedMode; // true if timed mode selected, false otherwise


    /**
     * Constructor for class ScrabbleView
     */
    public ScrabbleView() {
        // partially set up frame
        frame = new JFrame("Not Scrabble"); // create JFrame with title of main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1000);

        //Get the number of players, reject non-integer inputs
        int[] playerAndAI = getPlayerAndAISelection(frame);
        int numPlayers = playerAndAI[0];
        int numAI = playerAndAI[1];


        // initialize game with specified number of players
        this.game = new ScrabbleModel(numPlayers, this, numAI);

        askForXML();


        // initialize player rack, score, and board panels and timer
        playerRackPanel = new PlayerRackPanel(game.getCurrentPlayer().getRack(), this);
        boardPanel = new ScrabbleBoardPanel(playerRackPanel, this);
        turnTimer = new Timer(1000, e -> handleTimerTick());

        //Player scores panel
        scores.setLayout(new GridLayout(game.getPlayers().size() + 4, 0));
        for (Player player : game.getPlayers()) {
            JLabel playerScore = new JLabel(player.getName() + " score: " + player.getScore());
            playerScoreLabel.put(player, playerScore);
            scores.add(playerScore);
        }

        //Legend for premium tiles added to scoreboard
        JLabel doubleLetter = new JLabel("2x Letter Score: Cyan");
        doubleLetter.setOpaque(true);
        doubleLetter.setBackground(Color.cyan);
        JLabel doubleWord = new JLabel("2x Word Score: Pink");
        doubleWord.setOpaque(true);
        doubleWord.setBackground(Color.PINK);
        JLabel tripleLetter = new JLabel("3x Letter Score: Blue");
        tripleLetter.setOpaque(true);
        tripleLetter.setBackground(Color.blue);
        tripleLetter.setForeground(Color.white);
        JLabel tripleWord = new JLabel("3x Word Score: Red");
        tripleWord.setOpaque(true);
        tripleWord.setBackground(Color.red);
        tripleWord.setForeground(Color.white);
        scores.add(doubleLetter);
        scores.add(doubleWord);
        scores.add(tripleLetter);
        scores.add(tripleWord);

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

        timedMode = JOptionPane.showConfirmDialog(
                frame, "Enable Timed Mode?", "Game Mode", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;

        if (timedMode) {
            while (TURN_TIME_LIMIT < 15) {
                String getTimeLimit = (JOptionPane.showInputDialog(frame, "Enter the time given for each turn"));
                try {
                    TURN_TIME_LIMIT = Integer.parseInt(getTimeLimit);
                    if (TURN_TIME_LIMIT < 15) {
                        JOptionPane.showMessageDialog(frame, "Timer must be at least 15 seconds");
                    }
                } catch (NumberFormatException e) {
                    if (getTimeLimit == null) {
                        System.exit(0);
                    }
                    JOptionPane.showMessageDialog(frame, "Value must be an integer");
                }
            }
            // Timer display panel
            timerLabel = new JLabel("Time Remaining: " + TURN_TIME_LIMIT + "s");
            JPanel timerPanel = new JPanel();
            timerPanel.add(timerLabel);
            frame.add(timerPanel, BorderLayout.NORTH);
            startTurnTimer();
        }

        // set up rest of frame
        frame.add(saveLoad, BorderLayout.EAST);
        frame.add(scores, BorderLayout.WEST);
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
        if (timedMode) {
            startTurnTimer();
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
        if (timedMode) {
            startTurnTimer();
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
        if (tilesToExchange.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have not selected any tiles to swap");
            return;
        }
        // set tiles to exchange
        game.getCurrentPlayer().setTilesToExchange(tilesToExchange);

        // handle exchange
        game.handleExchange(game.getCurrentPlayer());

        playerRackPanel.clearExchangePanel(); // clear exchange panel
        updateViewForCurrentPlayer();  // refresh display after swap
        playerRackPanel.clearActionsPerformed(); // clears actions performed for next turn
        game.getCurrentPlayer().clearActionsPerformed(); // clears actions performed for next turn

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
        if (timedMode) {
            startTurnTimer();
        }
    }

    /**
     * Handle Undo
     */
    public void handleUndoAction() {
        if (selectedTile == null) {
            // handleUndo in scrabble model is called
            if (game.handleUndo(game.getCurrentPlayer())) {

                // Re-enable the corresponding button on the player's rack
                HashMap<Integer, JButton> rackButtonActions = playerRackPanel.getActionsPerformed();
                JButton buttonToEnable = rackButtonActions.get(playerRackPanel.getActionCounter());
                buttonToEnable.setEnabled(true);

                playerRackPanel.decrementActionCounter();
            }
        }
    }

    /**
     * Handle Redo
     */
    public void handleRedoAction() {
        if (selectedTile == null) {
            // handleRedo in scrabble model is called
            if (game.handleRedo(game.getCurrentPlayer())) {

                // Re-disable the corresponding button on the player's rack
                HashMap<Integer, JButton> rackButtonActions = playerRackPanel.getActionsPerformed();
                JButton buttonToDisable = rackButtonActions.get(playerRackPanel.getActionCounter() + 1);
                buttonToDisable.setEnabled(false);

                playerRackPanel.incrementActionCounter();
            }
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
        return JOptionPane.showConfirmDialog(this, scorelessTurns + " scoreless turns have passed, would you like to continue the game?", "Game Over Confirmation", JOptionPane.YES_NO_OPTION);
    }

    /**
     * Loads the game from a java serialization using the filename
     *
     * @throws IOException            if file is not found
     * @throws ClassNotFoundException if Class is not found
     */
    private void loadGame() throws IOException, ClassNotFoundException {
        // create a file chooser for loading a game file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Game");
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            game.loadGame(selectedFile.getAbsolutePath()); // pass the file path to load method
            updateFromLoad(); // update GUI
        }
    }

    /**
     * Saves the game using the model's serialization method
     *
     * @throws IOException
     */
    private void saveGame() throws IOException {
        // Create a file chooser for saving a game file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Game");

        // Show the dialog and get the user's selection
        int result = fileChooser.showSaveDialog(frame); // 'frame' is your main JFrame

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            String filePath = selectedFile.getAbsolutePath();

            game.saveGame(filePath); // Pass the file path to the save method
        }
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

    private void startTurnTimer() {
        timeRemaining = TURN_TIME_LIMIT;
        timerLabel.setText("Time Remaining: " + timeRemaining + "s");
        turnTimer.start();
        timerLabel.repaint();
    }

    private void handleTimerTick() {
        timeRemaining--;
        timerLabel.setText("Time Remaining: " + timeRemaining + "s");
        timerLabel.repaint();

        if (timeRemaining <= 0) {
            turnTimer.stop();
            JOptionPane.showMessageDialog(this, game.getCurrentPlayer().getName() + " has run out of time, turn passed.");
            handlePassAction();
        }
    }

    // Function to get the number of players using drop-down menus
    private int[] getPlayerAndAISelection(JFrame frame) {
        // Options for number of human players (1-4)
        Integer[] playerOptions = {1, 2, 3, 4};
        JComboBox<Integer> playerDropdown = new JComboBox<>(playerOptions);

        // Panel for the first selection (number of human players)
        JPanel playerPanel = new JPanel();
        playerPanel.add(new JLabel("Select the number of human players:"));
        playerPanel.add(playerDropdown);

        // Show the dialog and get the number of human players
        int result = JOptionPane.showConfirmDialog(
                frame,
                playerPanel,
                "Select Number of Players",
                JOptionPane.OK_CANCEL_OPTION
        );

        // Exit if the user cancels
        if (result != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        // Get the selected number of players
        int numPlayers = (int) playerDropdown.getSelectedItem();

        // Options for number of AI players based on the number of human players
        Integer[] aiOptions = new Integer[5 - numPlayers];
        for (int i = 0; i < aiOptions.length; i++) {
            aiOptions[i] = i; // 0 to max AI possible
        }

        JComboBox<Integer> aiDropdown = new JComboBox<>(aiOptions);

        // Panel for the second selection (number of AI players)
        JPanel aiPanel = new JPanel();
        aiPanel.add(new JLabel("Select the number of AI players:"));
        aiPanel.add(aiDropdown);

        // Show the dialog and get the number of AI players
        result = JOptionPane.showConfirmDialog(
                frame,
                aiPanel,
                "Select Number of AI Players",
                JOptionPane.OK_CANCEL_OPTION
        );

        // Exit if the user cancels
        if (result != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        // Get the selected number of AI players
        int numAI = (int) aiDropdown.getSelectedItem();

        return new int[]{numPlayers, numAI};
    }

    private void askForXML() {
        int ask = JOptionPane.showConfirmDialog(frame, "Use a custom board?");

        if (ask == JOptionPane.YES_OPTION) {

            //Ask user to select an XML file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Board");

            int result = fileChooser.showSaveDialog(frame); // 'frame' is your main JFrame

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                String filePath = selectedFile.getAbsolutePath();

                game.getGameBoard().importCustomBoardXML(filePath); // Pass the file path to the save method
            }
        }
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
