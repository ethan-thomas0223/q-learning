package robosim.core;

public class Polar {
    private double r, theta;

    public Polar(double r, double theta) {
        this.r = r;
        this.theta = theta;
        while (theta > Math.PI) {
            theta -= 2*Math.PI;
        }
        while (theta <= -Math.PI) {
            theta += 2*Math.PI;
        }
    }

    public double getR() {return r;}
    public double getTheta() {return theta;}

    @Override
    public String toString() {
        return "(" + r + ", " + theta + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Polar that) {
            return that.r == this.r && that.theta == this.theta;
        } else {
            return false;
        }
    }

    public static double angularDifference(double theta1, double theta2) {
        // See https://stackoverflow.com/a/7869457/906268
        return Math.atan2(Math.sin(theta1 - theta2), Math.cos(theta1 - theta2));
    }
}
