// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.controller.CommandController;
import frc.robot.subsystems.Chassis;

public class DriveCommand extends Command {
    
  Chassis chassis;
  CommandController controller;
  ChassisSpeeds speeds;

  public DriveCommand(Chassis chassis, CommandController controller) {
    this.chassis = chassis;
    this.controller = controller;
    addRequirements(chassis);
  }

  @Override
  public void initialize() {
    System.out.println("intialized");
  }

  @Override
  public void execute() {
    double rawOmega = controller.getRightTrigger() - controller.getLeftTrigger();
    double leftX = controller.getLeftX();
    double leftY = -controller.getLeftY();
    speeds = new ChassisSpeeds(leftX, leftY,
        rawOmega);

    chassis.drive(speeds, false);
  }

  @Override
  public void end(boolean interrupted) {
    chassis.stop();
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}

