// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class SteerMotorSubsistem extends SubsystemBase {
  private final TalonFXMotor steerMotor;
  private double powerSteer;
  public double ks;
  public double kv;

  public SteerMotorSubsistem() {
    super();
    steerMotor = new TalonFXMotor(Constants.CONFIG_STEER);
    SmartDashboard.putData(this);
  }

  public void setPositionSteer(double position) {
    steerMotor.setPositionVoltage(position);
  }

  public double getCurrentPositionSteer() {
    return steerMotor.getCurrentPosition();
  }

  public void setVelocitySteer(double velocity) {
    steerMotor.setVelocity(velocity);
  }

  public double getCurrentVelocitySteer() {
    return steerMotor.getCurrentVelocity();
  }

  public void setPowerSteer(double powerSteer) {
    this.powerSteer = powerSteer;
    steerMotor.setDuty(powerSteer);
  }

  public double getPowerSteer() {
    return powerSteer;
  }

  public void stop() {
    setPowerSteer(0);
  }

  public double getAngleSteer() {
    return (steerMotor.getCurrentPosition());
  }

  public double getAngleSteerDeg() {
    return getAngleSteer() * 180 / Math.PI;
  }

  public double calculateKsSteer() {
    return Math.signum(getCurrentVelocitySteer()) * Constants.KS_STEER;
  }

  public double calculateKvSteer() {
    return Constants.KV_STEER * getCurrentVelocitySteer();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("get Velocity steer", this::getCurrentVelocitySteer, this::setVelocitySteer);
    builder.addDoubleProperty("get Power steer", this::getPowerSteer, this::setPowerSteer);

  }

  @Override
  public void periodic() {
    // double angleFromMotor = SmartDashboard.getAngle(), -1);
    SmartDashboard.putNumber("angle steer", getAngleSteerDeg());
    SmartDashboard.putNumber("PID position", getCurrentPositionSteer());
    SmartDashboard.putNumber("PID velocity", getCurrentVelocitySteer());
    SmartDashboard.putNumber("Ks Calculate steer", calculateKsSteer());
    SmartDashboard.putNumber("Kv Calculate steer", calculateKvSteer());
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
