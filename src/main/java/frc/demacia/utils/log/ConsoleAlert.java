package frc.demacia.utils.log;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Timer;

/**
 * A wrapper for the WPILib Alert class that includes a timer.
 * This allows the alert to track how long it has been active, 
 * useful for expiring messages after a certain time.
 */
public class ConsoleAlert extends Alert {
  
  /** The timer that tracks the duration of the alert */
  Timer timer;

  /**
   * Creates a new Console Alert
   * @param text the text to display
   * @param type the type of alert (Info, Warning, Error)
   */
  public ConsoleAlert(String text, AlertType type) {
    super("Console", text, type);
    timer = new Timer();
  }

  /**
   * Sets the alert to be active or inactive.
   * Starts the timer when active, stops and resets when inactive.
   * @param active true to enable the alert, false to disable
   */
  @Override
  public void set(boolean active) {
    super.set(active);

    if (active) {
      timer.start();
    } else {
      timer.stop();
      timer.reset();
    }
  }

  /**
   * Updates the text of the alert.
   * Resets the timer so the new message gets the full duration.
   * @param text the new text to display
   */
  @Override
  public void setText(String text) {
    super.setText(text);
    timer.reset();
  }

  /**
   * Checks if the alert has been active for longer than the allowed time.
   * @return true if the timer has elapsed the constant time, false otherwise
   */
  public boolean isTimerOver() {
    if (ConsoleConstants.CONSOLE_MESSEGE_TIME == 0) {
      return false;
    }
    return timer.hasElapsed(ConsoleConstants.CONSOLE_MESSEGE_TIME);
  }
}