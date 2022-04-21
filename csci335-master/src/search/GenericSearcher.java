package search;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GenericSearcher<T, Q extends SearchQueue<T>> {
    private Supplier<Q> queueMaker;
    private Function<T, ArrayList<T>> successorFunc;
    private Predicate<T> achievesGoal;
    private boolean searching;
    private Optional<SearchNode<T>> result;
    private int numNodes;
    private int maxDepth;

    public GenericSearcher(Supplier<Q> queueMaker, Function<T, ArrayList<T>> successorFunc, Predicate<T> achievesGoal) {
        this.queueMaker = queueMaker;
        this.successorFunc = successorFunc;
        this.achievesGoal = achievesGoal;
    }

    public boolean success() {return result.isPresent();}

    public Optional<SearchNode<T>> getResult() {return result;}

    public int getNumNodes() {return numNodes;}

    public int getMaxDepth() {return maxDepth;}

    public int getSolutionLength() {
        return result.map(p -> p.searchPath().size()).orElse(0);
    }

    public void solve(T start) {
        Q openList = queueMaker.get();
        openList.enqueue(new SearchNode<>(start, Optional.empty()));
        searching = true;
        numNodes = 1;
        maxDepth = 0;
        result = Optional.empty();
        while (searching) {
            openList.dequeue().ifPresentOrElse(node -> updateUsing(openList, node), () -> searching = false);
        }
    }

    private void updateUsing(Q openList, SearchNode<T> node) {
        if (achievesGoal.test(node.getValue())) {
            result = Optional.of(node);
            searching = false;
        } else {
            for (T successor: successorFunc.apply(node.getValue())) {
                SearchNode<T> newNode = new SearchNode<>(successor, Optional.of(node));
                openList.enqueue(newNode);
                numNodes += 1;
                maxDepth = Math.max(maxDepth, newNode.getDepth());
            }
        }
    }

    public double getBranchingFactor(double maxError) {
        double lo = 0;
        double hi = (double)numNodes / (double)maxDepth;
        double error = 0;
        double bGuess = 0;
        do {
            bGuess = (lo + hi) / 2;
            error = computeError(bGuess);
            if (error > 0) {
                hi = bGuess;
            } else {
                lo = bGuess;
            }
        } while (Math.abs(error) > maxError);
        return bGuess;
    }

    private double computeError(double bGuess) {
        double sum = 0;
        for (int d = 1; d <= maxDepth; ++d) {
            sum += Math.pow(bGuess, d);
        }
        return sum - numNodes;
    }
}
