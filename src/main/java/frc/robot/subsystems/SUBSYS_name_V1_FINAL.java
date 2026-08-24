package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;
import frc.robot.Constants;

public class SUBSYS_name_V1_FINAL extends SubsystemBase {

  private final TalonFXMotor driveMotor;
  private final TalonFXMotor steerMotor;
  private final Cancoder driveEncoder;
  private final Cancoder steerEncoder;

  public SUBSYS_name_V1_FINAL() {

    driveMotor = new TalonFXMotor(
        Constants.SimpleMotorConstants.DRIVE_CONFIG);

    steerMotor = new TalonFXMotor(
        Constants.SimpleMotorConstants.STEER_CONFIG);

    driveEncoder = new Cancoder(
        Constants.SimpleMotorConstants.DRIVE_ENCODER_CONFIG);

    steerEncoder = new Cancoder(
        Constants.SimpleMotorConstants.STEER_ENCODER_CONFIG);

    SmartDashboard.putData(
        "Motors/Drive",
        driveMotor);

    SmartDashboard.putData(
        "Motors/Steer",
        steerMotor);
  }

  public void setDrivePower(double power) {

    driveMotor.setDuty(
        MathUtil.clamp(
            power,
            -1.0,
            1.0));
  }

  public void setDriveVelocity(
      double velocityMetersPerSecond) {

    driveMotor.setVelocity(
        velocityMetersPerSecond);
  }

  public double getDrivePosition() {

    return driveMotor.getCurrentPosition();
  }

  public double getDriveVelocity() {

    return driveMotor.getCurrentVelocity();
  }

  public double getDriveCurrent() {

    return driveMotor.getCurrentCurrent();
  }

  public void stopDrive() {

    driveMotor.stop();
  }

  public void setSteerPower(double power) {

    steerMotor.setDuty(
        MathUtil.clamp(
            power,
            -1.0,
            1.0));
  }

  public void setSteerAngleDegrees(
      double angleDegrees) {

    steerMotor.setAngle(
        Math.toRadians(angleDegrees));
  }

  public double getSteerAngleDegrees() {

    return Math.toDegrees(
        steerMotor.getCurrentAngle());
  }

  public double getSteerPosition() {

    return steerMotor.getCurrentPosition();
  }

  public double getSteerVelocity() {

    return steerMotor.getCurrentVelocity();
  }

  public double getSteerCurrent() {

    return steerMotor.getCurrentCurrent();
  }

  public void stopSteer() {

    steerMotor.stop();
  }

  public void stop() {

    driveMotor.stop();
    steerMotor.stop();
  }

  public void checkElectronics() {

    driveMotor.checkElectronics();
    steerMotor.checkElectronics();
  }

  // ELASTIC TELEMETRY

  @Override
  public void periodic() {

    // DRIVE

    SmartDashboard.putNumber(
        "Drive/Position (m)",
        getDrivePosition());

    SmartDashboard.putNumber(
        "Drive/Velocity (mps)",
        getDriveVelocity());

    SmartDashboard.putNumber(
        "Drive/Current (A)",
        getDriveCurrent());

    // STEER

    SmartDashboard.putNumber(
        "Steer/Angle (deg)",
        getSteerAngleDegrees());

    SmartDashboard.putNumber(
        "Steer/Velocity",
        getSteerVelocity());

    SmartDashboard.putNumber(
        "Steer/Current (A)",
        getSteerCurrent());
  }
}