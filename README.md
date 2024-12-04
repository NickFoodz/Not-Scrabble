

***************************************************IMPORTANT***************************************************

This is a school project for SYSC3110.
Created by Nick Fuda and Andrew Roberts

***************************************************************************************************************
Configuration:

The game can be played by up to 4 players, including up to 3 AI.

You will be prompted to enter the number of human players for your game. You must have between 1 and 4 human players
to begin a game of scrabble.

You will then be prompted to enter the number of AI players in your game. You cannot exceed a maximum of four players,
so if you choose 4 human players, enter 0. If you choose 1, you can have up to 3.

After entering the number of players and AI, you will be prompted to enter the human player's names. 
After doing so, you may choose a custom board. The example custom boards are SYSC3110CustomBoard.txt and XML.txt.
Opening these in notepad or any text editor will show you how to input a custom tile with a premium.

    XML Configuration
    Generally, within <board>(format goes here)</board>, follow this format
    For each square:
    <square>
	<pos>NumberLetter</pos>
	<type>integer</type>
    </square>
    Number letter example: 9A
    Integers can be between 2 and 5 for premium tiles, any other number is a normal tile
        2 -> Double Letter Tile
        3 -> Double Word Tile
        4 -> Triple Letter Tile
        5 -> Triple Word Tile

If no board is selected, or the options "No" or "Cancel" are chosen, the default board will be used.
Next, the game will begin.

For the rules of scrabble, follow this link https://www.scrabblepages.com/scrabble/rules/

The first word must cover the center square (H8) and be at least 2 letters. Valid placements are horizontal (read
left to right) and vertical (read top to bottom).

Score is calculated based on tile value and based on premium tiles covered. Premium tiles can only be used once,
and adding to a word that covers them does not allow reuse.

You may exchange any number of tiles instead of playing your turn. You also may pass your turn. After 6 successive
scoreless turns, you will be prompted on if you wish to continue. Yes continues, No finishes the game.

Once the game ends, the winner is announced.

**NOTE**
Some moves by the AI may appear to be invalid, such as placing letters beside each other that should not work.
Unfortunately, this is a product of the dictionary we use, which is much larger than the one we previously had,
and has some odd combinations. Feel free to Ctrl+F in the dictionary to validate this, as it is not an issue in the 
code that validates words. The AI can only play combinations of letters that are found in the dictionary, so it cannot
actually make an invalid move, while the wordValidator does not actually allow any invalid moves. This can be tested by 
looking up two letters that don't go beside each other in the dictionary, not finding them, and trying to play them.

***************************************************************************************************************
Known Bugs:
Milestone 2
- When inputting a non integer input for number of players, the game can crash. *FIXED*
- An invalid placement of 2 or more tiles sometimes registers as valid if they are in the same row/column
- Clicking a button twice may cause the input to read invalid until you pass. Next player should have it work.
- Undo button does not re-enable the selected tile from the player's rack. *FIXED*

Milestone 3
- Entering a blank tile sometimes does not show the placed tile's letter *FIXED*
- Selecting a tile from the rack and then selecting a second tile from the rack without placement will disable the first *FIXED*
- Selecting exchange while not selecting tiles to swap may cause your turn to pass with nothing being exchanged. *FIXED*

***************************************************************************************************************

Contributions:
Andrew Roberts: Position, Bag, Player, Game, WordValidator, Board, LetterPointValues, PlayerRackPanel,
ScrabbleBoardPanel, ScrabbleView, readme, AI, Timer, Undo/Redo features, Save/Load GUI.

Nick Fuda: Board, Game, Position, WordValidator classes, javadocs, scrabblewords.txt, readme, ScrabbleButton, AI,
ScrabbleBoardPanel, ScrabbleView, XML, CustomBoard methods, and UML, Save/Load feature.

Both of us also committed many times, with many iterations of ideas pushed to GitHub as we collaborated and 
discussed future design decisions.
***************************************************************************************************************
Design Decisions

Milestone 1:

