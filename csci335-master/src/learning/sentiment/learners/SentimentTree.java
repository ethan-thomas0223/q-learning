package learning.sentiment.learners;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;
import learning.decisiontree.DTTrainer;
import learning.decisiontree.DecisionTree;
import learning.sentiment.core.SentimentAnalyzer;

import java.util.ArrayList;

public class SentimentTree implements Classifier<Histogram<String>,String> {
    private DecisionTree<Histogram<String>, String, String, Integer> root;

    @Override
    public String classify(Histogram<String> value) {
        return root.classify(value);
    }

    @Override
    public void train(ArrayList<Duple<Histogram<String>, String>> data) {
        DTTrainer<Histogram<String>, String, String, Integer> trainer =
                new DTTrainer<>(data, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
        root = trainer.train();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
