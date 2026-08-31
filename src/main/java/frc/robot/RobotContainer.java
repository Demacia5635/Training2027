// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController.ControllerType;
import frc.robot.commands.BasicDriveCommand;
import frc.robot.subsystems.BasicChassis;


import edu.wpi.first.wpilibj2.command.Command;



public class RobotContainer {
  // The robot's subsystems and commands are defined1 here...
  // if something here says basic it basically means I created it and its not something that has already been made.
  // private final SimpleMotorSubsystem subsystem = new SimpleMotorSubsystem();
  // private CommandXboxController controller = new CommandXboxController(Constants.ControllerConstants.CONTROLLER_ID);
  private BasicChassis chassis = new BasicChassis();
  private CommandController controller = new CommandController(Constants.ControllerConstants.CONTROLLER_ID, ControllerType.kXbox); // if controller type is ps5 change it here.
  private BasicDriveCommand driveCommand = new BasicDriveCommand(chassis, controller); // the drive command


  public RobotContainer() {
    configureBindings();
    getAutonomousCommand();

    chassis.setDefaultCommand(driveCommand);

  }



  // public double leftYDeadBand() {
  //   if (Math.abs(controller.getLeftY()) < 0.1) {
  //     return 0;
  //   } else {
  //     return controller.getLeftY();
  //   }
  // }

  // public double rightYDeadBand() {
  //   if (Math.abs(controller.getRightY()) < 0.1) {
  //     return 0;
  //   } else {
  //     return controller.getRightY();
  //   }
  // }

  private void configureBindings() {


    // When a is pressed move steer motor
    // controller.a().onTrue(
    //     new SimpleMotorCommand(subsystem, 0.2, 0, 1)
    //         .alongWith(Commands.print("A pressed"))); // power is between -1 and 1

    // call the function
    // leftYDeadBand();

    // // call the function
    // rightYDeadBand();

    // when b is pressed move drive motor
  //   controller.b().onTrue(
  //       new SimpleMotorCommand(subsystem, 0, -0.3, 1)
  //           .alongWith(Commands.print("B pressed!")));

  //   // when the left trigger is pressed vibrate the controller
  //   controller.leftTrigger().whileTrue(
  //       Commands.startEnd(
  //           () -> controller.getHID().setRumble(RumbleType.kLeftRumble, 1.0), // When pressed
  //           () -> controller.getHID().setRumble(RumbleType.kLeftRumble, 0.0) // When released
  //       ));
  //   // when the right trigger is pressed vibrate the controller

  //   controller.rightTrigger().whileTrue(
  //       Commands.startEnd(
  //           () -> controller.getHID().setRumble(RumbleType.kRightRumble, 1.0), // When pressed
  //           () -> controller.getHID().setRumble(RumbleType.kRightRumble, 0.0) // When released
  //       ));
  }


  public Command getAutonomousCommand() {
    // return new SetSteerAngleCommand(subsystem, Math.toRadians(0)); // if u want degrees
    return null;
  }
}
