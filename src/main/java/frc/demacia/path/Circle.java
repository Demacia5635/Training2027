package frc.demacia.path;

import edu.wpi.first.math.geometry.Translation2d;

public class Circle {
    public Translation2d center;
    public double radius;
    public boolean isLeft;

    public Circle(Translation2d center, double radius, boolean isLeft) {
        this.center = center;
        this.radius = radius;
        this.isLeft = isLeft;
    }
}