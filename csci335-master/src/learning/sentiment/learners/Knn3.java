package learning.sentiment.learners;

import learning.classifiers.Knn;
import learning.core.Histogram;

public class Knn3 extends Knn<Histogram<String>,String>  {
    public Knn3() {
        super(3, Histogram::cosineDistance);
    }
}
