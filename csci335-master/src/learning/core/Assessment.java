package learning.core;

import core.Duple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

public class Assessment<L> {
    private Histogram<L> total = new Histogram<>(), correct = new Histogram<>();
    private ArrayList<L> ordered = new ArrayList<>();

    public int numLabels() {return ordered.size();}

    public L getLabel(int i) {return ordered.get(i);}
    public int getTotalFor(L label) {return total.getCountFor(label);}
    public int getCorrectFor(L label) {return correct.getCountFor(label);}

    public <V> void assess(Classifier<V, L> classifier, Duple<V,L> test) {
        total.bump(test.getSecond());
        if (!ordered.contains(test.getSecond())) {
            ordered.add(test.getSecond());
        }
        if (classifier.classify(test.getFirst()).equals(test.getSecond())) {
            correct.bump(test.getSecond());
        }
    }

    public <V> void assessAll(Classifier<V,L> classifier, ArrayList<Duple<V,L>> tests) {
        for (Duple<V,L> test: tests) {
            assess(classifier, test);
        }
    }

    public static <V,L> ArrayList<ArrayList<Duple<V,L>>> partition(int numPartitions, ArrayList<Duple<V,L>> samples) {
        int partitionSize = samples.size() / numPartitions;
        int numLeftovers = samples.size() % numPartitions;
        samples = new ArrayList<>(samples);
        Collections.shuffle(samples);
        ArrayList<ArrayList<Duple<V,L>>> partitions = new ArrayList<>();
        ArrayList<Duple<V,L>> current = new ArrayList<>();
        for (Duple<V,L> sample: samples) {
            int targetSize = partitionSize + (numLeftovers > 0 ? 1 : 0);
            if (current.size() == targetSize) {
                partitions.add(current);
                current = new ArrayList<>();
                numLeftovers = Math.max(0, numLeftovers - 1);
            }
            current.add(sample);
        }
        partitions.add(current);
        return partitions;
    }

    public static <V,L> ArrayList<Assessment<L>> multiTrial(Supplier<Classifier<V, L>> classifierMaker, ArrayList<ArrayList<Duple<V,L>>> partitions) {
        ArrayList<Assessment<L>> results = new ArrayList<>();
        for (int i = 0; i < partitions.size(); i++) {
            ArrayList<Duple<V,L>> training = new ArrayList<>();
            for (int j = 0; j < partitions.size(); j++) {
                if (i != j) {
                    training.addAll(partitions.get(j));
                }
            }
            Classifier<V,L> classifier = classifierMaker.get();
            classifier.train(training);
            Assessment<L> assessment = new Assessment<>();
            assessment.assessAll(classifier, partitions.get(i));
            results.add(assessment);
        }
        return results;
    }
}
