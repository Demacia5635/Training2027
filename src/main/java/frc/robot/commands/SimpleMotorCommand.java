package frc.robot.commands;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {
    private final double Steerpower;
    private final double Drivepower;
    private SimpleMotorSubsystem subsystem;
    private final double duration;
    private Timer timer = new Timer();
    private double startTime = 0;

    public SimpleMotorCommand(SimpleMotorSubsystem subsystem, double Steerpower, double Drivepower, double duration) {
        this.Steerpower = Steerpower;
        this.Drivepower = Drivepower;
        this.subsystem = subsystem;
        this.duration = duration;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        timer.restart();
        System.out
                .println("command started at " + startTime + " for duration " + duration + " with power " + Steerpower);
    }

    @Override
    public void execute() {
        subsystem.setSteerPower(Steerpower);
        subsystem.setDrivePower(Steerpower);
        
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime >= duration;
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stopSteer();
        subsystem.stopDrive();
        System.out.println("command ended at " + timer.getFPGATimestamp());
    }
}