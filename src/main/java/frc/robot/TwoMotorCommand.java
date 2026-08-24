package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class TwoMotorCommand extends Command {

    private final SimpleMotorSubsystem subsystem;

    private final double turnPower;
    private final double drivePower;
    private final double duration;

    private final Timer timer = new Timer();

    public TwoMotorCommand(
        SimpleMotorSubsystem subsystem,
        double turnPower,
        double drivePower,
        double duration
    ) {
        this.subsystem = subsystem;
        this.turnPower = turnPower;
        this.drivePower = drivePower;
        this.duration = duration;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        timer.restart();

        subsystem.setTwoPowers(turnPower, drivePower);

        System.out.println(
            "TwoMotorCommand started - turn power: " + turnPower
            + ", drive power: " + drivePower
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

        System.out.println("TwoMotorCommand ended");
    }
}