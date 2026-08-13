// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.odometry;


import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.Timer;

import frc.demacia.odometry.DemaciaPoseEstimator.OdometryObservation;
import frc.demacia.vision.VisionConstants;

public class RobotPose {

    private static RobotPose instance;

    private DemaciaPoseEstimator poseEstimator;
    
    private BuiltInAccelerometer accelerometer;

    private RobotPose(Translation2d[] modulePositions, Matrix<N3, N1> stateSTD,
            Matrix<N3, N1> questSTD) {

        this.poseEstimator = new DemaciaPoseEstimator(modulePositions, stateSTD, VisionConstants.LIMELIGHT_STD);
        this.accelerometer = new BuiltInAccelerometer(); 
    }

    public Pose2d getPose() {

        return poseEstimator.getEstimatedPose();
    }

    public static void initialize(Translation2d[] modulePositions, Matrix<N3, N1> stateSTD,
            Matrix<N3, N1> questSTD) {

        if (instance == null)
            instance = new RobotPose(modulePositions, stateSTD, questSTD);
    }

    public void resetPose() {
        resetPose(Pose2d.kZero);
    }

    public void resetPose(Pose2d pose) {
        System.out.println(pose);
        poseEstimator.resetPose(pose);
    }

    public static RobotPose getInstance() {
        return instance;
    }

    public void addOdometryCalculation(OdometryObservation odometryObservation) {
        poseEstimator.addOdometryCalculation(odometryObservation);
    }

    public void addOdometryCalculation(Pose2d odometryPose, Rotation2d gyroAngle,
            SwerveModulePosition[] modulePositions) {
        addOdometryCalculation(new OdometryObservation(Timer.getFPGATimestamp(), gyroAngle, modulePositions));
    }

    public void update(Pose2d odometryPose, Rotation2d gyroAngle,
            SwerveModulePosition[] modulePositions, Translation2d currentVelocity) {
        update(new OdometryObservation(Timer.getFPGATimestamp(), gyroAngle, modulePositions));

    }

    public void update(OdometryObservation odometryObservation) {
        if (Math.abs(accelerometer.getX()) < 0.3 && Math.abs(accelerometer.getZ()) < 0.3)
            addOdometryCalculation(odometryObservation);
    }
}
