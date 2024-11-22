import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AITest {

    @Test
    /**
     * Tests that having a blank tile in the rack makes the AI exchange the tile.
     */
    public void testCheckBlankTile(){
        AI ai = new AI("Tim");
        assertEquals(ai.play(), "exchange");
        ArrayList<Tile> AIT = new ArrayList<Tile>();
        AIT.add(new Tile('C', 2));
        AIT.add(new Tile('H', 1));
        AIT.add(new Tile('E', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('S', 2));
        AIT.add(new Tile('E', 1));
        AIT.add(new Tile('Y', 2));
        ai.setRack(AIT);
        assertEquals(ai.play(), "pass");
        assertTrue(ai.getValidWordCombinations().contains("cheese"));
    }

}