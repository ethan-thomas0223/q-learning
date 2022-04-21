package robosim.core;

import java.awt.*;

public class Obstacle extends SimObject {
    public Obstacle(double x, double y, double radius) {
        super(x, y, radius);
    }

    @Override
    public boolean isObstacle() {
        return true;
    }

    @Override
    public boolean isVacuumable() {
        return false;
    }

    @Override
    public Color getColor()  {return Color.GREEN;}
}
