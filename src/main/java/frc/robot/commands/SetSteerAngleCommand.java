// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SetSteerAngleCommand extends Command {

  // some variables:
  private final SimpleMotorSubsystem subsystem;
  private final double targetDegrees;

  public SetSteerAngleCommand(SimpleMotorSubsystem subsystem, double targetDegrees) {
    this.subsystem = subsystem;
    this.targetDegrees = targetDegrees;

  }

  @Override
  public void initialize() {
    subsystem.setSteerPositionDegrees(targetDegrees);
  }

  @Override
  public void execute() {
  }

  @Override
  public void end(boolean interrupted) {
    System.out.println("Steer command finished");
    System.out.println("Position " + subsystem.getSteerPositionDegrees() + " Degrees");
    System.out.println("Velocity " + subsystem.getSteerVelocityDegreesPerSec() + " Degrees per sec");
  }

  @Override
  public boolean isFinished() {
    return Math.abs(subsystem.getSteerPositionDegrees() - targetDegrees) < 2.0;
  }
}
