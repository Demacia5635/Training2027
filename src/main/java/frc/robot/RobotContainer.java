package frc.robot;

import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class RobotContainer {

    // Subsystem
    private final SimpleMotorSubsystem subsystem =
        new SimpleMotorSubsystem();

    // Controller
    private final CommandXboxController controller =
        new CommandXboxController(
            Constants.DriverConstants.DriverControllerPort
        );

    public RobotContainer() {

        configureBindings();
    }

    private void configureBindings() {

        controller.a().onTrue(
            new SimpleMotorCommand(
                subsystem,
                0.5,   // Motor 1 power
                0.3,   // Motor 2 power
                5.0    // Duration
            )
        );
    }

    public Command getAutonomousCommand() {

        return new SimpleMotorCommand(
            subsystem,
            0.5,
            0.3,
            5.0
        );
    }
}