package frc.demacia.utils;

/**
 * Static utility functions for common mathematical operations.
 */
public class Utilities {
    public static double deadband(double value, double deadband) {
        return Math.abs(value) < deadband ? 0 : value;
    }

    public static double hypot(double x, double y) {
      return Math.sqrt(x*x + y*y);
    }

    public static double angleFromTranslation2d(double x, double y) {
        return Math.atan2(y, x);
    }
}