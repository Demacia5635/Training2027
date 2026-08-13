package frc.demacia.utils.mechanisms;

import java.util.function.DoubleSupplier;
import edu.wpi.first.wpilibj2.command.Command;

/**
 * A simple command to drive specific motors within a mechanism using dynamic power sources.
 * <p>
 * This is typically used for teleoperated control where the power comes from joystick axes.
 * Supports driving multiple motors or a single motor.
 * </p>
 */
public class PowerCommand extends Command {
  private final BaseMechanism mechanism;
  private final String[] motorNames;
  private final DoubleSupplier[] powers;
  private final int length;

  /** * Creates a new DriveCommand for multiple motors.
   * * @param mechanism The mechanism containing the motors
   * @param motorNames The names of the motors to drive
   * @param powers The suppliers that provide the duty cycle power [-1.0, 1.0] (e.g., joystick input)
   */
  public PowerCommand(BaseMechanism mechanism, String[] motorNames, DoubleSupplier[] powers) {
    this.mechanism = mechanism;
    this.motorNames = motorNames;
    this.powers = powers;
    this.length = Math.min(motorNames.length, powers.length);
    addRequirements(mechanism);
  }

  /** * Creates a new DriveCommand for a single motor.
   * * @param mechanism The mechanism containing the motor
   * @param motorName The name of the motor to drive
   * @param power A supplier that provides the duty cycle power [-1.0, 1.0] (e.g., joystick input)
   */
  public PowerCommand(BaseMechanism mechanism, String motorName, DoubleSupplier power) {
    this(mechanism, new String[] { motorName }, new DoubleSupplier[] { power });
  }

  @Override
  public void initialize() {}

  /**
   * Continuously updates the power for all specified motors from their respective suppliers.
   */
  @Override
  public void execute() {
    for (int i = 0; i < length; i++) {
      mechanism.setPower(motorNames[i], powers[i].getAsDouble());
    }
  }

  /**
   * Stops all motors that were being driven by this command when it ends.
   */
  @Override
  public void end(boolean interrupted) {
    for (int i = 0; i < length; i++) {
      mechanism.stop(motorNames[i]);
    }
  }

  @Override
  public boolean isFinished() {
    return false;
  }
}