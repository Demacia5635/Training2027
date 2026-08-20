package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {
    private final SimpleMotorSubsystem subsystem;
    private final double duration;
    private final Timer timer = new Timer();

    public SimpleMotorCommand(
            SimpleMotorSubsystem subsystem,
            double duration) {
        this.subsystem = subsystem;
        this.duration = duration;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        timer.restart();
        System.out.println("SimpleMotorCommand started");
    }

    @Override
    public void execute() {
        double drivePower = SmartDashboard.getNumber(
            "Motor 1 Power",
            0.0
        );

        double steerPower = SmartDashboard.getNumber(
            "Motor 2 Power",
            0.0
        );

        drivePower = MathUtil.clamp(
            drivePower,
            -Constants.SimpleMotorConstants.POWER_LIMIT,
            Constants.SimpleMotorConstants.POWER_LIMIT
        );

        steerPower = MathUtil.clamp(
            steerPower,
            -Constants.SimpleMotorConstants.POWER_LIMIT,
            Constants.SimpleMotorConstants.POWER_LIMIT
        );

        subsystem.setDrivePower(drivePower);
        subsystem.setSteerPower(steerPower);
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(duration);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
        System.out.println("SimpleMotorCommand ended");
    }
}