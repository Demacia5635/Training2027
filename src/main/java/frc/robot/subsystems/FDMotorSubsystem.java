// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.FDMotorConstants;
public class FDMotorSubsystem extends SubsystemBase {
   TalonFX driveMotor = new TalonFXMotor(FDMotorConstants.DRIVECONFIG);
    
  public FDMotorSubsystem(){
    super();
  }

  public void setDriveVolt(double volt){
    driveMotor.setVoltage(volt);
  }

  public double getDriveVelocity(){
    return driveMotor.getVelocity().getValueAsDouble();
  }

  @Override
  public void periodic() {
  }
}
