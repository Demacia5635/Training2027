// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.sensors.Pigeon;
import frc.robot.Constants;

public class BasicChassis extends SubsystemBase {

  private BasicSwerveModule[] modules;
  private Pigeon gyro;
  private SwerveDrivePoseEstimator poseEstimator;
  private SwerveDriveKinematics kinematics;
  private SwerveDriveOdometry odometry;
  private Field2d field2d;
  private Rotation2d gyroAngle;

  public BasicChassis() {
    modules = new BasicSwerveModule[] {
        new BasicSwerveModule(Constants.ChassisConstants.FRONT_LEFT_CONFIG),
        new BasicSwerveModule(Constants.ChassisConstants.FRONT_RIGHT_CONFIG),
        new BasicSwerveModule(Constants.ChassisConstants.BACK_LEFT_CONFIG),
        new BasicSwerveModule(Constants.ChassisConstants.BACK_RIGHT_CONFIG) };

    gyro = new Pigeon(Constants.GyroConstants.PIGEON_CONFIG);
    gyroAngle = gyro.getGyroAngle();
    field2d = new Field2d();

    kinematics = new SwerveDriveKinematics(
        Constants.ChassisConstants.FRONT_LEFT_POSITION,
        Constants.ChassisConstants.FRONT_RIGHT_POSITION,
        Constants.ChassisConstants.BACK_LEFT_POSITION,
        Constants.ChassisConstants.BACK_RIGHT_POSITION);

    SwerveModulePosition[] modulePosAtStart = getModulePositions();
    odometry = new SwerveDriveOdometry(kinematics, gyroAngle, modulePosAtStart);
    poseEstimator = new SwerveDrivePoseEstimator(kinematics, gyroAngle, modulePosAtStart,Pose2d.kZero);



  }

  public SwerveModulePosition[] getModulePositions() {
    SwerveModulePosition[] positions = new SwerveModulePosition[modules.length];
    for (int i = 0; i < modules.length; i++) {
      positions[i] = modules[i].getModulePosition();
    }
    return positions;
  }

  public void setModuleStates(SwerveModuleState[] states) {
    for (int i = 0; i < states.length; i++) {
      modules[i].setModuleState(states[i]);
    }
  }

  // drives function which gets chassis speeds as a parameter
  public void drive(ChassisSpeeds speeds) {
    speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, gyro.getGyroAngle());
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
    setModuleStates(states);
  }

  // reset robot direction function which gets pose as a parameter
  public void resetPose(Pose2d pose) {
    poseEstimator.resetPosition(gyroAngle, getModulePositions(), pose);;
  }

  public void stopAll() {
    for (int i = 0; i < modules.length; i++) {
      modules[i].stopAll();
    }
  }

  @Override
  public void periodic() {
    gyroAngle = gyro.getGyroAngle();


    SwerveModulePosition[] positions = getModulePositions();
    poseEstimator.update(gyroAngle, positions);
    odometry.update(gyroAngle, positions);

    field2d.setRobotPose(poseEstimator.getEstimatedPosition());

  }
}
