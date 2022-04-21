package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;

public class BasicAvoider implements Controller {
	@Override
	public void control(Simulator sim) {
		if (sim.wasHit() || sim.findClosestProblem() < 30) {
			Action.LEFT.applyTo(sim);
		} else {
			Action.FORWARD.applyTo(sim);
		}
	}
}
