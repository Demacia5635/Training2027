package frc.demacia.utils.motors;


/** * Configuration class specifically for TalonSRX motors (Phoenix 5).
 * Extends the base configuration to support SRX-specific parameters.
 */
public class TalonSRXConfig extends BaseMotorConfig<TalonSRXConfig> {

    /** * Creates a new Talon SRX Configuration.
     * @param id The CAN bus ID of the motor
     * @param name The name of the motor for logging and dashboard
     */
    public TalonSRXConfig(int id, String name) {
        super(id, name);
        motorClass = MotorControllerType.TalonSRX;
    }

    /** * Creates a new Talon SRX Configuration by copying another config.
     * @param id The new CAN bus ID
     * @param name The new name
     * @param config The existing configuration to copy from
     */
    public TalonSRXConfig(int id, String name, BaseMotorConfig<?> config) {
        super(id, name);
        copyBaseFields(config);
    }
}