package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.DriveVelocityPIDCommand;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.commands.SteerPIDCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class RobotContainer {
    private final SimpleMotorSubsystem subsystem =
        new SimpleMotorSubsystem();

    public RobotContainer() {
        configureDashboard();
    }

    private void configureDashboard() {
        // Manual motor-test values
        SmartDashboard.putNumber("Motor 1 Power", 0.0);
        SmartDashboard.putNumber("Motor 2 Power", 0.0);

        // Software PID targets
        SmartDashboard.putNumber("Drive Target Velocity", 0.0);
        SmartDashboard.putNumber("Steer Target Position", 0.0);
            
        // Drive PID telemetry
        SmartDashboard.putNumber("Drive Current Velocity", 0.0);
        SmartDashboard.putNumber("Drive Error", 0.0);
        SmartDashboard.putNumber("Drive PID Output", 0.0);

        // Commands shown as buttons in Elastic
        SmartDashboard.putData(
            "Run Simple Motor Command",
            new SimpleMotorCommand(subsystem, 10.0)
        );

        SmartDashboard.putData(
            "Run Drive Velocity PID",
            new DriveVelocityPIDCommand(subsystem)
        );

        SmartDashboard.putData(
            "Run Steer Position PID",
            new SteerPIDCommand(subsystem)
        );
    }

    public Command getAutonomousCommand() {
        return null;
    }
}