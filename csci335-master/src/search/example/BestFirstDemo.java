package search.example;

import core.Pos;
import search.bestfirst.BestFirstSearcher;

public class BestFirstDemo extends BestFirstSearcher<Pos> {
    public BestFirstDemo(Pos target) {
        super(pos -> pos.getManhattanDist(target), Pos::getNeighbors, loc -> loc.equals(target));
    }
}
