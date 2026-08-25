package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants; 
import frc.demacia.utils.chassis.Chassis;
import frc.demacia.utils.controller.CommandController;

public class DriveCommand extends Command {
  private final Chassis chassis;
  private final CommandController controller;
  private ChassisSpeeds speeds;
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
  public void initialize() {}

  @Override
  public void execute() {
    if (RobotState.isAutonomous()) {
      return;
    }}


  @Override
  public void end(boolean interrupted) {
    chassis.setSpeedsFieldRel(new ChassisSpeeds(0, 0, 0));
  }

  @Override
  public boolean isFinished() {
    return false; }}