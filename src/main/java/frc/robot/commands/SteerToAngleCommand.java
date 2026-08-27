package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SteerToAngleCommand extends Command {

    private final SimpleMotorSubsystem subsystem;
    private final double targetRadians;

    private final double power = 0.2;


    private final double tolerance = Math.toRadians(2.0);

    public SteerToAngleCommand(
        SimpleMotorSubsystem subsystem,
        double targetRadians
    ) {
        this.subsystem = subsystem;
        this.targetRadians = targetRadians;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        System.out.println(
            "SteerToAngleCommand started - target: "
            + targetRadians + " radians"
        );
    }

    @Override
    public void execute() {

        double currentRadians =
            subsystem.getSteerPositionRadians();

        double error =
            targetRadians - currentRadians;

        if (error > 0) {
            subsystem.setPower(power);
        }
        else if (error < 0) {
            subsystem.setPower(-power);
        }
    }

    @Override
    public boolean isFinished() {

        double currentRadians =
            subsystem.getSteerPositionRadians();

        double error =
            targetRadians - currentRadians;

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
            + subsystem.getSteerPositionRadians()
            + " radians"
        );

        System.out.println(
            "Steer Velocity: "
            + subsystem.getSteerVelocityRadians()
            + " radians/second"
        );
    }
}