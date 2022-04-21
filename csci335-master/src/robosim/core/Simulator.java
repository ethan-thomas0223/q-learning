package robosim.core;

import core.Duple;
import learning.core.Histogram;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class Simulator {
	private SimMap map;
	private Robot bot;
	private double width, height;
	private boolean wasHit;
	private Histogram<String> stats;
	
	public Simulator(double width, double height) {
		map = new SimMap();
		resize(width, height);
	}

	public void resize(double width, double height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			bot = new Robot(width / 2, height / 2, 0);
			reset();
		}
	}
	
	public void reset() {
		wasHit = false;
		stats = new Histogram<>();
	}
	
	public void add(SimObject obj) {
		if (obj instanceof Robot bot) {
			this.bot = bot;
			reset();
		} else {
			map.add(obj);
		}
	}

	public void resetObjectsFrom(String mapString) {
		map = new SimMap(mapString);
	}
	
	public void drawOn(Graphics gc) {
		bot.render(gc);
		map.drawOn(gc, bot);
	}
	
	public boolean inBounds(SimObject obj) {
		return obj.getX() - obj.getRadius() >= 0 && obj.getX() + obj.getRadius() <= width &&
				obj.getY() - obj.getRadius() >= 0 && obj.getY() + obj.getRadius() <= height;
	}
	
	public void translate(Direction direction) {
		bot.setTranslate(direction);
	}
	
	public void turn(Direction direction) {
		bot.setTurn(direction);
	}
	
	public double getTranslationalVelocity() {return bot.getTranslationalVelocity();}
	public double getAngularVelocity() {return bot.getAngularVelocity();}
	
	public void move() {
		wasHit = false;
		bot.update();
		if (isColliding()) {
			wasHit = true;
			bot.update(Direction.REV);
		} else {
			map.vaccuumed(bot).ifPresent(dirt -> {
				map.remove(dirt);
				stats.bump("Dirt");
			});
		}
		stats.bump(wasHit ? "Collisions" : getAngularVelocity() == 0 && getTranslationalVelocity() > 0 ? "Forward" : "Other");
	}
	
	public int getTotalMoves() {return stats.getTotalCounts();}
	
	public int getForwardMoves() {return stats.getCountFor("Forward");}
	
	public int getCollisions() {return stats.getCountFor("Collisions");}

	public int getDirt() {return stats.getCountFor("Dirt");}
	
	public boolean wasHit() {return wasHit;}
	
	public boolean isColliding() {
		return !inBounds(bot) || map.isColliding(bot);
	}

	public double findClosestProblem() {
		double e = findClosestEdge();
		Optional<Polar> ob = findClosestObstacle();
		return ob.isPresent() && ob.get().getR() < e ? ob.get().getR() : e;
	}

	public Optional<Polar> findClosestObstacle() {
		return allVisibleObjects().stream()
				.filter(o -> o.getFirst().isObstacle())
				.map(Duple::getSecond).min(Comparator.comparingDouble(Polar::getR));
	}

	public ArrayList<Duple<SimObject,Polar>> allVisibleObjects() {
		return map.visibleObjects(bot);
	}
	
	public double findClosestEdge() {
		double x = oneD(bot.getX(), Math.cos(bot.getHeading()), width);
		double y = oneD(bot.getY(), Math.sin(bot.getHeading()), height);
		double closest = Math.min(x, y);
		return closest - bot.getRadius();
	}
	
	private double oneD(double where, double part, double dimSize) {
		return Math.abs((part >= 0 ? dimSize - where : where) / part);
	}

	public String getMapString() {
		return map.toString();
	}
}
