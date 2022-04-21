package learning.handwriting.core;

public class DrawingPoint {
    private int x, y;

    public DrawingPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    @Override
    public boolean equals(Object other) {
        if (other instanceof DrawingPoint that) {
            return this.x == that.x && this.y == that.y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 10000 * x + y;
    }

    @Override
    public String toString() {return "(" + x + "," + y + ")";}
}
