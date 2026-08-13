package frc.demacia.utils.sensors;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.configs.Pigeon2Configuration;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.demacia.utils.Data;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

import com.ctre.phoenix6.StatusSignal;

/**
 * CTRE Pigeon2 IMU (Inertial Measurement Unit) wrapper.
 * 
 * <p>Provides access to 3-axis gyroscope and accelerometer data with:</p>
 * <ul>
 *   <li>Automatic unit conversion (degrees → radians)</li>
 *   <li>Yaw, pitch, roll measurements</li>
 *   <li>Angular velocities and accelerations</li>
 *   <li>Automatic logging</li>
 *   <li>Fault monitoring</li>
 * </ul>
 * 
 * <p><b>Coordinate System:</b></p>
 * <ul>
 *   <li><b>Yaw:</b> Rotation around vertical axis (heading)</li>
 *   <li><b>Pitch:</b> Forward/backward tilt</li>
 *   <li><b>Roll:</b> Left/right tilt</li>
 * </ul>
 * 
 * <p><b>Example Usage:</b></p>
 * <pre>
 * PigeonConfig config = new PigeonConfig(10, CANBus.CANivore, "MainGyro")
 *     .withYawOffset(0)
 *     .withInvert(false);
 * 
 * Pigeon gyro = new Pigeon(config);
 * 
 * // Get current heading
 * double heading = gyro.getCurrentYaw();  // Radians
 * 
 * // Use for field-relative drive
 * ChassisSpeeds speeds = ChassisSpeeds.fromFieldRelativeSpeeds(
 *     vx, vy, omega,
 *     new Rotation2d(gyro.getCurrentYaw())
 * );
 * </pre>
 */
public class Pigeon extends Pigeon2 implements SensorInterface{
    PigeonConfig config;
    String name;
    Pigeon2Configuration pigeonConfig;

    StatusSignal<Angle> yawSignal;
    StatusSignal<Angle> pitchSignal;
    StatusSignal<Angle> rollSignal;
    StatusSignal<AngularVelocity> xVelocitySignal;
    StatusSignal<AngularVelocity> yVelocitySignal;
    StatusSignal<AngularVelocity> zVelocitySignal;
    StatusSignal<LinearAcceleration> xAccelerationSignal;
    StatusSignal<LinearAcceleration> yAccelerationSignal;
    StatusSignal<LinearAcceleration> zAccelerationSignal;

    double lastYaw;
    double lastPitch;
    double lastRoll;
    double lastXVelocity;
    double lastYVelocity;
    double lastZVelocity;
    double lastXAcceleration;
    double lastYAcceleration;
    double lastZAcceleration;

    /**
     * Creates a Pigeon2 IMU.
     * 
     * @param config Configuration with CAN ID, bus, and calibration
     */
    public Pigeon(PigeonConfig config){
        super(config.id, config.canbus);
        this.config = config;
        name = config.name;
        setName(name);
        configPigeon();
        setStatusSignals();
        addLog();
		LogManager.log(name + " pigeon initialized");
    }

    private void configPigeon() {
        pigeonConfig = new Pigeon2Configuration();
        pigeonConfig.MountPose.MountPosePitch = config.pitchOffset;
        pigeonConfig.MountPose.MountPoseRoll = config.rollOffset;
        pigeonConfig.MountPose.MountPoseYaw = config.yawOffset;
        pigeonConfig.GyroTrim.GyroScalarX = config.isInverted?-config.xScalar:config.xScalar;
        pigeonConfig.GyroTrim.GyroScalarY = config.isInverted?-config.yScalar:config.yScalar;
        pigeonConfig.GyroTrim.GyroScalarZ = config.isInverted?-config.zScalar:config.zScalar;
        pigeonConfig.Pigeon2Features.EnableCompass = config.compass;
        pigeonConfig.Pigeon2Features.DisableTemperatureCompensation = !config.temperatureCompensation;
        pigeonConfig.Pigeon2Features.DisableNoMotionCalibration = config.noMotionCalibration;
        getConfigurator().apply(pigeonConfig);
    }

