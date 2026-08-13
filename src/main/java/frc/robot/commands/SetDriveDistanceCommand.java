// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SetDriveDistanceCommand extends Command {

  // some variables:

  private final SimpleMotorSubsystem subsystem;
  private final double targetMeters;

  public SetDriveDistanceCommand(SimpleMotorSubsystem subsystem, double targetMeters) {
    this.subsystem = subsystem;
    this.targetMeters = targetMeters;

    addRequirements(subsystem);

  }

  @Override
  public void initialize() {
    subsystem.setDrivePositionMeters(targetMeters);
  }

  @Override
  public void execute() {
  }

  @Override
  public void end(boolean interrupted) {
    subsystem.driveStop();

    System.out.println("Drive command finished");
    System.out.println("Postion " + subsystem.getDrivePositionMeters() + " Meters");
    System.out.println("Velocity " + subsystem.getDriveVel() + "RPM");
  }

  @Override
  public boolean isFinished() {
    return Math.abs(subsystem.getDrivePositionMeters() - targetMeters) < 0.05;
  }
}
