package frc.robot.Vision;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Chassis;
import frc.robot.Robot;
import frc.robot.RobotContainer;

public class GettingPosition extends SubsystemBase {
    private double x;
    private double y;
    private Translation2d vectorTag;
    private double distans;
    private Translation2d vectorCamTag;
    private Translation2d vectorRobot;
    private Rotation2d alfa;
    private Rotation2d gyroAngle;
    private Chassis chassis;
    private Translation2d vectorCamRobot;
    private Translation2d vectorRobotTag;
    private Rotation2d rotation2dX;

    public GettingPosition() {
        super();
        chassis = RobotContainer.chassis;
    }

    @Override
    public void periodic() {
        x = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
        y = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
        rotation2dX = new Rotation2d(Math.toRadians(x));
        gyroAngle = chassis.getGyroAngle();
        alfa = rotation2dX.plus(gyroAngle);
        distans = ConstansVision.HIGHT_DIFF / Math.tan(y);
        vectorCamTag = new Translation2d(distans, alfa);
        vectorRobotTag = vectorCamTag.minus(vectorCamRobot);


        vectorRobot = vectorRobotTag.minus(vectorTag);
    }
}