    private void setStatusSignals(){
        yawSignal = getYaw();
        pitchSignal = getPitch();
        rollSignal = getRoll();
        xVelocitySignal = getAngularVelocityXWorld();
        yVelocitySignal = getAngularVelocityYWorld();
        zVelocitySignal = getAngularVelocityZWorld();
        xAccelerationSignal = getAccelerationX();
        yAccelerationSignal = getAccelerationY();
        zAccelerationSignal = getAccelerationZ();

        lastYaw = yawSignal.getValueAsDouble();
        lastPitch = pitchSignal.getValueAsDouble();
        lastRoll = rollSignal.getValueAsDouble();
        lastXVelocity = xVelocitySignal.getValueAsDouble();
        lastYVelocity = yVelocitySignal.getValueAsDouble();
        lastZVelocity = zVelocitySignal.getValueAsDouble();
        lastXAcceleration = xAccelerationSignal.getValueAsDouble();
        lastYAcceleration = yAccelerationSignal.getValueAsDouble();
        lastZAcceleration = zAccelerationSignal.getValueAsDouble();
    }

    /**
     * Checks for Pigeon2 faults and logs them.
     * 
     * <p>Detects:</p>
     * <ul>
     *   <li>Calibration errors</li>
     *   <li>CAN bus issues</li>
     *   <li>Hardware failures</li>
     * </ul>
     */
    public void checkElectronics() {
        if (getFaultField().getValue() != 0) {
            LogManager.log(name + " have a fault: " + getFaultField().getValue());
        }
    }

