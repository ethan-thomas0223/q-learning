package search.example;

import core.Pos;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BestFirstDemoTest {
    @Test
    public void test() {
        BreadthFirstDemo bfd = new BreadthFirstDemo(new Pos(2, 3));
        bfd.solve(new Pos(0, 0));

        BestFirstDemo better = new BestFirstDemo(new Pos(2, 3));
        better.solve(new Pos(0, 0));
        BreadthFirstDemoTest.validatePath(better.getResult().get().searchPath(), new Pos(0, 0), new Pos(2, 3));
        assertTrue(better.getNumNodes() < bfd.getNumNodes());
    }
}
