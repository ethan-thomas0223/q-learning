package robosim.core;

import robosim.gui.Sim;

public enum SimObjMaker {
	OBSTACLE {
		@Override
		public SimObject makeAt(double x, double y) {
			return new Obstacle(x, y, Sim.RADIUS);
		}
	}, ROBOT {
		@Override
		public SimObject makeAt(double x, double y) {
			return new Robot(x, y, 0);
		}
	}, DIRT {
		@Override
		public SimObject makeAt(double x, double y) {
			return new Dirt(x, y);
		}
	};
	
	abstract public SimObject makeAt(double x, double y);
}
