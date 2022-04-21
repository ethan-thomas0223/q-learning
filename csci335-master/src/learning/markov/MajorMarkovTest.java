package learning.markov;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

public class MajorMarkovTest {
    private MarkovLanguage chains;

    @Before
    public void setup() throws IOException {
        chains = new MarkovLanguage();
        chains.countFrom(new File("books/wonderland.txt"), "English");
        chains.countFrom(new File("books/quijote.txt"), "Spanish");
        chains.countFrom(new File("books/candide.txt"), "French");
        chains.countFrom(new File("books/zarathustra.txt"), "German");
    }

    public void testAllOn(String filename) throws IOException {
        System.out.println(filename);
        ArrayList<Character> text = SimpleMarkovTest.file2chars(filename);
        for (String language: chains.allLabels()) {
            System.out.println(language + ": " + chains.probability(text, language));
        }
    }

    @Test
    public void phraseTest() {
        testAllOnText("This is a test. This is only a test.",
                new double[]{1.225514110535642E-31, 2.9251201488287396E-41, 1.292352130718694E-36, 6.585811806049583E-39});
        testAllOnText("Este es una prueba. Este es solamente una prueba.",
                new double[]{2.092210415123763E-60, 3.4126938072799486E-46, 1.092936393620694E-50, 1.4600340192904676E-53});
    }

    public void testAllOnText(String text, double[] expected) {
        int i = 0;
        for (String language: chains.allLabels()) {
            assertEquals(expected[i], chains.probability(MarkovLanguage.usableCharacters(text), language), 1.0E-30);
            i += 1;
        }
    }

    @Test
    public void testSentenceDistributions() {
        LinkedHashMap<String,Double> d1 = chains.labelDistribution(MarkovLanguage.usableCharacters("This is a test. This is only a test."));
        assertEquals(1.0, d1.values().stream().reduce(0.0, Double::sum), 0.001);
        LinkedHashMap<String,Double> d2 = chains.labelDistribution(MarkovLanguage.usableCharacters("Este es una prueba. Este es solamente una prueba."));
        assertEquals(1.0, d2.values().stream().reduce(0.0, Double::sum), 0.001);
    }
    
    @Test
    public void bestChainTest() {
        assertEquals("English", chains.bestMatchingChain(MarkovLanguage.usableCharacters("This is a test. This is only a test.")));
        assertEquals("Spanish", chains.bestMatchingChain(MarkovLanguage.usableCharacters("Este es una prueba. Este es solamente una prueba.")));
    }
}
