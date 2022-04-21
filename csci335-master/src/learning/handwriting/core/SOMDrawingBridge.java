package learning.handwriting.core;

import core.Duple;
import learning.classifiers.SOMRecognizer;
import learning.core.Classifier;
import learning.handwriting.gui.DrawingEditor;
import learning.handwriting.gui.SelfOrgMapPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class SOMDrawingBridge implements Classifier<Drawing,String> {
    private SOMRecognizer<FloatDrawing,String> inner;

    public SOMDrawingBridge(int mapSide) {
        inner = new SOMRecognizer<>(mapSide, () -> new FloatDrawing(DrawingEditor.DRAWING_WIDTH, DrawingEditor.DRAWING_HEIGHT),
                FloatDrawing::euclideanDistance, FloatDrawing::weightedAverageOf);
    }

    @Override
    public String classify(Drawing value) {
        return inner.classify(new FloatDrawing(value));
    }

    @Override
    public void train(ArrayList<Duple<Drawing, String>> data) {
        ArrayList<Duple<FloatDrawing,String>> floated = data.stream()
                .map(d -> new Duple<>(new FloatDrawing(d.getFirst()), d.getSecond()))
                .collect(Collectors.toCollection(ArrayList::new));
        inner.train(floated);
    }

    @Override
    public Optional<JPanel> getVisualization() {
        return Optional.of(new SelfOrgMapPanel(inner.getSOM()));
    }
}
