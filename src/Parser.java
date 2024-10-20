import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
Parser CLass checks for the word a user wants to input. This uses a game dictionary in the form of a text file.
 */
public class Parser {

    /**
     * Checks if a word is valid by checking if it is in the dictionary
     * @param filePath the path to the dictionary
     * @param userInput the word the user entered
     * @return true if the word was found, false if not
     * @throws IOException if the file cannot be found
     */
    public static boolean validWord(String filePath, String userInput) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            if (line.equals(userInput)) {
                return true; // Word found
            }
        }
        return false; // Word not found
    }
}