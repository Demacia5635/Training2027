// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.ExampleCommand;
import frc.robot.commands.FDMotorCommand;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.FDMotorSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  public static double driveX;
  public static double driveY;
  private final FDMotorSubsystem fdMotorSubsystem;
  private final FDMotorCommand fdCommand;
  // private final PIDMotorSubsystem pidMotorSubsystem;
  // private final PIDMotorCommand posCommand;
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

  // Replace with CommandPS4Controller or CommandJoystick if needed
  public final CommandXboxController m_driverController =
      new CommandXboxController(OperatorConstants.kDriverControllerPort);

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    fdMotorSubsystem = new FDMotorSubsystem();
    fdCommand = new FDMotorCommand(fdMotorSubsystem);
        configureBindings();
        SmartDashboard.putData(CommandScheduler.getInstance());
    
    

    // getAutonomousCommand();
    // SmartDashboard.putData("Command Motor", new HomeworkMotorCommand(homeworkMotorSubsystem, 100, 10, true, true));
  }


  // public Command getAutonomousCommand(){
  //     // return new HomeworkMotorCommand(homeworkMotorSubsystem, 0.5 * Math.PI, 0, true, false).andThen(new HomeworkMotorCommand(homeworkMotorSubsystem, 0.75 * Math.PI, 1, true, true)).andThen(new HomeworkMotorCommand(homeworkMotorSubsystem, 0, -1, true, true));
  // }
  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  
  
  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
      driveY = -(m_driverController.getLeftY());
      driveX = m_driverController.getLeftX();
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
 
}
