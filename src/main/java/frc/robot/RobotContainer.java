
package frc.robot;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
im
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.commands.SimpleMotorcommand2;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {
  private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();

  public RobotContainer() {
    configureBindings();
    configureDefaultCommands();
  }

  private void configureBindings() {
  }

  private void configureDefaultCommands() {
    subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0));
  }

  public Command getAutonomousCommand() {
        controller.a().onTrue(); return Commands.parallel(
        new SimpleMotorCommand(subsystem, 0.3, 10.0),
        new SimpleMotorcommand2(subsystem, 0.3, 10.0));;
  }
}
