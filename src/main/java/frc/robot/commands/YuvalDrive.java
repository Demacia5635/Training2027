// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class YuvalDrive extends Command {
  /** Creates a new YuvalDrive. */
  private SimpleMotorSubsystem simpleMotorSubsystem;
  private double wantedAngle;
  public YuvalDrive(SimpleMotorSubsystem simpleMotorSubsystem, double wantedAngle, double distance) {// Use addRequirements() here to declare subsystem dependencies.
    this.wantedAngle = wantedAngle;// Store the desired angle
    this.simpleMotorSubsystem = simpleMotorSubsystem;

    addRequirements(simpleMotorSubsystem);
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    simpleMotorSubsystem.goToTargetAngle(wantedAngle); // Call the goToTargetAngle method to steer to the desired angle
    simpleMotorSubsystem.driveForward();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
   simpleMotorSubsystem.stopSteer();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
     // Check if the current angle is within 1 degree of the desired angle
      // Stop the steering motor
      return Math.abs(simpleMotorSubsystem.getSteerMotorPosition() - wantedAngle) < 2.0; // Command is finished
    
    

  }
}