The game dictionary was placed in a text file and a parser class was built first to allow the user to modify a
simple text file to add or remove words. Parser allows the words in the text file to be read so long as they are
comma separated. They are also each on their own line, and allows users to use the find function (Ctrl+F) to see 
if a word is valid. This is simpler than using a class with a list that not all users can easily access or modify.

We decided to represent key features of the game and entities interacting with the game as individual classes. 
This includes:
- A Board for the game to be played on, where each tile is a position in a 2D array. It provides methods to allow 
words to be gathered, to display tiles on the board in correct position, and place tiles.
    - Data Structures:
      A 2D array was used to give coordinates to each position, as it makes the most sense on a board. This allows
    positions to be referenced by row and positions ([row][position])


- A Bag for which tiles of the game are stored in limited number, to both limit how long a game can play and
provide a limited amount of each letter (nobody can work with a full rack of a single letter). It has methods
for the letter frequencies and to allow drawing tiles from the bag, as well as checking if it is empty.
    - Data Structures:
      - A List of tiles was used to store all tiles with an indexed position. This allowed the use of List methods
      - A Map was used for letterFrequencies and points so that each letter could be used as a key to easily find
      their points or frequencies when needed.

- A Player class to allow the player to have a rack of letters, score, and unique name to identify them. It also 
provides methods for them to interact with the game.
    - Data Structures:
      - A String was used for the player name
      - An ArrayList was used as the players rack to allow iterable searching for letters, and using methods such as
      remove for managing the letters in a rack
      - Int was used for score as there are no decimals for score

- A Position class to handle checks during gameplay, such as if a position is occupied, to display the tile in a 
position, to get and set tiles on the board, etc.
    - Data Structures:
      - Int was used for rows and columns because those are discrete values
      - Boolean was used for occupied as this is either true or false for any given position on the board

- A Game class to run the game and check if it is still ongoing or over. This class is the true main class that
is associated with 7 of 10 current classes. It allows for different numbers of players, switches turns, allows
players to choose what to do on their turns, and count the number of turns played. It provides the main loop to 
run and can calculate and display scores, including displaying who won!
    - Data Structures:
      - A list of players was used for players so that the game could iterate through using the index and determine
      whose turn it was
      - Boolean was used for gameOver as there can only be 2 values: true if over, false if not
      - Scanner was used for inputs to read the player's console inputs
      - int was used to count successive scoreless turns and the turn numbers, as well as the index of the player
      for turn purposes
      - wordsInPlay was a list of strings to track the words on the board. This also allows the use of iterating over 
      the list and adding to it (methods found natively in the list class)
      - String was used for dictionaryFilePath so that the user could copy the file path to the dictionary

- A Tile class to represent the tiles that are used on the board to build words. A tile has a letter and point
value, which are used in the game to determine score and validity of words.
    - Data Structures:
      - char was used for the letter as all tiles only have single letters
      - int was used for pointValues because these are discrete

Some smaller classes with specific functions:
- The WordValidator class provides methods to check if a player's input is legal. It calls the Parser once it determines
that the attempted word is legal to play to check if it is a valid word according to the dictionary.
    - Data Structures:
      - String was used for dictionaryFilePath so that it could be used in the parser

  
- The LetterPointValues class is used to store the point values of each letter. This is planned to be modified
for special tiles once those are implemented.
    - Data Structures:
      - A Map was used so that the Letters could be used as keys, as each key would have a value assigned as a
      point value for the letter.

Non-Classes
- scrabblewords.txt is a text document representing a dictionary. Each line is a single word. This file exists so
that the players can add custom words if they choose. The parser can find a word in the dictionary and determine
if it is valid in the game.

###############################################################################################################
Milestone 2:

- Changed "Game" class to "ScrabbleModel" to better reflect the nature of what it is
- Added ScrabbleView, ScrabbleBoardPanel, PlayerRackPanel, and ScrabbleButton GUI classes.
- Removed the Parser class and implemented it as a method into ScrabbleModel
- Created ScrabbleModelTest class
- Main class removed, replaced with view

