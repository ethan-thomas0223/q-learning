package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;

import java.util.Random;

public class RandomController implements Controller {
	private Random random = new Random();
	
	@Override
	public void control(Simulator sim) {
		Action.values()[random.nextInt(Action.values().length)].applyTo(sim);
	}
}
