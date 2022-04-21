package robosim.core;

import org.junit.Before;
import org.junit.Test;
import robosim.gui.Sim;

import static org.junit.Assert.*;

public class SimulatorTest {
	Simulator sim;
	Robot bot;

	@Before
	public void setup() {
		sim = new Simulator(100, 200);
		bot = new Robot(50, 100, 0);
		sim.add(bot);
	}

	@Test
	public void testObjs() {
		SimObject s1 = new Obstacle(10, 20, 2);
		sim.add(s1);
		SimObject s2 = new Obstacle(100, 100, 2);
		sim.add(s2);

		assertTrue(bot.withinSonar(s2));
		assertFalse(bot.withinSonar(s1));

		assertEquals(43, sim.findClosestProblem(), 0.01);

		sim.add(new Obstacle(80, 100, 2));
		assertEquals(23, sim.findClosestProblem(), 0.01);
	}

	@Test
	public void testWalls() {
		int numHeadings = (int)(2 * Math.PI / Robot.ANGULAR_VELOCITY);
		assertEquals(8, numHeadings);

		double[] targets = new double[]{45, 65.711, 95, 65.711, 45, 65.711, 95, 65.711};
		assertEquals(numHeadings, targets.length);
		bot.setTurn(Direction.FWD);
		for (int i = 0; i < targets.length; i++) {
			assertEquals(i * Robot.ANGULAR_VELOCITY, bot.getHeading(), 0.01);
			assertEquals(targets[i], sim.findClosestEdge(), 0.01);
			bot.update();
		}

		assertEquals(2.0 * Math.PI, bot.getHeading(), 0.01);
		sim.add(new Obstacle(75, 100, 0));
		assertEquals(20.0, sim.findClosestProblem(), 0.01);
	}

	@Test
	public void testCollisions() {
		Obstacle ob = new Obstacle(65, 100, 4);
		sim.add(ob);
		for (int i = 0; i < 10; i++) {
			Action.FORWARD.applyTo(sim);
			sim.move();
		}
		assertEquals(bot.toString(), "{(55.000000,100.000000);5.000000}");
	}

	@Test
	public void testStr() {
		SimObject obj = new Obstacle(12, 11, Sim.RADIUS);
		assertEquals(obj.toString(), "{(12.000000,11.000000);10.000000}");
		assertEquals(obj, SimObject.from(obj.toString()));
	}
}
