package frc.demacia.utils.sensors;

/**
 * Interface for sensors that provide binary (on/off) readings.
 * 
 * <p>Examples: limit switches, beam breaks, digital inputs</p>
 */
public interface DigitalSensorInterface extends SensorInterface {
    /**
     * Gets the current digital state.
    * 
    * @return true if sensor is triggered/active, false if not triggered/inactive
    */
    boolean get();
}
