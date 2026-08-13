// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SteerToAngle extends Command {
  /** Creates a new SteerToAngle. */
  SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  private double currentAngleSteer;
  private double wantedAngleSteer;
  private double power;

  public SteerToAngle(SimpleMotorSubsystem subsystem, double wantedAngleSteer, double power) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.subsystem = subsystem;
    this.wantedAngleSteer = wantedAngleSteer;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    int direction = ((wantedAngleSteer - currentAngleSteer)<0)? -1:1;
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
    return Math.abs(wantedAngleSteer - currentAngleSteer) < 0.5;
  }
}
