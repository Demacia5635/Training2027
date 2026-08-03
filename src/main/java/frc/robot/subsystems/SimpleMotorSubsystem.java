// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {

  TalonFX motor = new TalonFX(Constants.SimleMotorConstants.MOTOR_ID, Constants.SimleMotorConstants.MOTOR_CANBUS);

  /** Creates a new ExampleSubsystem. */
  public SimpleMotorSubsystem() {
    super();
  }

  public void setPower(double power) {
    motor.set(power);
  } 

  public void stop() {
    motor.set(0);
  }   

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
