package robosim.core;

public enum Direction {
	FWD {
		@Override
		public double direct(double value) {
			return value;
		}
	}, REV {
		@Override
		public double direct(double value) {
			return -value;
		}
	};
	abstract public double direct(double value);
}
