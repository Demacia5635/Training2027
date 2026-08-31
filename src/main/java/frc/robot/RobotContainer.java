// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.GoToTargetAngleCommand;
import frc.robot.commands.PidCommand;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.commands.YuvalSteer;
import frc.robot.commands.moduleCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;

import java.lang.ModuleLayer.Controller;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class RobotContainer {
  private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  private CommandXboxController controller = new CommandXboxController(Constants.driverConstants.driverControllerPort);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    ////subsystem.setDefaultCommand(new YuvalSteer(subsystem.getTargetSteerPosition(), subsystem.getTargetDriveVel(), subsystem));
    // SmartDashboard.putData("YuvalSteer", new YuvalSteer(0, 100, subsystem));
    SmartDashboard.putData("pid" , new PidCommand(subsystem));
    // SmartDashboard.putNumber("3rd cmd Target", 0.0);

    // SmartDashboard.putData("ThirdCommand", new GoToTargetAngleCommand(subsystem));

    configureBindings();
    // configureDefaultCommands();
  }

  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    // new Trigger(m_exampleSubsystem::exampleCondition)
    // .onTrue(new ExampleCommand(m_exampleSubsystem));
    // controller.a().onTrue(new SimpleMotorCommand(subsystem, 1, 0.5, 5.0));
  }

  private void configureDefaultCommands() {
    // subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0, 0));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return null;
  }

}