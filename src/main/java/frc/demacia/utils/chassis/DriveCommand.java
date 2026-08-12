// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.chassis;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.DemaciaUtils;
import frc.demacia.utils.controller.CommandController;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveCommand extends Command {
  private Chassis chassis;
  private CommandController controller;
  private double direction;
  private ChassisSpeeds speeds;
  private boolean precisionMode;


  /** Creates a new DriveCommand. */
  public DriveCommand(Chassis chassis, CommandController controller) {
    this.chassis = chassis;
    this.controller = controller;
    precisionMode = false;
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

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    direction = DemaciaUtils.getIsRed() ? 1 : -1;
    double joyX = controller.getLeftY() * direction;
    double joyY = controller.getLeftX() * direction;
    
    // Calculate r]otation from trigger axes
    double rot = controller.getLeftTrigger() - controller.getRightTrigger();
    
    double velX = Math.pow(joyX, 2) * chassis.getMaxDriveVelocity() * Math.signum(joyX);
    double velY = Math.pow(joyY, 2) * chassis.getMaxDriveVelocity() * Math.signum(joyY);
    double velRot = Math.pow(rot, 2) * chassis.getMaxRotationalVelocity() * Math.signum(rot);
    if(precisionMode){
        velX /= 4;
        velY /= 4;
        velRot /= 4;
    }
    
    speeds = new ChassisSpeeds(velX, velY,velRot);

    if(precisionMode) chassis.setVelocities(speeds);
    else {
        chassis.setRobotRelSpeedsWithAccel(speeds);}
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
