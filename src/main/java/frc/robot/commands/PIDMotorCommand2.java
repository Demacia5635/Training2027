// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.Constants.PIDMotorSubsystemConstants;
import frc.robot.subsystems.PIDMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PIDMotorCommand2 extends Command {
  private final PIDMotorSubsystem MOTOR_SUBSYSTEM;

  private double wantedPos = 0;
  private double error = 0;
  private double wantedSteerPos = 0;
  private double steerError = 0;
  private double driveSumError;
  private double driveLastError;
  private double drivePower;
  private double steerSumError;
  private double steerLastError;
  private double steerPower;

  /** Creates a new PIDMotorCommand. */
  public PIDMotorCommand2(PIDMotorSubsystem MOTOR_SUBSYSTEM) {
    this.MOTOR_SUBSYSTEM = MOTOR_SUBSYSTEM;
    SmartDashboard.putData("position command", this);
    SmartDashboard.putData("start command", new InstantCommand(() -> this.schedule()));
    addRequirements(MOTOR_SUBSYSTEM);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Wanted drive Position", () -> wantedPos, (x) -> wantedPos = x);
    builder.addDoubleProperty("Error", () -> error, null);
    builder.addDoubleProperty("Wanted steer Position", () -> Math.toDegrees(wantedSteerPos), (x) -> wantedSteerPos = Math.toRadians(x));
    builder.addDoubleProperty("Steer Error", () -> Math.toDegrees(steerError), null);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("Initialized");
    driveSumError = 0;
    driveLastError = 0;
    drivePower = 0;
    steerSumError = 0;
    steerLastError = 0;
    steerPower = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    driveLastError = error;
    steerLastError = steerError;
    error = wantedPos - MOTOR_SUBSYSTEM.getDrivePosition();
    steerError = wantedSteerPos - MOTOR_SUBSYSTEM.getSteerPosition();
    driveSumError += error;
    steerSumError += steerError;
    drivePower = PIDMotorSubsystemConstants.DRIVE_KP * error + PIDMotorSubsystemConstants.DRIVE_KI * driveSumError + PIDMotorSubsystemConstants.DRIVE_KD * (error - driveLastError);
    steerPower = PIDMotorSubsystemConstants.STEER_KP * steerError + PIDMotorSubsystemConstants.STEER_KI * steerSumError + PIDMotorSubsystemConstants.STEER_KD * (steerError - steerLastError);
    MOTOR_SUBSYSTEM.setDrivePower(drivePower);
    MOTOR_SUBSYSTEM.setSteerPower(steerPower);
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
