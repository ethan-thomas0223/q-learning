package learning.markov;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleMarkovTest {
    private MarkovLanguage chains;

    @Before
    public void setup() throws IOException {
        chains = new MarkovLanguage();
        chains.countFrom(new File("books/english_test.txt"), "English");
        chains.countFrom(new File("books/spanish_test.txt"), "Spanish");
    }

    @Test
    public void testCreateChains() {
        String expected = """
                English
                    Optional.empty:(t:1)
                    Optional[ ]:(c:1)(f:2)(i:1)(m:1)(s:1)(t:2)
                    Optional[a]:(i:1)(l:1)(r:1)
                    Optional[c]:(h:1)
                    Optional[e]:( :1)(s:2)
                    Optional[f]:(i:1)(o:1)
                    Optional[g]:( :1)
                    Optional[h]:(a:1)(i:1)
                    Optional[i]:(l:1)(n:2)(s:2)
                    Optional[k]:(o:1)
                    Optional[l]:( :1)(e:1)(l:1)
                    Optional[m]:(a:2)
                    Optional[n]:(g:1)(s:1)
                    Optional[o]:(r:1)(v:1)
                    Optional[r]:( :1)(k:1)
                    Optional[s]:( :2)(m:1)(t:2)
                    Optional[t]:( :1)(e:2)(h:1)(i:1)
                    Optional[v]:( :1)
                  
                Spanish
                    Optional.empty:(e:1)
                    Optional[ ]:(a:1)(c:1)(d:2)(e:1)(l:1)(m:1)(p:4)
                    Optional[a]:( :1)(d:1)(r:5)(s:2)
                    Optional[b]:(a:2)
                    Optional[c]:(a:1)(h:1)
                    Optional[d]:(e:3)
                    Optional[e]:( :3)(n:1)(q:1)(s:2)(ñ:1)
                    Optional[h]:(i:1)
                    Optional[i]:(v:1)
                    Optional[k]:(o:1)
                    Optional[l]:(a:1)
                    Optional[m]:(a:1)
                    Optional[n]:(a:1)
                    Optional[o]:( :2)(b:2)(v:1)
                    Optional[p]:(a:1)(e:1)(r:2)
                    Optional[q]:(u:1)
                    Optional[ñ]:(o:1)
                    Optional[r]:( :2)(a:1)(c:1)(k:1)(o:2)
                    Optional[s]:( :3)(t:1)
                    Optional[t]:(e:1)
                    Optional[u]:(e:1)
                    Optional[v]:(o:1)
                  
                """;
        assertEquals(expected, chains.toString());
    }

    @Test
    public void testSourceProbabilities() throws IOException {
        for (String filename: new String[]{"books/english_test.txt", "books/spanish_test.txt"}) {
            double eng = chains.probability(file2chars(filename), "English");
            double spa = chains.probability(file2chars(filename), "Spanish");
            if (filename.contains("english")) {
                assertTrue(eng > spa);
            } else {
                assertTrue(eng < spa);
            }
        }
    }

    public static ArrayList<Character> file2chars(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder result = new StringBuilder();
        for (;;) {
            int read = reader.read();
            if (read < 0) {
                return MarkovLanguage.usableCharacters(result.toString());
            } else {
                result.append((char)read);
            }
        }
    }
}
