package robosim.core;

import java.awt.*;

public class Robot extends SimObject {
	private double heading;
	private double vTranslational, vAngular;
	
	public static final double RADIUS = 5.0;
	public static final double FORWARD_VELOCITY = 1, ANGULAR_VELOCITY = Math.PI / 4;
	public static final double SONAR_WIDTH = Math.PI / 6;
	
	public Robot(double x, double y, double heading) {
		super(x, y, RADIUS);
		this.heading = heading;
		this.vTranslational = 0;
		this.vAngular = 0;
	}

	@Override
	public boolean isObstacle() {return false;}

	@Override
	public boolean isVacuumable() {return false;}

	public Robot() {
		this(0, 0, 0);
	}
	
	@Override
	public Color getColor() {return Color.RED;}
	
	@Override
	public void render(Graphics gc, Color override) {
		super.render(gc, override);
		gc.setColor(Color.YELLOW);
		int x1 = (int)(getX() + getRadius() * Math.cos(heading - SONAR_WIDTH));
		int y1 = (int)(getY() + getRadius() * Math.sin(heading - SONAR_WIDTH));
		int x2 = (int)(getX() + getRadius() * Math.cos(heading + SONAR_WIDTH));
		int y2 = (int)(getY() + getRadius() * Math.sin(heading + SONAR_WIDTH));
		gc.fillPolygon(new int[]{(int)getX(), x1, x2}, new int[]{(int)getY(), y1, y2}, 3);
	}

	public boolean isHitting(SimObject other) {
		return other.isObstacle() && isContacting(other);
	}

	public boolean canVacuum(SimObject other) {
		return other.isVacuumable() && isContacting(other);
	}
	
	public double getHeading() {return heading;}
	
	public double getTranslationalVelocity() {return vTranslational;}
	
	public double getAngularVelocity() {return vAngular;}
	
	public void stop() {
		vTranslational = 0;
		vAngular = 0;
	}
	
	public void setTranslate(Direction direction) {
		vTranslational = direction.direct(FORWARD_VELOCITY);
		vAngular = 0;
	}
	
	public void setTurn(Direction direction) {
		vTranslational = 0.0;
		vAngular = direction.direct(ANGULAR_VELOCITY);
	}
	
	public void update() {
		update(Direction.FWD);
	}
	
	public void update(Direction direction) {
		heading += direction.direct(vAngular);
		moveBy(direction.direct(vTranslational * Math.cos(heading)), 
				direction.direct(vTranslational * Math.sin(heading)));
	}
	
	public boolean withinSonar(SimObject other) {
		return Math.abs(angularOffset(other)) <= SONAR_WIDTH;
	}

	public double angularOffset(SimObject other) {
		return Polar.angularDifference(angularDistance(other), heading);
	}

	public Polar offsetTo(SimObject other) {
		return new Polar(distanceTo(other), angularOffset(other));
	}
}
