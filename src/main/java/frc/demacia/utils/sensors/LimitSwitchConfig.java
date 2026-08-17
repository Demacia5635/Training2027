package frc.demacia.utils.sensors;

/**
 * Configuration for limit switch sensors.
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * // Normally-open switch on DIO 0
 * LimitSwitchConfig top = new LimitSwitchConfig(0, "ElevatorTop")
 *     .withInvert(false);
 * 
 * // Normally-closed switch on DIO 1 (inverted)
 * LimitSwitchConfig bottom = new LimitSwitchConfig(1, "ElevatorBottom")
 *     .withInvert(true);
 * </pre>
 */
public class LimitSwitchConfig extends BaseSensorConfig<LimitSwitchConfig>{

    /**
     * Creates limit switch configuration.
     * 
     * @param channel DIO channel number (0-9 on RoboRIO)
     * @param name Descriptive name for logging and identification
     */
    public LimitSwitchConfig(int channel, String name) {
        super(channel, name);
        sensorType = LimitSwitch.class;
    }
}
