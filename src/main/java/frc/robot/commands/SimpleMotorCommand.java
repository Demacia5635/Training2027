// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.subsystems.SimpleMotorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class SimpleMotorCommand extends Command {
  SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  private double power;
  private double duration;
  private double startTime;
  private Timer timer = new Timer();

  public SimpleMotorCommand(SimpleMotorSubsystem subsystem, double power, double duration) {
    this.subsystem = subsystem;
    this.power = power;
    this.duration = duration;
    addRequirements(subsystem);
  }

  @Override
  public void initialize() {
    startTime = Timer.getFPGATimestamp();
    timer.restart();
    System.out.println("Command started at: " + startTime + "seconds for " + duration + " secods with power: " + power);
  }

  @Override
  public void execute() {
    subsystem.setPower(power);
  }

  @Override
  public boolean isFinished() {
    return Timer.getFPGATimestamp() >= startTime + duration;
  }

  @Override
  public void end(boolean interrupted) {
    subsystem.stop();
    ;
    System.out.println("Command ended ar: " + Timer.getFPGATimestamp());
  }

}