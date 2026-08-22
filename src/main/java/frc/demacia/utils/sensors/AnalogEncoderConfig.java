package frc.demacia.utils.sensors;

/**
 * Configuration for analog absolute encoders.
 * 
 * <p><b>Key Parameters:</b></p>
 * <ul>
 *   <li><b>fullRange:</b> Mechanical range in rotations (typically 1.0)</li>
 *   <li><b>minRange/maxRange:</b> Voltage range as fraction (0-1)</li>
 *   <li><b>offset:</b> Zero position calibration</li>
 * </ul>
 * 
 * <p><b>Example for MA3 Encoder:</b></p>
 * <pre>
 * AnalogEncoderConfig config = new AnalogEncoderConfig(0, "Turret")
 *     .withFullRange(1.0)      // 360° rotation
 *     .withRange(0.0, 1.0)     // Full 0-5V range
 *     .withOffset(0.5);        // Zero at 180°
 * </pre>
 */
public class AnalogEncoderConfig extends AnalogSensorConfig<AnalogEncoderConfig>{
    public double fullRange = 2 * Math.PI;
    public double minRange = 0;
    public double maxRange = 1.0;

    /**
     * Creates analog encoder configuration.
     * 
     * @param channel Analog input port (0-3 on RoboRIO)
     * @param name Descriptive name for logging
     */
    public AnalogEncoderConfig(int channel, String name) {
        super(channel, name);
        sensorType = AnalogEncoder.class;
    }

    /**
     * Sets the full mechanical range.
     * 
     * @param fullRange Number of rotations (typically 1.0 for absolute encoders)
     * @return this config for method chaining
     */
    public AnalogEncoderConfig withFullRange(double fullRange) {
        this.fullRange = fullRange * 2 * Math.PI;
        return this;
    }

    /**
     * Sets the voltage percentage range.
     * 
     * @param minRange Minimum voltage as fraction (0.0 = 0V)
     * @param maxRange Maximum voltage as fraction (1.0 = 5V)
     * @return this config for method chaining
     */
    public AnalogEncoderConfig withRange(double minRange, double maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        return this;
    }

    /**
     * Sets only the minimum voltage range.
     * 
     * @param minRange Minimum voltage as fraction
     * @return this config for method chaining
     */
    public AnalogEncoderConfig withMinRange(double minRange) {
        this.minRange = minRange;
        return this;
    }

    /**
     * Sets only the maximum voltage range.
     * 
     * @param maxRange Maximum voltage as fraction
     * @return this config for method chaining
     */
    public AnalogEncoderConfig withMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }
}