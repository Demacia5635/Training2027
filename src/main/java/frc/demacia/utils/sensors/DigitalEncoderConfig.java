package frc.demacia.utils.sensors;

/**
 * Configuration for digital duty-cycle encoders.
 * 
 * <p><b>Parameters:</b></p>
 * <ul>
 *   <li><b>scalar:</b> Scaling multiplier (typically 1.0)</li>
 *   <li><b>minRange/maxRange:</b> Valid duty cycle range (0.0-1.0)</li>
 *   <li><b>frequency:</b> Expected update frequency in Hz</li>
 *   <li><b>offset:</b> Zero position calibration</li>
 * </ul>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * DigitalEncoderConfig config = new DigitalEncoderConfig(5, "ArmEncoder")
 *     .withScalar(1.0)
 *     .withRange(0.025, 0.975)     // Standard duty cycle range
 *     .withFrequency(1000)          // 1kHz expected
 *     .withOffset(Math.PI / 2);     // Zero at 90°
 * </pre>
 */
public class DigitalEncoderConfig extends BaseSensorConfig<DigitalEncoderConfig>{
    public double fullRange = 2 * Math.PI;
    public double minRange = 0;
    public double maxRange = 1;
    public double frequency = 1000;
    public double connectedFrequencyThreshold = 100;
    public double offset = 0;

    /**
     * Creates digital encoder configuration.
     * 
     * @param channel DIO channel number
     * @param name Descriptive name for logging
     */
    public DigitalEncoderConfig(int channel, String name) {
        super(channel, name);
        sensorType = DigitalEncoder.class;
    }

    /**
     * Sets the scaling multiplier.
     * 
     * @param scalar Scaling factor (typically 1.0)
     * @return this config for method chaining
     */ 
    public DigitalEncoderConfig withScalar(double fullRange) {
        this.fullRange = fullRange * 2 * Math.PI;
        return this;
    }

    /**
     * Sets the valid duty cycle range.
     * 
     * <p>Duty cycles outside this range indicate invalid readings.</p>
     * 
     * @param minRange Minimum valid duty cycle (0.0-1.0)
     * @param maxRange Maximum valid duty cycle (0.0-1.0)
     * @return this config for method chaining
     */
    public DigitalEncoderConfig withRange(double minRange, double maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        return this;
    }

    /**
     * Sets only the minimum duty cycle range.
     * 
     * @param minRange Minimum valid duty cycle
     * @return this config for method chaining
     */
    public DigitalEncoderConfig withMinRange(double minRange) {
        this.minRange = minRange;
        return this;
    }

    /**
     * Sets only the maximum duty cycle range.
     * 
     * @param maxRange Maximum valid duty cycle
     * @return this config for method chaining
     */
    public DigitalEncoderConfig withMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }

    /**
     * Sets the expected encoder frequency.
     * 
     * <p>Used for connection detection. Encoder is considered disconnected
     * if frequency drops below connectedFrequencyThreshold.</p>
     * 
     * @param frequency Expected frequency in Hz (typically 1000)
     * @return this config for method chaining
     */
    public DigitalEncoderConfig withFrequency(double frequency) {
        this.frequency = frequency;
        return this;
    }

    /**
     * Sets the zero position offset.
     * 
     * @param offset Offset value to subtract from reading
     * @return this config for method chaining
     */
    public DigitalEncoderConfig withOffset(double offset) {
        this.offset = offset;
        return this;
    }
}