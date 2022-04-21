package robosim.ai;

import core.Duple;
import robosim.core.*;

import java.util.Optional;

public class DirtChaser implements Controller {
	@Override
	public void control(Simulator sim) {
		if (sim.wasHit()) {
			Action.LEFT.applyTo(sim);
		} else {
			Optional<Action> dirt = dirtAction(sim);
			if (dirt.isPresent()) {
				dirt.get().applyTo(sim);
			} else {
				if (sim.findClosestProblem() < 30) {
					Action.LEFT.applyTo(sim);
				} else {
					Action.FORWARD.applyTo(sim);
				}
			}
		}
	}

	public Optional<Action> dirtAction(Simulator sim) {
		int leftDirt = 0;
		int rightDirt = 0;
		int straightDirt = 0;
		for (Duple<SimObject, Polar> obj: sim.allVisibleObjects()) {
			if (obj.getFirst().isVacuumable()) {
				if (Math.abs(obj.getSecond().getTheta()) < Robot.ANGULAR_VELOCITY) {
					straightDirt += 1;
				} else if (obj.getSecond().getTheta() < 0) {
					leftDirt += 1;
				} else {
					rightDirt += 1;
				}
			}
		}
		if (straightDirt > 0) {
			return Optional.of(Action.FORWARD);
		} else if (leftDirt > 0) {
			return Optional.of(Action.LEFT);
		} else if (rightDirt > 0) {
			return Optional.of(Action.RIGHT);
		} else {
			return Optional.empty();
		}
	}
}
