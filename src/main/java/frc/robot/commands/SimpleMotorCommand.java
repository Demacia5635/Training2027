package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SimpleMotorCommand extends Command {

    private final SimpleMotorSubsystem subsystem;
    private final double power;
    private final double duration;

    private final Timer timer = new Timer();

    public SimpleMotorCommand(
        SimpleMotorSubsystem subsystem,
        double power,
        double duration
    ) {
        this.subsystem = subsystem;
        this.power = power;
        this.duration = duration;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        timer.restart();
        subsystem.setPower(power);

        System.out.println(
            "Command started - power: " + power
            + ", duration: " + duration
        );
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() { 
        return timer.hasElapsed(duration);
    }

    @Override
    public void end(boolean interrupted) {
        subsystem.stop();
        timer.stop();

        System.out.println("Command ended");
    }
}