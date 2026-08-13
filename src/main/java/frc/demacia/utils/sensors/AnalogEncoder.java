package frc.demacia.utils.sensors;

import edu.wpi.first.util.sendable.SendableBuilder;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

/**
 * Analog absolute encoder wrapper (e.g., MA3, REV Through Bore).
 * 
 * <p>Provides interface for analog encoders connected to RoboRIO analog inputs with:</p>
 * <ul>
 *   <li>Configurable voltage range</li>
 *   <li>Offset calibration</li>
 *   <li>Inversion support</li>
 *   <li>Automatic logging</li>
 * </ul>
 * 
 * <p><b>Typical Encoders:</b></p>
 * <ul>
 *   <li>US Digital MA3: 10-bit absolute, 0-5V output</li>
 *   <li>REV Through Bore: 12-bit absolute, 0-3.3V or PWM</li>
 * </ul>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * AnalogEncoderConfig config = new AnalogEncoderConfig(0, "ArmEncoder")
 *     .withFullRange(1.0)      // Single rotation
 *     .withOffset(0.25)        // Zero at 90°
 *     .withRange(0.1, 0.9);    // Voltage range
 * 
 * AnalogEncoder encoder = new AnalogEncoder(config);
 * double position = encoder.get();  // Position in radians
 * </pre>
 */
public class AnalogEncoder extends edu.wpi.first.wpilibj.AnalogEncoder implements AnalogSensorInterface{
    AnalogEncoderConfig config;

    /**
     * Creates an analog encoder.
     * 
     * @param config Configuration with analog port and settings
     */
    public AnalogEncoder(AnalogEncoderConfig config){
        super(config.echoChannel, config.fullRange, config.offset);
        this.config = config;
        setName(config.name);
        configEncoder();
        addLog();
        LogManager.log(getName() + " analog encoder initialized");
    }

    private void configEncoder() {
        setInverted(config.isInverted);
        setVoltagePercentageRange(config.minRange, config.maxRange);
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(getName() + ": Position", this::get)
        .withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
    }

    /**
     * Checks sensor health (no-op for analog inputs).
     */
    public void checkElectronics(){
        
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
     * Checks if encoder direction is inverted.
     * 
     * @return true if inverted
     */
    public boolean isInverted(){
        return config.isInverted;
    }
    
    /**
     * Gets current encoder position with offset applied.
     * 
     * @return Position in radians (0 to 2π typically)
     */
    @Override
    public double get(){
        return super.get();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("AbsoluteEncoder");
        builder.addDoubleProperty("Position", this::get, null);
    }
}