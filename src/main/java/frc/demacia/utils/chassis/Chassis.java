// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.chassis;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.kinematics.DemaciaKinematics;
import frc.demacia.utils.RobotCommon;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.sensors.Cancoder;
import frc.demacia.utils.sensors.Pigeon;
import frc.demacia.vision.subsystem.Vision;

public class Chassis extends SubsystemBase {

    private static Chassis instance;

    public static void initialize(ChassisConfig chassisConfig) {
        if (instance == null)
            instance = new Chassis(chassisConfig);
    }

    public static Chassis getInstance() {
        return instance;
    }

    private final ChassisConfig chassisConfig;

    public SwerveModule[] modules;
    public Pigeon gyro;
    
    private DemaciaKinematics demaciaKinematics;
    private SwerveDriveKinematics wpilibKinematics;
    private SwerveDrivePoseEstimator poseEstimator;

    private Field2d field;

    private ChassisSpeeds lastSpeedsFieldRel = new ChassisSpeeds();
    private double lastAccelTime = Timer.getFPGATimestamp();

    private double lastOmega = 0;
    private double lastOmegaTime = Timer.getFPGATimestamp();

    private Chassis(ChassisConfig chassisConfig) {
        setName(getName());

        this.chassisConfig = chassisConfig;
        modules = new SwerveModule[4];
        Translation2d[] modulePositions = new Translation2d[4];
        for (int i = 0; i < 4; i++) {
            modules[i] = new SwerveModule(chassisConfig.swerveModuleConfig[i]);
            modulePositions[i] = chassisConfig.swerveModuleConfig[i].position;
        }
        gyro = new Pigeon(chassisConfig.pigeonConfig);

        demaciaKinematics = new DemaciaKinematics(modulePositions);
        wpilibKinematics = new SwerveDriveKinematics(modulePositions);

        poseEstimator = new SwerveDrivePoseEstimator(
                wpilibKinematics,
                getGyroAngle(),
                getModulePositions(),
                new Pose2d());

        field = new Field2d();

        Vision.getInstance();

        addLog();
    }

    public void addLog() {
        Log.putData("chassis/gyro angle", () -> getGyroAngle().getDegrees());

        SmartDashboard.putData("chassis/reset gyro",
                new InstantCommand(() -> setYaw(Rotation2d.kZero)).ignoringDisable(true));
        SmartDashboard.putData("chassis/reset gyro 180",
                new InstantCommand(() -> setYaw(Rotation2d.kPi)).ignoringDisable(true));
        SmartDashboard.putData("chassis/field", field);
        SmartDashboard.putData("chassis/set coast",
                new InstantCommand(() -> setNeutralMode(false)).ignoringDisable(true));
        SmartDashboard.putData("chassis/set brake",
                new InstantCommand(() -> setNeutralMode(true)).ignoringDisable(true));
        SmartDashboard.putData("chassis/reset moduls", new InstantCommand(()-> resetMudolse()).ignoringDisable(true));
    }

    public SwerveDrivePoseEstimator getPoseEstimate() {
        return poseEstimator;
    }

    public void checkElectronics() {
        for (SwerveModule module : modules) {
            module.checkElectronics();
        }
    }

    public void setNeutralMode(boolean isBrake) {
        for (SwerveModule module : modules) {
            module.setNeutralMode(isBrake);
        }
    }

    public ChassisConfig getConfig() {
        return chassisConfig;
    }

    public void restGyro() {
        double gyroAngle = !RobotCommon.getIsRed() ? 0 : 180;
        gyro.setYaw(gyroAngle);
    }

    public void resrtGyro180() {
        double gyroAngle = !RobotCommon.getIsRed() ? 180 : 0;
        gyro.setYaw(gyroAngle);
    }

    public void resetMudolse(){
        for (int i = 0; i < modules.length; i++) {
            modules[i].resetModule();
        }
    }

    public void resetPose(Pose2d pose) {
        poseEstimator.resetPosition(getGyroAngle(), getModulePositions(), pose);
    }

    public Cancoder[] getCancoders() {
        Cancoder[] cancoders = new Cancoder[modules.length];
        for (int i = 0; i < modules.length; i++) {
            cancoders[i] = modules[i].getCancoder();
        }
        return cancoders;
    }

    public Rotation2d getGyroAngle() {
        return gyro.getGyroAngle();
    }

    public double getGyroAngularVelocity() {
        return gyro.getZVelocity();
    }

    public void addVisionMeasurement(Pose2d visionPose, double timestampSeconds) {
        poseEstimator.addVisionMeasurement(visionPose, timestampSeconds);
    }

    public void addVisionMeasurement(Pose2d visionPose, double timestampSeconds,
                                     Matrix<N3, N1> stdDevs) {
        poseEstimator.addVisionMeasurement(visionPose, timestampSeconds, stdDevs);
    }

    public void setYaw(Rotation2d angle) {
        if (angle != null) {
            gyro.setYaw(angle.getDegrees());
            poseEstimator.resetPosition(
                    angle,
                    getModulePositions(),
                    new Pose2d(getPose().getTranslation(), angle));
        }
    }

    public void stop() {
        for (SwerveModule i : modules) {
            i.stop();
        }
    }

    public void setSteerPower(double pow, int id) {
        modules[id].setSteerPower(pow);
    }

    public double getSteerVelocity(int id) {
        return modules[id].getSteerVel();
    }

    public double getSteerAcceleration(int id) {
        return modules[id].getSteerAccel();
    }

    public void setSteerPositions(double position) {
        setSteerPositions(new double[]{position, position, position, position});
    }

    public void setDrivePower(double pow, int id) {
        modules[id].setDrivePower(pow);
    }

