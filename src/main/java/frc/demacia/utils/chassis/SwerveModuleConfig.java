package frc.demacia.utils.chassis;

import edu.wpi.first.math.geometry.Translation2d;
import frc.demacia.utils.motors.BaseMotorConfig;
import frc.demacia.utils.sensors.CancoderConfig;

/**
 * Configuration for a single swerve module.
 * 
 * <p>Contains motor configurations and encoder offset.</p>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * SwerveModuleConfig frontLeft = new SwerveModuleConfig(
 *     "FrontLeft",
 *     steerMotorConfig,
 *     driveMotorConfig,
 *     cancoderConfig
 * ).withSteerOffset(0.123);  // Calibrated offset in radians
 * </pre>
 */
public class SwerveModuleConfig {

    public final String name;             // Name of the motor - used for logging

    public final BaseMotorConfig<?> steerConfig;
    public final BaseMotorConfig<?> driveConfig;
    public final CancoderConfig cancoderConfig;
    public double steerOffset = 0;

    public Translation2d position = Translation2d.kZero;

    public double steerVelToDriveVel = 0;

        /**
     * Constructor
     * @param id - CAN bus ID
     * @param name - name of motor for logging
     */
    public SwerveModuleConfig(String name, BaseMotorConfig<?> steerConfig, BaseMotorConfig<?> driveConfig, CancoderConfig cancoderConfig) {
        this.name = name;
        this.steerConfig = steerConfig;
        this.driveConfig = driveConfig;
        this.cancoderConfig = cancoderConfig;
    }
    
    /**
     * Sets the steer encoder offset.
     * 
     * <p>This is the absolute encoder reading when the wheel is pointing straight forward.
     * Calibrate by manually aligning wheels and recording encoder values.</p>
     * 
     * @param steerOffset Offset in radians
     * @return this config for chaining
     */
    public SwerveModuleConfig withSteerOffset(double steerOffset) {
        this.steerOffset = steerOffset;
        return this;
    }

    public SwerveModuleConfig withPosion(Translation2d position) {
        this.position = position;
        return this;
    }

    public SwerveModuleConfig withDrivePowerToSteerPower(double drivePowerToSteerPower){
        this.steerVelToDriveVel = drivePowerToSteerPower;
        return this;
    }
}