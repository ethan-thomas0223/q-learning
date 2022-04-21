package learning.handwriting.learners;

import learning.classifiers.Knn;
import learning.handwriting.core.Drawing;

public class Knn3 extends Knn<Drawing,String> {
    public Knn3() {
        super(3, (d1, d2) -> (double)d1.distance(d2));
    }
}
