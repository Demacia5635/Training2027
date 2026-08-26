package frc.robot;

import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController.ControllerType;
import frc.robot.commands.Autos;
import frc.robot.commands.DriveCommand;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final CommandController driverController;
  private final Chassis chassisDriveAndSteer;

  public RobotContainer() {
    driverController = new CommandController(Constants.OperatorConstants.CONTROLLER_PORT, ControllerType.kXbox);
    chassisDriveAndSteer = new Chassis();
    chassisDriveAndSteer.setDefaultCommand(
        new DriveCommand(chassisDriveAndSteer, driverController));

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