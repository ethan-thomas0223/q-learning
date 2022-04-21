package core;

import java.util.ArrayList;

public class Pos implements Comparable<Pos> {
    private int x, y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getManhattanDist(Pos other) {
        return Math.abs(other.x - x) + Math.abs(other.y - y);
    }

    public ArrayList<Pos> getNeighbors() {
        ArrayList<Pos> result = new ArrayList<>();
        for (Direction d: Direction.values()) {
            result.add(d.successor(this));
        }
        return result;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    public int hashCode() {return toString().hashCode();}

    public String toString() {return "(" + x + "," + y + ")";}

    public boolean equals(Object other) {
        if (other instanceof Pos that) {
            return this.compareTo(that) == 0;
        } else {
            return false;
        }
    }

    public boolean isNeighbor(Pos other) {
        boolean xDiffer = Math.abs(getX() - other.getX()) == 1;
        boolean yDiffer = Math.abs(getY() - other.getY()) == 1;
        return (!xDiffer || !yDiffer) && (xDiffer || yDiffer);
    }

    @Override
    public int compareTo(Pos that) {
        if (this.x == that.x) {
            return this.y - that.y;
        } else {
            return this.x - that.x;
        }
    }
}
