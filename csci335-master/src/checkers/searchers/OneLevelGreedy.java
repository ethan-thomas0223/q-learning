package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class OneLevelGreedy extends CheckersSearcher {
    private int numNodes = 0;

    public OneLevelGreedy(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        Optional<Duple<Integer, Move>> best = Optional.empty();
        for (Checkerboard alternative: board.getNextBoards()) {
            numNodes += 1;
            int negation = board.getCurrentPlayer() != alternative.getCurrentPlayer() ? -1 : 1;
            int scoreFor = negation * getEvaluator().applyAsInt(alternative);
            if (best.isEmpty() || best.get().getFirst() < scoreFor) {
                best = Optional.of(new Duple<>(scoreFor, alternative.getLastMove()));
            }
        }
        return best;
    }
}
