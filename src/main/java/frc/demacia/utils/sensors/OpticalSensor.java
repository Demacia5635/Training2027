package frc.demacia.utils.sensors;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.AnalogInput;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

/**
 * A wrapper for analog optical sensors that measure voltage levels.
 * 
 * <p>Optical sensors typically output an analog voltage that varies based on
 * light intensity or object proximity. This class provides easy access to
 * voltage readings and integrates with WPILib's Sendable interface for
 * dashboard display.
 * 
 * <p>Example usage:
 * <pre>
 * OpticalSensorConfig config = new OpticalSensorConfig("BeamBreak", 0);
 * OpticalSensor sensor = new OpticalSensor(config);
 * double voltage = sensor.get();
 * </pre>
 * 
 * @see AnalogInput
 * @see OpticalSensorConfig
 */
public class OpticalSensor extends AnalogInput implements SensorInterface {
    private final OpticalSensorConfig config;
    String name;
    
    /**
     * Creates a new OpticalSensor with the specified configuration.
     * 
     * @param config the configuration containing the sensor name and analog channel
     */
    public OpticalSensor(OpticalSensorConfig config) {
        super(config.echoChannel);
        this.config = config;
		name = config.name;
        setName(name);
        addLog();
		LogManager.log(name + " Optical Sensor initialized");
    }
    
    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(name + ": value",  this::get)
        .withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();

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
     * Reads the current voltage from the optical sensor.
     * 
     * @return the voltage reading in volts (typically 0-5V range)
     */
    public double getCurrentValue() {
        return getVoltage();
    }

    /**
     * Gets the current sensor value (voltage).
     * Convenience method that calls getCurrentValue().
     * 
     * @return the voltage reading in volts
     */
    public double get(){
        return getCurrentValue();
    }

    /**
     * Reads the current voltage from the optical sensor.
     * 
     * @return the voltage reading in volts (typically 0-5V range)
     */
    public double getVoltage() {
        return super.getVoltage();
    }

    public void checkElectronics(){
        
    }

    /**
     * Checks sensor health (no-op for analog inputs).
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("value" , this::get, null);
    }
}

