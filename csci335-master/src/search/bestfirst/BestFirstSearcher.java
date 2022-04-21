package search.bestfirst;

import search.GenericSearcher;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.Predicate;

public class BestFirstSearcher<T> extends GenericSearcher<T, BestFirstQueue<T>> {
    public BestFirstSearcher(ToIntFunction<T> heuristic, Function<T, ArrayList<T>> successorFunc, Predicate<T> achievesGoal) {
        super(() -> new BestFirstQueue<>(heuristic), successorFunc, achievesGoal);
    }
}
