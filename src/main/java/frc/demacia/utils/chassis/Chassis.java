// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.chassis;


import org.ejml.simple.SimpleMatrix;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;

import choreo.trajectory.SwerveSample;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.demacia.kinematics.DemaciaKinematics;
import frc.demacia.odometry.DemaciaPoseEstimator.OdometryObservation;
import frc.demacia.odometry.RobotPose;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.sensors.Pigeon;
import frc.demacia.vision.utils.VisionConstants;

/**
 * Main swerve drive chassis controller.
 * 
 * <p>
 * Manages four swerve modules, odometry, and provides high-level drive control
 * with acceleration limiting and smooth motion profiling.
 * </p>
 * <p>
 * Manages four swerve modules, odometry, and provides high-level drive control
 * with acceleration limiting and smooth motion profiling.
 * </p>
 * 
 * <p>
 * <b>Features:</b>
 * </p>
 * <ul>
 * <li>Field-relative and robot-relative control</li>
 * <li>Pose estimation with vision integration</li>
 * <li>Smooth acceleration limiting</li>
 * <li>Path following capabilities</li>
 * <li>Auto-rotate to target angle</li>
 * </ul>
 * 
 * <p>
 * <b>Example Usage:</b>
 * </p>
 * 
 * <p>
 * <b>Example Usage:</b>
 * </p>
 * 
 * <pre>
 * ChassisConfig config = new ChassisConfig(
 *         "MainChassis",
 *         swerveModueles[] swerveModulesConfig,
 *         pigeonConfig,
 * );
 * 
 * Chassis chassis = new Chassis(config);
 * 
 * // Field-relative drive with acceleration limiting
 * chassis.setVelocitiesWithAccel(new ChassisSpeeds(vx, vy, omega));
 * </pre>
 */
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
    private Pigeon gyro;
    private DemaciaKinematics demaciaKinematics;
    private SwerveDriveKinematics wpilibKinematics;

    private Field2d field;

    private StatusSignal<Angle> gyroYawStatus;
    private StatusSignal<AngularVelocity> gyroAngularVelocityStatus;

    private Rotation2d lastGyroYaw;
    private double lastGyroAngularVelocity;

    private final PIDController xController = new PIDController(0.2, 0.0, 0.0);

    private final PIDController yController = new PIDController(0.2, 0.0, 0.0);
    private final PIDController headingController = new PIDController(0.03, 0.0, 0) {
        {
            enableContinuousInput(-Math.PI, Math.PI);
        }
    };
    private int index = 0;

    private boolean isRotateToHub = false;

    Rotation2d gyroAngle;

    OdometryObservation observation;

    private Chassis(ChassisConfig chassisConfig) {
        setName(getName());

        this.chassisConfig = chassisConfig;

        modules = new SwerveModule[4];
        Translation2d[] modulePositions = new Translation2d[4];
        for (int i = 0; i < 4; i++) {
            modules[i] = new SwerveModule(chassisConfig.swerveModuleConfig[i]);
            // modulePositions[i] = chassisConfig.swerveModuleConfig[i].position;
        }

        gyro = new Pigeon(chassisConfig.pigeonConfig);

        addStatus();
        demaciaKinematics = new DemaciaKinematics(modulePositions);
        wpilibKinematics = new SwerveDriveKinematics(modulePositions);
      
        field = new Field2d();
        SmartDashboard.putData("chassis/reset gyro",
                new InstantCommand(() -> setYaw(Rotation2d.kZero)).ignoringDisable(true));
        SmartDashboard.putData("chassis/reset gyro 180",
                new InstantCommand(() -> setYaw(Rotation2d.kPi)).ignoringDisable(true));
        SmartDashboard.putData("chassis/field", field);
        // SmartDashboard.putData("chassis/quest field", questField);
        // SmartDashboard.putData("chassis/tags field", tagsField);
        SmartDashboard.putData("chassis/set coast",
                new InstantCommand(() -> setNeutralMode(false)).ignoringDisable(true));
        SmartDashboard.putData("chassis/set brake",
                new InstantCommand(() -> setNeutralMode(true)).ignoringDisable(true));

        RobotPose.initialize(modulePositions, new Matrix<>(
                new SimpleMatrix(
                        new double[] { 0.03, 0.03, 0 })),
                VisionConstants.QUEST_STD);

        SmartDashboard.putData("reset with 3d",
                new InstantCommand(() -> RobotPose.getInstance().setAngle3DLimelight()).ignoringDisable(true));

        headingController.enableContinuousInput(-Math.PI, Math.PI);

        LogManager.log(chassisConfig.name + " initalize");
    }

    public void followTrajectory(SwerveSample sample) {


        Pose2d pose = getPose();

        ChassisSpeeds speeds = new ChassisSpeeds(
                sample.vx + xController.calculate(pose.getX(), sample.x),
                sample.vy + yController.calculate(pose.getY(), sample.y),
                -sample.omega + headingController.calculate(pose.getRotation().getRadians(), -sample.heading));


        

        SmartDashboard.putNumber("traj/current heading", pose.getRotation().getDegrees());
        SmartDashboard.putNumber("traj/heading error", sample.heading - pose.getRotation().getRadians());
        SmartDashboard.putNumber("traj/speeds omega", speeds.omegaRadiansPerSecond);
        SmartDashboard.putNumber("traj/sample time", sample.getTimestamp());

        field.getObject("trajectory point #" + index).setPose(sample.getPose());
        index++;

        setVelocities(speeds);
    }

    public void resetTrajectory() {
        for (int i = index; i >= 0; i--) {
            field.getObject("trajectory point #" + i).setPose(Pose2d.kZero);
        }
        index = 0;
    }

    public void setDrivePower(double pow, int id) {
        modules[id].setDrivePower(pow);
    }

    public void setDrivePower(double pow) {
        for (int i = 0; i < 4; i++)
            setDrivePower(pow, i);
    }

    public double getMaxDriveVelocity(){
        return chassisConfig.maxDriveVelocity;
    }

    public double getMaxRotationalVelocity(){
        return chassisConfig.maxRotationalVelocity;
    }

    /**
     * Checks all module electronics for faults and logs them.
     */
    public void checkElectronics() {
        for (SwerveModule module : modules) {
            module.checkElectronics();
        }
    }

    /**
     * Sets neutral mode (brake/coast) for all modules.
     * 
     * @param isBrake true for brake mode, false for coast
     */
    public void setNeutralMode(boolean isBrake) {
        for (SwerveModule module : modules) {
            module.setNeutralMode(isBrake);
        }
    }

    public void resetPose(Pose2d pose) {
        RobotPose.getInstance().resetPose(pose);
    }

    /**
     * Gets the current estimated robot pose on the field.
     * 
     * @return Current pose (position and rotation) using odometry fusion
     */
    public Pose2d getPose() {
        return RobotPose.getInstance().getPose();
    }

    public Pose2d getPoseWithVelocity(double dt) {
        Pose2d currentPose = getPose();
        ChassisSpeeds currentSpeeds = getChassisSpeedsFieldRel();
        return new Pose2d(currentPose.getX() + (currentSpeeds.vxMetersPerSecond * dt),
                currentPose.getY() + (currentSpeeds.vyMetersPerSecond * dt),
                currentPose.getRotation().plus(new Rotation2d(currentSpeeds.omegaRadiansPerSecond * dt)));
    }

    public void setRotateToHub() {
        this.isRotateToHub = !isRotateToHub;
    }

    /**
     * Sets chassis velocities without acceleration limiting.
     * 
     * <p>
     * Applies discrete kinematics for accurate odometry.
     * Use this for precise path following where acceleration is pre-profiled.
     * </p>
     * 
     * @param speeds Desired chassis speeds (field-relative)
     */

    public void setVelocities(ChassisSpeeds speeds) {

        SwerveModuleState[] states = demaciaKinematics.toSwerveModuleStates(speeds);

        setModuleStates(states);
    }

    public Translation2d getVelocityAsVector() {
        return new Translation2d(getChassisSpeedsFieldRel().vxMetersPerSecond,
                getChassisSpeedsFieldRel().vyMetersPerSecond);
    }

    /**
     * Sets robot-relative velocities with acceleration limiting.
     * 
     * <p>
     * Useful for manual control where joystick inputs are in robot frame.
     * </p>
     * <p>
     * Useful for manual control where joystick inputs are in robot frame.
     * </p>
     * 
     * @param speeds Desired chassis speeds (robot-relative)
     */
    public void setRobotRelSpeedsWithAccel(ChassisSpeeds speeds) {
        ChassisSpeeds fieldSpeeds = ChassisSpeeds.fromRobotRelativeSpeeds(speeds, getGyroAngle());
        setVelocities(fieldSpeeds);
    }

    public void setSteerPositions(double[] positions) {
        for (int i = 0; i < positions.length; i++) {
            modules[i].setSteerPosition(positions[i]);
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
        setSteerPositions(new double[] { position, position, position, position });
    }

    public ChassisSpeeds getRobotRelVelocities() {
        return ChassisSpeeds.fromFieldRelativeSpeeds(getChassisSpeedsFieldRel(), getGyroAngle());
    }

    public void setRobotRelVelocities(ChassisSpeeds speeds) {
        SwerveModuleState[] states = wpilibKinematics.toSwerveModuleStates(speeds);
        setModuleStates(states);
    }

    public void setDriveVelocities(double[] velocities) {
        for (int i = 0; i < velocities.length; i++) {
            modules[i].setDriveVelocity(velocities[i]);
        }
    }

    public void setDriveVelocities(double velocity) {
        setDriveVelocities(new double[] { velocity, velocity, velocity, velocity });
    }

    public Rotation2d getGyroAngle() {
        gyroYawStatus.refresh();
        if (gyroYawStatus.getStatus() == StatusCode.OK) {
            lastGyroYaw = new Rotation2d(gyroYawStatus.getValue());
        }
        return lastGyroYaw;
    }

    public double getGyroAngularVelocity() {
        gyroAngularVelocityStatus.refresh();
        if (gyroAngularVelocityStatus.getStatus() == StatusCode.OK) {
            lastGyroAngularVelocity = gyroAngularVelocityStatus.getValue().in(Units.RadiansPerSecond);
        }
        return lastGyroAngularVelocity;
    }

    public void setModuleState(SwerveModuleState state) {
        setModuleStates(new SwerveModuleState[] { state, state, state, state });
    }

    public void setModuleStates(SwerveModuleState[] states) {
        for (int i = 0; i < states.length; i++) {
            modules[i].setState(states[i]);
        }
    }

    @Override
    public void periodic() {
        // updateCommon();
        //TODO: RETORN IT 
        observation = new OdometryObservation(
                Timer.getFPGATimestamp(),
                getGyroAngle(),
                getModulePositions());

        RobotPose.getInstance().update(observation);
        field.setRobotPose(getPose());
        // field.getObject("Turret").setPose(new Pose2d(RobotCommon.getCurrentRobotPose().getTranslation()
        //         .plus(TurretConstants.TURRET_POSITION_ON_ROBOT.rotateBy(RobotCommon.getRobotAngle())),
        //         Rotation2d.fromRadians(RobotCommon.getRobotAngle().getRadians()
        //                 + MathUtil.angleModulus(Turret.getInstance().getTurretAngle()))));
        // field.getObject("estimation").setPose(ShooterUtils.computeFuturePosition(getChassisSpeedsFieldRel(), getPose(), 0.1));
    }

    // public void updateCommon() {
    //     RobotCommon.setRobotAngle(getGyroAngle());
    //     RobotCommon.setCurrentRobotPose(getPose());
    //     RobotCommon.setFieldRelativeSpeeds(getChassisSpeedsFieldRel());
    //     RobotCommon.setFutureRobotPose(getFuturePose(0.2));
    // } 
    //TODO: reotrn it

    public Pose2d getFuturePose(double dtSeconds) {
        return getPose().exp(new Twist2d(
                (getChassisSpeedsFieldRel().vxMetersPerSecond * dtSeconds),
                (getChassisSpeedsFieldRel().vyMetersPerSecond * dtSeconds),
                getChassisSpeedsFieldRel().omegaRadiansPerSecond * dtSeconds));
    }

    /**
     * Gets the current chassis speeds in robot-relative frame.
     * 
     * @return Current velocities in robot frame
     */
    public ChassisSpeeds getChassisSpeedsRobotRel() {
        return demaciaKinematics.toChassisSpeeds(
                getModuleStates(),
                Math.toRadians(gyroYawStatus.getValueAsDouble()));
    }

    /**
     * Gets the current chassis speeds in field-relative frame.
     * 
     * @return Current velocities transformed to field frame
     */
    public ChassisSpeeds getChassisSpeedsFieldRel() {
        return ChassisSpeeds.fromRobotRelativeSpeeds(
                demaciaKinematics.toChassisSpeeds(getModuleStates(),
                        getGyroAngularVelocity()),
                getGyroAngle());
    }

    public Translation2d getChassisSpeedsVector() {
        ChassisSpeeds s = getChassisSpeedsFieldRel();
        return new Translation2d(s.vxMetersPerSecond, s.vyMetersPerSecond);
    }

    /**
     * Returns the state of every module
     * 
     * 
     * @return Velocity in m/s, angle in Rotation2d
     */
    public SwerveModuleState[] getModuleStates() {
        SwerveModuleState[] res = new SwerveModuleState[modules.length];
        for (int i = 0; i < modules.length; i++) {
            res[i] = modules[i].getState();
        }
        return res;
    }

    /**
     * Sets the gyro yaw angle (for field-relative reset).
     * 
     * <p>
     * Call this at the start of autonomous to set known field orientation.
     * </p>
     * 
     * @param angle New yaw angle (null to skip)
     */
    public void setYaw(Rotation2d angle) {
        if (angle != null) {
            gyro.setYaw(angle.getDegrees());
            RobotPose.getInstance().setQuestHeading(angle);
            RobotPose.getInstance()
                    .resetPose(
                            new Pose2d(Translation2d.kZero, gyro.getRotation2d()));
        }
    }

    public ChassisConfig getConfig() {
        return chassisConfig;
    }

    /**
     * Stops all swerve modules immediately.
     */
    public void stop() {
        for (SwerveModule i : modules) {
            i.stop();
        }
    }

    private void addStatus() {
        gyroYawStatus = gyro.getYaw();
        lastGyroYaw = new Rotation2d(gyroYawStatus.getValueAsDouble());
        gyroAngularVelocityStatus = gyro.getAngularVelocityZWorld();
        lastGyroAngularVelocity = gyroAngularVelocityStatus.getValue().in(Units.RadiansPerSecond);
    }

    private SwerveModulePosition[] getModulePositions() {
        SwerveModulePosition[] arr = new SwerveModulePosition[modules.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = modules[i].getModulePosition();
        }
        return arr;
    }
}
