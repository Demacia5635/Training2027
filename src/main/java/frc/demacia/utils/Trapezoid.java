package frc.demacia.utils;

public class Trapezoid {

    public static double calculate(double currentV, double wantedV, double maxV, double accel, double distanceLeft) {
        if(distanceLeft < 0) {
            return - calculate(-currentV, -wantedV, maxV, accel, -distanceLeft);
        }

        // calculate the maximum velocity possible with acceleration and distance left ignoring maxV
        double vMax = Math.sqrt(2*accel*distanceLeft + currentV*currentV + wantedV*wantedV) / 2;
        if(vMax > currentV || wantedV > currentV) {
            return Math.min(vMax, currentV + accel*0.03); // Accelerate
        } else {
            double t = 2*distanceLeft / (currentV + wantedV);   // Time to decelerate to wantedV
            if(t < 0.03) {
                return wantedV; // Close enough to wantedV, just return it
            }
            double a = (currentV - wantedV) / t; // Total change in velocity needed
            return Math.max(wantedV, currentV - a * 0.03); // Decelerate
        }
    }

}
