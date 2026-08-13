// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SteerMotorSubsistem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SteerToAngle extends Command {
  /** Creates a new SteerToAngle. */
  SteerMotorSubsistem subsystem1 = new SteerMotorSubsistem();
  private double currentAngleSteer;
  private double wantedAngleSteer;
  private double power;

  public SteerToAngle(SteerMotorSubsistem subsystem, double wantedAngleSteer, double power) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.subsystem1 = subsystem;
    this.wantedAngleSteer = wantedAngleSteer;
    this.power = power;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    currentAngleSteer = subsystem1.getAngleSteer();
    int direction = ((wantedAngleSteer - currentAngleSteer) < 0) ? -1 : 1;
    subsystem1.setPowerSteer(power * direction);

    SmartDashboard.putNumber("currentAngleSteer", currentAngleSteer);
    SmartDashboard.putNumber("wantedAngleSteer", wantedAngleSteer);
    SmartDashboard.putNumber("direction", direction);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem1.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Math.abs(wantedAngleSteer - currentAngleSteer) < 0.05;
  }
}
