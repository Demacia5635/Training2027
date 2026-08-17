// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

public class DriveHWCommand extends Command {
private final SimpleMotorSubsystem subsystem;
  private final double targetPoint;

  public DriveHWCommand(
      SimpleMotorSubsystem subsystem,
      double targetPoint) {
    this.subsystem = subsystem;
    this.targetPoint = targetPoint;

    addRequirements(subsystem);
  }
    @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("power", () -> getDriveVelocity(), null);
    builder.addDoubleProperty("volt", () -> getDriveVolt(), null);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
