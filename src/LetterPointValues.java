import java.util.HashMap;
import java.util.Map;
public class LetterPointValues {
    private static final Map<Character, Integer> letterPoints = new HashMap<>();

    static {
        letterPoints.put('E', 1);
        letterPoints.put('A', 1);
        letterPoints.put('I', 1);
        letterPoints.put('O', 1);
        letterPoints.put('N', 1);
        letterPoints.put('R', 1);
        letterPoints.put('T', 1);
        letterPoints.put('L', 1);
        letterPoints.put('S', 1);
        letterPoints.put('U', 1);
        letterPoints.put('D', 2);
        letterPoints.put('G', 2);
        letterPoints.put('B', 3);
        letterPoints.put('C', 3);
        letterPoints.put('M', 3);
        letterPoints.put('P', 3);
        letterPoints.put('F', 4);
        letterPoints.put('H', 4);
        letterPoints.put('V', 4);
        letterPoints.put('W', 4);
        letterPoints.put('Y', 4);
        letterPoints.put('K', 5);
        letterPoints.put('J', 8);
        letterPoints.put('X', 8);
        letterPoints.put('Q', 10);
        letterPoints.put('Z', 10);
    }

    public static int getPointValue(char letter){
        return letterPoints.get(Character.toUpperCase(letter));
    }
}
