This is a school project for SYSC3110.
Created by Nick Fuda and Andrew Roberts

***************************************************IMPORTANT***************************************************

Change dictionaryFilePath variable in Game Class to the file path where scrabblewords.txt
is located in your system for the dictionary to work as intended.

***************************************************************************************************************
Known Bugs:
-When inputting an incorrect input as a player, the game can crash. Also, inputting a word that does not exist 
can remove tiles from the rack despite not being used.

***************************************************************************************************************

Contributions:
Andrew Roberts: Position, Bag, Player, Game, WordValidator, Board, LetterPointValues, and Main classes 
Nick Fuda: Main, Board, Game, Position, WordValidator classes, javadocs, scrabblewords.txt, readme, and UML

***************************************************************************************************************
Design Decisions

The game dictionary was placed in a text file and a parser class was built first to allow the user to modify a
simple text file to add or remove words. Parser allows the words in the text file to be read so long as they are
comma separated. They are also each on their own line, and allows users to use the find function (Ctrl+F) to see 
if a word is valid. This is simpler than using a class with a list that not all users can easily access or modify.

We decided to represent key features of the game and entities interacting with the game as individual classes. 
This includes:
- A Board for the game to be played on, where each tile is a position in a 2D array. It provides methods to allow 
words to be gathered, to display tiles on the board in correct position, and place tiles.
- A Bag for which tiles of the game are stored in limited number, to both limit how long a game can play and
provide a limited amount of each letter (nobody can work with a full rack of a single letter). It has methods
for the letter frequencies and to allow drawing tiles from the bag, as well as checking if it is empty.
- A Player class to allow the player to have a rack of letters, score, and unique name to identify them. It also 
provides methods for them to interact with the game.
- A Position class to handle checks during gameplay, such as if a position is occupied, to display the tile in a 
position, to get and set tiles on the board, etc.
- A Game class to run the game and check if it is still ongoing or over. This class is the true main class that
is associated with 7 of 10 current classes. It allows for different numbers of players, switches turns, allows
players to choose what to do on their turns, and count the number of turns played. It provides the main loop to 
run and can calculate and display scores, including displaying who won!
- A Tile class to represent the tiles that are used on the board to build words. A tile has a letter and point
value, which are used in the game to determine score and validity of words.

Some smaller classes with specific functions:
- The WordValidator class provides methods to check if a player's input is legal. It calls the Parser once it determines
that the attempted word is legal to play to check if it is a valid word according to the dictionary.
- The Parser class is used to read from the dictionary (scrabblewords.txt). If a word is in there, it can be played.
- The LetterPointValues class is used to store the point values of each letter. This is planned to be modified
for special tiles once those are implemented.
- The Main class is the class that we are currently running the game through. We also kept it primarily for testing the
methods of each class prior to implementing Game.

Non-Classes
- scrabblewords.txt is a text document representing a dictionary. Each line is a single word. This file exists so
that the players can add custom words if they choose. The parser can find a word in the dictionary and determine
if it is valid in the game.


***************************************************************************************************************

Future Goals;

- Implementing GUI, Special/Blank Tiles, and some simpler functionality for input (solvable with GUI).
- Decoupling some of the classes to allow more functionality with less dependency.
- Removing the Main class and running the main function directly through Main, to remove an unnecessary class

***************************************************************************************************************
Other Notes:

This project thus far was made by only two people in a short time (as exams were in the week preceding).
We are in a group of three, a smaller group than many of our peers, and one member has not responded to any
attempt at communication. As such, we have had limited resources for testing, and have perhaps spent more
time comparatively trying to make up for two missing group members.

We hope you take this into consideration when reviewing our first deliverable, as we focused on delivering
a functioning foundation to the whole project.

Thank you, we hope you enjoy our recreation of a classic game!