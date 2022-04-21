package learning.markov;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class MarkovLanguage extends MarkovChain<String,Character> {

    public void countFrom(File languageFile, String language) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(languageFile));
        Optional<Character> prev = Optional.empty();
        for (;;) {
            int read = reader.read();
            if (read < 0) {
                break;
            } else {
                Optional<Character> next = usableCharacter((char)read);
                if (next.isPresent()) {
                    char c = next.get();
                    count(prev, language, c);
                    prev = next;
                }
            }
        }
    }

    public static Optional<Character> usableCharacter(char c) {
        c = Character.toLowerCase(c);
        if (Character.isSpaceChar(c)) {
            c = ' ';
        }
        if (Character.isAlphabetic(c) || c == ' ') {
            return Optional.of(c);
        } else {
            return Optional.empty();
        }
    }

    public static ArrayList<Character> usableCharacters(String input) {
        ArrayList<Character> result = new ArrayList<>();
        for (char c: input.toCharArray()) {
            usableCharacter(c).ifPresent(result::add);
        }
        return result;
    }
}
