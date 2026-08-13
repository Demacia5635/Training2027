package frc.demacia.utils.sensors;
import java.util.ArrayDeque;
import java.util.Queue;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Ultrasonic;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

/**
 * Ultrasonic distance sensor wrapper (e.g., MaxBotix, HC-SR04).
 * 
 * <p>Provides distance measurement using ultrasonic time-of-flight with:</p>
 * <ul>
 *   <li>Automatic conversion to meters</li>
 *   <li>Range validity checking</li>
 *   <li>Automatic ping mode</li>
 *   <li>Logging support</li>
 * </ul>
 * 
 * <p><b>How It Works:</b></p>
 * <ol>
 *   <li>Ping channel sends ultrasonic pulse</li>
 *   <li>Echo channel receives reflected pulse</li>
 *   <li>Time difference calculated to determine distance</li>
 * </ol>
 * 
 * <p><b>Limitations:</b></p>
 * <ul>
 *   <li>Narrow beam angle (~15°)</li>
 *   <li>Affected by soft surfaces, angles</li>
 *   <li>Limited range (typically 0.1-5 meters)</li>
 *   <li>Slower update rate than other sensors</li>
 * </ul>
 * 
 * <p><b>Example:</b></p>
 * <pre>
 * UltraSonicSensorConfig config = new UltraSonicSensorConfig(
 *     0,  // Echo channel
 *     1,  // Ping channel
 *     "FrontDistance"
 * );
 * 
 * UltraSonicSensor sensor = new UltraSonicSensor(config);
 * 
 * if (sensor.isRangeValid()) {
 *     double distance = sensor.get();  // Meters
 *     System.out.println("Object at: " + distance + "m");
 * }
 * </pre>
 */
public class UltraSonicSensor extends Ultrasonic implements AnalogSensorInterface {
    String name;
    UltraSonicSensorConfig config;

    /**
     * Creates an ultrasonic distance sensor.
     * 
     * @param config Configuration with ping and echo channels
     */
    public UltraSonicSensor(UltraSonicSensorConfig config) {
        super(config.pingChannel, config.echoChannel);
        this.config = config;
        name = config.name;
        setName(name);
        setAutomaticMode(true);
        addLog();
		LogManager.log(name + " UltraSonicSensor initialized");
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(name + ": range", this::getRangeMeters)
        .withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
    }

    /**
     * Gets the sensor name.
     * 
     * @return Sensor name from configuration
     */ 
    public String getName() {
        return config.name;
    }

    /**
     * Checks if sensor is returning valid ranges.
     * 
     * <p>Logs warning if range is invalid (too close, too far, or no echo).</p>
     */
    public void checkElectronics(){
        if (!isRangeValid()) {
            LogManager.log(name + " is at invalid range");
        }
    }

    /**
     * Gets the measured distance in meters.
     * 
     * <p>Check isRangeValid() before using this value to ensure measurement is reliable.</p>
     * 
     * @return Distance in meters (typically 0.1-5.0m range)
     */
    public double get() {
        return getRangeMeters();
    }

    /**
     * Gets distance in meters (same as get()).
     * 
     * @return Distance in meters
     */
    public double getRangeMeters() {
        return getRangeMM() / 1000d;
    }

        private static final int average_Window = 5;
    private final Queue<Double> samples = new ArrayDeque<>();
    private double sum = 0;

    public double getAverage() {
        double current = getRangeMeters();
        samples.add(current);
        sum += current;

        if (samples.size() > average_Window) {
            sum -= samples.remove();
        }

        return sum / samples.size();
    }
    /**
     * Initializes dashboard display.
     * 
     * @param builder Sendable builder for dashboard integration
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("range", this::getRangeMeters, null);
        builder.addDoubleProperty("avg range", this::getAverage, null);
        
    }
}
