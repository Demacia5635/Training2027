// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.commands.SetDriveDistanceCommand;
import frc.robot.commands.SetSteerAngleCommand;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.CHASSIS;
import frc.robot.subsystems.SimpleMotorSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of * the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final CHASSIS subsystem = new CHASSIS();
  private CommandXboxController controller = new CommandXboxController(Constants.ControllerConstants.CONTROLLER_ID); // Replace with CommandPS4Controller or CommandJoystick if needed

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    // Configure the trigger bindings
    configureBindings();

    getAutonomousCommand();

    // Displays the current position and odometry matrix details directly onto the dashboard
    new RunCommand(
        () -> {
          SmartDashboard.putString("Robot Pose", subsystem.getPose().toString());
          SmartDashboard.putNumber("Robot Gyro Angle", subsystem.getRotation2d().getDegrees());
        }, 
        subsystem
    ).schedule();
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
  public double leftYDeadBand() {
    if (Math.abs(controller.getLeftY()) < 0.1) {
      return 0;
    } else {
      return controller.getLeftY();
    }
  }

  public double leftXDeadBand() {
    if (Math.abs(controller.getLeftX()) < 0.1) {
      return 0;
    } else {
      return controller.getLeftX();
    }
  }

  public double rightXDeadBand() {
    if (Math.abs(controller.getRightX()) < 0.1) {
      return 0;
    } else {
      return controller.getRightX();
    }
  }

  public double rightYDeadBand() {
    if (Math.abs(controller.getRightY()) < 0.1) {
      return 0;
    } else {
      return controller.getRightY();
    }
  }

  private void configureBindings() {
    // set the default command using RunCommand
    // set default command for both drive and steer
    // bonus: when you move the joystick you move the motor, the left joystick for
    // the drive motor and the right joystick for the steer motor.
    subsystem.setDefaultCommand(
        new RunCommand(
            () -> {
              // check left joystick and update drive power
              // subsystem.setDriveVelocity(leftYDeadBand());
              // check right joystick and update steer power
              // subsystem.setSteerVelocity(rightYDeadBand());
              
              // Standard scaling: Converts raw joysticks (-1.0 to 1.0) into target physical meters/second velocities.
              double xSpeed = -leftYDeadBand() * 4.5;  // Inverted because Xbox forward axis outputs negative values.
              double ySpeed = -leftXDeadBand() * 4.5;  // Left/right lateral velocity mapping
              double rotSpeed = -rightXDeadBand() * 3.0; // Dynamic rotation angle modification scaling
              
              subsystem.drive(new ChassisSpeeds(xSpeed, ySpeed, rotSpeed));
            }, 
            subsystem
        )
    );

    // When a is pressed move steer motor
    // controller.a().onTrue(
    //     new getModulePositions(subsystem, 0.2, 0, 1)
    //     .alongWith(Commands.print("A pressed"))); // power is between -1 and 1

    // X BUTTON CRITICAL FIX: Reset wheel position tracking & odometry metrics instantly
    controller.x().onTrue(
        new InstantCommand(() -> subsystem.resetOdometry(new Pose2d(0, 0, subsystem.getRotation2d())), subsystem)
        .alongWith(Commands.print("Wheel Odometry Positions Reset Successfully!"))
    );

    // Y Button: Reset gyro field orientation heading to face 0 degrees
    controller.y().onTrue(
        new InstantCommand(() -> subsystem.resetGyro(), subsystem)
        .alongWith(Commands.print("Gyro Reset to Field Zero!"))
    );

    // power is between -1 and 1
    // call the function
    // leftYDeadBand();
    //
    // call the function
    // rightYDeadBand();
    // when b is pressed move drive motor
    // controller.b().onTrue(
    // new SimpleMotorCommand(subsystem, 0, -0.3, 1)
    // .alongWith(Commands.print("B pressed!")));
    // when the left trigger is pressed vibrate the controller
    controller.leftTrigger().whileTrue(
        Commands.startEnd(
            () -> controller.getHID().setRumble(RumbleType.kLeftRumble, 1.0), // When pressed
            () -> controller.getHID().setRumble(RumbleType.kLeftRumble, 0.0) // When released
        ));

    // when the right trigger is pressed vibrate the controller
    controller.rightTrigger().whileTrue(
        Commands.startEnd(
            () -> controller.getHID().setRumble(RumbleType.kRightRumble, 1.0), // When pressed
            () -> controller.getHID().setRumble(RumbleType.kRightRumble, 0.0) // When released
        ));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // return new SetSteerAngleCommand(subsystem, Math.toRadians(0)); // if u want degrees
    return null;
    // return null;
    // return new SequentialCommandGroup(
    // new SetSteerAngleCommand(subsystem, 90.0 * Constants.ConvertionConstants.DEGREES_TO_RADIANS),
    // new ParallelCommandGroup(
    // new SetSteerAngleCommand(subsystem, 135.0 * Constants.ConvertionConstants.DEGREES_TO_RADIANS)
    // ).withTimeout(2.0),
    // new ParallelCommandGroup(
    // new SetDriveDistanceCommand(subsystem, -1.0),
    // new SetSteerAngleCommand(subsystem, 0.0 * Constants.ConvertionConstants.DEGREES_TO_RADIANS)
    // ).withTimeout(2.0),
    // Commands.runOnce(() -> subsystem.stopAll(), subsystem)
    // );
  }
}
