package learning.core;

import core.Duple;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Optional;

public interface Classifier<V, L> {
    L classify(V value);

    void train(ArrayList<Duple<V, L>> data);

    default Assessment<L> correctPerLabel(ArrayList<Duple<V,L>> testData) {
        Assessment<L> result = new Assessment<>();
        for (Duple<V,L> test: testData) {
            result.assess(this, test);
        }
        return result;
    }

    default Optional<JPanel> getVisualization() {return Optional.empty();}
}
