// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class SteerMotorSubsistem extends SubsystemBase {
  private final TalonFX steerMotor;
  private double powerSteer;

  public SteerMotorSubsistem() {
    super();
    steerMotor = new TalonFX(Constants.STEER_MOTOR_ID,Constants.CANBUS);
  }

  //public void setPositionSteer(double position) {
  //  steerMotor.setPositionVoltage(position);
  //}

  //public double getCurrentPositionSteer() {
  //  return steerMotor.getCurrentPosition();
  //}

  //public void setVelocitySteer(double velocity) {
  //  steerMotor.setVelocity(velocity);
  //}

  //public double getCurrentVelocitySteer() {
  //  return steerMotor.getCurrentVelocity();
  //}

  public void setPowerSteer(double powerSteer) {
    this.powerSteer = powerSteer;
    steerMotor.set(powerSteer);
  }

  public void stop() {
    setPowerSteer(0);
  }

  public double getAngleSteer() {
    return (steerMotor.getPosition().getValueAsDouble()) / Constants.GEAR_RATIO_STEER;
  }

  public double getAngleSteerDeg() {
    return getAngleSteer() * 180 / Math.PI;
  }
  public double getCurrentVelocity() {
    return (steerMotor.getVelocity().getValueAsDouble());
  }
  @Override
  public void periodic() {
    // double angleFromMotor = SmartDashboard.getAngle(), -1);
    SmartDashboard.putNumber("angle steer", getAngleSteerDeg());
    SmartDashboard.putNumber("steer velocity", getCurrentVelocity());
    //SmartDashboard.putNumber("PID position", getCurrentPositionSteer());
    //SmartDashboard.putNumber("PID velocity", getCurrentVelocitySteer());
  }
}
/** Creates a new ExampleSubsystem. */
// public SimpleMotorSubsystem() {}

/**
 * Example command factory method.
 *
 * // * @return a command
 */
// public Command exampleMethodCommand() {
// Inline construction of command goes here.
// Subsystem::RunOnce implicitly requires `this` subsystem.
/// return runOnce(
// () -> {
/* one-time action goes here */
// });
// }

/**
 * An example method querying a boolean state of the subsystem (for example, a
 * digital sensor).
 *
 * // * @return value of some boolean subsystem state, such as a digital sensor.
 */
// public boolean exampleCondition() {
// Query some boolean state, such as a digital sensr.
// return false;
// }

// @Override
// public void periodic() {
// This method will be called once per scheduler run
// }

// @Override
// public void simulationPeriodic() {
// This method will be called once per scheduler run during simulation
// }
