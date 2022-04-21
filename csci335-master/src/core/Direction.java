package core;

public enum Direction {
	N {
		@Override
		public Pos successor(Pos location) {
			return new Pos(location.getX(), location.getY() - 1);
		}
	}, 
	S {
		@Override
		public Pos successor(Pos location) {
			return new Pos(location.getX(), location.getY() + 1);
		}
	}, 
	E {
		@Override
		public Pos successor(Pos location) {
			return new Pos(location.getX() + 1, location.getY());
		}
	}, 
	W {
		@Override
		public Pos successor(Pos location) {
			return new Pos(location.getX() - 1, location.getY());
		}
	};
	
	abstract public Pos successor(Pos location);
	
	public static Direction between(Pos start, Pos end) {
		int xDiff = start.getX() - end.getX();
		int yDiff = start.getY() - end.getY();
		if (Math.abs(xDiff) > Math.abs(yDiff)) {
			return xDiff > 0 ? W : E;
		} else {
			return yDiff > 0 ? N : S;
		}
	}
}
