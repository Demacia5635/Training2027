// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import static edu.wpi.first.units.Units.Kilo;

import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.Kinematics;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.sensors.Pigeon;
import frc.robot.Constants;

public class Chassis extends SubsystemBase {

  public SwerveModule[] modules;
  public Pigeon gyro;
  private SwerveDrivePoseEstimator poseEstimator;
  private SwerveDriveKinematics kinematics;
  private Field2d field2d;

  public Chassis() {
    modules = new SwerveModule[] { new SwerveModule(Constants.SwerveModuleConfigs.FRONT_LEFT_CONFIG),

        new SwerveModule(Constants.SwerveModuleConfigs.FRONT_RIGHT_CONFIG),

        new SwerveModule(Constants.SwerveModuleConfigs.BACK_LEFT_CONFIG),

        new SwerveModule(Constants.SwerveModuleConfigs.BACK_RIGHT_CONFIG) };

    gyro = new Pigeon(Constants.GyroConstants.PIGEON_CONFIG);
    field2d = new Field2d();
    kinematics = new SwerveDriveKinematics(new Translation2d[4]);

    poseEstimator = new SwerveDrivePoseEstimator(kinematics, Rotation2d.kZero, new SwerveModulePosition[4],
        Pose2d.kZero);
  }

  public void setModuleStates(SwerveModuleState[] states) {
    for (int i = 0; i < states.length; i++) {
      modules[i].setModuleState(states[i]);
    }
  }

  // drive function
  public void drive(ChassisSpeeds speeds) {
    speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, gyro.getGyroAngle());
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
    setModuleStates(states);
  }

  @Override
  public void periodic() {

  }
}
