import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * PlayerRackPanel is a JPanel that shows the player rack
 * @author Andrew Roberts
 * @version 1
 */
public class PlayerRackPanel extends JPanel {

    private List<Tile> rack; // The players rack
    private ScrabbleView scrabbleView;  // Reference to ScrabbleView
    private JButton playButton;
    private JButton passButton;
    private JButton swapButton;
    private JLabel playerName;
    private JPanel tilePanel;  // Panel for holding tile buttons

    /**
     * Constructor for PlayerRackPanel
     * @param rack the player's rack to be represented
     * @param view the JFrame GUI instance
     */
    public PlayerRackPanel(List<Tile> rack, ScrabbleView view) {
        this.rack = rack;
        this.scrabbleView = view;  // Set the reference to ScrabbleView

        playerName = new JLabel(view.getGame().getCurrentPlayer().getName()); // add player's name

        // add buttons and their action listeners
        playButton = new JButton("Play");
        passButton = new JButton("Pass");
        swapButton = new JButton("Swap");
        playButton.addActionListener(e -> view.handlePlayAction());
        passButton.addActionListener(e -> view.handlePassAction());
        swapButton.addActionListener(e -> view.handleSwapAction());

        // Initialize layout and sub-panels
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // Add space between components
        constraints.gridy = 0; // All components will be in the same row

        // Panel for tile buttons
        tilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // Align tiles to the left

        // Add player name to the layout in gridx = 0
        constraints.gridx = 0;
        constraints.anchor = GridBagConstraints.WEST; // Align left
        add(playerName, constraints);

        // Add the tile panel just after the player name in gridx = 1
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.CENTER;  // Align tiles in the center
        add(tilePanel, constraints);

        // Panel for control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));  // Align buttons to the left
        buttonPanel.add(playButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(passButton);

        // Add button panel after the tile panel in gridx = 2
        constraints.gridx = 2;
        constraints.anchor = GridBagConstraints.EAST; // Align buttons to the right
        add(buttonPanel, constraints);

        // Initial display of tiles
        updateRack(rack);
    }

    /**
     * Updates the rack display
     * @param tiles the tiles in the rack
     */
    public void updateRack(List<Tile> tiles) {
        // Update player name
        playerName.setText(scrabbleView.getGame().getCurrentPlayer().getName());

        // Clear tile panel and add updated tile buttons
        tilePanel.removeAll();
        for (Tile tile : tiles) {
            JButton tileButton = new JButton(String.valueOf(tile.getLetter()));
            tileButton.addActionListener(new TileButtonListener(tile));
            tilePanel.add(tileButton);
        }

        revalidate();
        repaint();
    }

    /**
     * Helper Class for TileButtons
     * @author Andrew Roberts
     * @version 1
     */
    private class TileButtonListener implements ActionListener {
        private Tile tile;

        public TileButtonListener(Tile tile) {
            this.tile = tile;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            scrabbleView.setSelectedTile(tile);
        }
    }
}

