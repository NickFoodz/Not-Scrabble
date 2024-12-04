import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Main class of scrabble game. Handles play, exchange, pass, and if the game continues or not.
 *
 * @author Andrew Roberts
 * @author Nick Fuda
 * @version 1
 */
public class ScrabbleModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private  Board gameBoard;
    private  List<Player> players;
    private  Bag gameBag;
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
        if (numAI != 0) {
            for (int i = 1; i <= numAI; i++) {
                String aiName = "Bot " + i; //e.g. Bot 1, Bot 2, Bot3, etc.

                Player ai = new AI(this, aiName, dictionary, gameBoard);
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
        File dictFile = new File("CollinsScrabbleWords.txt");
        Scanner scanner;
        try {
            scanner = new Scanner(dictFile);
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File \"CollinsScrabbleWords.txt\" is missing");
            throw new RuntimeException(e);
        }
        while (scanner.hasNextLine()) {
            dictionary.add(scanner.nextLine().toLowerCase());
        }
        return dictionary;
    }

    /**
     * Method that checks if current player is AI, and if so, handles their turn
     *
     * @param currentPlayer the player to check if they are AI
     */
    public void handleAI(Player currentPlayer) {
        //If the current player is AI
        if (currentPlayer.checkAIPlayer()) {
            currentPlayer.play();
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
    public boolean handleExchange(Player currentPlayer) {
        // get selected tiles to exchange
        ArrayList<String> exchangeTiles = currentPlayer.getTilesToExchange();

        int numTilesToDraw = 0;
        //Determine how many tiles to replace, remove tiles from rack
        for (String tileLetter : exchangeTiles) {
            if (currentPlayer.removeTile(tileLetter)) {
                numTilesToDraw++;
            }
        }
        //Draw tiles from game bag
        if (!currentPlayer.drawTiles(gameBag, numTilesToDraw, this)) {
            return false;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        successiveScorelessTurns++;
        turnNumber++;
        checkGameOver();

        return true;
    }

    /**
     * Method to handle the play condition and word when a player chooses "Play" on their turn
     *
     * @param currentPlayer the player whose turn it is
     */
    public boolean handlePlay(Player currentPlayer) {

        // Step 1: get tiles played
        Map<Position, Tile> tilesToPlay = currentPlayer.getTilesPlayed();

        // check if at least one tile was played
        if (tilesToPlay.isEmpty()) {
            showMessage("No tiles placed on the board. Please place tiles before playing.");
            return false;
        }

        // Step 2: Validate tile alignment and adjacency
        List<Position> positions = new ArrayList<>(tilesToPlay.keySet());
        if (!validateAlignmentAndAdjacency(positions)) {
            revertTiles(tilesToPlay); // revert tiles on board
            if (!isTest) {
                view.getBoardPanel().revertTiles(currentPlayer);
            } // clear gui
            currentPlayer.clearTilesPlayed(); // clear played tiles
            currentPlayer.clearActionsPerformed(); // clear stored actions taken this turn
            return false;
        }

        // Step 3: Place tiles and validate words
        if (attemptPlay(currentPlayer, tilesToPlay, positions)) {
            currentPlayer.clearTilesPlayed(); // clear stored played tiles
            currentPlayer.clearActionsPerformed(); // clear stored actions taken this turn
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            turnNumber++;
            successiveScorelessTurns = 0; // reset successive scoreless turns counter
            checkGameOver();
            return true;
        }

        revertTiles(tilesToPlay); // Revert if play fails
        if (!isTest) {
            view.getBoardPanel().revertTiles(currentPlayer);
        } // clear gui
        currentPlayer.clearTilesPlayed(); // Clear the played tiles to reset
        currentPlayer.clearActionsPerformed(); // clear stored actions taken this turn

        return false;
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
    private boolean attemptPlay(Player currentPlayer, Map<Position, Tile> tilesToPlay, List<Position> positions) {
        WordValidator wordValidator = new WordValidator(gameBoard, dictionary);

        for (Map.Entry<Position, Tile> entry : tilesToPlay.entrySet()) {
            if (!gameBoard.placeTile(entry.getValue(), entry.getKey().getRow(), entry.getKey().getCol())) {
                return false;
            }
        }

        if (!wordValidator.isConnectedToOtherTilesInTurn(positions)) {
            showMessage("Invalid formation, there must not be empty spaces between tiles");
            return false;
        }

        // get new words formed
        Map<Map<Position, Tile>, String> attemptedWords = gameBoard.gatherWordsOnBoard();
        List<String> newWords = (turnNumber == 0) ? new ArrayList<>(attemptedWords.values()) : getNewWords(new ArrayList<>(attemptedWords.values()));


        for (String word : newWords) {
            if (!wordValidator.isValidWord(dictionary, word)) {
                showMessage("Invalid formation, please try again");
                return false;
            }
        }

        // Update game state for valid play
        wordsInPlay = new ArrayList<>(attemptedWords.values());
        for (Tile tileToRemove : tilesToPlay.values()) {
            currentPlayer.removeTile(String.valueOf(tileToRemove.getLetter()));
        }
        currentPlayer.drawTiles(gameBag, tilesToPlay.size(), this);

        // calculate score
        int turnScore = calculateScore(attemptedWords, newWords);
        currentPlayer.setScore(currentPlayer.getScore() + turnScore);

        // display updated score
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
    public void revertTiles(Map<Position, Tile> tilesToPlay) {
        for (Map.Entry<Position, Tile> tile : tilesToPlay.entrySet()) {
            Position position = tile.getKey();
            position.setTile(null); // reset the position to empty
            if (tile.getValue().isBlank()){
                tile.getValue().setLetter(' ');
            }
        }
    }

    /**
     * Method calculateScore() will calculate the score for each word played.
     *
     * @param wordsToTiles the map of tile lists to their corresponding words
     * @param wordsFormed  the words that are formed by the player
     * @return the score
     */
    private int calculateScore(Map<Map<Position, Tile>, String> wordsToTiles, List<String> wordsFormed) {
        // Initial score
        int score = 0;

        // get premium positions
        HashMap<String, Integer> premiumPositions = gameBoard.getPremiumPositions();

        // Iterate through the list of newly formed words
        for (String word : wordsFormed) {

            Map<Position, Tile> positionTileMap = findTilesForWord(wordsToTiles, word);

            // track multiplier for premium tiles
            int multiplier = 1;

            // Calculate score for this word
            for (Map.Entry<Position, Tile> entry : positionTileMap.entrySet()) {
                Position position = entry.getKey();
                Tile tile = entry.getValue();

                String positionKey = position.toString();

                // handle double letter square
                if (premiumPositions.containsKey(positionKey) && premiumPositions.get(positionKey) == 2) {
                    score += tile.getPointValue() * 2;
                    gameBoard.removePremiumPosition(positionKey);
                }

                // handle triple letter square
                else if (premiumPositions.containsKey(positionKey) && premiumPositions.get(positionKey) == 4) {
                    score += tile.getPointValue() * 3;
                    gameBoard.removePremiumPosition(positionKey);
                }
                // no premium tile
                else {
                    score += tile.getPointValue();
                }

                // handle double word score
                if (premiumPositions.containsKey(positionKey) && premiumPositions.get(positionKey) == 3) {
                    multiplier = 2;
                    gameBoard.removePremiumPosition(positionKey);
                }

                // handle triple word score
                else if (premiumPositions.containsKey(positionKey) && premiumPositions.get(positionKey) == 5) {
                    multiplier = 3;
                    gameBoard.removePremiumPosition(positionKey);
                }
            }
            score *= multiplier;
        }
        return score;
    }

    /**
     * Helper method to find the list of tiles corresponding to a word.
     *
     * @param wordsToTiles the map of tile lists to words
     * @param word         the word to find tiles for
     * @return the list of tiles corresponding to the word
     */
    private Map<Position, Tile> findTilesForWord(Map<Map<Position, Tile>, String> wordsToTiles, String word) {
        // Iterate through the map to find the matching word and return its tiles
        for (Map.Entry<Map<Position, Tile>, String> entry : wordsToTiles.entrySet()) {
            if (entry.getValue().equals(word)) {
                return entry.getKey();
            }
        }
        return Collections.emptyMap(); // Return an empty list if no tiles found (shouldn't happen)
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
    public Board getGameBoard() {
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
        if (!isTest && !getCurrentPlayer().checkAIPlayer()) {
            JOptionPane.showMessageDialog(view.getFrame(), message);
        }
    }

    /**
     * Getter for words in play
     *
     * @return words in play currently
     */
    public List<String> getWordsInPlay() {
        return wordsInPlay;
    }

    /**
     * getter for view reference
     * @return view reference
     */
    public ScrabbleView getView() {
        return view;
    }

    /**
     * getter for game bag
     * @return the game bag
     */
    public Bag getGameBag() {
        return gameBag;
    }

    /**
     * Getter for current player index
     */
    private int getPlayerIndex(){return currentPlayerIndex;}

    /**
     * Saves the game using Serialization
     */

    public void saveGame(String fileName) throws IOException {
        try (FileOutputStream f = new FileOutputStream(fileName);
             ObjectOutputStream s = new ObjectOutputStream(f)) {
            s.writeObject(this);
            s.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error: Game not saved");
        }
        System.out.println("Saved Game");
    }

    public void loadGame(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        ScrabbleModel loadGame = null;
        try (FileInputStream inputStream = new FileInputStream(fileName);
             ObjectInputStream s = new ObjectInputStream(inputStream)) {
                loadGame = (ScrabbleModel) s.readObject();
                gameBoard = loadGame.getGameBoard();
                players = loadGame.getPlayers();
                gameBag = loadGame.getGameBag();
                currentPlayerIndex = loadGame.getPlayerIndex();
                gameOver = isGameOver();
                successiveScorelessTurns = loadGame.successiveScorelessTurns;
                wordsInPlay = loadGame.getWordsInPlay();
                turnNumber = loadGame.turnNumber;
                isTest = loadGame.isTest;
                //Show that the model is successfully imported
                System.out.println(getPlayers());
                gameBoard.displayBoard();
                System.out.println(getCurrentPlayer().getRack());
        } catch (FileNotFoundException e){
            e.printStackTrace();
            showMessage("Save File not Found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded Game");

    }
}
