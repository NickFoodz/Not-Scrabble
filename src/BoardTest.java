import org.junit.Test;

import java.io.FileNotFoundException;

public class BoardTest {

    @Test
    public void importCustomBoardXML() throws FileNotFoundException {
        Position h8 = new Position(7,7);
        //System.out.println(h8);
        Board board1 = new Board();
        //System.out.println(board1.getPremiumPositions());
        Board board = new Board("XML.txt");
        System.out.println(board.getPremiumPositions());

    }
}