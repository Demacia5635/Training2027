package frc.demacia.utils.sensors;

/**
 * Configuration class for OpticalSensor instances.
 * 
 * <p>Specifies the analog input channel and name for an optical sensor.
 * 
 * <p>Example:
 * <pre>
 * OpticalSensorConfig config = new OpticalSensorConfig("BeamBreak", 0);
 * </pre>
 */
public class OpticalSensorConfig extends AnalogSensorConfig<OpticalSensorConfig> {

    /**
     * Creates a new configuration for an optical sensor.
     * 
     * @param name a unique identifier for this sensor (e.g., "BeamBreak", "LineSensor")
     * @param port the analog input channel (0-3 on RoboRIO)
     */
    public OpticalSensorConfig(String name, int port) {
        super(port, name);
        sensorType = OpticalSensor.class;
    }

}
