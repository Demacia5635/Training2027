package frc.demacia.utils.sensors;

import java.util.function.Supplier;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;

/**
 * Digital limit switch sensor wrapper.
 * 
 * <p>Provides a simple interface for limit switches with:</p>
 * <ul>
 *   <li>Automatic logging</li>
 *   <li>Inversion support</li>
 *   <li>Hot-reload configuration</li>
 * </ul>
 * 
 * <p><b>Typical Usage:</b></p>
 * <pre>
 * LimitSwitchConfig config = new LimitSwitchConfig(0, "TopLimit")
 *     .withInvert(false);  // Normal: closed = true
 * 
 * LimitSwitch topLimit = new LimitSwitch(config);
 * 
 * // In mechanism
 * elevator.addStop(() -> topLimit.get());  // Stop at top
 * </pre>
 */
public class LimitSwitch extends DigitalInput implements DigitalSensorInterface{
    LimitSwitchConfig config;
    String name;

    boolean inverted;

    /**
     * Creates a limit switch sensor.
     * 
     * @param config Configuration containing DIO port and settings
     */
    public LimitSwitch(LimitSwitchConfig config){
        super(config.echoChannel);
        this.config = config;
		name = config.name;
        setName(name);
        configLimitSwitch();
        addLog();
        SmartDashboard.putData("sensors/" + config.name, this);
		Log.log(name + " limit switch initialized");
        ElasticGenerator.getInstance().registerSensor(this);
    }

    private void configLimitSwitch() {
        inverted = config.isInverted;
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        Log.putData(name + ": isTriggered", 
            new Supplier[]{
                this::get
            }
            , LogLevel.LOG_ONLY, "sensors", false);
    }

    /**
     * Checks sensor health (no-op for simple digital inputs).
     */
    public void checkElectronics(){
        
    }

    /**
     * Gets the sensor name from configuration.
     * 
     * @return Sensor name for identification
     */
    public String getName(){
        return config.name;
    }

    /**
     * Gets the limit switch state with inversion applied.
     * 
     * <p>Inversion logic:</p>
     * <ul>
     *   <li>Not inverted: Closed = true, Open = false (typical NO switch)</li>
     *   <li>Inverted: Closed = false, Open = true (for NC switches)</li>
     * </ul>
     * 
     * @return true if limit switch is triggered
     */
    public boolean get(){
        return !(inverted == super.get());
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Digital Input");
        builder.addBooleanProperty("Value", this::get, null);
    }
}
