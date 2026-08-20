package frc.robot.commands;

import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

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

        System.out.println(
            "SimpleMotorCommand started"
        );
    }


    @Override
    public void execute() {

        // Read the requested powers from Elastic

        double motor1Power =
            SmartDashboard.getNumber(
                "Motor 1 Power",
                0.0
            );

        double motor2Power =
            SmartDashboard.getNumber(
                "Motor 2 Power",
                0.0
            );


        // Safety limit
        motor1Power = Math.max(
            -0.3,
            Math.min(0.3, motor1Power)
        );

        motor2Power = Math.max(
            -0.3,
            Math.min(0.3, motor2Power)
        );


        // Send power to the motors

        subsystem.setMotor1Power(
            motor1Power
        );

        subsystem.setMotor2Power(
            motor2Power
        );
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