package frc.demacia.utils.sensors;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;

/**
 * A wrapper class for the REV Color Sensor V3 that provides simplified color detection
 * and proximity sensing capabilities.
 * 
 * <p>This sensor automatically configures default colors (blue, red, green, yellow) and
 * integrates with the team's logging system for competition and debug monitoring.
 * 
 * <p>Example usage:
 * <pre>
 * ColorSensorConfig config = new ColorSensorConfig("IntakeSensor");
 * ColorSensor sensor = new ColorSensor(config);
 * Color detectedColor = sensor.get();
 * </pre>
 * 
 * @see ColorSensorV3
 * @see ColorSensorConfig
 */
public class ColorSensor extends ColorSensorV3 implements ColorSensorInterface {

    private final ColorSensorConfig config;
    private final String name;
    private final ColorMatch matcher;

    /**
     * Creates a new ColorSensor with the specified configuration.
     * Automatically initializes the I2C connection on the onboard port and sets up
     * default color matching for blue, red, green, and yellow.
     * 
     * @param config the configuration object containing the sensor name and settings
     */
    public ColorSensor(ColorSensorConfig config) {
        super(I2C.Port.kOnboard); 
        this.config = config;
        name = config.name;
        setName(name);
        matcher = new ColorMatch();

        addDefaultColors();
        addLog();

        LogManager.log(name + " color sensor initialized");
     
    }

    @Override
    public void setName(String name) {
        ColorSensorInterface.super.setName(name);
    }

    private void addDefaultColors() {
        matcher.addColorMatch(Color.kBlue);
        matcher.addColorMatch(Color.kRed);
        matcher.addColorMatch(Color.kGreen);
        matcher.addColorMatch(Color.kYellow);
    }

    @SuppressWarnings("unchecked")
    private void addLog() {
        LogManager.addEntry(name + ": Color", this::get
        ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();

        LogManager.addEntry(name + ": Proximity", this::getProximity)
            .withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();

        LogManager.addEntry(name + ": Matched Color", this::getMatchedColorName)
            .withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP).build();
    }

    /**
     * Checks if the sensor electronics are functioning properly.
     * Can be used for diagnostics and health monitoring.
     */
    public void checkElectronics() {
        try {
            Color color = getColor();
            int proximity = getProximity();
            
            if (color == null) {
                LogManager.log(name + " color sensor - failed to read color", AlertType.kError);
            } else if (proximity < 0) {
                LogManager.log(name + " color sensor - invalid proximity reading", AlertType.kWarning);
            } else {
                LogManager.log(name + " color sensor - electronics OK");
            }
        } catch (Exception e) {
            LogManager.log(name + " color sensor - electronics check failed: " + e.getMessage(), AlertType.kError);
        }
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
     * Gets the current detected color from the sensor.
     * 
     * @return the detected Color object with RGB values
     */
    public Color get(){
        return getColor();
    }

    @Override
    public Color getColor(){
        return super.getColor();
    }

    /**
     * Matches the detected color against the registered color targets.
     * 
     * @return ColorMatchResult containing the matched color and confidence level,
     *         or null if no match is found
     */
    public ColorMatchResult getMatchedColor() {
        Color detectedColor = getColor();
        return matcher.matchClosestColor(detectedColor);
    }

    /**
     * Gets the name of the matched color as a human-readable string.
     * 
     * @return the name of the matched color (e.g., "Blue", "Red", "Unknown")
     */
    public String getMatchedColorName() {
        ColorMatchResult match = getMatchedColor();
        if (match == null || match.color == null) {
            return "Unknown";
        }

        Color matchedColor = match.color;
        if (matchedColor.equals(Color.kBlue)) {
            return "Blue";
        } else if (matchedColor.equals(Color.kRed)) {
            return "Red";
        } else if (matchedColor.equals(Color.kGreen)) {
            return "Green";
        } else if (matchedColor.equals(Color.kYellow)) {
            return "Yellow";
        }
        return "Unknown";
    }

    /**
     * Gets the confidence level of the last color match.
     * 
     * @return confidence value between 0.0 (no match) and 1.0 (perfect match),
     *         or 0.0 if no match was found
     */
    public double getMatchConfidence() {
        ColorMatchResult match = getMatchedColor();
        return (match != null) ? match.confidence : 0.0;
    }

    /**
     * Adds a custom color target to the matcher.
     * Useful for detecting team-specific or game-specific colors.
     * 
     * @param color the Color object to add as a target
     */
    public void addColorMatch(Color color) {
        matcher.addColorMatch(color);
    }

    /**
     * Gets the IR proximity value from the sensor.
     * Higher values indicate objects closer to the sensor.
     * 
     * @return proximity value (typically 0-2047 range)
     */
    @Override
    public int getProximity() {
        return super.getProximity();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Color Sensor");
        builder.addDoubleProperty("Proximity", this::getProximity, null);
        builder.addStringProperty("Matched Color", this::getMatchedColorName, null);
    }
}