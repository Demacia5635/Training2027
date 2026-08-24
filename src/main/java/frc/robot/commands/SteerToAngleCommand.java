package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SteerToAngleCommand extends Command {

    private final SimpleMotorSubsystem subsystem;
    private final double targetDegrees;

    private final double power = 0.2;
    private final double tolerance = 2.0;

    public SteerToAngleCommand(
        SimpleMotorSubsystem subsystem,
        double targetDegrees
    ) {
        this.subsystem = subsystem;
        this.targetDegrees = targetDegrees;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        System.out.println(
            "SteerToAngleCommand started - target: "
            + targetDegrees + " degrees"
        );
    }

    @Override
    public void execute() {

        double currentDegrees =
            subsystem.getSteerPositionDegrees();

        double error =
            targetDegrees - currentDegrees;

        if (error > 0) {
            subsystem.setPower(power);
        }
        else if (error < 0) {
            subsystem.setPower(-power);
        }
    }

    @Override
    public boolean isFinished() {

        double currentDegrees =
            subsystem.getSteerPositionDegrees();

        double error =
            targetDegrees - currentDegrees;

        return Math.abs(error) <= tolerance;
    }

    @Override
    public void end(boolean interrupted) {

        subsystem.stop();

        System.out.println(
            "SteerToAngleCommand ended"
        );

        System.out.println(
            "Steer Position: "
            + subsystem.getSteerPositionDegrees()
            + " degrees"
        );

        System.out.println(
            "Steer Velocity: "
            + subsystem.getSteerVelocity()
        );
    }
}