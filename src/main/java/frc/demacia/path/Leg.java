package frc.demacia.path;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public class Leg {
    private Translation2d start;
    private Translation2d end;

    public Leg(Circle start, Circle end) {
        Translation2d startToEnd = end.center.minus(start.center);
        Rotation2d startAngleChange;
        Rotation2d endAngleChange;

        if (start.isLeft == end.isLeft) {

            startAngleChange = new Rotation2d(Math.PI / 2);
            endAngleChange = new Rotation2d(Math.PI / 2);

        } else {
        
            startAngleChange = new Rotation2d(Math.acos(2 * (start.radius / startToEnd.getNorm())));
            endAngleChange = new Rotation2d(Math.acos(2 * (end.radius / startToEnd.getNorm())));

        }

        startAngleChange = startAngleChange.times((start.isLeft ? -1 : 1));
        endAngleChange = endAngleChange.times((end.isLeft ? -1 : 1));

        this.start = start.center.plus(new Translation2d(start.radius, startToEnd.getAngle().rotateBy(startAngleChange)));
        this.end = end.center.plus(new Translation2d(end.radius, startToEnd.getAngle().rotateBy(endAngleChange)));
    }
    public Translation2d getStart() {
        return start;
    }
    public Translation2d getEnd() {
        return end;
    }
}