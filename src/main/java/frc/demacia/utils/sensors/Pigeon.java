package frc.demacia.utils.sensors;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.configs.Pigeon2Configuration;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.demacia.utils.Data;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import java.util.function.Supplier;

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

    Data<Angle> yawSignal;
    Data<Angle> pitchSignal;
    Data<Angle> rollSignal;
    Data<AngularVelocity> xVelocitySignal;
    Data<AngularVelocity> yVelocitySignal;
    Data<AngularVelocity> zVelocitySignal;
    Data<LinearAcceleration> xAccelerationSignal;
    Data<LinearAcceleration> yAccelerationSignal;
    Data<LinearAcceleration> zAccelerationSignal;

    double lastXVelocity;
    double lastXTimestamp;
    double lastXAcceleration;
    double lastYVelocity;
    double lastYTimestamp;
    double lastYAcceleration;
    double lastZVelocity;
    double lastZTimestamp;
    double lastZAcceleration;

    /**
     * Creates a Pigeon2 IMU.
     * 
     * @param config Configuration with CAN ID, bus, and calibration
     */
    public Pigeon(PigeonConfig config){
        super(config.id, config.canbus.canbus);
        this.config = config;
        name = config.name;
        setName(name);
        configPigeon();
        setStatusSignals();
        addLog();
        SmartDashboard.putData("sensors/" + config.name, this);
		Log.log(name + " pigeon initialized");
        ElasticGenerator.getInstance().registerSensor(this);
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
        yawSignal = new Data<> (getYaw(), config.canbus.equals(Canbus.Rio));
        pitchSignal = new Data<> (getPitch(), config.canbus.equals(Canbus.Rio));
        rollSignal = new Data<> (getRoll(), config.canbus.equals(Canbus.Rio));
        xVelocitySignal = new Data<> (getAngularVelocityXWorld(), config.canbus.equals(Canbus.Rio));
        yVelocitySignal = new Data<> (getAngularVelocityYWorld(), config.canbus.equals(Canbus.Rio));
        zVelocitySignal = new Data<> (getAngularVelocityZWorld(), config.canbus.equals(Canbus.Rio));
        xAccelerationSignal = new Data<> (getAccelerationX(), config.canbus.equals(Canbus.Rio));
        yAccelerationSignal = new Data<> (getAccelerationY(), config.canbus.equals(Canbus.Rio));
        zAccelerationSignal = new Data<> (getAccelerationZ(), config.canbus.equals(Canbus.Rio));

        lastXVelocity = Math.toRadians(xVelocitySignal.getDouble());
        lastYVelocity = Math.toRadians(yVelocitySignal.getDouble());
        lastZVelocity = Math.toRadians(zVelocitySignal.getDouble());
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
            Log.log(name + " have a fault: " + getFaultField().getValue());
        }
    }

    @SuppressWarnings({"unchecked"})
    private void addLog() {
        Log.putData(name + ": yaw, pitch, roll", 
            new Data[]{
                yawSignal,
                pitchSignal,
                rollSignal
            }
            , LogLevel.LOG_ONLY, "sensors", false);
            
        Log.putData(name + ": is Connected", 
            new Supplier[]{
                this::isConnected
            }
            , LogLevel.LOG_ONLY, "sensors", false);
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
        return Math.toRadians(getCurrentYawDegree());
    }

    /**
     * Gets yaw normalized to 0-2π range.
     * 
     * @return Yaw angle in radians (0 to 2π)
     */
    public double getYawInZeroTo2Pi() {
        return (getCurrentYaw() % (2* Math.PI) + (2* Math.PI)) % (2* Math.PI);
    }

    
    public double getCurrentYawDegree() {
        return yawSignal.getDouble();
    }

    /**
     * Gets the current pitch angle (forward/back tilt).
     * 
     * @return Pitch angle in radians
     */
    public double getCurrentPitch() {
        return Math.toRadians(pitchSignal.getDouble());
    }

    /**
     * Gets pitch normalized to 0-2π range.
     * 
     * @return Pitch angle in radians (0 to 2π)
     */
    public double getPitchInZeroTo2Pi() {
        return (getCurrentPitch() % (2 * Math.PI) + (2 * Math.PI)) % (2 * Math.PI);
    }

    /**
     * Gets the current roll angle (left/right tilt).
     * 
     * @return Roll angle in radians
     */
    public double getCurrentRoll() {
        return Math.toRadians(rollSignal.getDouble());
    }

    /**
     * Gets roll normalized to 0-2π range.
     * 
     * @return Roll angle in radians (0 to 2π)
     */
    public double getRollInZeroTo2Pi() {
        return (getCurrentRoll() % (2 * Math.PI) + (2 * Math.PI)) % (2 * Math.PI);
    }

    /**
     * Gets X-axis angular velocity (pitch rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getXVelocity() {
        return Math.toRadians(xVelocitySignal.getDouble());
    }

    /**
     * Gets Y-axis angular velocity (roll rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getYVelocity() {
        return Math.toRadians(yVelocitySignal.getDouble());
    }

    /**
     * Gets Z-axis angular velocity (yaw rate).
     * 
     * @return Angular velocity in radians per second
     */
    public double getZVelocity() {
        return Math.toRadians(zVelocitySignal.getDouble());
    }

    /**
     * Gets X-axis linear acceleration.
     * 
     * @return Acceleration in m/s²
     */
    public double getXAcceleration() {
       return xAccelerationSignal.getDouble() * 9.81;
    }

    /**
     * Gets Y-axis linear acceleration.
     * 
     * @return Acceleration in m/s²
     */
    public double getYAcceleration() {
        return yAccelerationSignal.getDouble() * 9.81;
    }

    /**
     * Gets Z-axis linear acceleration (includes gravity).
     * 
     * @return Acceleration in m/s² (9.8 when stationary)
     */
    public double getZAcceleration() {
        return zAccelerationSignal.getDouble() * 9.81;
    }

    /**
     * Gets X-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getXAngularAcceleration() {
        double currentTimestamp = Timer.getFPGATimestamp();
        double dt = currentTimestamp - lastXTimestamp;
        
        if (dt < 0.001) {
            return lastXAcceleration;
        }
        
        if (lastXTimestamp != 0) {
            lastXAcceleration = (getXVelocity() - lastXVelocity) / dt;
        }
        
        lastXVelocity = getXVelocity();
        lastXTimestamp = currentTimestamp;
        
        return lastXAcceleration;
    }

    /**
     * Gets Y-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getYAngularAcceleration() {
        double currentTimestamp = Timer.getFPGATimestamp();
        double dt = currentTimestamp - lastYTimestamp;
        
        if (dt < 0.001) {
            return lastYAcceleration;
        }
        
        if (lastYTimestamp != 0) {
            lastYAcceleration = (getYVelocity() - lastYVelocity) / dt;
        }
        
        lastYVelocity = getYVelocity();
        lastYTimestamp = currentTimestamp;
        
        return lastYAcceleration;
    }

    /**
     * Gets Z-axis angular acceleration (calculated).
     * 
     * @return Angular acceleration in rad/s²
     */
    public double getZAngularAcceleration() {
        double currentTimestamp = Timer.getFPGATimestamp();
        double dt = currentTimestamp - lastZTimestamp;
        
        if (dt < 0.001) {
            return lastZAcceleration;
        }
        
        if (lastZTimestamp != 0) {
            lastZAcceleration = (getZVelocity() - lastZVelocity) / dt;
        }
        
        lastZVelocity = getZVelocity();
        lastZTimestamp = currentTimestamp;
        
        return lastZAcceleration;
    }

    public Rotation2d getGyroAngle() {
        return new Rotation2d(getCurrentYaw());
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Gyro");
        builder.addBooleanProperty("is Connected", this::isConnected, null);
        builder.addDoubleProperty("value", this::getCurrentYaw, null);
        builder.addDoubleProperty("yaw", this::getCurrentYaw, null);
        builder.addDoubleProperty("yaw Degree", this::getCurrentYawDegree, null);
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