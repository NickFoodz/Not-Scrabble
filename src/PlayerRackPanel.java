import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
    private JButton undoButton;
    private JLabel playerName;
    private JPanel tilePanel;  // Panel for holding tile buttons
    private JPanel exchangePanel; // panel to handle exchanges
    private List<ScrabbleButton> exchangeButtons; // Track exchange buttons for tile placement
    private List<Tile> selectedTilesForExchange; // Track selected tiles
    private List<JButton> tileButtons; // List to store references to rack buttons


    /**
     * Constructor for PlayerRackPanel
     * @param rack the player's rack to be represented
     * @param view the JFrame GUI instance
     */
    public PlayerRackPanel(List<Tile> rack, ScrabbleView view) {
        this.rack = rack;
        this.scrabbleView = view;  // Set the reference to ScrabbleView
        this.exchangeButtons = new ArrayList<>(); // Initialize exchangeButtons list
        this.selectedTilesForExchange = new ArrayList<>(); // Initialize selectedTilesForExchange list
        this.tileButtons = new ArrayList<>(); // Initialize tileButtons list

        playerName = new JLabel(view.getGame().getCurrentPlayer().getName()); // add player's name

        // add buttons and their action listeners
        playButton = new JButton("Play");
        passButton = new JButton("Pass");
        swapButton = new JButton("Swap");
        undoButton = new JButton("Undo");
        playButton.addActionListener(e -> view.handlePlayAction());
        passButton.addActionListener(e -> view.handlePassAction());
        swapButton.addActionListener(e -> view.handleSwapAction());
        undoButton.addActionListener(e -> view.handleUndoAction());

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
        buttonPanel.add(undoButton);

        // Add button panel after the tile panel in gridx = 2
        constraints.gridx = 2;
        constraints.anchor = GridBagConstraints.EAST; // Align buttons to the right
        add(buttonPanel, constraints);

        // Add exchange panel under the main panel
        exchangePanel = new JPanel(new FlowLayout());
        exchangePanel.setBorder(BorderFactory.createTitledBorder("Select Tiles for Swap"));

        // Create 7 empty slots for exchange selection
        for (int i = 0; i < 7; i++) {
            ScrabbleButton exchangeButton = new ScrabbleButton();
            exchangeButton.setPreferredSize(new Dimension(50, 50));
            exchangeButton.addActionListener(new ExchangeButtonListener(i));
            exchangeButtons.add(exchangeButton);
            exchangePanel.add(exchangeButton);
        }

        // add constraints for exchange panel
        constraints.gridx = 1;
        constraints.gridy = -1;
        constraints.anchor = GridBagConstraints.CENTER;
        add(exchangePanel, constraints);

        // Initialize tile buttons and add them to the list
        for (Tile tile : rack) {
            JButton tileButton = new JButton(String.valueOf(tile.getLetter()));
            tileButton.addActionListener(new TileButtonListener(tile, tileButton));
            tilePanel.add(tileButton);
            tileButtons.add(tileButton); // Add button to list
        }

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
            tileButton.addActionListener(new TileButtonListener(tile, tileButton));
            tilePanel.add(tileButton);
        }

        revalidate();
        repaint();
    }

    /**
     * Removes tile placement in exchange panel
     */
    public void clearExchangePanel() {
        selectedTilesForExchange.clear();
        for (ScrabbleButton exchangeButton : exchangeButtons) {
            exchangeButton.revertExchangeTile();
        }
        for (JButton tileButton : tileButtons) {
            tileButton.setEnabled(true); // Re-enable all rack buttons
        }
    }

    public List<Tile> getSelectedTilesForExchange() {
        return selectedTilesForExchange;
    }

    /**
     * Helper Class for TileButtons
     * @author Andrew Roberts
     * @version 1
     */
    private class TileButtonListener implements ActionListener {
        private Tile tile;
        private JButton tileButton;

        public TileButtonListener(Tile tile, JButton tileButton) {
            this.tile = tile;
            this.tileButton = tileButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            scrabbleView.setSelectedTile(tile);
            tileButton.setEnabled(false);
        }
    }

    /**
     * Helper class for Exchange buttons
     * @author Andrew Roberts
     * @version 1
     */
    private class ExchangeButtonListener implements ActionListener {
        private int index;

        public ExchangeButtonListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Tile tile = scrabbleView.getSelectedTile();
            if (tile != null) {

                exchangeButtons.get(index).placeTile(tile);
                selectedTilesForExchange.add(tile);
                tile = null; // Clear selected tile after adding

            }
        }
    }
}

