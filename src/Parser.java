import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
Parser CLass checks for the word a user wants to input. This uses a game dictionary in the form of a text file.
 */
public class Parser {

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