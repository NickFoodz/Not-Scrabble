import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Main class of scrabble game. Handles play, exchange, pass, and if the game continues or not.
 *
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleModel {
    private static Board gameBoard;
    private static List<Player> players;
    private static Bag gameBag;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Scanner scanner;
    private int successiveScorelessTurns;
    private List<String> wordsInPlay;
    private ArrayList<String> dictionary;
    private int turnNumber;
    private ScrabbleView view;

    /**
     * Constructor for Game class
     *
     * @param numPlayers the number of players who will be playing the game
     */
    public ScrabbleModel(int numPlayers, ScrabbleView view) {
        gameBoard = new Board();
        gameBag = new Bag();
        players = new ArrayList<>();
        gameOver = false;
        successiveScorelessTurns = 0;
        turnNumber = 0;
        this.wordsInPlay = new ArrayList<>();
        this.view = view;
        dictionary = new ArrayList<String>();
        dictionary = createDictionary();


        for (int i = 1; i <= numPlayers; i++) {
            String playerName = JOptionPane.showInputDialog(view.getFrame(), "Enter player " + i + "'s name");

            // If playerName is null (e.g., if the user cancels the dialog), handle it appropriately
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player " + i; // or prompt again if preferred
            }

            Player player = new Player(playerName);
            player.drawTiles(gameBag, 7);
            players.add(player);
        }

        currentPlayerIndex = 0;
    }

    /**
     * Constructor primarily used for test-cases. Does not use GUI, just tests model.
     */
    public ScrabbleModel(ArrayList<Player> playerList){
        gameBoard = new Board();
        gameBag = new Bag();
        players = playerList;
        gameOver = false;
        successiveScorelessTurns = 0;
        turnNumber = 0;
        this.wordsInPlay = new ArrayList<>();
        dictionary = new ArrayList<String>();
        dictionary = createDictionary();

        for(Player player : players){
            player.drawTiles(gameBag, 7);
        }
        currentPlayerIndex = 0;

    }

    private ArrayList<String> createDictionary() {
        ArrayList<String> dictionary = new ArrayList<String>();
        File dictFile = new File("scrabblewords.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(dictFile);
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File \"scrabblewords.txt\" is missing");
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            dictionary.add(scanner.nextLine());
        }
        return dictionary;
    }

    // from milestone 1
//    /**
//     * Method start initializes the game and checks if it is over.
//     */
//    public void start() {
//        showMessage("Welcome to \"Not Scrabble\"");
//        //Checks if game is over. If not, repeat
//        while (!gameOver) {
//            takeTurn();
//            checkGameOver();
//        }
//        displayScores();
//    }

    //         Part of the first milestone
//    /**
//     * Method takeTurn() allows players to choose to Pass, Exchange Tiles, or Play their turn.
//     */
//    public void takeTurn() {
//        Player currentPlayer = players.get(currentPlayerIndex);
//        showMessage(currentPlayer.getName() + " 's turn");
//
//        //Display board and score
//        gameBoard.displayBoard();
//        currentPlayer.showTiles();
//        showMessage(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());
//
//        //Ask player to make choice for their turn
//        boolean validChoice = false;
//        do {
//            showMessage("Please type Pass, Exchange or Play if you would like to pass your turn, exchange tiles or play tiles respectively");
//            String choice = scanner.nextLine().toLowerCase();
//
//            switch (choice) {
//                case ("pass"):
//                    validChoice = true;
//                    handlePass(currentPlayer);
//                    break;
//
//                case ("exchange"):
//                    validChoice = true;
//                    handleExchange(currentPlayer);
//                    break;
//
//                case ("play"):
//                    validChoice = true;
//                    handlePlay(currentPlayer);
//                    break;
//
//                default:
//                    showMessage("Invalid choice");
//            }
//        } while (!validChoice);
//  }

    /**
     * Handler for passing a turn
     *
     * @param currentPlayer the player who will pass the turn
     */
    public void handlePass(Player currentPlayer) {

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        successiveScorelessTurns++;
        turnNumber++;
        checkGameOver();
    }

    /**
     * Method to handle if the player wishes to exchange a tile on their turn
     *
     * @param currentPlayer the player whose turn it is
     */
    public void handleExchange(Player currentPlayer) {
        //Players enter the tiles to exchange
        String[] exchangeTiles = scanner.nextLine().split(",");
        for (int i = 0; i < exchangeTiles.length; i++) {
            exchangeTiles[i] = exchangeTiles[i].trim();
        }

        int numTilesToDraw = 0;
        //Determine how many tiles to replace, remove tiles from rack
        for (String tileLetter : exchangeTiles) {
            if (currentPlayer.removeTile(tileLetter)) {
                numTilesToDraw++;
            } else {
                showMessage("You don't have this tile: " + tileLetter);
            }
        }
        //Draw tiles from game bag
        currentPlayer.drawTiles(gameBag, numTilesToDraw);
        currentPlayer.showTiles();
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        successiveScorelessTurns++;
        turnNumber++;
        checkGameOver();

    }

    /**
     * Method to handle the play condition and word when a player chooses "Play" on their turn
     *
     * @param currentPlayer the player whose turn it is
     */
    public int handlePlay(Player currentPlayer) {

        // Step 1: get tiles played
        Map<Tile, Position> tilesToPlay = currentPlayer.getTilesPlayed();

        gameBoard.displayBoard(); // test
        System.out.println("Turn#: " + turnNumber); // test
        System.out.println("WIP: " + wordsInPlay); // test

        // check if at least one tile was played
        if (tilesToPlay.isEmpty()) {
            showMessage("No tiles placed on the board. Please place tiles before playing.");
            return 5;
        }

        // Step 2: Validate tile alignment and adjacency
        List<Position> positions = new ArrayList<>(tilesToPlay.values());
        if (validateAlignmentAndAdjacency(positions) != 0) {
            revertTiles(tilesToPlay); // revert tiles on board
            view.getBoardPanel().revertTiles(currentPlayer); // clear gui
            currentPlayer.clearTilesPlayed(); // clear played tiles
            return validateAlignmentAndAdjacency(positions);
        }

        // Step 3: Place tiles and validate words
        if (attemptPlay(currentPlayer, tilesToPlay, positions)) {
            currentPlayer.clearTilesPlayed(); // clear stored played tiles
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            turnNumber++;
        } else {
            revertTiles(tilesToPlay); // Revert if play fails
            view.getBoardPanel().revertTiles(currentPlayer); // clear gui
            currentPlayer.clearTilesPlayed(); // Clear the played tiles to reset
        }
        checkGameOver();

        gameBoard.displayBoard(); // test
        System.out.println("Turn#: " + turnNumber); // test
        System.out.println("WIP: " + wordsInPlay); // test
        return 0;
    }
