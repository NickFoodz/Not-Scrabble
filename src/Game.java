import java.util.*;
/**
 * Main class of scrabble game. Handles play, exchange, pass, and if the game continues or not.
 * @version 1
 * @author Andrew Roberts
 * @author Nick Fuda
 */
public class Game {
    private static Board gameBoard;
    private static List<Player> players;
    private static Bag gameBag;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Scanner scanner;
    private int successiveScorelessTurns;
    private List<String> wordsInPlay;
    private String dictionaryFilePath = "C:\\Users\\npfud\\Desktop\\School\\Sysc3110\\Scrabble v1\\src\\scrabblewords.txt"; // change to appropriate file path
    private int turnNumber;

    /**
     * Constructor for Game class
     * @param numPlayers the number of players who will be playing the game
     */
    public Game(int numPlayers) {
        gameBoard = new Board();
        gameBag = new Bag();
        players = new ArrayList<>();
        gameOver = false;
        scanner = new Scanner(System.in);
        successiveScorelessTurns = 0;
        turnNumber = 0;
        this.wordsInPlay = new ArrayList<>();

        for (int i = 1; i <= numPlayers; i++) {
            System.out.println("Enter player " + i + "'s name");
            String playerName = scanner.nextLine();
            Player player = new Player(playerName);
            player.drawTiles(gameBag, 7);
            players.add(player);
        }
        currentPlayerIndex = 0;
    }

    /**
     * Method start initializes the game and checks if it is over.
     */
    public void start() {
        System.out.println("Welcome to \"Not Scrabble\"");
        //Checks if game is over. If not, repeat
        while (!gameOver) {
            takeTurn();
            checkGameOver();
        }
        displayScores();
    }

    /**
     * Method takeTurn() allows players to choose to Pass, Exchange Tiles, or Play their turn.
     */
    private void takeTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println(currentPlayer.getName() + " 's turn");

