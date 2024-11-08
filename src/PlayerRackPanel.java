import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PlayerRackPanel extends JPanel {

    private List<Tile> rack; // The players rack
    private ScrabbleView scrabbleView;  // Reference to ScrabbleView
    private JButton playButton;
    private JButton passButton;
    private JButton swapButton;
    private JLabel playerName;

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

        // create the scrabble play area layout
        setLayout(new FlowLayout());
        add(playerName);
        updateRack(rack);
        add(playButton);
        add(passButton);
        add(swapButton);
    }

    // updates rack display
    public void updateRack(List<Tile> tiles) {
        // Remove only tile buttons
        Component[] components = this.getComponents();
        for (Component component : components) {
            if (component instanceof JButton && component != playButton && component != passButton && component != swapButton) {
                remove(component);
            }
        }

        // Add updated tile buttons
        for (Tile tile : tiles) {
            JButton tileButton = new JButton(String.valueOf(tile.getLetter()));
            tileButton.addActionListener(new TileButtonListener(tile));
            add(tileButton);
        }

        revalidate();
        repaint();
    }

    // helper class for button action listener
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

