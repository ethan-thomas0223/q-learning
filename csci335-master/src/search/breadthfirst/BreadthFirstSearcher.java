package search.breadthfirst;

import search.GenericSearcher;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class BreadthFirstSearcher<T> extends GenericSearcher<T, BreadthFirstQueue<T>> {
    public BreadthFirstSearcher(Function<T, ArrayList<T>> successorFunc, Predicate<T> achievesGoal) {
        super(BreadthFirstQueue::new, successorFunc, achievesGoal);
    }
}
