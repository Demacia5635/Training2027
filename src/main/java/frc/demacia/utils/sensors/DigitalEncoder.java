package frc.demacia.utils.sensors;

import java.util.function.Supplier;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Alert.AlertType;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Digital duty-cycle encoder wrapper (e.g., REV Through Bore in digital mode).
 * 
 * <p>Provides interface for duty-cycle encoders with:</p>
 * <ul>
 *   <li>Automatic unit conversion to radians</li>
 *   <li>Connection monitoring</li>
 *   <li>Offset calibration</li>
 *   <li>Configurable duty cycle range</li>
 * </ul>
 * 
 * <p><b>Duty Cycle Encoding:</b> Position encoded as pulse width percentage.
 * More robust than analog for long cable runs.</p>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * DigitalEncoderConfig config = new DigitalEncoderConfig(0, "WristEncoder")
 *     .withScalar(1.0)
 *     .withOffset(0.0)
 *     .withRange(0.025, 0.975);  // Duty cycle range
 * 
 * DigitalEncoder encoder = new DigitalEncoder(config);
 * if (encoder.isConnected()) {
 *     double angle = encoder.get();
 * }
 * </pre>
 */
public class DigitalEncoder extends DutyCycleEncoder implements AnalogSensorInterface{
    DigitalEncoderConfig config;
    String name;

    /**
     * Creates a digital duty-cycle encoder.
     * 
     * @param config Configuration with DIO channel and settings
     */
    public DigitalEncoder(DigitalEncoderConfig config){
        super(config.echoChannel, config.fullRange, config.offset);
        this.config = config;
        name = config.name;
        setName(name);
        configEncoder();
        addLog();
        SmartDashboard.putData("sensors/" + name, this);
        Log.log(name + " digital encoder initialized");
        ElasticGenerator.getInstance().registerSensor(this);
    }
    
    private void configEncoder() {
        setDutyCycleRange(config.minRange, config.maxRange);
        setInverted(config.isInverted);
        setAssumedFrequency(config.frequency);
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        Log.putData(name + ": Position", 
            new Supplier[]{
                this::get
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
     * Checks encoder connection and logs warning if disconnected.
     */
    public void checkElectronics() {
        if (!isConnected()) {
            Log.log(name + " encoder disconnected", AlertType.kWarning);
        }
    }
    
    /**
     * Gets current position in radians.
     * 
     * @return Position in radians (0 to 2π)
     */
    @Override
    public double get(){
        return super.get();
    }

    /**
     * Checks if encoder is connected and communicating.
     * 
     * @return true if encoder detected on port
     */
    public boolean isConnected() {
        return super.isConnected();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("AbsoluteEncoder");
        builder.addDoubleProperty("Position", this::get, null);
        builder.addDoubleProperty("value", this::get, null);
        builder.addBooleanProperty("Is Connected", this::isConnected, null);
    }
}