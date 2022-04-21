package robosim.core;

import robosim.gui.Sim;

import java.awt.*;

public class Dirt extends SimObject {

    public Dirt(double x, double y) {
        super(x, y, Sim.RADIUS / 2);
    }

    @Override
    public boolean isObstacle() {return false;}

    @Override
    public boolean isVacuumable() {return true;}

    @Override
    public Color getColor() {return Color.BLACK;}
}
