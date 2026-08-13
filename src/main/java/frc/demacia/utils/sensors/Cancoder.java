package frc.demacia.utils.sensors;

import java.util.function.Supplier;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.demacia.utils.Data;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

/**
 * CTRE CANcoder absolute magnetic encoder wrapper.
 * 
 * <p>
 * Provides access to CANcoder position, velocity, and acceleration with:
 * </p>
 * <ul>
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

    Data<Angle> positionSignal;
    Data<Angle> absPositionSignal;
    Data<AngularVelocity> velocitySignal;

    double lastVelocity;
    double lastTimestamp;
    double lastAcceleration;

    /**
     * Creates a CANcoder sensor.
     * 
     * @param config Configuration with CAN ID, bus, and calibration
     */
    public Cancoder(CancoderConfig config) {
        super(config.id, config.canbus.canbus);
        this.config = config;
        name = config.name;
        setName(name);
        configCancoder();
        setStatusSignals();
        addLog();
        SmartDashboard.putData("sensors/" + name, this);
        Log.log(name + " cancoder initialized");
        ElasticGenerator.getInstance().registerSensor(this);
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
        positionSignal = new Data<>(getPosition(), config.canbus.equals(Canbus.Rio));
        absPositionSignal = new Data<>(getAbsolutePosition(), config.canbus.equals(Canbus.Rio));
        velocitySignal = new Data<>(getVelocity(), config.canbus.equals(Canbus.Rio));
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
            Log.log(name + " have a fault: " + getFaultField().getValue());
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void addLog() {
        Log.putData(name + ": abs Position", 
            new Supplier[]{
                this::getCurrentAbsPosition
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
    public String getName() {
        return config.name;
    }

    /**
     * Gets current position.
     * 
     * @return Current relative position in Radians
     */
    public double get() {
        return getCurrentPosition();
    }

    /**
     * Gets the relative position since power-on.
     * 
     * @return Relative position in Radians
     */
    public double getCurrentPosition() {
        return positionSignal.getDouble() * 2 * Math.PI;
    }

    /**
     * Gets the absolute position (persists through power cycles).
     * 
     * <p>
     * Value is in Radians and includes configured offset.
     * This is exactly the value you can copy-paste into the offset config.
     * </p>
     * 
     * @return Absolute position in Radians
     */
    public double getCurrentAbsPosition() {
        return absPositionSignal.getDouble() * 2 * Math.PI;
    }

    /**
     * Gets the current velocity.
     * 
     * @return Velocity in Radians per second (RPS)
     */
    public double getCurrentVelocity() {
        return velocitySignal.getDouble() * 2 * Math.PI;
    }

    /**
     * Gets the current acceleration.
     * 
     * @return Acceleration in Radians per second² (RPS²)
     */
    public double getCurrentAcceleration() {
        double currentTimestamp = Timer.getFPGATimestamp();
        double dt = currentTimestamp - lastTimestamp;
        
        if (dt < 0.001) {
            return lastAcceleration;
        }
        
        if (lastTimestamp != 0) {
            lastAcceleration = (getCurrentVelocity() - lastVelocity) / dt;
        }
        
        lastVelocity = getCurrentVelocity();
        lastTimestamp = currentTimestamp;
        
        return lastAcceleration;
    }
    
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("CANcoder");
        builder.addBooleanProperty("is Connected", this::isConnected, null);
        builder.addDoubleProperty("value", this::getCurrentAbsPosition, null);
        builder.addDoubleProperty("Abs Position", this::getCurrentAbsPosition, null);
        builder.addDoubleProperty("Position", this::getCurrentPosition, null);
        builder.addDoubleProperty("Velocity", this::getCurrentVelocity, null);
        builder.addDoubleProperty("Acceleration", this::getCurrentAcceleration, null);
    }
}