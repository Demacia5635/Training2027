// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.SimpleMotorCommand;
import frc.robot.subsystems.SimpleMotorSubsystem;


public class RobotContainer {

    // Create the motor subsystem
    private final SimpleMotorSubsystem motorSubsystem =
        new SimpleMotorSubsystem();

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        // Empty for now - controller will be added later
    }

    public Command getAutonomousCommand() {
    return new TwoMotorCommand(
        motorSubsystem,
        0.2,   // turning motor
        0.6,   // drive motor
        3.0    // both run for 3 seconds
    );
    }
}