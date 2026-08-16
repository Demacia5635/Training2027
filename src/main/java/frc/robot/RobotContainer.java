
package frc.robot;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.SimpleMotorCommand;
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
private CommandXboxController controller = new CommandXboxController(Constants.OperatorConstants.kDriverControllerPort);

  public Command getAutonomousCommand() {
        controller.a().onTrue(null); return Commands.parallel(
        new SimpleMotorCommand(subsystem, 0.3, 1 ,10.0);
  }
}