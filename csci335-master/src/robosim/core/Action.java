package robosim.core;

public enum Action {
	FORWARD {
		@Override
		public void applyTo(Simulator bot) {
			bot.translate(Direction.FWD);
		}
	}, BACKWARD {
		@Override
		public void applyTo(Simulator bot) {
			bot.translate(Direction.REV);
		}
	}, LEFT {
		@Override
		public void applyTo(Simulator bot) {
			bot.turn(Direction.REV);
		}
	}, RIGHT {
		@Override
		public void applyTo(Simulator bot) {
			bot.turn(Direction.FWD);
		}
	};
	
	abstract public void applyTo(Simulator bot);
}