    @SuppressWarnings({ "unchecked", "unlikely-arg-type" })
    private void addLog() {
        Data.addSignals(config.canbus.equals(Canbus.Rio), yawSignal, pitchSignal, rollSignal);
        LogManager.addEntry(name + ": yaw, pitch, roll",
            () -> yawSignal.getValueAsDouble() * 2 * Math.PI,
            () -> pitchSignal.getValueAsDouble() * 2 * Math.PI,
            () -> rollSignal.getValueAsDouble() * 2 * Math.PI
        ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
    }

    /**
     * Gets the sensor name.
     * 
     * @return Sensor name from configuration
     */
    public String getName(){
        return config.name;
    }

    /**
     * Gets the current yaw (heading) angle.
     * 
     * <p>Most commonly used for robot heading in swerve drive.
     * Returns angle in radians, can be positive or negative.</p>
     * 
     * @return Yaw angle in radians (unbounded)
     */
    public double getCurrentYaw() {
        lastYaw = StatusSignalHelper.getStatusSignalInRad(yawSignal, lastYaw);
        return lastYaw;
    }

    /**
     * Gets yaw normalized to 0-2π range.
     * 
     * @return Yaw angle in radians (0 to 2π)
     */
    public double getYawInZeroTo2Pi() {
        return (getCurrentYaw()% (2* Math.PI) + (2* Math.PI)) % (2* Math.PI);
    }

    /**
     * Gets the current pitch angle (forward/back tilt).
     * 
     * @return Pitch angle in radians
     */
    public double getCurrentPitch() {
        lastPitch = StatusSignalHelper.getStatusSignalInRad(pitchSignal, lastPitch);
        return lastPitch;
    }

    /**
     * Gets pitch normalized to 0-2π range.
     * 
     * @return Pitch angle in radians (0 to 2π)
     */
    public double getPitchInZeroTo2Pi() {
        return (getCurrentPitch() % (2* Math.PI) + (2* Math.PI)) % (2* Math.PI);
    }

    /**
     * Gets the current roll angle (left/right tilt).
     * 
     * @return Roll angle in radians
     */
    public double getCurrentRoll() {
        lastRoll = StatusSignalHelper.getStatusSignalInRad(rollSignal, lastRoll);
        return lastRoll;
    }

    /**
     * Gets roll normalized to 0-2π range.
     * 
     * @return Roll angle in radians (0 to 2π)
     */
    public double getRollInZeroTo2Pi() {
        return (getCurrentRoll()% (2* Math.PI) + (2* Math.PI)) % (2* Math.PI);
    }

    /**
     * Gets X-axis angular velocity (pitch rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getXVelocity() {
        lastXVelocity = StatusSignalHelper.getStatusSignalBasic(xVelocitySignal, lastXVelocity);
        return lastXVelocity;
    }

    /**
     * Gets Y-axis angular velocity (roll rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getYVelocity() {
        lastYVelocity = StatusSignalHelper.getStatusSignalBasic(yVelocitySignal, lastYVelocity);
        return lastYVelocity;
    }

    /**
     * Gets Z-axis angular velocity (yaw rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getZVelocity() {
        lastZVelocity = StatusSignalHelper.getStatusSignalBasic(zVelocitySignal, lastZVelocity);
        return lastZVelocity;
    }

    /**
     * Gets X-axis linear acceleration.
     * 
     * @return Acceleration in m/s²
     */
    public double getXAcceleration() {
        lastXAcceleration = StatusSignalHelper.getStatusSignalBasic(xAccelerationSignal, lastXAcceleration);
        return lastXAcceleration;
    }

    /**
     * Gets Y-axis linear acceleration.
     * 
     * @return Acceleration in m/s²
     */
    public double getYAcceleration() {
        lastYAcceleration = StatusSignalHelper.getStatusSignalBasic(yAccelerationSignal, lastYAcceleration);
        return lastYAcceleration;
    }

    /**
     * Gets Z-axis linear acceleration (includes gravity).
     * 
     * @return Acceleration in m/s² (9.8 when stationary)
     */
    public double getZAcceleration() {
        lastZAcceleration = StatusSignalHelper.getStatusSignalBasic(zAccelerationSignal, lastZAcceleration);
        return lastZAcceleration;
    }

    /**
     * Gets X-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getXAngularAcceleration() {
        double acceleration = (StatusSignalHelper.getStatusSignalBasic(xVelocitySignal, lastXVelocity)) - lastXVelocity;
        lastXVelocity = xVelocitySignal.getValueAsDouble();
        return acceleration;
    }

    /**
     * Gets Y-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getYAngularAcceleration() {
        double acceleration = (StatusSignalHelper.getStatusSignalBasic(yVelocitySignal, lastYVelocity)) - lastYVelocity;
        lastYVelocity = yVelocitySignal.getValueAsDouble();
        return acceleration;
    }

    /**
     * Gets Z-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getZAngularAcceleration() {
        double acceleration = (StatusSignalHelper.getStatusSignalBasic(zVelocitySignal, lastZVelocity)) - lastZVelocity;
        lastZVelocity = zVelocitySignal.getValueAsDouble();
        return acceleration;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Gyro");
        builder.addDoubleProperty("yaw", this::getCurrentYaw, null);
        builder.addDoubleProperty("pitch", this::getCurrentPitch, null);
        builder.addDoubleProperty("roll", this::getCurrentRoll, null);
        builder.addDoubleProperty("x velocity", this::getXVelocity, null);
        builder.addDoubleProperty("y velocity", this::getYVelocity, null);
        builder.addDoubleProperty("z velocity", this::getZVelocity, null);
        builder.addDoubleProperty("x acceleration", this::getXAcceleration, null);
        builder.addDoubleProperty("y acceleration", this::getYAcceleration, null);
        builder.addDoubleProperty("z acceleration", this::getZAcceleration, null);
        builder.addDoubleProperty("x angular acceleration", this::getXAngularAcceleration, null);
        builder.addDoubleProperty("y angular acceleration", this::getYAngularAcceleration, null);
        builder.addDoubleProperty("z angular acceleration", this::getZAngularAcceleration, null);
    }
}