- ScrabbleView
  - The main GUI class, which creates the frame of the GUI and implements different java swing components.
  - The board, scoreboard, rack, and turn options are all JComponents
  - Errors in play currently popup as JDialogs
  - The JFrame reflects the changes to the model
    - Data Structures: Hashmap used to map <Player,JLabel> locations for the scoreboard to allow updates

- ScrabbleBoardPanel
  - Represents the board in a game of scrabble
  - JPanel containing a grid of ScrabbleButton objects
  - Provides a method to revert the board if turn is invalid
    - Data Structures: None
  
- PlayerRackPanel
  - Represents player's tiles to play (The rack from class Player)
  - Changes depending on the current player
  - Allows the players to click a tile, then place it on the board.
    - Data Structures:None
  - Added an incomplete implementation of UNDO (Undo does not re-enable selected rack tile)

- ScrabbleButton
  - Makes up each place on the board, has coordinates and stores the score and Tile
  - Provides color changing to allow users to see where tiles are placed.
  - Provides method to revert button to empty, used by ScrabbleBoardPanel

- Parser
  - Removed Parser, changed how dictionary works.
  - Dictionary is now stored in project module, scanned using Scanner to add it to an ArrayList.
  - No longer requires filepath and user tampering with code.

- WordValidator
  - Methods updated to work without Parser
  - Added method to check if tile placements will have gaps.

- Player
  - Added field to track player's moves in a turn.
  - Added field to track what tiles a player wishes to exchange in a turn.
  - Updated drawTiles method to work with the GUI.
  - Added corresponding getter, setter, add and remove methods for gameplay execution .

- ScrabbleModel
  - Completely overhauled class to work with GUI.
  - Added a constructor specifically for test cases
  - Added ScrabbleView field to pass a reference of the game view.
  - Field isTest to simplify testing applications.
  - handlePlay was refactored to make adjustments easier and improve readability.
  - checkGameOver now makes use of GUI to get player input.
  - Added method showMessage to make GUI feedback dependent on isTest variable.
  - Added getters and setters for fields.


- ScrabbleModelTest
  - Contains all the tests we did for the model
  - test handlePass() ensures that the handlePass method switches to the next player
    - Also checks that the rack of the current player isn't the rack of the former
  - test handleExchange() ensures that handleExchange swaps tiles, and the player does not have the same tiles afterwords
    - also ensures the game switches whose turn it is
  - test handlePlay() tests if a player's move is valid, and if it switches players afterwords
    -first tests an invalid move, then tests a valid one  
  - test testPlacements() tests that valid moves allow handlePlay to return true, and invalid returns false.
    - Tests to check first move in invalid spot, with valid word and alignment returns false after play attempt
    - Checks that a word is fully aligned but in invalid spot returns false after play attempt
    - Checks that word that covers center square for first move but has invalid positions returns false after attempt
    - Checks that valid word returns true for first move
    - Checks that valid word in invalid position returns false for second move
    - Checks that valid word returns true for second move
      - Checks that word can use existing tiles to create a new word and returns true
      - Checks that a valid play switches players
  - test testScoring() checks that scores are properly calculated
    - Checks that scores are initialized to 0
    - Checks that valid turns add to score
  - test NextPlayerTurn() checks that the NextPlayerTurn() function returns the correct player
  - test getCurrentPlayer() checks that the player whose turn it is returns
  - test getPlayers() checks that the player list contains all the players in the game

###############################################################################################################
Milestone 3

- Changed maximum players to 4, with a minimum of 1 human player.
  - No longer accepts non-integer inputs leading to errors
  - No longer accepts values greater than 4 or under 0.
- Added ability to add AI players

- Added Blank Tiles
  - 2 per game
  - Blank tiles can be changed into any letter at a value of 0 points
  - Only usable by human players, AI class will exchange them

- Added new class: AI
  - Class AI allows for an AI player to make plays in Scrabble
    - Priority 1: If the AI has any blank pieces, they will exchange them. The AI will only exchange blank tiles.
    - Priority 2: If the AI can play a word, it plays it on the board.
    - Priority 3: If the AI cannot form a word, exchange 3 tiles
    - Priority 3: If the AI cannot play a word, exchange, and has no blank tiles, it passes its turn
  - The priorities given were done to allow the play option to not be too complex to include blank tiles
  - Blank tiles are best left for human players given current functionality of the game
  - If no move can be made, the AI player would need to skip their turn.
  - The AI's tiles are not shown to the player to ensure a fair game.

