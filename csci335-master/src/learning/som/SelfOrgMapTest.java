package learning.som;

import core.Duple;
import learning.classifiers.SOMRecognizer;
import learning.handwriting.core.Drawing;
import learning.handwriting.core.FloatDrawing;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class SelfOrgMapTest {
    final static int TEST_WIDTH = 3;
    final static int TEST_HEIGHT = 4;
    final static int TEST_VALUES = TEST_HEIGHT * TEST_WIDTH;

    FloatDrawing fd1 = setupDrawing(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
    FloatDrawing fd2 = setupDrawing(new double[]{1, 3, 5, 7, 9, 11, 2, 4, 6, 8, 10, 12});

    static int flattenIndex(int x, int y, FloatDrawing f) {
        return x * f.getHeight() + y;
    }

    static FloatDrawing setupDrawing(double[] values) {
        assertEquals(TEST_VALUES, values.length);
        FloatDrawing f = new FloatDrawing(TEST_WIDTH, TEST_HEIGHT);
        for (int x = 0; x < f.getWidth(); x++) {
            for (int y = 0; y < f.getHeight(); y++) {
                f.set(x, y, values[flattenIndex(x, y, f)]);
            }
        }
        return f;
    }

    void testDrawing(FloatDrawing f, double[] testValues) {
        for (int x = 0; x < f.getWidth(); x++) {
            for (int y = 0; y < f.getHeight(); y++) {
                assertEquals(testValues[flattenIndex(x, y, f)], f.get(x, y), 0.001);
            }
        }
    }

    @Test
    public void testAvg() {
        fd1 = FloatDrawing.weightedAverageOf(fd2, fd1, 0.2);
        double[] target = new double[]{1.0, 2.2, 3.4, 4.6, 5.8, 7.0, 6.0, 7.2, 8.4, 9.6, 10.8, 12};
        testDrawing(fd1, target);
    }

    @Test
    public void testEuclideanDistance() {
        assertEquals(110.0, fd1.euclideanDistance(fd2), 0.01);
    }

    @Test
    public void testDistanceWeight() {
        SelfOrgMap<FloatDrawing> som = new SelfOrgMap<FloatDrawing>(10, () -> new FloatDrawing(1, 1), FloatDrawing::euclideanDistance, FloatDrawing::weightedAverageOf);
        assertEquals(0.0, som.computeDistanceWeight(new SOMPoint(0, 0), new SOMPoint(9, 9)), 0.001);
        assertEquals(1.0, som.computeDistanceWeight(new SOMPoint(5, 5), new SOMPoint(5, 5)), 0.001);
        assertEquals(0.5, som.computeDistanceWeight(new SOMPoint(2, 2), new SOMPoint(7, 2)), 0.001);
        assertEquals(0.68377, som.computeDistanceWeight(new SOMPoint(4, 3), new SOMPoint(7,2)), 0.001);
    }

    @Test
    public void testLearningRate() {
        assertEquals(0.5, SelfOrgMap.effectiveLearningRate(0.5, 1.0), 0.001);
        assertEquals(0.25, SelfOrgMap.effectiveLearningRate(0.5, 2.0), 0.001);
        assertEquals(0.24, SelfOrgMap.effectiveLearningRate(0.6, 2.5), 0.001);
        assertEquals(1.0, SelfOrgMap.effectiveLearningRate(1.0, 0.0), 0.001);
    }

    @Test
    public void testTrain() {
        SelfOrgMap<FloatDrawing> som = new SelfOrgMap<>(3, () -> new FloatDrawing(2, 2), FloatDrawing::euclideanDistance, FloatDrawing::weightedAverageOf);
        Drawing d1 = new Drawing(2, 2);
        d1.set(0, 0, true);
        d1.set(0, 1, true);
        d1.set(1, 0, false);
        d1.set(1, 1, true);
        FloatDrawing f1 = new FloatDrawing(d1);
        som.train(f1);
        assertEquals(new SOMPoint(0, 0), som.bestFor(f1));
        FloatDrawing n0 = new FloatDrawing(new double[][]{{1.0, 1.0}, {0.0, 1.0}});
        FloatDrawing n1 = new FloatDrawing(new double[][]{{0.6666666666666667, 0.6666666666666667}, {0.0, 0.6666666666666667}});
        FloatDrawing n2 = new FloatDrawing(new double[][]{{0.33333333333333337, 0.33333333333333337}, {0.0, 0.33333333333333337}});
        FloatDrawing n3 = new FloatDrawing(new double[][]{{0.5285954792089682, 0.5285954792089682}, {0.0, 0.5285954792089682}});
        FloatDrawing n4 = new FloatDrawing(new double[][]{{0.2546440075000701, 0.2546440075000701}, {0.0, 0.2546440075000701}});
        FloatDrawing n5 = new FloatDrawing(new double[][]{{0.05719095841793653, 0.05719095841793653}, {0.0,0.05719095841793653}});

        assertEquals(n0, som.getNode(0, 0));
        assertEquals(n1, som.getNode(0, 1));
        assertEquals(n1, som.getNode(1, 0));
        assertEquals(n2, som.getNode(0, 2));
        assertEquals(n2, som.getNode(2, 0));
        assertEquals(n3, som.getNode(1, 1));
        assertEquals(n4, som.getNode(1, 2));
        assertEquals(n4, som.getNode(2, 1));
        assertEquals(n5, som.getNode(2, 2));
    }

    @Test
    public void testLabel() {
        FloatDrawing n0 = new FloatDrawing(new double[][]{{1.0, 1.0}, {0.0, 1.0}});
        ArrayList<Duple<FloatDrawing,String>> samples = new ArrayList<>();
        samples.add(new Duple<>(new FloatDrawing(new Drawing("2|2|XO|OO")), "A"));
        samples.add(new Duple<>(new FloatDrawing(new Drawing("2|2|XX|OX")), "B"));
        assertEquals("B", SOMRecognizer.findLabelFor(n0, 1, samples, FloatDrawing::euclideanDistance));
    }
}
