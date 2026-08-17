package frc.demacia.utils.sensors;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.util.sendable.SendableRegistry;

/**
 * Base interface for all sensors.
 * 
 * <p>Provides common methods that all sensor types must implement for
 * identification and health monitoring.</p>
 */
public  interface SensorInterface extends Sendable{
    /**
     * Gets the sensor's configured name.
     * 
     * @return Sensor name as specified in configuration
     */
    default String getName() {
        return SendableRegistry.getName(this);
    }

    default void setName(String name) {
        SendableRegistry.setName(this, name);
    }
    
    /**
     * Checks sensor health and logs any faults.
     * 
     * <p>Should be called periodically (e.g., in subsystem periodic() method).
     * Logs warnings or errors if sensor is disconnected, reporting faults, etc.</p>
     */
    public void checkElectronics();

    void initSendable(SendableBuilder builder);
}