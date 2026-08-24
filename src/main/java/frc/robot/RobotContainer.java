package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController.ControllerType;
import frc.robot.commands.DriveVelocityPIDCommand;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.commands.SteerPIDCommand;
import frc.robot.commands.XboxDriveSteerCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class RobotContainer {
    private final SimpleMotorSubsystem subsystem =
        new SimpleMotorSubsystem();

    private final CommandController driverController =
        new CommandController(
            Constants.SimpleMotorConstants.DRIVER_CONTROLLER_PORT,
            ControllerType.kXbox
        );

    public RobotContainer() {
        configureDashboard();
        configureDefaultCommand();
    }

    private void configureDefaultCommand() {
        subsystem.setDefaultCommand(
            new XboxDriveSteerCommand(
                subsystem,
                driverController,
                driverController.leftBumper()
            )
        );
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

        // Xbox teleop telemetry
        SmartDashboard.putNumber("Xbox Drive Power Command", 0.0);
        SmartDashboard.putNumber("Xbox Steer Power Command", 0.0);
        SmartDashboard.putBoolean("Xbox Precision Mode", false);

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
