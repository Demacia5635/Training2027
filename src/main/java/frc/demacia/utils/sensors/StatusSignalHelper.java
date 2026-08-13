package frc.demacia.utils.sensors;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;

/**
 * Helper utilities for safely reading CTRE StatusSignal values.
 * 
 * <p>StatusSignals can fail to update due to CAN bus errors. These methods
 * provide safe fallback to last known good value if refresh fails.</p>
 * 
 * <p><b>Usage:</b></p>
 * <pre>
 * StatusSignal<Angle> yawSignal = pigeon.getYaw();
 * double lastYaw = 0;
 * 
 * // In periodic() or getter method:
 * lastYaw = StatusSignalHelper.getStatusSignalInRad(yawSignal, lastYaw);
 * </pre>
 * 
 * <p><b>Why This Matters:</b></p>
 * <ul>
 *   <li>Prevents NaN or null values from CAN errors</li>
 *   <li>Maintains last good reading during transient faults</li>
 *   <li>Provides graceful degradation</li>
 * </ul>
 */
public class StatusSignalHelper {
    
    /**
     * Generic status signal reader with multiplier.
     * 
     * <p>Refreshes signal and returns value if successful, otherwise returns
     * last known value.</p>
     * 
     * @param statusSignal Signal to read
     * @param lastValue Last known good value (fallback)
     * @param multiplier Multiplier to apply to raw value
     * @return Current value * multiplier, or lastValue if read fails
     */
    @SuppressWarnings("rawtypes")
    public static double getStatusSignal(StatusSignal statusSignal, double lastValue, double multiplier) {
        statusSignal.refresh();
        if (statusSignal.getStatus() == StatusCode.OK) {
            return statusSignal.getValueAsDouble() * multiplier;
        }
        return lastValue;
    }
    
    /**
     * Gets status signal value converted to radians.
     * 
     * <p>Multiplies degrees by π/180 for conversion.</p>
     * 
     * @param statusSignal Signal to read (in degrees)
     * @param lastValue Last known value (fallback)
     * @return Value in radians, or lastValue if read fails
     */
    @SuppressWarnings("rawtypes")
    public static double getStatusSignalInRad(StatusSignal statusSignal, double lastValue) {
        return getStatusSignal(statusSignal, lastValue, Math.PI / 180);
    }
    
    /**
     * Gets status signal value converted to full rotation (0-2π).
     * 
     * <p>Multiplies rotations by 2π for conversion.</p>
     * 
     * @param statusSignal Signal to read (in rotations)
     * @param lastValue Last known value (fallback)
     * @return Value in radians (0-2π range), or lastValue if read fails
     */
    @SuppressWarnings("rawtypes")
    public static double getStatusSignalWith2Pi(StatusSignal statusSignal, double lastValue) {
        return getStatusSignal(statusSignal, lastValue, 2 * Math.PI);
    }
    
    /**
     * Gets status signal value without conversion.
     * 
     * <p>Returns raw value as-is if read succeeds.</p>
     * 
     * @param statusSignal Signal to read
     * @param lastValue Last known value (fallback)
     * @return Current value, or lastValue if read fails
     */
    @SuppressWarnings("rawtypes")
    public static double getStatusSignalBasic(StatusSignal statusSignal, double lastValue) {
        return getStatusSignal(statusSignal, lastValue, 1.0);
    }
}