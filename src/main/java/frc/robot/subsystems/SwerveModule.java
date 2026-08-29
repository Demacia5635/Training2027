// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.chassis.SwerveModuleConfig;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;

public class SwerveModule extends SubsystemBase {
private final TalonFXMotor driveMotor;
  private final TalonFXMotor steerMotor;
  private final Cancoder cancoder;

  public SwerveModule(SwerveModuleConfig config) {
    super();
  driveMotor = new TalonFXMotor((TalonFXConfig) config.driveConfig);
  steerMotor = new TalonFXMotor((TalonFXConfig) config.steerConfig);
    cancoder = new Cancoder(config.cancoderConfig);

    steerMotor.setEncoderPosition(cancoder.getCurrentAbsPosition() - config.steerOffset);
  }

  public void setDrivePower(double power) {
    driveMotor.setDuty(power);
  }

  public void setSteerPower(double power) {
    steerMotor.setDuty(power);
  }

  public void setSteerPosition(double rad) {
    steerMotor.setPositionVoltage(rad);
  }

  public void setDriveVelocity(double vel) {
    driveMotor.setVelocity(vel);
  }

  public void setModuleState(SwerveModuleState moduleState) {
    double targetAngle = moduleState.angle.getRadians();
    double diff = targetAngle - steerMotor.getCurrentPosition();
    double vel = moduleState.speedMetersPerSecond;
    diff = MathUtil.angleModulus(diff);
    setSteerPosition(steerMotor.getCurrentPosition() + diff);
    setDriveVelocity(vel);
  }

  public SwerveModulePosition getModulePosition() {
    return new SwerveModulePosition(
        driveMotor.getCurrentPosition(),
        Rotation2d.fromRadians(steerMotor.getCurrentPosition()));
  }

  public SwerveModuleState getModuleState() {
    return new SwerveModuleState(
        driveMotor.getCurrentVelocity(),
        Rotation2d.fromRadians(steerMotor.getCurrentPosition()));
  }

  public void stopAllMotors() {
    steerMotor.stop();
    driveMotor.stop();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    super.initSendable(builder);
    builder.addDoubleProperty("Encoder", () -> cancoder.getCurrentAbsPosition(), null);
    builder.addDoubleProperty("Steer position ", () -> steerMotor.getCurrentPosition(), null);
    builder.addDoubleProperty("Drive velocity", () -> driveMotor.getCurrentVelocity(), null);
  }


  

}
