package sk.o2.utilities;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtilities {
    public static String padSpaces(String input) {
        return padSpaces(input, 2);
    }
    
    public static String padSpaces(String input, int spaces) {
        StringBuilder padding = new StringBuilder();
        
        for (int i = 0; i < spaces; i++) {
            padding.append(" ");
        }
        
        String paddingString = padding.toString();
        
        return Arrays.stream(input.split("\n"))
            .map(line -> paddingString + line)
            .collect(Collectors.joining("\n"));
    }
}
