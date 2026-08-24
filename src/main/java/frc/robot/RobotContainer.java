// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.controller.CommandController;
import frc.demacia.utils.controller.CommandController.ControllerType;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveToMeter;
import frc.robot.commands.SetSteerVoltage;
import frc.robot.commands.SteerToAngle;
import frc.robot.subsystems.DriveMotorSubsistem;
import frc.robot.subsystems.ModuleSubsystem;
import frc.robot.subsystems.SteerMotorSubsistem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  //private final SteerMotorSubsistem subsystemSteer;
  //private final DriveMotorSubsistem subsystemDrive;
  //public final DriveToMeter driveToMeterCommand;
  //public final SetSteerVoltage setSteerVelocityCommand;
  private final ModuleSubsystem moduleSubsystem;

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandController controller;

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    //subsystemSteer = new SteerMotorSubsistem();
    //subsystemDrive = new DriveMotorSubsistem();
    //driveToMeterCommand = new DriveToMeter(subsystemDrive, 0);
    //setSteerVelocityCommand = new SetSteerVoltage(subsystemSteer, 0);
    moduleSubsystem = new ModuleSubsystem();
    controller = new CommandController(OperatorConstants.CONTROLLER_PORT, ControllerType.kXbox);
    //controller = new CommandPS4Controller(OperatorConstants.CONTROLLER_PORT);

    // Configure the trigger bindings
    configureBindings();
    //subsystemDrive.setDefaultCommand(driveToMeterCommand);
   // subsystemSteer.setDefaultCommand(setSteerVelocityCommand);
    // subsystemSteer.setDefaultCommand(subsystemSteerCommand);
    // configureDifultCommands();
    // getAutonomousCommand();
    moduleSubsystem.setDefaultCommand(
        new RunCommand(
            () -> {
                double x = controller.getLeftX();
                double y = controller.getLeftY();

              System.out.println("Joystick X: " + x + ", Y: " + y);

                if (Math.abs(x) < 0.1) {
                    x = 0;
                }
                if (Math.abs(y) < 0.1) {
                    y = 0;
                }

                double speed = Math.hypot(x, y); // pitagoras
                moduleSubsystem.setVelocityDrive(speed);

                if (x != 0 || y != 0) {
                    double targetAngle = Math.atan2(x, -y); // tangas
                    moduleSubsystem.setSteerAngle(targetAngle);
                } else {
                    moduleSubsystem.setSteerAngle(0.0);
                }
            },
            moduleSubsystem
        )
    );
  }
  /**
   * Use this method to define your trigger->command mappings. Triggers can be
   * created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with
   * an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link
   * CommandXboxController
   * Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or
   * {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    // controller.a().onTrue(new SimpleMotorCommand(subsystem, -0.2, 2.0, 0.5));
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
  }
  // private void configureDifultCommands(){
  // subsystem.setDefaultCommand(new SimpleMotorCommand(subsystem, 0, 0));
  // }

  public Command getAutonomousCommand() {
    // return new SteerToAngle(subsystemSteer, Math.PI / 2, 0.03)
    // .andThen(new DriveToMeter(subsystemDrive,1.0 ,0.3)
    // .alongWith(new SteerToAngle(subsystemSteer, Math.PI*135.0/180.0 , 0.03 )));
    // return new SteerToAngle(subsystemSteer, 0, 0, 0,2);
    // return new SteerToAngle(subsystemSteer, 0, 0, Math.toRadians(180), 0)
    // .alongWith(new DriveToMeter(subsystemDrive, 0, 0, 1));
    // return new SetSteerVoltage(subsystemSteer, 0.7)
    // .andThen(new SetSteerVoltage(subsystemSteer, 0.3));
    //return new SteerToAngle(subsystemSteer, Math.toRadians(90));
    return null;
  }
}