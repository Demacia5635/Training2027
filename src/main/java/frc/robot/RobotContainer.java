package frc.robot;

import frc.robot.commands.DriveVelocityPIDCommand;
import frc.robot.commands.SteerPIDCommand;
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
        // STEER PID
        // =========================

        // Requested steer angle
        SmartDashboard.putNumber(
            "Steer Target Angle",
            0.0
        );

        // Start/stop Steer PID from Elastic
        SmartDashboard.putData(
            "Start Steer PID",
            new SteerPIDCommand(subsystem)
        );


        // =========================
        // DRIVE PID
        // =========================

        // Requested drive velocity
        SmartDashboard.putNumber(
            "Drive Target Velocity",
            0.0
        );

        // Start/stop Drive PID from Elastic
        SmartDashboard.putData(
            "Start Drive Velocity PID",
            new DriveVelocityPIDCommand(subsystem)
        );
    }


    public Command getAutonomousCommand() {
        return null;
    }
}