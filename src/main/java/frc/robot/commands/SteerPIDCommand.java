package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SteerPIDCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final PIDController pidController;

    public SteerPIDCommand(
            SimpleMotorSubsystem subsystem) {
        this.subsystem = subsystem;

        pidController = new PIDController(
            Constants.SimpleMotorConstants.STEER_COMMAND_KP,
            Constants.SimpleMotorConstants.STEER_COMMAND_KI,
            Constants.SimpleMotorConstants.STEER_COMMAND_KD
        );

        pidController.setTolerance(0.05);

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        pidController.reset();
        System.out.println("Steer PID Command started");
    }

    @Override
    public void execute() {
        // The current units are motor rotations, not degrees.
        double targetPosition = SmartDashboard.getNumber(
            "Steer Target Position",
            0.0
        );

        double currentPosition = subsystem.getSteerPosition();

        double output = pidController.calculate(
            currentPosition,
            targetPosition
        );

        output = MathUtil.clamp(
            output,
            -Constants.SimpleMotorConstants.POWER_LIMIT,
            Constants.SimpleMotorConstants.POWER_LIMIT
        );

        subsystem.setSteerPower(output);

        SmartDashboard.putNumber(
            "Steer Target Position",
            targetPosition
        );

        SmartDashboard.putNumber(
            "Steer Current Position",
            currentPosition
        );

        SmartDashboard.putNumber(
            "Steer Error",
            targetPosition - currentPosition
        );

        SmartDashboard.putNumber(
            "Steer PID Output",
            output
        );
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stopSteer();
        System.out.println("Steer PID Command ended");
    }
}