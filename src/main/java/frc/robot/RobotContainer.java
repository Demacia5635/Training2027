// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveToMeter;
import frc.robot.commands.SetSteerVoltage;
import frc.robot.commands.SteerToAngle;
import frc.robot.subsystems.DriveMotorSubsistem;
import frc.robot.subsystems.SteerMotorSubsistem;

import static frc.demacia.vision.VisionConstants.MAX_CROP;

import edu.wpi.first.wpilibj2.command.Command;
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

  private final SteerMotorSubsistem subsystemSteer;
  private final DriveMotorSubsistem subsystemDrive;
  public final DriveToMeter driveToMeterCommand;
  public final SetSteerVoltage setSteerVelocityCommand;

  // Replace with CommandPS4Controller or CommandJoystick if needed
  private final CommandXboxController m_driverController = new CommandXboxController(
      OperatorConstants.CONTROLLER_PORT);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    subsystemSteer = new SteerMotorSubsistem();
    subsystemDrive = new DriveMotorSubsistem();
    driveToMeterCommand = new DriveToMeter(subsystemDrive , 0.7);
    setSteerVelocityCommand = new SetSteerVoltage(subsystemSteer, 0.6);

    
    // Configure the trigger bindings
    configureBindings();
    subsystemDrive.setDefaultCommand(driveToMeterCommand);
    subsystemSteer.setDefaultCommand(setSteerVelocityCommand);
    //subsystemSteer.setDefaultCommand(subsystemSteerCommand);
    // configureDifultCommands();
    //  getAutonomousCommand();
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

  private CommandXboxController controller = new CommandXboxController(
      Constants.OperatorConstants.CONTROLLER_PORT);
  double leftY = controller.getLeftY(); // -1 forward!!! -1 to 1

  public Command getAutonomousCommand() {
     //return new SteerToAngle(subsystemSteer, Math.PI / 2, 0.03)
    // .andThen(new DriveToMeter(subsystemDrive,1.0 ,0.3)
    // .alongWith(new SteerToAngle(subsystemSteer, Math.PI*135.0/180.0 , 0.03 )));
   // return new SteerToAngle(subsystemSteer, 0, 0, 0,2);
  //  return new SteerToAngle(subsystemSteer, 0, 0, Math.toRadians(180), 0)
  //  .alongWith(new DriveToMeter(subsystemDrive, 0, 0, 1));
  //return new SetSteerVoltage(subsystemSteer, 0.7)
  //.andThen(new SetSteerVoltage(subsystemSteer, 0.3));
 return new SteerToAngle(subsystemSteer, Math.toRadians(90));
  }
}