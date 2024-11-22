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
    private int successiveScorelessTurns;
    private List<String> wordsInPlay;
    private ArrayList<String> dictionary;
    private int turnNumber;
    private ScrabbleView view;
    private boolean isTest;

    /**
     * Constructor for Game class
     *
     * @param numPlayers the number of players who will be playing the game
     */
    public ScrabbleModel(int numPlayers, ScrabbleView view, int numAI) {
        gameBoard = new Board();
        isTest = false;
        gameBag = new Bag(isTest);
        players = new ArrayList<>();
        gameOver = false;
        successiveScorelessTurns = 0;
        turnNumber = 0;
        this.wordsInPlay = new ArrayList<>();
        this.view = view;
        dictionary = new ArrayList<String>();
        dictionary = createDictionary();

        //Set up human players
        for (int i = 1; i <= numPlayers; i++) {
            String playerName = JOptionPane.showInputDialog(view.getFrame(), "Enter player " + i + "'s name");

            // If playerName is null (e.g., if the user cancels the dialog), handle it appropriately
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player " + i; // or prompt again if preferred
            }

            Player player = new Player(playerName);
            player.drawTiles(gameBag, 7, this);
            players.add(player);
        }

        //Set up AI Players
        if(numAI != 0) {
            for (int i = 1; i <= numAI; i++) {
                String aiName = "Bot" + i; //e.g. Bot 1, Bot 2, Bot3, etc.

                Player ai = new AI(this,aiName,dictionary);
                ai.drawTiles(gameBag, 7, this);
                players.add(ai);
            }

        }

        currentPlayerIndex = 0;
    }

    /**
     * Constructor primarily used for test-cases. Does not use GUI, just tests model.
     *
     * @param playerList the list of players in the game
     */
    public ScrabbleModel(ArrayList<Player> playerList) {
        gameBoard = new Board();
        isTest = true;
        gameBag = new Bag(isTest);
        players = playerList;
        gameOver = false;
        successiveScorelessTurns = 0;
        turnNumber = 0;
        this.wordsInPlay = new ArrayList<>();
        dictionary = new ArrayList<String>();
        dictionary = createDictionary();

        for (Player player : players) {
            player.drawTiles(gameBag, 7, this);
        }
        currentPlayerIndex = 0;

    }

    /**
     * creates the dictionary used for the game
     *
     * @return array list of all valid words for the game
     */
    private ArrayList<String> createDictionary() {
        ArrayList<String> dictionary = new ArrayList<String>();
        File dictFile = new File("bigDictionary.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(dictFile);
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File \"bigDictionary.txt\" is missing");
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            dictionary.add(scanner.nextLine().toLowerCase());
        }
        return dictionary;
    }

    /**
     * Method that checks if current player is AI, and if so, handles their turn
     * @param currentPlayer the player to check if they are AI
     */
    public void handleAI(Player currentPlayer){
        //If the current player is AI
        if(currentPlayer.checkAIPlayer()){
            String command = currentPlayer.play();
            switch(command){
                case "exchange": handleExchange(currentPlayer);
                case "play": handlePlay(currentPlayer);
                case "pass": handlePass(currentPlayer);
            }
        }
        //Otherwise it does nothing
    }

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
        // get selected tiles to exchange
        ArrayList<String> exchangeTiles = currentPlayer.getTilesToExchange();

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
        currentPlayer.drawTiles(gameBag, numTilesToDraw, this);
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
    public boolean handlePlay(Player currentPlayer) {

        // Step 1: get tiles played
        Map<Tile, Position> tilesToPlay = currentPlayer.getTilesPlayed();

        // check if at least one tile was played
        if (tilesToPlay.isEmpty()) {
            showMessage("No tiles placed on the board. Please place tiles before playing.");
            return false;
        }

        // Step 2: Validate tile alignment and adjacency
        List<Position> positions = new ArrayList<>(tilesToPlay.values());
        if (!validateAlignmentAndAdjacency(positions)) {
            revertTiles(tilesToPlay); // revert tiles on board
            if (!isTest) {
                view.getBoardPanel().revertTiles(currentPlayer);
            } // clear gui
            currentPlayer.clearTilesPlayed(); // clear played tiles
            return false;
        }

        // Step 3: Place tiles and validate words
        if (attemptPlay(currentPlayer, tilesToPlay, positions)) {
            currentPlayer.clearTilesPlayed(); // clear stored played tiles
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            turnNumber++;
        } else {
            revertTiles(tilesToPlay); // Revert if play fails
            if (!isTest) {
                view.getBoardPanel().revertTiles(currentPlayer);
            } // clear gui
            currentPlayer.clearTilesPlayed(); // Clear the played tiles to reset
        }
        successiveScorelessTurns = 0; // reset successive scoreless turns counter
        checkGameOver();

        return true;
    }

    /**
     * Checks tile adjacency, alignment, and rule compliance
     *
     * @param positions the positions of the tiles to be placed
     * @return true if valid alignment and adjacency
     */
    private boolean validateAlignmentAndAdjacency(List<Position> positions) {
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);

        if (!wordValidator.arePositionsAligned(positions)) {
            showMessage("Invalid formation, tiles must be in a straight line");
            return false;
        }

        if (!wordsInPlay.isEmpty() && !wordValidator.isConnectedToAdjacentTiles(positions)) {
            showMessage("Invalid formation, must be adjacent to existing tiles");
            return false;
        }

        if (wordsInPlay.isEmpty()) {
            return validateFirstPlay(positions);
        }

        return true;
    }

    /**
     * Validates the first play to ensure it covers the center tile H8 and has at least 2 tiles
     *
     * @param positions positions of the tiles to be played
     * @return true if valid first move
     */
    // Validates the first play to ensure it covers the center and has at least 2 tiles
    private boolean validateFirstPlay(List<Position> positions) {
        Position center = gameBoard.parsePosition("H8");
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);
        //Check if tile placed on H8
        if (!positions.contains(center)) {
            showMessage("First word must cover center square (H8)");
            return false;
        }

        //Check if word has at least 2 letters
        if (positions.size() < 2) {
            showMessage("First turn must play at least 2 tiles");
            return false;
        }
        return true;
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
        for (Tile tileToRemove : tilesToPlay.keySet()) {
            currentPlayer.removeTile(String.valueOf(tileToRemove.getLetter()));
        }
        currentPlayer.drawTiles(gameBag, tilesToPlay.size(), this);
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
        // If no tiles left to be played in the game
        if (players.get(currentPlayerIndex).isRackEmpty() && gameBag.isEmpty()) {
            gameOver = true;
        } else if (successiveScorelessTurns >= 6) {
            // If players are skipping consecutively, give them the option to end the game
            boolean validChoice = false;

            // Instead of input dialog, request decision from the view
            while (!validChoice) {
                int response = view.showGameOverDialog(successiveScorelessTurns); // Call method in view

                if (response == JOptionPane.YES_OPTION) {
                    validChoice = true;
                    gameOver = false;
                } else if (response == JOptionPane.NO_OPTION) {
                    validChoice = true;
                    gameOver = true;
                } else {
                    showMessage("Invalid choice. Please enter either Yes or No.");
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

    /**
     * Method for advancing the turn
     */
    public void nextPlayerTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        successiveScorelessTurns++;
    }

    /**
     * Method for getting the current player
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Method for getter the last player that acted
     *
     * @return the last player that acted
     */
    public Player getLastPlayer() {
        return players.get((currentPlayerIndex - 1 + players.size()) % players.size());
    }

    /**
     * Method for getting the list of players in the game
     *
     * @return list of game players
     */
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
     * determine if game is over
     *
     * @return true if game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }


    /**
     * Method to display message
     *
     * @param message a string message to display
     */
    //method to display messages
    public void showMessage(String message) {
        if (!isTest | !getCurrentPlayer().checkAIPlayer()) {
            JOptionPane.showMessageDialog(view.getFrame(), message);
        }
    }


}
