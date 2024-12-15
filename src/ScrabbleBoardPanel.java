import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.lang.Character;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class ScrabbleBoardPanel represents the Scrabble Board in a game of scrabble. It provides
 * the necessary GUI and methods to play the game with an interactive board
 *
 * @version 1
 */
public class ScrabbleBoardPanel extends JPanel implements Serializable {
    private final ScrabbleButton[][] boardButtons; // the board buttons
    private final PlayerRackPanel playerRackPanel; // the players rack
    private final ScrabbleView scrabbleView; // reference to scrabble view

    public ScrabbleBoardPanel(PlayerRackPanel rackPanel, ScrabbleView view) {
        this.playerRackPanel = rackPanel;
        this.scrabbleView = view;  // Set the reference to ScrabbleView
        setLayout(new GridLayout(15, 15));
        setBackground(new Color(50, 138, 73));

        // initialize button array and add buttons to panel
        boardButtons = new ScrabbleButton[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Position buttonPosition = new Position(row, col);
                ScrabbleButton button = new ScrabbleButton(row, col);
                char ch = 'A';
                button.setText(Character.toString(ch + col) + (row + 1));
                switch (scrabbleView.getGame().getGameBoard().getPremiumPositions().get(buttonPosition.toString())) {
                    case 2:
                        button.setColor(Color.cyan);
                        break;
                    case 3:
                        button.setColor(Color.pink);
                        break;
                    case 4:
                        button.setColor(Color.blue);
                        break;
                    case 5:
                        button.setColor(Color.red);
                        break;
                    case null, default:
                        button.setColor(Color.white);
                        break;
                }
                button.addActionListener(new BoardButtonClickListener(row, col));
                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    /**
     * Updates from a load
     */
    public void updateBoard() {
        Board board = scrabbleView.getGame().getGameBoard();
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                Position buttonPosition = new Position(row, col);
                //First, reset the square
                char ch = 'A';
                boardButtons[row][col].revertTile();

                //If the board position has a tile, make the boardbutton have that tile
                if (board.getPosition(row, col).getTile() != null) {
                    boardButtons[row][col].placeTile(board.getPosition(row, col).getTile(), false);
                    boardButtons[row][col].setEnabled(false);
                }

            }
        }
    }

    // helper class for button click action listener
    private class BoardButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        /**
         * Action listener for board buttons
         *
         * @param row the row of the button selected
         * @param col the column of the button selected
         */
        public BoardButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        /**
         * Action event for board buttons that will place the selected tile
         * on the board and add it to played tiles of the player for play action
         *
         * @param e the event to be processed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            ScrabbleModel model = scrabbleView.getGame();
            Tile tile = scrabbleView.getSelectedTile();
            if (tile != null) {
                // Place tile in GUI board
                boardButtons[row][col].placeTile(tile, false);

                // Disable button to prevent further modification
                boardButtons[row][col].setEnabled(false);
                scrabbleView.setSelectedTile(null);

                // Add tile to playedTiles field for current player and add it to placedTiles
                Position position = model.getGameBoard().getPosition(row, col);
                scrabbleView.getGame().getCurrentPlayer().addTilesPlayed(tile, position);

                // create new action performed hashmap to store tile
                HashMap<Tile, Boolean> actionPerformed = new HashMap<Tile, Boolean>();
                actionPerformed.put(tile, false);
                scrabbleView.getGame().getCurrentPlayer().addActionPerformed(actionPerformed); // add action to hashmap of actions performed by the player
                scrabbleView.getGame().getCurrentPlayer().addActionPerformedPosition(position); // add position of action performed this move
            }
        }
    }

    /**
     * Method for getting the board buttons
     *
     * @return the board buttons
     */
    public ScrabbleButton[][] getBoardButtons() {
        return boardButtons;
    }

    /**
     * Reverts Tiles if move was invalid
     *
     * @param player the player to revert tiles to
     */
    public void revertTiles(Player player) {
        for (Position pos : player.getTilesPlayed().keySet()) {
            boardButtons[pos.getRow()][pos.getCol()].revertTile();
        }
    }
}


