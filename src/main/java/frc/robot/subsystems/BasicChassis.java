// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.sensors.Pigeon;
import frc.robot.Constants;

public class BasicChassis extends SubsystemBase {

  public BasicSwerveModule[] modules;
  public Pigeon gyro;
  private SwerveDrivePoseEstimator poseEstimator;
  private SwerveDriveKinematics kinematics;
  private Field2d field2d;

  public BasicChassis() {
    modules = new BasicSwerveModule[] { new BasicSwerveModule(Constants.SwerveModuleConfigs.FRONT_LEFT_CONFIG),

        new BasicSwerveModule(Constants.SwerveModuleConfigs.FRONT_RIGHT_CONFIG),

        new BasicSwerveModule(Constants.SwerveModuleConfigs.BACK_LEFT_CONFIG),

        new BasicSwerveModule(Constants.SwerveModuleConfigs.BACK_RIGHT_CONFIG) };


        
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

  // drives function which gets chassis speeds as a parameter
  public void drive(ChassisSpeeds speeds) {
    speeds = ChassisSpeeds.fromFieldRelativeSpeeds(speeds, gyro.getGyroAngle());
    SwerveModuleState[] states = kinematics.toSwerveModuleStates(speeds);
    setModuleStates(states);
  }

  // reset robot direction function which gets pose as a parameter
  public void resetPose(Pose2d pose) {
    poseEstimator.resetPose(pose); 
  }

  public void stopAll() {
    for (int i = 0; i < modules.length; i++) {
      modules[i].stopAll();
    }
  }

  @Override
  public void periodic() {}
}
