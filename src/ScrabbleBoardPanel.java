import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Character;

public class ScrabbleBoardPanel extends JPanel {
    private final ScrabbleButton[][] boardButtons; // the board buttons
    private final PlayerRackPanel playerRackPanel; // the players rack
    private final ScrabbleView scrabbleView; // reference to scrabble view

    public ScrabbleBoardPanel(PlayerRackPanel rackPanel, ScrabbleView view) {
        this.playerRackPanel = rackPanel;
        this.scrabbleView = view;  // Set the reference to ScrabbleView
        setLayout(new GridLayout(15, 15));

        // initialize button array and add buttons to panel
        boardButtons = new ScrabbleButton[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                ScrabbleButton button = new ScrabbleButton(row, col);
                char ch = 'A';
                button.setText(Character.toString(ch + col) + (row + 1));
                button.addActionListener(new BoardButtonClickListener(row, col));
                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    // helper class for button click action listener
    private class BoardButtonClickListener implements ActionListener {
        private final int row;
        private final int col;

        public BoardButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Tile tile = scrabbleView.getSelectedTile();
            if (tile != null) {
                // Place tile in GUI board and game board simultaneously
                boardButtons[row][col].placeTile(tile);

                // Disable button to prevent further modification
                boardButtons[row][col].setEnabled(false);
                scrabbleView.setSelectedTile(null);

                // Add tile to playedTiles field for current player
                Position position = ScrabbleModel.getGameBoard().getPosition(row, col);
                scrabbleView.getGame().getCurrentPlayer().addTilesPlayed(tile, position);
            }
        }
    }

    public void revertTiles(Player player) {
        for (Position pos : player.getTilesPlayed().values()) {
            char ch = 'A';
            boardButtons[pos.getRow()][pos.getCol()].setText(Character.toString(ch + pos.getCol()) + (pos.getRow() + 1));
            boardButtons[pos.getRow()][pos.getCol()].setBackground(Color.WHITE);
            boardButtons[pos.getRow()][pos.getCol()].setEnabled(true);
        }
    }
}

