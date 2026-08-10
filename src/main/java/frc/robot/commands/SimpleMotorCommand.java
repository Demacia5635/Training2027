package frc.robot.commands;

import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorCommand extends Command {

    private final SimpleMotorSubsystem subsystem;

    private final double motor1Power;
    private final double motor2Power;
    private final double duration;

    private final Timer timer = new Timer();

    public SimpleMotorCommand(
            SimpleMotorSubsystem subsystem,
            double motor1Power,
            double motor2Power,
            double duration) {

        this.subsystem = subsystem;
        this.motor1Power = motor1Power;
        this.motor2Power = motor2Power;
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

        subsystem.setMotor1Power(motor1Power);
        subsystem.setMotor2Power(motor2Power);

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