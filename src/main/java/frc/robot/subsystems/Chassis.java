// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.sensors.Pigeon;
import frc.robot.Constants;

public class Chassis extends SubsystemBase {

  public final SwerveModule[] modules;
  public final Pigeon gyro;
  private final SwerveDrivePoseEstimator poseEstimator;
  private final SwerveDriveKinematics kinematics;
  private final Field2d field2d;

  public Chassis() {
    modules = new SwerveModule[] {
        new SwerveModule(Constants.SwerveModuleConstants.FR_MODULE_CONFIG),
        new SwerveModule(Constants.SwerveModuleConstants.FL_MODULE_CONFIG),
        new SwerveModule(Constants.SwerveModuleConstants.BR_MODULE_CONFIG),
        new SwerveModule(Constants.SwerveModuleConstants.BL_MODULE_CONFIG)
    };

    gyro = new Pigeon(Constants.GyroConstants.PIGEON_CONFIG);
    field2d = new Field2d();

    kinematics = new SwerveDriveKinematics(
        Constants.SwerveModuleConstants.FR_POSITION,
        Constants.SwerveModuleConstants.FL_POSITION,
        Constants.SwerveModuleConstants.BR_POSITION,
        Constants.SwerveModuleConstants.BL_POSITION);

    poseEstimator = new SwerveDrivePoseEstimator(
        kinematics,
        getGyroRotation(),
        getModulePositions(),
        Pose2d.kZero);

    SmartDashboard.putData("Field", field2d);
  }

  public Rotation2d getGyroRotation() {
    return Rotation2d.fromRadians(gyro.getCurrentYaw());
  }

  public SwerveModulePosition[] getModulePositions() {
    SwerveModulePosition[] positions = new SwerveModulePosition[modules.length];
    for (int i = 0; i < modules.length; i++) {
      positions[i] = modules[i].getModulePosition();
    }
    return positions;
  }

  public void setModuleStates(SwerveModuleState[] states) {
    SwerveDriveKinematics.desaturateWheelSpeeds(states, 5);
    for (int i = 0; i < states.length; i++) {
      modules[i].setModuleState(states[i]);
    }
  }

  public void drive(ChassisSpeeds speeds, boolean fieldRelative) {
    ChassisSpeeds targetSpeed = fieldRelative
        ? ChassisSpeeds.fromFieldRelativeSpeeds(speeds, getGyroRotation())
        : speeds;
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(targetSpeed);
    setModuleStates(states);
  }

  public void resetGyro() {
    gyro.reset();
  }

  public void resetPose(Pose2d pose) {
    poseEstimator.resetPosition(getGyroRotation(), getModulePositions(), pose);
  }

  public Pose2d getPose() {
    return poseEstimator.getEstimatedPosition();
  }

  public void stop() {
    for (SwerveModule module : modules) {
      module.stopAllMotors();
    }
  }

  @Override
  public void periodic() {
    poseEstimator.update(getGyroRotation(), getModulePositions());
    field2d.setRobotPose(poseEstimator.getEstimatedPosition());
  }
}
