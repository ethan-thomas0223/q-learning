package learning.sentiment.core;

import core.AIReflector;
import core.Duple;
import learning.core.Assessment;
import learning.core.Classifier;
import learning.core.Histogram;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class SentimentAnalyzer {
    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, FileNotFoundException, InterruptedException {
        if (args.length != 2) {
            System.out.println("Usage: SentimentAnalyzer ClassifierClassName numDataSegments");
        } else {
            AIReflector<Classifier<Histogram<String>,String>> reflector = new AIReflector<>(Classifier.class, "learning.sentiment.learners");
            ArrayList<Duple<Histogram<String>,String>> sentimentStrings = openSentimentStrings("tagged_selections_by_sentence_simplified.csv");
            ArrayList<ArrayList<Duple<Histogram<String>,String>>> partitions = Assessment.partition(Integer.parseInt(args[1]), sentimentStrings);
            ArrayList<Assessment<String>> assessments = Assessment.multiTrial(() -> reflector.optionalInstanceOf(args[0]).get(), partitions);

        }
    }

    public static Histogram<String> bagOfWordsFrom(String line) {
        Histogram<String> result = new Histogram<>();
        // YOUR CODE HERE
        // From https://www.delftstack.com/howto/java/how-to-remove-punctuation-from-string-in-java/#:~:text=Remove%20Punctuation%20From%20String%20Using%20the%20replaceAll%20%28%29,%5Cp%20%7BPunct%7D%2C%20which%20means%20all%20the%20punctuation%20symbols.
        line = line.replaceAll("\\p{Punct}", " ");
        for (String word: line.split("\\s")) {
            result.bump(word);
        }
        return result;
    }

    public static ArrayList<Duple<Histogram<String>,String>> openSentimentStrings(String filename) throws FileNotFoundException {
        Scanner s = new Scanner(new File(filename));
        ArrayList<Duple<Histogram<String>,String>> result = new ArrayList<>();
        while (s.hasNextLine()) {
            String[] label_text = s.nextLine().split(",", 2);
            result.add(new Duple<>(bagOfWordsFrom(label_text[1]), label_text[0]));
        }
        return result;
    }

    public static ArrayList<Duple<String, Integer>> allFeatures(ArrayList<Duple<Histogram<String>,String>> data) {
        HashSet<Duple<String,Integer>> features = new HashSet<>();
        for (Duple<Histogram<String>,String> datum: data) {
            for (String key: datum.getFirst()) {
                features.add(new Duple<>(key, 0));
                features.add(new Duple<>(key, datum.getFirst().getCountFor(key)));
            }
        }
        return new ArrayList<>(features);
    }
}
