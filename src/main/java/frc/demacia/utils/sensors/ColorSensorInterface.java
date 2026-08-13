package frc.demacia.utils.sensors;

import edu.wpi.first.wpilibj.util.Color;

/**
 * Interface for color detection sensors.
 * 
 * <p>Color sensors detect RGB values and can identify game piece colors
 * or field elements.</p>
 * 
 * <p><b>Common Uses:</b></p>
 * <ul>
 *   <li>Game piece identification</li>
 *   <li>Color wheel control (2020 game)</li>
 *   <li>Line following</li>
 *   <li>Target recognition</li>
 * </ul>
 * 
 * <p><b>Example Sensors:</b></p>
 * <ul>
 *   <li>REV Color Sensor V3</li>
 *   <li>Adafruit TCS34725</li>
 * </ul>
 */
public interface ColorSensorInterface extends SensorInterface{ 

    /**
     * Gets the detected color.
     * 
     * <p>Interpretation depends on sensor implementation.
     * Could be hue, color ID, or dominant wavelength.</p>
     * 
     * @return Color value
     */
    Color getColor();
}
