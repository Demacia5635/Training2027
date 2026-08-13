package frc.demacia.utils.sensors;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.demacia.utils.Data;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

/**
 * CTRE CANcoder absolute magnetic encoder wrapper.
 * 
 * <p>
 * Provides access to CANcoder position, velocity, and acceleration with:
 * </p>
 * <ul>
 * <li>Automatic unit conversion (rotations → radians)</li>
 * <li>Offset calibration support</li>
 * <li>Automatic logging of telemetry</li>
 * <li>Fault monitoring</li>
 * </ul>
 * 
 * <p>
 * <b>Key Features:</b>
 * </p>
 * <ul>
 * <li>Absolute position (survives power cycles)</li>
 * <li>±0.5° accuracy typical</li>
 * <li>Up to 100Hz update rate</li>
 * </ul>
 * 
 * <p>
 * <b>Example Usage:</b>
 * </p>
 * 
 * <pre>
 * CancoderConfig config = new CancoderConfig(10, CANBus.CANivore, "SteerEncoder")
 *         .withOffset(0.245) // Calibrated offset
 *         .withInvert(false);
 * 
 * Cancoder encoder = new Cancoder(config);
 * 
 * // Read position
 * double angle = encoder.getCurrentAbsPosition(); // Radians
 * 
 * // Use in swerve module
 * steerMotor.setPosition(encoder.getCurrentAbsPosition() - offset);
 * </pre>
 */
public class Cancoder extends CANcoder implements AnalogSensorInterface {

    CancoderConfig config;
    String name;

    StatusSignal<Angle> positionSignal;
    StatusSignal<Angle> absPositionSignal;
    StatusSignal<AngularVelocity> velocitySignal;

    double lastPosition;
    double lastAbsPosition;
    double lastVelocity;

    /**
     * Creates a CANcoder sensor.
     * 
     * @param config Configuration with CAN ID, bus, and calibration
     */
    public Cancoder(CancoderConfig config) {
        super(config.id, config.canbus);
        this.config = config;
        name = config.name;
        setName(name);
        configCancoder();
        setStatusSignals();
        addLog();
        LogManager.log(name + " cancoder initialized");
    }

    private void configCancoder() {
        CANcoderConfiguration canConfig = new CANcoderConfiguration();
        canConfig.MagnetSensor.MagnetOffset = config.offset;
        canConfig.MagnetSensor.SensorDirection = config.isInverted ? SensorDirectionValue.Clockwise_Positive
                : SensorDirectionValue.CounterClockwise_Positive;
        getConfigurator().apply(canConfig);
    }

    @Override
    public void setName(String name) {
        AnalogSensorInterface.super.setName(name);
        this.name = name;
    }

    private void setStatusSignals() {
        positionSignal = getPosition();
        absPositionSignal = getAbsolutePosition();
        velocitySignal = getVelocity();

        lastPosition = positionSignal.getValueAsDouble();
        lastAbsPosition = absPositionSignal.getValueAsDouble();
        lastVelocity = velocitySignal.getValueAsDouble();
    }

    /**
     * Checks for CANcoder faults and logs them.
     * 
     * <p>
     * Call periodically to catch:
     * </p>
     * <ul>
     * <li>Magnet not detected</li>
     * <li>CAN bus errors</li>
     * <li>Hardware failures</li>
     * </ul>
     */
    public void checkElectronics() {
        if (getFaultField().getValue() != 0) {
            LogManager.log(name + " have a fault: " + getFaultField().getValue());
        }
    }

    @SuppressWarnings({ "unchecked", "unlikely-arg-type" })
    private void addLog() {
        Data.addSignals(config.canbus.equals(Canbus.Rio), absPositionSignal);
        LogManager.addEntry(name + ": abs Position",
                () -> absPositionSignal.getValueAsDouble() * 2 * Math.PI).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP)
                .build();
    }

    /**
     * Gets the sensor name.
     * 
     * @return Sensor name from configuration
     */
    public String getName() {
        return config.name;
    }

    /**
     * Gets current position (implements AnalogSensorInterface).
     * 
     * @return Current relative position in radians
     */
    public double get() {
        return getCurrentPosition();
    }

    /**
     * Gets the relative position since power-on.
     * 
     * <p>
     * Starts at absolute position on boot, then tracks changes.
     * Can exceed 2π for continuous rotation tracking.
     * </p>
     * 
     * @return Relative position in radians (can be > 2π)
     */
    public double getCurrentPosition() {
        lastPosition = StatusSignalHelper.getStatusSignalWith2Pi(positionSignal, lastPosition);
        return lastPosition;
    }

    /**
     * Gets the absolute position (persists through power cycles).
     * 
     * <p>
     * This is the primary method for reading CANcoder position.
     * Value is in radians and includes configured offset.
     * </p>
     * 
     * @return Absolute position in radians (0 to 2π)
     */
    public double getCurrentAbsPosition() {
        lastAbsPosition = StatusSignalHelper.getStatusSignalWith2Pi(absPositionSignal, lastAbsPosition);
        return lastAbsPosition;
    }

    /**
     * Gets the current velocity.
     * 
     * @return Velocity in radians per second
     */
    public double getCurrentVelocity() {
        lastVelocity = StatusSignalHelper.getStatusSignalWith2Pi(velocitySignal, lastVelocity);
        return lastVelocity;
    }

    /**
     * Gets the current acceleration (calculated from velocity change).
     * 
     * @return Acceleration in radians per second²
     */
    public double getCurrentAcceleration() {
        velocitySignal.refresh();
        if (velocitySignal.getStatus() == StatusCode.OK) {
            return (velocitySignal.getValueAsDouble() * 2 * Math.PI) - lastVelocity;
        }
        return 0;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("CANcoder");
        builder.addDoubleProperty("Abs Position", this::getCurrentAbsPosition, null);
        builder.addDoubleProperty("Position", this::getCurrentPosition, null);
        builder.addDoubleProperty("Velocity", this::getCurrentVelocity, null);
        builder.addDoubleProperty("Acceleration", this::getCurrentAcceleration, null);
    }
}