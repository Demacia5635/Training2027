// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.SimpleMotorSubsystem;

import java.lang.ModuleLayer.Controller;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {
  // המערכות שלנו
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  private CommandXboxController controller = new CommandXboxController(Constants.driverConstants.driverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    
    configureBindings();
    // configureDefaultCommands();
  }

  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    //new Trigger(m_exampleSubsystem::exampleCondition)
        //.onTrue(new ExampleCommand(m_exampleSubsystem));
    controller.a().onTrue(new SimpleMotorCommand(subsystem, 0.5, 5.0));
  }

  private void configureDefaultCommands() {
    subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return new SimpleMotorCommand(subsystem, 0.3, 10.0);
  }

}