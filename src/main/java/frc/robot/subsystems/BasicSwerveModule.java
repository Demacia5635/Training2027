// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.chassis.SwerveModuleConfig;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;
import frc.robot.Constants;

public class BasicSwerveModule extends SubsystemBase {

  private final TalonFXMotor driveMotor;
  private final TalonFXMotor steerMotor;
  private final Cancoder cancoder;

  public BasicSwerveModule(SwerveModuleConfig swerveModuleConfig) {
    super();
    driveMotor = new TalonFXMotor(Constants.MotorConstants.DRIVE_CONFIG);
    steerMotor = new TalonFXMotor(Constants.MotorConstants.STEER_CONFIG);
    cancoder = new Cancoder(Constants.cancoderConstants.CANCODER_CONFIG);

    steerMotor.setEncoderPosition(cancoder.getCurrentAbsPosition()  - swerveModuleConfig.steerOffset);

  }

  // Set drive motor power
  public void setDrivePower(double power) {
    driveMotor.setDuty(power);
  }

  // Set steeer motor power
  public void setSteerPower(double power) {
    steerMotor.setDuty(power);
  }

  // Steer setters:
  public void setSteerPosition(double targetRadians) {
    steerMotor.setPositionVoltage(targetRadians);
  }

  public void setSteerVelocity(double targetVelocityRPM) {
    steerMotor.setVelocity(targetVelocityRPM);
  }

  // Drive setters:
  public void setDriveVelocity(double targetVelocity) {
    driveMotor.setVelocity(targetVelocity);
  }

  // module state
  public void setModuleState(SwerveModuleState moduleState) {
    double targetAngle = moduleState.angle.getRadians();
    double diffrence = targetAngle - steerMotor.getCurrentPosition();
    double velocity = moduleState.speedMetersPerSecond;
    diffrence = MathUtil.angleModulus(diffrence);
    setSteerPosition(steerMotor.getCurrentPosition() + diffrence);
    setDriveVelocity(velocity);
  }

  public double getModulePosition() {
    return driveMotor.getCurrentPosition();
  }

  public double getModuleState() {
    return driveMotor.getCurrentVelocity();
  }

  public void stopAll() {
  steerMotor.stop();;
  driveMotor.stop();
}


  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addDoubleProperty("Absolute Encoder", () -> cancoder.getCurrentAbsPosition(), null);
    builder.addDoubleProperty("Steer Motor Angle", () -> steerMotor.getCurrentAngle(), null);
    builder.addDoubleProperty("Drive Motor Velocity", () -> driveMotor.getCurrentVelocity(), null);
    builder.addDoubleProperty("Steer Motor Velocity", () -> steerMotor.getCurrentVelocity(), null);

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
