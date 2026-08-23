// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants.FDMotorConstants;
public class FDMotorSubsystem extends SubsystemBase {
   TalonFXMotor driveMotor = new TalonFXMotor(FDMotorConstants.DRIVECONFIG);
   TalonFXMotor steerMotor = new TalonFXMotor(FDMotorConstants.STEERCONFIG);

    
  public FDMotorSubsystem(){
    super();
  }

  public void setDriveVolt(double volt){
    driveMotor.setVoltage(volt);
  }

  public double getDrivePosition(){
    return driveMotor.getPosition().getValueAsDouble();
  }

  public double getDriveVolt(){
    return driveMotor.getMotorVoltage().getValueAsDouble();
  }

  public double getDriveVelocity(){
    return driveMotor.getVelocity().getValueAsDouble();
  }

  public void setDriveVelocity(double velocity){
    driveMotor.setVelocity(velocity);
  }

  public double getSteerPosition(){
    return steerMotor.getPosition().getValueAsDouble();
  }

  public double getSteerVolt(){
    return steerMotor.getMotorVoltage().getValueAsDouble();
  }

  public void setSteerVolt(double volt){
    steerMotor.setVoltage(volt);
  }

  public double getSteerVelocity(){
    return steerMotor.getVelocity().getValueAsDouble();
  }

  public void setSteerVelocity(double velocity){
    steerMotor.setVelocity(velocity);
  }

  public void setDrivePosition(double pos){
    driveMotor.setPosition(pos);
  }


  @Override
  public void periodic() {
    SmartDashboard.putNumber("Drive Position", getDrivePosition());
    SmartDashboard.putNumber("Steer Velocity", getSteerVelocity());
    SmartDashboard.putNumber("Steer Position", getSteerPosition());
    SmartDashboard.putNumber("Drive Position", getDrivePosition());
  }
}
