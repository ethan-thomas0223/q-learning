package robosim.reinforcement;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class QTableTest {
    QTable testTable;
    final static String[] tablesAfter = new String[]{"""
targetVisits:1;discount:0.5;rateConstant:2.0;lastState:1;lastAction:0
0:1.2(1),0.0(0)
1:0.0(0),0.0(0)
            """,
            """
targetVisits:1;discount:0.5;rateConstant:2.0;lastState:0;lastAction:1
0:1.2(1),0.0(0)
1:1.0(1),0.0(0)
""",
            """
targetVisits:1;discount:0.5;rateConstant:2.0;lastState:1;lastAction:1
0:1.2(1),-0.95(1)
1:1.0(1),0.0(0)
""",
            """
targetVisits:1;discount:0.5;rateConstant:2.0;lastState:0;lastAction:0
0:1.2(1),-0.95(1)
1:1.0(1),1.6(1)
""",
            """
targetVisits:1;discount:0.5;rateConstant:2.0;lastState:1;lastAction:1
0:2.0(2),-0.95(1)
1:1.0(1),1.6(1)
"""};

    static int[] actions = new int[]{0, 1, 1, 0, 1};
    static int[] newStates = new int[]{1, 0, 1, 0, 1};
    static double[] rewards = new double[]{1.2, 0.4, -1.45, 1.0, 1.6};

    @Before
    public void setup() {
        testTable = QTable.from(tablesAfter[0]);
        assertEquals(testTable.toString(), tablesAfter[0]);
    }

    @Test
    public void testSenseActLearn() {
        testTable = new QTable(2, 2, 0, 1, 2, 0.5);
        for (int i = 0; i < tablesAfter.length; i++) {
            assertEquals(actions[i], testTable.senseActLearn(newStates[i], rewards[i]));
            assertEquals(testTable.toString(), tablesAfter[i]);
        }
    }

    @Test
    public void testLearningRate() {
        double[][] rates = new double[][]{
                new double[]{0.66666, 1.0, 1.0, 1.0},
                new double[]{0.66666, 1.0, 1.0, 0.66666},
                new double[]{0.66666, 0.66666, 1.0, 0.66666},
                new double[]{0.66666, 0.66666, 0.66666, 0.66666},
                new double[]{0.5, 0.66666, 0.66666, 0.66666}
        };
        for (int i = 0; i < tablesAfter.length; i++) {
            testTable = QTable.from(tablesAfter[i]);
            double[] row = rates[i];
            assertEquals(row[0], testTable.getLearningRate(0, 0), 0.001);
            assertEquals(row[1], testTable.getLearningRate(0, 1), 0.001);
            assertEquals(row[2], testTable.getLearningRate(1, 1), 0.001);
            assertEquals(row[3], testTable.getLearningRate(1, 0), 0.001);
        }
    }

    @Test
    public void testIsExploring() {
        assertTrue(testTable.isExploring(0));
        assertTrue(testTable.isExploring(1));

        testTable = QTable.from(tablesAfter[1]);
        assertTrue(testTable.isExploring(0));
        assertTrue(testTable.isExploring(1));

        testTable = QTable.from(tablesAfter[2]);
        assertFalse(testTable.isExploring(0));
        assertTrue(testTable.isExploring(1));

        testTable = QTable.from(tablesAfter[3]);
        assertFalse(testTable.isExploring(0));
        assertFalse(testTable.isExploring(1));

        testTable = QTable.from(tablesAfter[4]);
        assertFalse(testTable.isExploring(0));
        assertFalse(testTable.isExploring(1));
    }

    @Test
    public void testLeastVisitedAction() {
        assertEquals(1, testTable.leastVisitedAction(0));
        testTable = QTable.from(tablesAfter[1]);
        assertEquals(1, testTable.leastVisitedAction(0));
        assertEquals(1, testTable.leastVisitedAction(1));

        testTable = QTable.from(tablesAfter[2]);
        assertEquals(1, testTable.leastVisitedAction(1));
        testTable = QTable.from(tablesAfter[4]);
        assertEquals(1, testTable.leastVisitedAction(0));
    }

    @Test
    public void testBestAction() {
        assertEquals(0, testTable.getBestAction(0));

        testTable = QTable.from(tablesAfter[1]);
        assertEquals(0, testTable.getBestAction(0));
        assertEquals(0, testTable.getBestAction(1));

        testTable = QTable.from(tablesAfter[2]);
        assertEquals(0, testTable.getBestAction(0));
        assertEquals(0, testTable.getBestAction(1));

        testTable = QTable.from(tablesAfter[3]);
        assertEquals(0, testTable.getBestAction(0));
        assertEquals(1, testTable.getBestAction(1));

        testTable = QTable.from(tablesAfter[4]);
        assertEquals(0, testTable.getBestAction(0));
        assertEquals(1, testTable.getBestAction(1));
    }}