        //Display board and score
        gameBoard.displayBoard();
        currentPlayer.showTiles();
        System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());

        //Ask player to make choice for their turn
        boolean validChoice = false;
        do {
            System.out.println("Please type Pass, Exchange or Play if you would like to pass your turn, exchange tiles or play tiles respectively");
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
                case ("pass"):
                    validChoice = true;
                    System.out.println("You passed your turn");
                    System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    successiveScorelessTurns++;
                    turnNumber++;
                    break;

                case ("exchange"):
                    validChoice = true;
                    handleExchange(currentPlayer);
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    successiveScorelessTurns++;
                    turnNumber++;
                    break;

                case ("play"):
                    validChoice = true;
                    handlePlay(currentPlayer);
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    turnNumber++;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        } while (!validChoice);
    }

    /**
     * Method to handle if the player wishes to exchange a tile on their turn
     * @param currentPlayer the player whose turn it is
     */
    private void handleExchange(Player currentPlayer) {
        System.out.println("Please enter the tiles you wish to exchange, separated by a comma");

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
                System.out.println("You don't have this tile: " + tileLetter);
            }
        }
        //Draw tiles from game bag
        currentPlayer.drawTiles(gameBag, numTilesToDraw);
        currentPlayer.showTiles();

    }

    /**
     * Method to handle the play condition and word when a player chooses "Play" on their turn
     * @param currentPlayer the player whose turn it is
     */
    private void handlePlay(Player currentPlayer) {
        gameBoard.displayBoard();
        currentPlayer.showTiles();
        boolean validInput = false;

        while (!validInput) {
            // get tile letter and position from player
            System.out.println("Please enter tiles and positions (e.g. R:A6, R:A8, E:A9), or enter pass to cancel turn");
            String input = scanner.nextLine();
            String[] tilePositionCords = input.split(",");

            WordValidator wordValidator = new WordValidator(gameBoard, dictionaryFilePath);

            List<Position> checkPositions = new ArrayList<>();
            Map<Tile, Position> tilesToPlay = new HashMap<>();

            boolean isValid = true;

            //User cancels turn
            if(input.equalsIgnoreCase("Pass")){break;}

            // separate the tile letter from its position and verify proper format was used
            for (String tileInfo : tilePositionCords) {
                String[] info = tileInfo.split(":");
                if (info.length != 2) {
                    System.out.println("invalid format, please use Tile:Position format");
                    isValid = false;
                    break;
                }
                // check if entered tile letter is a letter
                char tileLetter = info[0].trim().toUpperCase().charAt(0);
                if (!Character.isLetter(tileLetter)) {
                    System.out.println("invalid tile letter: " + tileLetter);
                    isValid = false;
                    break;
                }
                Position position = gameBoard.parsePosition(info[1]);
                // check if a valid position was entered
                if (position == null) {
                    System.out.println("invalid position entered, please try again");
                    isValid = false;
                    break;
                }
                // check if player has tile in rack
                Tile tile = currentPlayer.getTile(String.valueOf(tileLetter));
                if (tile == null) {
                    System.out.println("you do not have this tile in your rack: " + tileLetter);
                    isValid = false;
                    break;
                }

                // check if position is already occupied
                if (position.isOccupied()) {
                    System.out.println("invalid formation, the position " + info[1] + " is already occupied");
                    isValid = false;
                    break;
                }
                checkPositions.add(position);
                tilesToPlay.put(tile, position);
            }
            if (!isValid){
                continue;
            }
            if (!wordValidator.arePositionsAligned(checkPositions)) {
                System.out.println("invalid formation, tiles must be placed in a straight line, either horizontally or vertically");
                continue;
            }
            if (turnNumber != 0 && !wordValidator.isConnectedToAdjacentTiles(checkPositions)) {
                System.out.println("invalid formation, at least one tile must be adjacent to an existing tile");
                continue;
            }
            if (turnNumber == 0) {
                if (!checkPositions.contains(gameBoard.parsePosition("H8"))) {
                    System.out.println("invalid formation, first word must cover centre square (H8)");
                    continue;
                }
                else if (checkPositions.size() == 1){
                    System.out.println("invalid formation, the first turn must play at least 2 tiles");
                    continue;
                }
            }

            for (Map.Entry<Tile, Position> currentTile : tilesToPlay.entrySet()) {
                Tile tile = currentTile.getKey();
                Position position = currentTile.getValue();

                gameBoard.placeTile(tile, position.getRow(), position.getCol());
                currentPlayer.removeTile(tile.getLetter() + "");
            }

            List<String> attemptedWordsInPlay = gameBoard.gatherWordsOnBoard();

            if (turnNumber != 0) {
                List<String> newWords = getNewWords(attemptedWordsInPlay);


                if (!wordValidator.isValidWord(newWords)) {
                    System.out.println("invalid formation, please try again");
                    revertTiles(tilesToPlay);
                    break;
                } else {
                    // draw new tiles to replace played tiles
                    currentPlayer.drawTiles(gameBag, tilesToPlay.size());
                    int turnScore = calculateScore(newWords);
                    currentPlayer.setScore(currentPlayer.getScore() + turnScore);
                    System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());
                    wordsInPlay = attemptedWordsInPlay;
                    validInput = true;
                }

            } else {
                if (!wordValidator.isValidWord(attemptedWordsInPlay)) {
                    System.out.println("invalid formation, please try again");
                    revertTiles(tilesToPlay);
                } else {
                    wordsInPlay.add(attemptedWordsInPlay.get(0));
                    // draw new tiles to replace played tiles
                    currentPlayer.drawTiles(gameBag, tilesToPlay.size());
                    int turnScore = calculateScore(attemptedWordsInPlay);
                    currentPlayer.setScore(currentPlayer.getScore() + turnScore);
                    System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());
                    validInput = true;
                }
            }
        }
    }

    /**
     * Method to get a list of new words formed during the current players turn
     * @param attemptedWordsInPlay list of words on the board after current players turn
     * @return a list of all the new words created from current player's turn
     */
    private List<String> getNewWords(List<String> attemptedWordsInPlay) {
        Map<String, Integer> wordsInPlayCount = new HashMap<>();
        for (String word : wordsInPlay){
            wordsInPlayCount.put(word, wordsInPlayCount.getOrDefault(word, 0) + 1);
        }

        List<String> tempList = new ArrayList<>();

        for (String word : attemptedWordsInPlay){
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
            System.out.println(successiveScorelessTurns + " scoreless turns have passed, would you like to continue the game? Type Yes or No");

            //if not yes or no, demand a valid command
            while (!validChoice) {
                String continuePlaying = scanner.nextLine();
                if (continuePlaying.equalsIgnoreCase("yes")) {
                    validChoice = true;
                    gameOver = false;
                } else if (continuePlaying.equalsIgnoreCase("no")) {
                    validChoice = true;
                    gameOver = true;
                } else {
                    System.out.println("invalid choice. please enter either Yes or No");
                }
            }
        } else {
            gameOver = false;
        }
    }

    /**
     * Reverts the tiles to empty
     * @param tilesToPlay the tiles the player attempted to play
     */
    private void revertTiles(Map<Tile, Position> tilesToPlay) {
        for (Map.Entry<Tile, Position> tile : tilesToPlay.entrySet()) {
            Position position = tile.getValue();
            position.setTile(null); // reset the position to empty
        }
    }

    /**
     * Method displayScores() will display each player's score and also choose the winner based on the highest score
     */
    private void displayScores() {
        //Make winner variable the first player by default
        Player winner = players.get(currentPlayerIndex);
        System.out.println("Game over! Final scores:");
        //Print each player's score
        for (Player player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
            System.out.println(player.getName() + ": " + player.getScore());
        }
        //Print winner and score
        System.out.println("The winner is " + winner.getName() + " with a score of " + winner.getScore() + "!");
    }

    /**
     * Method calculateScore() will calculate the score for each word played.
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
}
