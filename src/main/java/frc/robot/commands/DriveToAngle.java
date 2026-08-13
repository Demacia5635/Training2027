// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveToAngle extends Command {
  SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  private double currentAngleDrive;
  private double wantedAngleDrive;
  private double power;

  /** Creates a new DriveToAngle. */
  public DriveToAngle(SimpleMotorSubsystem subsystem, double wantedAngleDrive , double power) {
    this.subsystem = subsystem;
    this.wantedAngleDrive = wantedAngleDrive;
    addRequirements(subsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    int direction = ((wantedAngleDrive - currentAngleDrive)<0)? -1:1;
    subsystem.setPowerSteer(power * direction);

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(wantedAngleDrive - currentAngleDrive) < 0.5;
  }
}
