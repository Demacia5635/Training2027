package frc.robot;

import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class RobotContainer {

    private final SimpleMotorSubsystem subsystem =
        new SimpleMotorSubsystem();


    public RobotContainer() {

        configureDashboard();
    }


    private void configureDashboard() {

        // =========================
        // MOTOR 1 - DRIVE POWER
        // =========================

        SmartDashboard.putNumber(
            "Motor 1 Power",
            0.0
        );


        // =========================
        // MOTOR 2 - STEER POWER
        // =========================

        SmartDashboard.putNumber(
            "Motor 2 Power",
            0.0
        );


        // =========================
        // MOTOR COMMAND
        // =========================

        SmartDashboard.putData(
            "Run Simple Motor Command",
            new SimpleMotorCommand(
                subsystem,
                10.0
            )
        );
    }


    public Command getAutonomousCommand() {

        return null;
    }
}