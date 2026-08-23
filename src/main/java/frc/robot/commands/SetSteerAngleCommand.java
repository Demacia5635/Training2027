// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class SetSteerAngleCommand extends Command {

  // some variables:
  private final SimpleMotorSubsystem subsystem;
  private final double targetRadians;

  public SetSteerAngleCommand(SimpleMotorSubsystem subsystem, double targetRadians) {
    this.subsystem = subsystem;
    this.targetRadians = targetRadians;

    addRequirements(subsystem);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    subsystem.setSteerPositionRadians(targetRadians);
  }

  @Override
  public void end(boolean interrupted) {
    System.out.println("Steer command finished");
    System.out.println("Position " + subsystem.getSteerPositionRadians() + "  Radians");
    System.out.println("Velocity " + subsystem.getSteerVelocity() + " RPM");
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}