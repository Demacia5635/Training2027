package frc.demacia.utils.leds;

/**constants for leds */
public class LedConstants {
  /**the size of every strip for every port */
  public static final int LENGTH = 43;
  /**the port of the leds */
  public static final int PORT = 9;

  /**
   * the blink time between what is color and what is off <br></br>
   * {@code Timer.getFPGATimestamp() % BLINK_TIME != 0 ? color : Color.kBlack}
   */
  public static final double BLINK_TIME = 3;
}