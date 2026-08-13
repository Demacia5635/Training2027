package frc.demacia.utils.chassis;

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

    public String name;             // Name of the motor - used for logging

    public BaseMotorConfig<?> steerConfig;
    public BaseMotorConfig<?> driveConfig;
    public CancoderConfig cancoderConfig;
    public double steerOffset;

    public double position; 

    public double SteerVelToDriveVel;

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

    public SwerveModuleConfig withDrivePowerToSteerPower(double drivePowerToSteerPower){
        this.SteerVelToDriveVel = drivePowerToSteerPower;
        return this;
    }
}
