
package frc.robot;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {
  private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  //private CommandXboxController controller = new CommandXboxController(Constants.OperatorConstants.kDriverControllerPort);

  public RobotContainer() {
    configureBindings();
    configureDefaultCommands();
  }

  private void configureBindings() {
  }

  private void configureDefaultCommands() {
    subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0, 0));
  }

  public Command getAutonomousCommand() {
       return new SimpleMotorCommand(subsystem, 0.3, 0.7, 10.0);
  }
}