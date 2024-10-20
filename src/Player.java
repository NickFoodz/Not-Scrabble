import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    private String name; // the player's name
    private ArrayList<Tile> rack; //the tiles the player currently holds
    private int score; //the players score

    /**
     * creates new player object with corresponding name
     *
     * @param name the name of the player
     */
    public Player(String name) {
        this.name = name;
        rack = new ArrayList<Tile>();
        score = 0;
    }

    /**
     * method for drawing a tile from the bag
     *
     * @param bag      the bag to be drawn from
     * @param numTiles the number of tiles the player wishes to draw
     */
    public void drawTiles(Bag bag, int numTiles) {
        for (int i = 0; i < numTiles; i++) {
            if (!bag.isEmpty() && (rack.size() < 7)) {
                Tile drawnTile = bag.drawTile();
                rack.add(drawnTile);
            } else if (bag.isEmpty()) {
                System.out.println("there are no more tiles in the bag");
                break;
            } else if (rack.size() == 7) {
                System.out.println("you already have 7 tiles");
                break;
            }
        }
    }

    /**
     * method for displaying the tiles of the player
     */
    public void showTiles() {
        System.out.print(name + "'s tiles: ");
        for (Tile tile : rack) {
            System.out.print(tile.getLetter() + " ");
        }
        System.out.println();
    }

    /**
     * getter for player's name
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * getter for player's rack
     *
     * @return the player's rack
     */
    public ArrayList<Tile> getRack() {
        return rack;
    }

    /**
     * getter for the player's score
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * method to check if player's rack is empty
     *
     * @return true if player rack is empty, false otherwise
     */
    public boolean isRackEmpty() {
        return rack.isEmpty();
    }

    public boolean findTile(String tileLetter) {
        Iterator<Tile> iterator = this.getRack().iterator();

        while (iterator.hasNext()) {
            Tile currentTile = iterator.next();
            if (String.valueOf(currentTile.getLetter()).equalsIgnoreCase(tileLetter)) {
                return true; // tile found
            }
        }

        return false; // tile not found
    }

    public boolean removeTile(String tileLetter) {
        Iterator<Tile> iterator = this.getRack().iterator();

        while (iterator.hasNext()) {
            Tile currentTile = iterator.next();
            if (String.valueOf(currentTile.getLetter()).equalsIgnoreCase(tileLetter)) {
                iterator.remove();
                return true; // tile found and removed
            }
        }

        return false; // tile not found
    }
}
