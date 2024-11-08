import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScrabbleBoardPanel extends JPanel {
    private JButton[][] boardButtons; // the board buttons
    private PlayerRackPanel playerRackPanel; // the players rack
    private ScrabbleView scrabbleView; // reference to scrabble view

    public ScrabbleBoardPanel(PlayerRackPanel rackPanel, ScrabbleView view) {
        this.playerRackPanel = rackPanel;
        this.scrabbleView = view;  // Set the reference to ScrabbleView
        setLayout(new GridLayout(15, 15));

        // initialize button array and add buttons to panel
        boardButtons = new JButton[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                JButton button = new JButton();
                button.addActionListener(new BoardButtonClickListener(row, col));
                boardButtons[row][col] = button;
                add(button);
            }
        }
    }

    // helper class for button click action listener
    private class BoardButtonClickListener implements ActionListener {
        private int row;
        private int col;

        public BoardButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Tile tile = scrabbleView.getSelectedTile();
            if (tile != null) {
                boardButtons[row][col].setText(String.valueOf(tile.getLetter()));

                // place tile method should be created to handle placing tiles instead of in handle play
                // scrabbleView.getGame().placeTile(tile, row, col);


                scrabbleView.setSelectedTile(null);
                boardButtons[row][col].setEnabled(false);

                scrabbleView.getGame().handlePlay(scrabbleView.getGame().getCurrentPlayer());
            }
        }
    }
}

