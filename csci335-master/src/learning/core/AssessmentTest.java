package learning.core;

import core.Duple;
import org.junit.Test;

import java.util.ArrayList;

public class AssessmentTest {
    @Test
    public void testPartition() {
        ArrayList<Duple<Integer,Integer>> partitionable = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            partitionable.add(new Duple<>(i, i * 10));
        }
        assert partitionable.size() == 12;
        ArrayList<ArrayList<Duple<Integer,Integer>>> parts = Assessment.partition(2, partitionable);
        System.out.println(parts.size());
        assert parts.size() == 2;
        for (int i = 0; i < parts.size(); i++) {
            assert parts.get(i).size() == partitionable.size() / 2;
        }
    }
}
