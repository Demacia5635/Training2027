package frc.demacia.utils.sensors;

/**
 * Interface for sensors that provide continuous analog readings.
 * 
 * <p>Examples: distance sensors, potentiometers, encoders, pressure sensors</p>
 */
public interface AnalogSensorInterface extends SensorInterface {
    /**
     * Gets the current sensor reading.
     * 
     * <p>Units depend on sensor type:</p>
     * <ul>
     *   <li>Encoders: radians or rotations</li>
     *   <li>Distance sensors: meters</li>
     *   <li>Potentiometers: voltage or position</li>
     * </ul>
     * 
     * @return Current sensor value in appropriate units
     */
    double get();
}