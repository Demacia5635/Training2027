// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.controller.CommandController;
import frc.robot.subsystems.BasicChassis;

public class BasicDriveCommand extends Command {
  private BasicChassis chassis; // Basic are the ones I made
  private CommandController controller;
  private ChassisSpeeds speeds;

  public BasicDriveCommand(BasicChassis chassis, CommandController controller) {
    this.chassis = chassis;
    this.controller = controller;
    addRequirements(chassis);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    double vx = -controller.getLeftY(); // if needed, add max speed. (for all values)
    double vy = -controller.getLeftX();
    double velRotation = controller.getRightTrigger() - controller.getLeftTrigger();

    speeds = new ChassisSpeeds(vx, vy, velRotation);

    chassis.drive(speeds);
  }

  @Override
  public void end(boolean interrupted) {
    chassis.stopAll(); // when finished, stop all modules.
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}