    public void setDrivePower(double pow) {
        for (int i = 0; i < 4; i++)
            setDrivePower(pow, i);
    }

    public void setDriveVelocities(double[] velocities) {
        for (int i = 0; i < velocities.length; i++) {
            modules[i].setDriveVelocity(velocities[i]);
        }
    }

    public void setSpeedsFieldRel(ChassisSpeeds speeds) {
        SwerveModuleState[] states = demaciaKinematics.toSwerveModuleStates(speeds);
        setModuleStates(states);

        if (RobotBase.isSimulation()) {
            gyro.getSimState().setRawYaw(Math.toDegrees(gyro.getCurrentYaw() + speeds.omegaRadiansPerSecond * 0.02));
        }
    }

    public void setSpeedsRobotRel(ChassisSpeeds speeds) {
        SwerveModuleState[] states = wpilibKinematics.toSwerveModuleStates(speeds);
        setModuleStates(states);
    }

    public void setSpeedsRobotRelWithAccel(ChassisSpeeds speeds) {
        ChassisSpeeds fieldSpeeds = ChassisSpeeds.fromRobotRelativeSpeeds(speeds, getGyroAngle());
        setSpeedsFieldRel(fieldSpeeds);
    }

    public void setSteerPositions(double[] positions) {
        for (int i = 0; i < positions.length; i++) {
            modules[i].setSteerPosition(positions[i]);
        }
    }

    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < states.length; i++) {
            modules[i].setState(states[i]);
        }
    }

    public double getMaxDriveVelocity() {
        return chassisConfig.maxDriveVelocity;
    }

    public double getMaxRotationalVelocity() {
        return chassisConfig.maxRotationalVelocity;
    }

    public Translation2d getVelocityAsVector() {
        return new Translation2d(
                getChassisSpeedsFieldRel().vxMetersPerSecond,
                getChassisSpeedsFieldRel().vyMetersPerSecond);
    }

    public ChassisSpeeds getSpeedsRobotRel() {
        return ChassisSpeeds.fromFieldRelativeSpeeds(getChassisSpeedsFieldRel(), getGyroAngle());
    }

    /**
     * Returns linear acceleration [ax, ay] in m/s² (field-relative)
     * and angular acceleration [alpha] in rad/s², derived from velocity delta.
     */
    public double[] getAcceleration() {
        double now = Timer.getFPGATimestamp();
        double dt = now - lastAccelTime;

        ChassisSpeeds currentSpeedsFieldRel = getChassisSpeedsFieldRel();

        double ax = (currentSpeedsFieldRel.vxMetersPerSecond - lastSpeedsFieldRel.vxMetersPerSecond) / dt;
        double ay = (currentSpeedsFieldRel.vyMetersPerSecond - lastSpeedsFieldRel.vyMetersPerSecond) / dt;
        double aOmga = (currentSpeedsFieldRel.omegaRadiansPerSecond - lastSpeedsFieldRel.omegaRadiansPerSecond) / dt;

        lastSpeedsFieldRel = currentSpeedsFieldRel;
        lastAccelTime = now;

        return new double[]{ax, ay, aOmga};
    }

    /**
     * Returns angular acceleration (alpha) in rad/s² from the gyro.
     */
    public double getAngularAcceleration() {
        double now = Timer.getFPGATimestamp();
        double dt = now - lastOmegaTime;

        double currentOmega = getGyroAngularVelocity();
        double alpha = (currentOmega - lastOmega) / dt;

        lastOmega = currentOmega;
        lastOmegaTime = now;

        return alpha;
    }

    public ChassisSpeeds getChassisSpeedsRobotRel() {
        return demaciaKinematics.toChassisSpeeds(
                getModuleStates(),
                Math.toRadians(gyro.getCurrentYaw()));
    }

    public ChassisSpeeds getChassisSpeedsFieldRel() {
        return ChassisSpeeds.fromRobotRelativeSpeeds(
                demaciaKinematics.toChassisSpeeds(getModuleStates(), getGyroAngularVelocity()),
                getGyroAngle());
    }

    public Translation2d getChassisSpeedsVector() {
        ChassisSpeeds s = getChassisSpeedsFieldRel();
        return new Translation2d(s.vxMetersPerSecond, s.vyMetersPerSecond);
    }

    public Pose2d getPose() {
        return poseEstimator.getEstimatedPosition();
    }

    public Pose2d getPoseWithVelocity(double dt) {
        Pose2d currentPose = getPose();
        ChassisSpeeds currentSpeeds = getChassisSpeedsFieldRel();
        return new Pose2d(
                currentPose.getX() + (currentSpeeds.vxMetersPerSecond * dt),
                currentPose.getY() + (currentSpeeds.vyMetersPerSecond * dt),
                currentPose.getRotation().plus(new Rotation2d(currentSpeeds.omegaRadiansPerSecond * dt)));
    }

    public Pose2d getFuturePose(double dtSeconds) {
        return getPose().exp(new Twist2d(
                getChassisSpeedsFieldRel().vxMetersPerSecond * dtSeconds,
                getChassisSpeedsFieldRel().vyMetersPerSecond * dtSeconds,
                getChassisSpeedsFieldRel().omegaRadiansPerSecond * dtSeconds));
    }

    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] res = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) {
            res[i] = modules[i].getState();
        }
        return res;
    }

    private SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] arr = new SwerveModulePosition[modules.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = modules[i].getModulePosition();
        }
        return arr;
    }

    @Override
    public void periodic() {
        poseEstimator.update(getGyroAngle(), getModulePositions());

        field.setRobotPose(getPose());
    }
}