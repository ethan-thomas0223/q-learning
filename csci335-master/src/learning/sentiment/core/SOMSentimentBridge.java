package learning.sentiment.core;

import core.Duple;
import learning.classifiers.SOMRecognizer;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.HashMap;

public class SOMSentimentBridge implements Classifier<Histogram<String>, String> {
    private SOMRecognizer<HashMap<String,Double>,String> inner;

    public SOMSentimentBridge(int mapSide) {
        inner = new SOMRecognizer<>(mapSide, HashMap::new, BagOfWordsFuncs::cosineSimilarity, BagOfWordsFuncs::weightedAverage);
    }

    @Override
    public String classify(Histogram<String> value) {
        return inner.classify(convert(value));
    }

    @Override
    public void train(ArrayList<Duple<Histogram<String>, String>> data) {
        ArrayList<Duple<HashMap<String,Double>, String>> converted = new ArrayList<>();
        for (Duple<Histogram<String>, String> datum: data) {
            converted.add(new Duple<>(convert(datum.getFirst()), datum.getSecond()));
        }
        inner.train(converted);
    }

    public static HashMap<String,Double> convert(Histogram<String> hist) {
        HashMap<String,Double> result = new HashMap<>();
        for (String key: hist) {
            result.put(key, (double)hist.getCountFor(key));
        }
        return result;
    }
}
