// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
 
  public RobotContainer() {
    configureBindings();
    configureDefaultCommands();
  }

  private void configureBindings() {
  }

  private void configureDefaultCommands() {
    subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0));
  }

  public Command getAutonomousCommand() {
    return new SimpleMotorCommand(subsystem, 0.3, 10.0);
  }
}
