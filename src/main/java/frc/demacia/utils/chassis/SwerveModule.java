package frc.demacia.utils.chassis;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import frc.demacia.utils.motors.MotorInterface;
import frc.demacia.utils.sensors.Cancoder;

/**
 * Individual swerve module controller.
 * 
 * <p>Manages one corner of the swerve drive: steer motor, drive motor, and absolute encoder.</p>
 * 
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Automatic optimization (shortest path to target angle)</li>
 *   <li>Absolute encoder integration for zero-retention</li>
 *   <li>Separate control of steer and drive</li>
 * </ul>
 * 
 * <p><b>Angle Optimization:</b> When commanded to rotate >90°, the module will
 * reverse drive direction and rotate <90° instead for faster response.</p>
 */
public class SwerveModule {
    public String name;
    private SwerveModuleConfig config;
    private MotorInterface steerMotor;
    private MotorInterface driveMotor;
    private Cancoder cancoder;

    public SwerveModule(SwerveModuleConfig config) {
        this.config = config;
        steerMotor = config.steerConfig.getMotorClass().create(config.steerConfig);
        driveMotor = config.driveConfig.getMotorClass().create(config.driveConfig);
        cancoder = new Cancoder(config.cancoderConfig);
        name = config.name;

        steerMotor.setEncoderPosition(getAbsoluteAngle() - config.steerOffset);
    }

    /**
     * Checks electronics for both motors and encoder.
     */
    public void checkElectronics() {
        driveMotor.checkElectronics();
        steerMotor.checkElectronics();
        cancoder.checkElectronics();
    }

    public void setNeutralMode(boolean isBrake) {
        driveMotor.setNeutralMode(isBrake);
        steerMotor.setNeutralMode(isBrake);
    }

    public Cancoder getCancoder() {
        return cancoder;
    }

    /**
     * Gets the absolute encoder angle (for initialization).
     * 
     * @return Absolute angle in radians
     */
    public double getAbsoluteAngle() {
        return cancoder.getCurrentAbsPosition();
    }
    
    public void resetModule() {
        steerMotor.setEncoderPosition(0);
    }

    public void setSteerPower(double power) {
        steerMotor.setDuty(power);
    }

    /**
     * Sets the steer motor to a target position (radians).
     * 
     * @param positionRadians Target angle in radians
     */
    public void setSteerPosition(double positionRadians) {
        if(Math.abs(positionRadians - steerMotor.getCurrentPosition()) <= Math.toRadians(0.5) ) steerMotor.setDuty(0);
        steerMotor.setPositionVoltage(positionRadians);
    }

    public void setDrivePower(double power) {
        driveMotor.setDuty(power);
    }

    /**
     * Sets the drive motor to a target velocity (m/s).
     * 
     * @param velocityMetersPerSecond Target velocity
     */
    public void setDriveVelocity(double velocityMetersPerSecond) {
        driveMotor.setVelocity(velocityMetersPerSecond);
    }

    /**
     * Sets the desired state for this module (velocity and angle).
     * 
     * <p>Automatically optimizes the state to minimize rotation.
     * If target angle is >90° away, reverses drive direction.</p>
     * 
     * @param state Target state with speed (m/s) and angle (Rotation2d)
     */
    public void setState(SwerveModuleState state) {
        double wantedAngle = state.angle.getRadians();
        double diff = wantedAngle - steerMotor.getCurrentPosition();
        double vel = state.speedMetersPerSecond;
        diff = MathUtil.angleModulus(diff);
        if(diff > 0.5 * Math.PI) {
            vel = -vel;
            diff = diff-Math.PI;
        } else if(diff < -0.5 * Math.PI) {
            vel = -vel;
            diff = diff + Math.PI;
        }

        if (Math.abs(diff) <= Math.toRadians(0.7)) {
            setSteerPower(0);
        } else {
            setSteerPosition(steerMotor.getCurrentPosition() + diff);
        }

        if (vel == 0) {
            setDrivePower(0);
        } else {
            setDriveVelocity(vel - steerMotor.getCurrentVelocity() * config.steerVelToDriveVel);
        }
    }

    public double getSteerAngle() {
        return steerMotor.getCurrentAngle();
    }
    
    public Rotation2d getSteerRotation() {
        return new Rotation2d(getSteerAngle());
    }

    public double getSteerVel() {
        return steerMotor.getCurrentVelocity();
    }

    public double getSteerAccel() {
        return steerMotor.getCurrentAcceleration();
    }

    public double getDriveVel() {
        return driveMotor.getCurrentVelocity();
    }

    /**
     * Gets the module position for odometry.
     * 
     * @return Current drive position (meters) and steer angle
     */
    public SwerveModulePosition getModulePosition() {
        return new SwerveModulePosition(driveMotor.getCurrentPosition(), Rotation2d.fromRadians(steerMotor.getCurrentPosition()));
    }

    /**
     * Gets the current state of the module.
     * 
     * @return Current velocity (m/s) and angle (Rotation2d)
     */
    public SwerveModuleState getState() {
        return new SwerveModuleState(getDriveVel(), getSteerRotation());
    }

    /**
     * Stops both motors immediately.
     */
    public void stop() {
        steerMotor.stop();
        driveMotor.stop();
    }
}