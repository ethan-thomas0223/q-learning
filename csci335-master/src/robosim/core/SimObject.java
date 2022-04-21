package robosim.core;

import robosim.gui.Sim;

import java.awt.*;

abstract public class SimObject {
	private double x, y, radius;
	
	public SimObject(double x, double y, double radius) {
		this.x = x;
		this.y = y;
		this.radius = radius;
	}

	abstract public boolean isObstacle();
	abstract public boolean isVacuumable();

	public String toString() {
		return String.format("{(%f,%f);%f}", x, y, radius);
	}

	public static SimObject from(String src) {
		src = src.substring(1, src.trim().length() - 1);
		String[] top = src.split(";");
		String coords = top[0].substring(1, top[0].length() - 1);
		String[] xy = coords.split(",");
		double x = Double.parseDouble(xy[0]);
		double y = Double.parseDouble(xy[1]);
		double r = Double.parseDouble(top[1]);
		return r == Sim.RADIUS ? new Obstacle(x, y, r) : new Dirt(x, y);
	}
	
	public double getRadius() {return radius;}
	
	public double getX() {return x;}
	public double getY() {return y;}
	
	abstract public Color getColor();
	
	public void render(Graphics gc, Color override) {
		gc.setColor(override);
		gc.fillOval((int)(getX() - getRadius()), (int)(getY() - getRadius()), (int)getRadius() * 2, (int)getRadius() * 2);
	}
	
	public void render(Graphics gc) {
		render(gc, getColor());
	}
	
	public double distanceTo(SimObject other) {
		return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2)) - (this.radius + other.radius);
	}
	
	public double angularDistance(SimObject other) {
		return Math.atan2(other.y - this.y, other.x - this.x);
	}
	
	public boolean isContacting(SimObject other) {
		return this.distanceTo(other) <= 0;
	}
	
	public void moveBy(double dx, double dy) {
		x += dx;
		y += dy;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SimObject that) {
			return this.x == that.x && this.y == that.y && this.radius == that.radius;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(radius) + 10 * Double.hashCode(x) + 100 * Double.hashCode(y);
	}
}
