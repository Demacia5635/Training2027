package frc.demacia.utils.sensors;

/**
 * Configuration class for ColorSensor instances.
 * 
 * <p>This config only requires a name identifier, as the ColorSensor
 * uses a fixed I2C port (onboard).
 * 
 * <p>Example:
 * <pre>
 * ColorSensorConfig config = new ColorSensorConfig("IntakeSensor");
 * </pre>
 */

public class ColorSensorConfig extends BaseSensorConfig<ColorSensorConfig>{
    
    /**
     * Creates a new configuration for a color sensor.
     * 
    * @param name a unique identifier for this sensor (e.g., "IntakeSensor", "ShooterSensor")
    */
    public ColorSensorConfig(String name){
        super(name);
        sensorType = ColorSensor.class;
    }
}