- Added Premium Squares
  - The squares follow the coloration of an original scrabble board, and follow similar function
    - Pink tiles are Double word score tiles
    - Red tiles are Triple word score tiles
    - Cyan tiles are double letter score tiles
    - Blue tiles are triple letter score tiles.

###############################################################################################################
Milestone 4/Bonus

- Added a save/load feature that allows the user to select/create a file to load/save their game from
  - Implemented using java serialization
  - Changes all fields to the saved fields and updates the GUI to reflect.
  - Uses FileChooser to choose a file, preventing errors from inputting the wrong file name

  - Added the option to import custom boards from an XML
    - Formatting can be seen in XML configuration at top of readme
    - XML examples were included.
    - Choosing an XML uses FileChooser.
      -  Choosing an invalid file will give a blank board, as format is not checked.
      - If not in the required XML format, no premium squares are added to the board.

- Added multi-level Undo/Redo buttons to the game
  - Currently, you cannot undo/redo a move if you have a tile selected, you must first place it somewhere 
    before undo redoing a move

- Added a timer mode option for players, they can specify the length of time they get for each turn
  - their turn will be automatically passed if the timer runs out

- Changed how the number of players/AI is taken from the player to a drop-down menu to improve simplicity
  for the user

- Added FileChooser GUI for the 
***************************************************************************************************************

Future Goals;

Milestone 1:
- Implementing GUI, Special/Blank Tiles, and some simpler functionality for input (solvable with GUI).
- Decoupling some of the classes to allow more functionality with less dependency.
- Removing the Main class and running the main function directly through Main, to remove an unnecessary class
- Fixing bugs 
- More testing and development time allocated

###############################################################################################################

Milestone 2:
- Better implementing MVC, which was difficult to do for this milestone, as the two of us were quite busy
  - Such as creating a controller, having view be an interface, having the current view become modelview or frame
- Get better with GUI, as both of us did not learn programming prior to university.
- Fix bugs in model, logic and GUI.
- More time to test and develop, especially GUI related components.
- Addition of blank tiles, premium squares and AI Players.
- Aim for looser coupling and have higher cohesion

###############################################################################################################

Milestone 3: 
- Getting rid of code smell
  - We would like to move more towards MVC
- We would like to try to optimize the code and reduce any junk functions. Many of the functions and even some
  constructors are results of needing to have a different test model that does not use GUI or player input.

***************************************************************************************************************
Other Notes:

Milestone 1:
This project thus far was made by only two people in a short time (as exams were in the week preceding).
We are in a group of three, a smaller group than many of our peers, and one member has not responded to any
attempt at communication. As such, we have had limited resources for testing, and have perhaps spent more
time comparatively trying to make up for two missing group members.

We hope you take this into consideration when reviewing our first deliverable, as we focused on delivering
a functioning foundation to the whole project.

Thank you, we hope you enjoy our recreation of a classic game!

###############################################################################################################

Milestone 2:

We still have not heard back from our third group member, and the actual delegation of work has been harder now
that the project is picking up. Andrew and I are both relatively new to creating GUI, so we got hung up on trying 
to make the GUI work and did not follow the proper MVC format. Now that we are closing in on having the majority
of the GUI made and mostly need to work on the model, we are hoping this allows us to create a better foundation.
We unfortunately could not meet the Sunday deadline, but thankfully the professor put an announcement out that
saved our confidence in being able to present something that is somewhat functional.

#################################################################################################################

Milestone 3: 

Thus far, we have everyone in the Git repository, though this milestone was completed by only Nick and Andrew.

#################################################################################################################

Milestone 4/Bonus:

Milestone 4, the bonus was completed exclusively by Nick and Andrew. 
As this is the last milestone, this entire project was created by Nick and Andrew.