package search.example;

import core.Pos;
import org.junit.Test;

import java.util.ArrayDeque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BreadthFirstDemoTest {
    @Test
    public void test() {
        BreadthFirstDemo bfd = new BreadthFirstDemo(new Pos(2, 3));
        bfd.solve(new Pos(0, 0));
        assertEquals(6, bfd.getSolutionLength());
        ArrayDeque<Pos> path = bfd.getResult().get().searchPath();
        System.out.println("Path: "+ path);
        System.out.println("Nodes expanded: " + bfd.getNumNodes());
        System.out.println("Depth: " + bfd.getMaxDepth());
        validatePath(path, new Pos(0, 0), new Pos(2, 3));
    }

    public static void validatePath(ArrayDeque<Pos> path, Pos start, Pos end) {
        assertEquals(start, path.getFirst());
        assertEquals(end, path.getLast());
        Pos current = path.removeFirst();
        while (!path.isEmpty()) {
            assertTrue(current.isNeighbor(path.getFirst()));
            current = path.removeFirst();
        }
    }
}
