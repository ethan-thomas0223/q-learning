package learning.classifiers;

import core.Duple;
import org.junit.Test;

import java.util.ArrayList;

public class KnnTest {
    @Test
    public void test() throws InterruptedException {
        double[] values = new double[]{10.0, 20.0, 30.0, 40.0, 50.0};
        String[] labels = new String[]{"A", "A", "B", "B", "A"};
        ArrayList<Duple<Double, String>> data = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            data.add(new Duple<>(values[i], labels[i]));
        }

        Knn<Double,String> classifier = new Knn<>(3, (d1, d2) -> Math.abs(d1 - d2));
        classifier.train(data);

        double[] tests = new double[]{15.0, 35.0, 55.0};
        String[] expected = new String[]{"A", "B", "B"};
        for (int i = 0; i < tests.length; i++) {
            assert expected[i].equals(classifier.classify(tests[i]));
        }
    }
}
