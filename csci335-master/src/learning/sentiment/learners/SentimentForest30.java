package learning.sentiment.learners;

import learning.core.Histogram;
import learning.decisiontree.RandomForest;
import learning.sentiment.core.SentimentAnalyzer;

public class SentimentForest30 extends RandomForest<Histogram<String>, String, String, Integer> {
    public SentimentForest30() {
        super(30, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
    }
}
