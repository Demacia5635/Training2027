// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class HWcommand extends Command {
  private final SimpleMotorSubsystem subsystem;
  private double wantedSteeringAngle;
  
  /** Creates a new HWcommand. */
  public HWcommand(SimpleMotorSubsystem subsystem, double wantedSteeringAngle) {
    this.subsystem = subsystem;
    this.wantedSteeringAngle = wantedSteeringAngle;
    addRequirements(subsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  




  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double currentAngle = subsystem.getAngleDegrees();

    if (currentAngle < wantedSteeringAngle) {
        subsystem.setSteerPower(0.2);
    } else if (currentAngle > wantedSteeringAngle) {
        subsystem.setSteerPower(-0.2);
    }

  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem.stopSteer();

    System.out.println("Steer Position: " + subsystem.getAngleDegrees());
    System.out.println("Steer Velocity: " + subsystem.getSteerVelocity());
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(wantedSteeringAngle - subsystem.getAngleDegrees()) < 2;
    
  }
}
