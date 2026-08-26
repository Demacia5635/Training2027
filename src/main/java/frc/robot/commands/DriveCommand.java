package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.controller.CommandController;
import frc.robot.subsystems.Chassis;

public class DriveCommand extends Command {
  private final Chassis chassis;
  private final CommandController controller;
  private boolean precisionMode;


  public DriveCommand(Chassis chassis, CommandController controller) {
    this.chassis = chassis;
    this.controller = controller;
    this.precisionMode = false;
    addRequirements(chassis);
  }

  public void invertPrecisionMode() {
    setPrecisionMode(!precisionMode);
  }

  public void setPrecisionMode(boolean precisionMode) {
    this.precisionMode = precisionMode;
  }

  public boolean getPrecisionMode() {
    return precisionMode;
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    chassis.drive(new ChassisSpeeds(controller.getLeftX(), controller.getLeftY(),0));
  }
  

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    chassis.drive(new ChassisSpeeds(0, 0, 0));
  }
}
