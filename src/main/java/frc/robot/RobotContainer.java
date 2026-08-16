package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class RobotContainer {
    private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
    private final CommandXboxController controller = new CommandXboxController(0);

    public RobotContainer() {
        configureBindings();
        configureDefaultCommands();
    }

    private void configureBindings() {
        // Configure your button bindings here
    }

    private void configureDefaultCommands() {
        subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0, 0));
    }

    public Command getAutonomousCommand() {
        return new SequentialCommandGroup(
            new SimpleMotorCommand(subsystem, 0.5, 0, 1.0),
            new ParallelRaceGroup(
                new WaitCommand(5.0),
                new WaitUntilCommand(() -> controller.a().getAsBoolean())
            ),
            new SimpleMotorCommand(subsystem, 0.5, 0, 1.5),
            new ParallelRaceGroup(
                new WaitCommand(5.0),
                new WaitUntilCommand(() -> controller.a().getAsBoolean())
            ),
            new SimpleMotorCommand(subsystem, -0.5, 0, 2.5)
        );
    }
}