//      From Milestone 1
//    // Parses and validates the player's input, returns tiles to play if valid, null otherwise
//    private Map<Tile, Position> parsePlayerInput(Player currentPlayer) {
//        showMessage("Please enter tiles and positions (e.g. R:A6, R:A8, E:A9)");
//        String input = scanner.nextLine();
//        String[] tilePositionCords = input.split(",");
//        Map<Tile, Position> tilesToPlay = new HashMap<>();
//
//        for (String tileInfo : tilePositionCords) {
//            String[] info = tileInfo.split(":");
//            if (info.length != 2) {
//                showMessage("Invalid format, please use Tile:Position format");
//                return null;
//            }
//
//            char tileLetter = info[0].trim().toUpperCase().charAt(0);
//            if (!Character.isLetter(tileLetter)) {
//                showMessage("Invalid tile letter: " + tileLetter);
//                return null;
//            }
//
//            Position position = gameBoard.parsePosition(info[1]);
//            if (position == null || position.isOccupied()) {
//                showMessage("Invalid position: " + info[1]);
//                return null;
//            }
//
//            Tile tile = currentPlayer.getTile(String.valueOf(tileLetter));
//            if (tile == null) {
//                showMessage("You do not have this tile in your rack: " + tileLetter);
//                return null;
//            }
//
//            tilesToPlay.put(tile, position);
//        }
//        return tilesToPlay;
//    }

    /**
     * Checks tile adjacency, alignment, and rule compliance
     *
     * @param positions the positions of the tiles to be placed
     * @return true if valid alignment and adjacency
     */
    private int validateAlignmentAndAdjacency(List<Position> positions) {
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);

        if (!wordValidator.arePositionsAligned(positions)) {
            //showMessage("Invalid formation, tiles must be in a straight line");
            return 1;
        }

        if (!wordsInPlay.isEmpty() && !wordValidator.isConnectedToAdjacentTiles(positions)) {
            //showMessage("Invalid formation, must be adjacent to existing tiles");
            return 2;
        }

        if (wordsInPlay.isEmpty()) {
            return validateFirstPlay(positions);
        }

        return 0;
    }

    /**
     * Validates the first play to ensure it covers the center tile H8 and has at least 2 tiles
     *
     * @param positions positions of the tiles to be played
     * @return true if valid first move
     */
    // Validates the first play to ensure it covers the center and has at least 2 tiles
    private int validateFirstPlay(List<Position> positions) {
        Position center = gameBoard.parsePosition("H8");
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);
        //Check if tile placed on H8
        if (!positions.contains(center)) {
            showMessage("First word must cover center square (H8)");
            return 3;
        }

        //Check if word has at least 2 letters
        if (positions.size() < 2) {
            showMessage("First turn must play at least 2 tiles");
            return 4;
        }
        return 0;
    }

    /**
     * Attempts to play a turn
     *
     * @param currentPlayer the player whose turn it is
     * @param tilesToPlay   the tiles the player is trying to play
     * @return true if successful move, false otherwise
     */
    // Places tiles on the board, validates words, and updates score if valid
    private boolean attemptPlay(Player currentPlayer, Map<Tile, Position> tilesToPlay, List<Position> positions) {
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);

        for (Map.Entry<Tile, Position> entry : tilesToPlay.entrySet()) {
            gameBoard.placeTile(entry.getKey(), entry.getValue().getRow(), entry.getValue().getCol());
        }

        if (!wordValidator.isConnectedToOtherTilesInTurn(positions)) {
            showMessage("Invalid formation, there must not be empty spaces between tiles");
            return false;
        }

        List<String> attemptedWords = gameBoard.gatherWordsOnBoard();

        List<String> newWords = (turnNumber == 0) ? attemptedWords : getNewWords(attemptedWords);
        for (String word : newWords) {
            if (!wordValidator.isValidWord(dictionary, word)) {
                showMessage("Invalid formation, please try again");
                return false;
            }
        }

        // Update game state for valid play
        wordsInPlay = attemptedWords;
        currentPlayer.drawTiles(gameBag, tilesToPlay.size());
        int turnScore = calculateScore(newWords);
        currentPlayer.setScore(currentPlayer.getScore() + turnScore);
        showMessage(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());

        return true;
    }

    /**
     * Method to get a list of new words formed during the current players turn
     *
     * @param attemptedWordsInPlay list of words on the board after current players turn
     * @return a list of all the new words created from current player's turn
     */
    private List<String> getNewWords(List<String> attemptedWordsInPlay) {
        Map<String, Integer> wordsInPlayCount = new HashMap<>();
        for (String word : wordsInPlay) {
            wordsInPlayCount.put(word, wordsInPlayCount.getOrDefault(word, 0) + 1);
        }

        List<String> tempList = new ArrayList<>();

        for (String word : attemptedWordsInPlay) {
            int currentCount = wordsInPlayCount.getOrDefault(word, 0);

            if (currentCount == 0 || currentCount < Collections.frequency(attemptedWordsInPlay, word)) {
                tempList.add(word);
            }
            wordsInPlayCount.put(word, currentCount + 1);
        }
        return tempList;
    }

    /**
     * Method checkGameOver() checks if the players want to end the game or if game is naturally over
     */
    private void checkGameOver() {
        //If no tiles left to be played in the game
        if (players.get(currentPlayerIndex).isRackEmpty() && gameBag.isEmpty()) {
            gameOver = true;

        } else if (successiveScorelessTurns >= 6) {
            //If players are skipping consecutively, give them the option to end the game
            boolean validChoice = false;

            //if not yes or no, demand a valid command
            while (!validChoice) {
                String continuePlaying = JOptionPane.showInputDialog(successiveScorelessTurns + " scoreless turns have passed, would you like to continue the game? Type Yes or No");
                if (continuePlaying.equalsIgnoreCase("yes")) {
                    validChoice = true;
                    gameOver = false;
                } else if (continuePlaying.equalsIgnoreCase("no")) {
                    validChoice = true;
                    gameOver = true;
                } else {
                    showMessage("invalid choice. please enter either Yes or No");
                }
            }
        } else {
            gameOver = false;
        }
    }

    /**
     * Reverts the tiles to empty
     *
     * @param tilesToPlay the tiles the player attempted to play
     */
    private void revertTiles(Map<Tile, Position> tilesToPlay) {
        for (Map.Entry<Tile, Position> tile : tilesToPlay.entrySet()) {
            Position position = tile.getValue();
            position.setTile(null); // reset the position to empty
            //GUI to revert tile
        }
    }

    /**
     * Iterates through the player list and finds the player with the highest score
     *
     * @Return the winning player
     */
    private Player getWinner() {
        //Make winner variable the first player by default
        Player winner = players.get(currentPlayerIndex);

        // find player with the highest score
        for (Player player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
        }
        return winner;
    }

    /**
     * Method calculateScore() will calculate the score for each word played.
     *
     * @param wordsFormed the words that are formed by the player
     * @return the score
     */
    private int calculateScore(List<String> wordsFormed) {
        //Initial Score is 0
        int score = 0;
        //Calculate each word of the score
        for (String word : wordsFormed) {
            for (char letter : word.toCharArray()) {
                score += LetterPointValues.getPointValue(letter);
            }
        }
        return score;
    }

    public void nextPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        successiveScorelessTurns++;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for game board
     *
     * @return game board
     */
    public static Board getGameBoard() {
        return gameBoard;
    }

    /**
     * Method to display message
     *
     * @param message a string message to display
     */
    //method to display messages
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(view.getFrame(), message);
    }

    private String errorMessage(int errorCode){
        switch(errorCode){
            case 0: return null;
            case 1: return "Invalid Formation, tiles must be in straight lines.";
            case 2: return "Invalid Formation, must be adjacent to existing tiles";
            case 3: return "First word must cover center square (H8)";
            case 4: return "First turn must play at least 2 tiles";
            case 5: return "No tiles placed on board, please place tiles before playing";
        }
        return null;
    }

}
