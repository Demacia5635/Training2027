// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;
import frc.demacia.utils.sensors.CancoderConfig;

public class SwerveModuleSubsystem extends SubsystemBase {
  TalonFXMotor steerMotor;
  TalonFXMotor driveMotor;
  Cancoder cancoder;

  public SwerveModuleSubsystem(TalonFXConfig steerConfig, TalonFXConfig driveConfig, CancoderConfig cancoderConfig) {
    this.steerMotor = new TalonFXMotor(steerConfig);
    this.driveMotor = new TalonFXMotor(driveConfig);
    this.cancoder = new Cancoder(cancoderConfig);
  }

  public void drive(double radians, int direction) throws Exception {
    double currentAngle = driveMotor.getCurrentAngle();
    driveMotor.setPosition(Math.signum(direction) * Math.abs(currentAngle - radians));

  }

  public void goToAngle(double angle) {
   double currentAngle = cancoder.get();
   steerMotor.setPosition(angle-currentAngle);
  }

  public void resetAngle() { // go to position 0
    steerMotor.setPosition(0);
  }

}
