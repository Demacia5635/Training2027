package frc.robot;

import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController.ControllerType;
import frc.robot.commands.DriveCommand;
import edu.wpi.first.wpilibj2.command.Command;

public class RobotContainer {
  public static Chassis chassis;
  private CommandController driverController;

  public RobotContainer() {
    driverController = new CommandController(Constants.OperatorConstants.CONTROLLER_PORT, ControllerType.kPS5);
    chassis = new Chassis();
    chassis.setDefaultCommand(new DriveCommand(chassis, driverController));
    configureBindings();
  }

  private void configureBindings() {
    
  }

  public Command getAutonomousCommand() {
    return null;
  }
}