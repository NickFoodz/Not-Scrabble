import java.io.IOException;
/*
Main class to play "Not Scrabble". An educational project
 */
public class Main {
//    static Board gameBoard;

    public static void main(String[] args) {
//        gameBoard = new Board();
//        gameBoard.displayBoard();
//        String filePath = "E:\\SYSC 3110 Labs\\SYSC3110_Group33_F24\\src\\scrabblewords.txt"; // Update this to your file path
//        String userInput =  "throw"; // The word to search for, replace this once user input is gathered in an input class
//
//        System.out.println("Current working directory: " + System.getProperty("user.dir"));
//        try {
//            boolean found = Parser.validWord(filePath, userInput);
//            if (found) {
//                System.out.println("The word '" + userInput + "' was found in the file.");
//            } else {
//                System.out.println("The word '" + userInput + "' was not found in the file.");
//            }
//        } catch (IOException error) {
//            System.err.println("Error reading the file: " + error.getMessage());
//        }
//
//
//        Bag gameBag = new Bag();
//        for (Tile entry : gameBag.getTiles()){
//            System.out.println(entry.getLetter() + ", " + entry.getPointValue());
//        }
        Game testGame = new Game(3);
        testGame.start();
    }
}