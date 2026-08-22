// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class YuvalSteer extends Command {
  /** Creates a new YuvalSteer. */
  private final double targetAngle;
  private final double targetPosition;
  private double error;
  private double driveError;
  private final SimpleMotorSubsystem subsystem;

  public YuvalSteer(double targetAngle, double targetPosition, SimpleMotorSubsystem subsystem) {
    this.targetAngle = targetAngle;
    this.targetPosition = targetPosition;
    this.subsystem = subsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    error = targetAngle - subsystem.getSteerAngle();
    driveError = targetPosition - subsystem.getDrivePosition();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    error = targetAngle - subsystem.getSteerAngle();
    subsystem.setSteerPower(Math.signum(error) * 0.5);
    driveError = targetPosition - subsystem.getDrivePosition();
    subsystem.setDrivePower(Math.signum(driveError) * 0.5);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(error) < 2 && Math.abs(driveError) < 2;
  }
}
