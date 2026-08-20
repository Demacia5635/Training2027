// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.subsystems.FDMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class FDMotorCommand extends Command {
  public final FDMotorSubsystem subsystem;
  private double driveVolt;
  // private double driveVelocity;

  /** Creates a new FDMotorCommand. */
  public FDMotorCommand(FDMotorSubsystem subsystem) {
    this.subsystem = subsystem;
    SmartDashboard.putData("voltage", this);
    SmartDashboard.putData("start command", new InstantCommand(() -> this.schedule()));
    addRequirements(subsystem);
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("Drive volt", () -> driveVolt, (x) -> driveVolt = x);
    builder.addDoubleProperty("Drive velocity", () -> subsystem.getDriveVelocity(), null);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("initialized");
    driveVolt = 0;
    // driveVelocity = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    subsystem.setDriveVolt(driveVolt);
    System.out.println("drive volt: " + driveVolt);
    // subsystem.setDriveVelocity(driveVelocity);

    
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem.setDriveVolt(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

// 20.2109375 - 2.37
// -23.056640625 -2.68