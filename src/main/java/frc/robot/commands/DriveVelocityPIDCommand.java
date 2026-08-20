package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class DriveVelocityPIDCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final PIDController pidController;

    public DriveVelocityPIDCommand(
            SimpleMotorSubsystem subsystem) {
        this.subsystem = subsystem;

        pidController = new PIDController(
            Constants.SimpleMotorConstants.DRIVE_COMMAND_KP,
            Constants.SimpleMotorConstants.DRIVE_COMMAND_KI,
            Constants.SimpleMotorConstants.DRIVE_COMMAND_KD
        );

        pidController.setTolerance(0.05);

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        pidController.reset();
        System.out.println("Drive Velocity PID Command started");
    }

    @Override
    public void execute() {
        double targetVelocity = SmartDashboard.getNumber(
            "Drive Target Velocity",
            0.0
        );

        // This is Demacia's refreshed cached velocity data.
        double currentVelocity = subsystem.getDriveVelocity();

        double output = pidController.calculate(
            currentVelocity,
            targetVelocity
        );

        output = MathUtil.clamp(
            output,
            -Constants.SimpleMotorConstants.POWER_LIMIT,
            Constants.SimpleMotorConstants.POWER_LIMIT
        );

        subsystem.setDrivePower(output);

        SmartDashboard.putNumber(
            "Drive Target Velocity",
            targetVelocity
        );

        SmartDashboard.putNumber(
            "Drive Current Velocity",
            currentVelocity
        );

        SmartDashboard.putNumber(
            "Drive Error",
            targetVelocity - currentVelocity
        );

        SmartDashboard.putNumber(
            "Drive PID Output",
            output
        );
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stopDrive();
        System.out.println("Drive Velocity PID Command ended");
    }
}