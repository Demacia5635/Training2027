// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.EnumSet;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {

  TalonFX motor = new TalonFX(Constants.SimleMotorConstants.MOTOR_ID, Constants.SimleMotorConstants.MOTOR_CANBUS);
  
  // Status signals to get motor data
  StatusSignal<Angle> motorPositionStatusSignal;
  StatusSignal<AngularVelocity> motorVelocityStatusSignal;
  StatusSignal<Voltage> motorVoltageStatusSignal;
  StatusSignal<Current> motorCurrentStatusSignal;

  // network table for motor data
  NetworkTable motorDataTable = NetworkTableInstance.getDefault().getTable("MotorData");
  DoubleEntry motorPositionEntry = motorDataTable.getDoubleTopic("Motor Position").getEntry(0.0);
  DoubleEntry motorVelocityEntry = motorDataTable.getDoubleTopic("Motor Velocity").getEntry(0.0);
  DoubleEntry motorVoltageEntry = motorDataTable.getDoubleTopic("Motor Voltage").getEntry(0.0);
  DoubleEntry motorCurrentEntry = motorDataTable.getDoubleTopic("Motor Current").getEntry(0.0); 
  DoubleEntry motorRequiredPower = motorDataTable.getDoubleTopic("Motor Required Power").getEntry(0.0); 

  /** Creates a new ExampleSubsystem. */
  public SimpleMotorSubsystem() {
    super();
    // create the status signals
    motorPositionStatusSignal = motor.getPosition();
    motorVelocityStatusSignal = motor.getVelocity();
    motorVoltageStatusSignal = motor.getMotorVoltage();
    motorCurrentStatusSignal = motor.getSupplyCurrent();

    // update network table with initial values
    updateNetworkTable();
    motorRequiredPower.set(0.0);
    NetworkTableInstance.getDefault().addListener(motorRequiredPower,EnumSet.of(NetworkTableEvent.Kind.kValueAll),
      event -> {
        System.out.println("Motor Required Power changed to: " + motorRequiredPower.get());
    });

    // a simple Command to use required power
    SmartDashboard.putData("Set Power", new RunCommand(()->motor.set(getRequiredPower()), this));
    
  }

  public void setPower(double power) {
    motor.set(power);
  } 

  public void stop() {
    motor.set(0);
  }

  // add getters for motor data
  public double getMotorPosition() {
    return motorPositionStatusSignal.getValueAsDouble();
  }   
  public double getMotorVelocity() {
    return motorVelocityStatusSignal.getValueAsDouble();
  }   
  public double getMotorCurrent() {
    return motorCurrentStatusSignal.getValueAsDouble();
  }   
  public double getMotorVoltage() {
    return motorVoltageStatusSignal.getValueAsDouble();
  }   

  // required power
  public double getRequiredPower() {
    return motorRequiredPower.get();
  }

  // update network table
  private void updateNetworkTable() {
    // publish the status signals to the network table
    motorPositionEntry.set(getMotorPosition());
    motorVelocityEntry.set(getMotorVelocity());
    motorCurrentEntry.set(getMotorCurrent());
    motorVoltageEntry.set(getMotorVoltage());
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    StatusSignal.refreshAll(motorCurrentStatusSignal, motorPositionStatusSignal, motorVelocityStatusSignal, motorVoltageStatusSignal);
    updateNetworkTable();
  }
}
