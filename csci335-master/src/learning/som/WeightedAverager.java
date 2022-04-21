package learning.som;

public interface WeightedAverager<V> {
    // Calculate a weighted average. The argument v1Weight is a value between zero and one.
    // It should weigh components of v1 by v1Weight, and v2 by 1.0 - v1Weight.
    V weightedAverage(V v1, V v2, double v1Weight);
}
