// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PIDMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PIDMotorCommandVelocity extends Command {
  private final PIDMotorSubsystem MOTOR_SUBSYSTEM;
  private double wantedVelocity = 0;
  private double wantedSteerVelocity = 0;
  private double driveError = 0;
  private double steerError = 0;
  /** Creates a new PIDMotorCommandVelocity. */
  public PIDMotorCommandVelocity(PIDMotorSubsystem MOTOR_SUBSYSTEM) {
    this.MOTOR_SUBSYSTEM = MOTOR_SUBSYSTEM;
    SmartDashboard.putData("velocity command", this);
    addRequirements(MOTOR_SUBSYSTEM);

  }
@Override
    public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Wanted drive Velocity", ()-> wantedVelocity, (x) -> wantedVelocity = x);
    builder.addDoubleProperty("Wanted steer Velocity", ()-> wantedSteerVelocity, (x) -> wantedSteerVelocity = x);
    builder.addDoubleProperty("Drive Error", () ->driveError,null);
    builder.addDoubleProperty("Steer error", () ->steerError,null);
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("initialized");
  }
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    MOTOR_SUBSYSTEM.setDriveVelocity(wantedVelocity);
    MOTOR_SUBSYSTEM.setSteerVelocity(wantedSteerVelocity);
    driveError = wantedVelocity - MOTOR_SUBSYSTEM.getDriveVelocity();
    steerError = wantedSteerVelocity - MOTOR_SUBSYSTEM.getSteerVelocity();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
        MOTOR_SUBSYSTEM.setDriveVelocity(0);
        MOTOR_SUBSYSTEM.setSteerVelocity(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
