package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {
    private final Timer timer = new Timer();
    private final SimpleMotorSubsystem subsystem;
    private final double drivePower;
    private final double steerPower;
    private final double duration;
    private double startTime = 0;

    public SimpleMotorCommand(SimpleMotorSubsystem subsystem, double drivePower, double steerPower, double duration) {
        this.subsystem = subsystem;
        this.drivePower = drivePower;
        this.steerPower = steerPower;
        this.duration = duration;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        timer.restart();
        System.out.println(
                "command started at " + startTime + " seconds for duration " + duration + " with power " + drivePower + " for drive and " + steerPower + " for steer");
    }

    @Override
    public void execute() {
        subsystem.setDrivePower(drivePower);
        subsystem.setSteerPower(steerPower);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
        System.out.println("command ended at " + Timer.getFPGATimestamp() + " seconds");
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime >= duration;
    }
}
