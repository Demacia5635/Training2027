package frc.demacia.utils.sensors;

/**
 * Configuration for ultrasonic distance sensors.
 * 
 * <p>Requires two DIO channels:</p>
 * <ul>
 *   <li><b>Ping channel:</b> Sends ultrasonic pulse</li>
 *   <li><b>Echo channel:</b> Receives reflected pulse</li>
 * </ul>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * // MaxBotix sensor on DIO 0 (echo) and 1 (ping)
 * UltraSonicSensorConfig config = new UltraSonicSensorConfig(
 *     0,              // Echo channel
 *     1,              // Ping channel  
 *     "RearSensor"
 * );
 * </pre>
 */
public class UltraSonicSensorConfig extends AnalogSensorConfig<UltraSonicSensorConfig> {
    int pingChannel;

    /**
     * Creates ultrasonic sensor configuration.
     * 
     * @param channel Echo channel (receives pulse)
     * @param pingChannel Ping channel (sends pulse)
     * @param name Descriptive name for logging
     */
    public UltraSonicSensorConfig(int channel, int pingChannel, String name) {
        super(channel, name);
        this.pingChannel = pingChannel;
        sensorType = UltraSonicSensor.class;
    }
}