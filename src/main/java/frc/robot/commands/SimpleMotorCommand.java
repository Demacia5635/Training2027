package frc.robot.commands;

import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class SimpleMotorCommand extends Command {

    private final SimpleMotorSubsystem subsystem;

    private final double drivePower;
    private final double steerPower;
    private final double duration;

    private final Timer timer = new Timer();


    public SimpleMotorCommand(
            SimpleMotorSubsystem subsystem,
            double drivePower,
            double steerPower,
            double duration) {

        this.subsystem = subsystem;
        this.drivePower = drivePower;
        this.steerPower = steerPower;
        this.duration = duration;

        addRequirements(subsystem);
    }


    @Override
    public void initialize() {

        timer.restart();

        System.out.println(
            "SimpleMotorCommand started"
        );
    }


    @Override
    public void execute() {

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

        System.out.println(
            "SimpleMotorCommand ended"
        );
    }
}