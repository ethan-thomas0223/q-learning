package search.example;

import core.Pos;
import search.breadthfirst.BreadthFirstSearcher;

public class BreadthFirstDemo extends BreadthFirstSearcher<Pos> {
    public BreadthFirstDemo(Pos target) {
        super(Pos::getNeighbors, loc -> loc.equals(target));
    }
}
