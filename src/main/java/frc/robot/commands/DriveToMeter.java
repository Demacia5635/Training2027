// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.DriveMotorSubsistem;
import frc.robot.subsystems.SteerMotorSubsistem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class DriveToMeter extends Command {
  DriveMotorSubsistem subsystem1 = new DriveMotorSubsistem();
  private double currentMeterDrive;
  private double wantedMeterDrive;
  private double power;

  /** Creates a new DriveToAngle. */
  public DriveToMeter(DriveMotorSubsistem subsystem, double wantedMeterDrive, double power) {
    this.subsystem1 = subsystem;
    this.wantedMeterDrive = wantedMeterDrive;
    this.power = power;
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
    currentMeterDrive = subsystem1.getMeterDrive();
    int direction = ((wantedMeterDrive - currentMeterDrive) < 0) ? -1 : 1;
    subsystem1.setPowerDrive(power * direction);
    //subsystem1.setVelocityDrive(velocity);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    subsystem1.stop();
    System.out.println("angle steer"+ subsystem1.getMeterDrive());
    System.out.println("drive velocity" + subsystem1.getCurrentVelocity());
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    //return false;
    return Math.abs(wantedMeterDrive - currentMeterDrive) < 0.05;
  }
}
