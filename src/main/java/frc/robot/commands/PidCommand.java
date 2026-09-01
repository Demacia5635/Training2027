// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PidCommand extends Command {
  private double targetPosition;
  private final SimpleMotorSubsystem subsystem;

  /** Creates a new PidCommand. */
  public PidCommand(SimpleMotorSubsystem subsystem) {
    // Use addRequirements() here to declare subsystem dependencies.
  
    this.subsystem = subsystem;
    addRequirements(subsystem);

  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    subsystem.setDrivePosition(0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }

  // @Override
  // public void initSendable(SendableBuilder builder){
  //   builder.addDoubleProperty("targetPosition", () -> targetPosition, (pos) -> targetPosition = pos);
  // }
}
