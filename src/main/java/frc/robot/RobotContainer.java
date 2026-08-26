package frc.robot;

import frc.robot.commands.Autos;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
public class RobotContainer {
    private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final CommandXboxController driverController;
  private final Chassis chassisDriveAndSteer;

  public RobotContainer() {
    driverController = new CommandXboxController(Constants.OperatorConstants.CONTROLLER_PORT);
    chassisDriveAndSteer = new Chassis();
    chassisDriveAndSteer.setDefaultCommand(
        new DriveCommand(chassisDriveAndSteer, driverController)
    );

    configureBindings();
  }

  private void configureBindings() {
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));
  }

  public Command getAutonomousCommand() {
    return Autos.exampleAuto(m_exampleSubsystem);
  }
}