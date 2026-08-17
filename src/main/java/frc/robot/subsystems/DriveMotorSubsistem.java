// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class DriveMotorSubsistem extends SubsystemBase {
  private final TalonFXMotor driveMotor;
  private double powerDrive;
  public double ks;
  public double kv;

  public DriveMotorSubsistem() {
    super();
    driveMotor = new TalonFXMotor(Constants.CONFIG_DRIVE);
    SmartDashboard.putData(this);
  }

  public void setPositionDrive(double position) {
    driveMotor.setPositionVoltage(position);
  }

  public void setVelocityDrive(double velocity) {
    driveMotor.setVelocity(velocity);
  }

  public double getCurrentVelocityDrive() {
    return driveMotor.getCurrentVelocity();
  }

  public double getPowerDrive() {
    return powerDrive;
  }

  public void setPowerDrive(double powerDrive) {
    this.powerDrive = powerDrive;
    driveMotor.setDuty(powerDrive);
  }

  public void stop() {
    setPowerDrive(0);
  }

  public double getMeterDrive() {
    return (driveMotor.getCurrentPosition());
  }

  public double calculateKsDrive() {
    return Math.signum(getCurrentVelocityDrive()) * Constants.KS_STEER;
  }

  public double calculateKvDrive() {
    return Constants.KV_STEER * getCurrentVelocityDrive();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.addDoubleProperty("get Velocity drive", this::getCurrentVelocityDrive, this::setVelocityDrive);
    builder.addDoubleProperty("get Power drive", this::getPowerDrive, this::setPowerDrive);
  }

  @Override
  public void periodic() {
    // double angleFromMotor = SmartDashboard.getAngle(), -1);
    SmartDashboard.putNumber("meter drive", getMeterDrive());
    SmartDashboard.putNumber("PID Drive Velocity", getCurrentVelocityDrive());
        SmartDashboard.putNumber("Ks Calculate drive", calculateKsDrive());
    SmartDashboard.putNumber("Kv Calculate drive", calculateKvDrive());
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
