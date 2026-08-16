package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;


public class TwoMotorCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final double powerDrive;
    private final double targetAngle;
    private final double targetDistance;
        
        public TwoMotorCommand(
        SimpleMotorSubsystem subsystem,
        double powerDrive,
        double targetAngle,
        double targetDistance
    ) {
        this.subsystem = subsystem;
        this.powerDrive = powerDrive;
        this.targetAngle = targetAngle;
        this.targetDistance = targetDistance;

    addRequirements(subsystem);
}

    @Override
    public void execute() {
        double currentAngle = subsystem.getAngleDegrees();

    double steerPower = 0;

    if (currentAngle < targetAngle) {
        steerPower = 0.2;
    } else if (currentAngle > targetAngle) {
        steerPower = -0.2;
    }

    subsystem.setBothMotorsPower(steerPower, powerDrive);
    }
    @Override
public boolean isFinished() {
    boolean steerFinished =
        Math.abs(targetAngle - subsystem.getAngleDegrees()) < 2;

    boolean driveFinished =
        Math.abs(targetDistance - subsystem.getDriveRotations()) < 0.1;

    return steerFinished && driveFinished;
}

    @Override
    public void end(boolean interrupted) {
        subsystem.stopBoth();

    System.out.println("Steer Position: " + subsystem.getAngleDegrees());
    System.out.println("Steer Velocity: " + subsystem.getSteerVelocity());

    System.out.println("Drive Position: " + subsystem.getDrivePosition());
    System.out.println("Drive Velocity: " + subsystem.getDriveVelocity());
    }

}
