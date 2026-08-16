// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.PIDMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PIDMotorCommand extends Command {
  private final PIDMotorSubsystem MOTOR_SUBSYSTEM;

  private double wantedPos = 0;
  private double error = 0;
  private double wantedSteerPos = 0;
  private double steerError = 0;
  /** Creates a new PIDMotorCommand. */
  public PIDMotorCommand(PIDMotorSubsystem MOTOR_SUBSYSTEM) {
    this.MOTOR_SUBSYSTEM = MOTOR_SUBSYSTEM;
    SmartDashboard.putData("position command", this);
    SmartDashboard.putData("start command", new InstantCommand(() -> this.schedule()));
    addRequirements(MOTOR_SUBSYSTEM);
  }

  @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Wanted drive Position", ()-> wantedPos, (x) -> wantedPos = x);
        builder.addDoubleProperty("Error", () ->error,null);
        builder.addDoubleProperty("Wanted steer Position", ()-> wantedSteerPos, (x) -> wantedSteerPos = x);
        builder.addDoubleProperty("Steer Error", () ->steerError,null);
      
      } 
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("Initialized");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    MOTOR_SUBSYSTEM.setDrivePosition(Math.toRadians(wantedPos));
     error = wantedPos - MOTOR_SUBSYSTEM.getDrivePosition();
     MOTOR_SUBSYSTEM.setSteerPosition(Math.toRadians(wantedSteerPos));
     steerError = wantedSteerPos - MOTOR_SUBSYSTEM.getSteerPosition();
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
