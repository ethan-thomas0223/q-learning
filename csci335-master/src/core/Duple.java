package core;

public class Duple<X,Y> {
	private X x;
	private Y y;
	
	public Duple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public X getFirst() {return x;}
	public Y getSecond() {return y;}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public boolean equals(Object other) {
		if (other instanceof Duple<?,?> that) {
			return this.x.equals(that.x) && this.y.equals(that.y);
		} else {
			return false;
		}
	}

	public int hashCode() {return x.hashCode() + 1000 * y.hashCode();}
}
