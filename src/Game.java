import java.util.*;

public class Game {
    private static Board gameBoard;
    private static List<Player> players;
    private static Bag gameBag;
    private int currentPlayerIndex;
    private boolean gameOver;
    private Scanner scanner;
    private int successiveScorelessTurns;
    private List<String> wordsInPlay;
    private String dictionaryFilePath = "E:\\SYSC 3110 Labs\\SYSC3110_Group33_F24\\src\\scrabblewords.txt"; // change to appropriate file path
    private int turnNumber;

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

    public void start() {
        System.out.println("Welcome to \"Not Scrabble\"");

        while (!gameOver) {
            takeTurn();
            checkGameOver();
        }
        displayScores();
    }

    private void takeTurn() {
        Player currentPlayer = players.get(currentPlayerIndex);
        System.out.println(currentPlayer.getName() + " 's turn");

        gameBoard.displayBoard();
        currentPlayer.showTiles();
        System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());

        boolean validChoice = false;
        do {
            System.out.println("Please type Pass, Exchange or Play if you would like to pass your turn, exchange tiles or play tiles respectively");
            String choice = scanner.nextLine().toLowerCase();

            switch (choice) {
                case ("pass"):
                    validChoice = true;
                    System.out.println("You passed your turn");
                    System.out.println(currentPlayer.getName() + "'s score: " + currentPlayer.getScore());
                    currentPlayerIndex = (currentPlayerIndex + 1) & players.size();
                    successiveScorelessTurns++;
                    turnNumber++;
                    break;

                case ("exchange"):
                    validChoice = true;
                    handleExchange(currentPlayer);
                    currentPlayerIndex = (currentPlayerIndex + 1) & players.size();
                    successiveScorelessTurns++;
                    turnNumber++;
                    break;

                case ("play"):
                    validChoice = true;
                    handlePlay(currentPlayer);
                    currentPlayerIndex = (currentPlayerIndex + 1) & players.size();
                    turnNumber++;
                    break;

                default:
                    System.out.println("Invalid choice");
            }
        } while (!validChoice);
    }

    private void handleExchange(Player currentPlayer) {
        System.out.println("Please enter the tiles you wish to exchange, separated by a comma");

        String[] exchangeTiles = scanner.nextLine().split(",");
        for (int i = 0; i < exchangeTiles.length; i++) {
            exchangeTiles[i] = exchangeTiles[i].trim();
        }

        int numTilesToDraw = 0;
        for (String tileLetter : exchangeTiles) {
            if (currentPlayer.removeTile(tileLetter)) {
                numTilesToDraw++;
            } else {
                System.out.println("you don't have this tile: " + tileLetter);
            }
        }
        currentPlayer.drawTiles(gameBag, numTilesToDraw);
        currentPlayer.showTiles();

    }

    private void handlePlay(Player currentPlayer) {
        gameBoard.displayBoard();
        currentPlayer.showTiles();
        boolean validInput = false;

        while (!validInput) {
            // get tile letter and position from player
            System.out.println("Please enter tiles and positions (e.g. R:A6, R:A8, E:A9");
            String input = scanner.nextLine();
            String[] tilePositionCords = input.split(",");

            WordValidator wordValidator = new WordValidator(gameBoard, dictionaryFilePath);

            List<Position> checkPositions = new ArrayList<>();
            Map<Tile, Position> tilesToPlay = new HashMap<>();

            // separate the tile letter from its position and verify proper format was used
            for (String tileInfo : tilePositionCords) {
                String[] info = tileInfo.split(":");
                if (info.length != 2) {
                    System.out.println("invalid format, please use Tile:Position format");
                    break;
                }
                // check if entered tile letter is a letter
                char tileLetter = info[0].trim().toUpperCase().charAt(0);
                if (!Character.isLetter(tileLetter)) {
                    System.out.println("invalid tile letter: " + tileLetter);
                    break;
                }
                Position position = gameBoard.parsePosition(info[1]);
                // check if a valid position was entered
                if (position == null) {
                    System.out.println("invalid position entered, please try again");
                    break;
                }
                // check if player has tile in rack
                Tile tile = currentPlayer.getTile(String.valueOf(tileLetter));
                if (tile == null) {
                    System.out.println("you do not have this tile in your rack: " + tileLetter);
                    break;
                }

                // check if position is already occupied
                if (position.isOccupied()) {
                    System.out.println("invalid formation, the position " + info[1] + " is already occupied");
                    break;
                }
                checkPositions.add(position);
                tilesToPlay.put(tile, position);
            }
            if (!wordValidator.arePositionsAligned(checkPositions)) {
                System.out.println("invalid formation, tiles must be placed in a straight line, either horizontally or vertically");
                continue;
            }
            if (turnNumber != 0 && !wordValidator.isConnectedToAdjacentTiles(checkPositions)) {
                System.out.println("invalid formation, at least one tile must be adjacent to an existing tile");
                continue;
            }
            if (turnNumber == 0 && !checkPositions.contains(gameBoard.parsePosition("H8"))){
                System.out.println("invalid formation, first word must cover centre square (H8)");
                continue;
            }

            for (Map.Entry<Tile, Position> currentTile : tilesToPlay.entrySet()) {
                Tile tile = currentTile.getKey();
                Position position = currentTile.getValue();

                gameBoard.placeTile(tile, position.getRow(), position.getCol());
                currentPlayer.removeTile(tile.getLetter() + "");
            }

            List<String> attemptedWordsInPlay = gameBoard.gatherWordsOnBoard();

            if (turnNumber != 0) {
                int numNewWords = attemptedWordsInPlay.size() - wordsInPlay.size();
                int startingIndex = attemptedWordsInPlay.size() - numNewWords - 1;
                List<String> tempList = new ArrayList<>(attemptedWordsInPlay.subList(startingIndex, attemptedWordsInPlay.size()));

                if (!wordValidator.isValidWord(tempList)) {
                    System.out.println("invalid formation, please try again");
                    revertTiles(tilesToPlay);
                    break;
                } else {
                    // draw new tiles to replace played tiles
                    currentPlayer.drawTiles(gameBag, tilesToPlay.size());
                    int turnScore = calculateScore(tempList);
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


    private void checkGameOver() {
        if (players.get(currentPlayerIndex).isRackEmpty() && gameBag.isEmpty()) {
            gameOver = true;

        } else if (successiveScorelessTurns >= 6) {
            boolean validChoice = false;
            System.out.println(successiveScorelessTurns + " scoreless turns have passed, would you like to continue the game? Type Yes or No");

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

    private void revertTiles(Map<Tile, Position> tilesToPlay) {
        for (Map.Entry<Tile, Position> tile : tilesToPlay.entrySet()) {
            Position position = tile.getValue();
            position.setTile(null); // reset the position to empty
        }
    }

    private void displayScores() {
        Player winner = players.get(currentPlayerIndex);

        System.out.println("Game over! Final scores:");
        for (Player player : players) {
            if (player.getScore() > winner.getScore()) {
                winner = player;
            }
            System.out.println(player.getName() + ": " + player.getScore());
        }
        System.out.println("The winner is " + winner.getName() + " with a score of " + winner.getScore() + "!");
    }

    private int calculateScore(List<String> wordsFormed) {
        int score = 0;

        for (String word : wordsFormed) {
            for (char letter : word.toCharArray()) {
                score += LetterPointValues.getPointValue(letter);
            }
        }
        return score;
    }
}
