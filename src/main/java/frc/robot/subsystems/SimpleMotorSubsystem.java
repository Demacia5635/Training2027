// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
  public final TalonFX motor;
  public final TalonFX motor2;

  /** Creates a new SimpleMotorSubsystem. */
  public SimpleMotorSubsystem() {
    super();
    motor = new TalonFX(Constants.OperatorConstants.STEER_ID, Constants.OperatorConstants.MotorCANbus);
    motor2 = new TalonFX(Constants.OperatorConstants.DRIVE_ID, Constants.OperatorConstants.MotorCANbus);
  }

  public void setPowersteer(double Power) {
    motor.set(Power);
  }
  public void setPowerdrive(double Power) {
    motor2.set(Power);
  }

  public void stop() {
    motor.set(0);
    motor2.set(0);
  }

  public void setDifrentPower(double Power){
     motor.set(Power);
    motor2.set(Power);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
