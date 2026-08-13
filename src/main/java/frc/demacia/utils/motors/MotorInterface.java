package frc.demacia.utils.motors;

import java.util.function.Supplier;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableRegistry;

/**
 * Common interface for all motor controllers in the robot.
 * <p>
 * Defines standard methods for controlling motors (Voltage, Velocity, Position,
 * etc.)
 * and retrieving telemetry data, ensuring interchangeable use of different
 * motor types.
 * </p>
 */
public interface MotorInterface extends Sendable {

  /** Enumeration of supported control modes */
  enum ControlMode {
    DISABLE, DUTYCYCLE, VOLTAGE, VELOCITY, POSITION_VOLTAGE, MAGIC_MOTION, ANGLE
  }

  /** @return The name of the motor */
  String getName();

  /**
   * Sets the name of the motor in the SendableRegistry.
   * 
   * @param name The new name
   */
  default void setName(String name) {
    SendableRegistry.setName(this, name);
  }

  boolean isConnected();

  /**
   * Checks for hardware faults and logs them.
   */
  void checkElectronics();

  /**
   * Sets the neutral mode of the motor.
   * 
   * @param isBrake true for Brake mode, false for Coast mode
   */
  void setNeutralMode(boolean isBrake);

  /**
   * Changes the active control slot (PID profile).
   * 
   * @param slot The slot index (typically 0, 1, or 2)
   */
  void setSlot(int slot);

  int getSlot();

  /**
   * Stops the motor immediately and disables control.
   */
  void stop();

  /**
   * Sets the motor output as a duty cycle (percent output).
   * 
   * @param power The power output [-1.0, 1.0]
   */
  void setDuty(double power);

  /**
   * Sets the motor output voltage.
   * 
   * @param voltage The voltage to apply
   */
  void setVoltage(double voltage);

  /**
   * Sets the target velocity with an optional feedforward.
   * 
   * @param velocity    The target velocity
   * @param feedForward Arbitrary feedforward value
   */
  void setVelocity(double velocity, double feedForward);

  /**
   * Sets the target velocity.
   * 
   * @param velocity The target velocity
   */
  void setVelocity(double velocity);

  /**
   * Sets the target velocity using Ka.
   * 
   * @param velocity                   The target velocity
   * @param wantedAccelerationSupplier The supplier for the acceleration
   */
  void setVelocityWithAcceleration(double velocity, Supplier<Double> wantedAccelerationSupplier);

  /**
   * Sets the target position using Motion Magic.
   * 
   * @param position    The target position
   * @param feedForward Arbitrary feedforward value
   */
  void setMotion(double position, double feedForward);

  /**
   * Sets the target position using Motion Magic.
   * 
   * @param position The target position
   */
  void setMotion(double position);

  /**
   * Sets the target angle using Motion Magic.
   * 
   * @param angle       The target angle (in radians)
   * @param feedForward Arbitrary feedforward value
   */
  void setAngle(double angle, double feedForward);

  /**
   * Sets the target angle using Motion Magic.
   * 
   * @param angle The target angle
   */
  void setAngle(double angle);

  /**
   * Sets the target position using Position Voltage.
   * 
   * @param position    The target position
   * @param feedForward Arbitrary feedforward value
   */
  void setPositionVoltage(double position, double feedForward);

  /**
   * Sets the target position using Position Voltage.
   * 
   * @param position The target position
   */
  void setPositionVoltage(double position);

  /** @return The integer representation of the current control mode */
  int getCurrentControlModeInteger();

  /** @return The current control mode */
  ControlMode getCurrentControlMode();

  /** @return The current closed-loop setpoint */
  double getCurrentClosedLoopSP();

  /** @return The current closed-loop error */
  double getCurrentClosedLoopError();

  /** @return The current position (in mechanism units) */
  double getCurrentPosition();

  /** @return The current angle (in mechanism units) */
  double getCurrentAngle();

  /** @return The current velocity (in mechanism units/sec) */
  double getCurrentVelocity();

  /** @return The current acceleration (in mechanism units/sec^2) */
  double getCurrentAcceleration();

  /** @return The current motor voltage */
  double getCurrentVoltage();

  /** @return The current stator current (Amps) */
  double getCurrentCurrent();

  /** @return The current calculated value based on control modes */
  double getCurrentValue();

  /**
   * @return The last active control mode (used for fallback when
   *         disabled/dutycycle)
   */
  ControlMode getLastControlMode();

  /**
   * Overrides the internal encoder position.
   * 
   * @param position The new position to set
   */
  void setEncoderPosition(double position);

  double getWantedValue();

  /**
   * @return The flags used for Sysid OLS calculation [kS, kV, kA, kG, kCos, kV2]
   */
  boolean[] getSysidFlags();

  void setConfigPidFf(CloseLoopParam newParams, int Slot);

  void setConfigMotionParam(double maxVelocity, double maxAcceleration, double maxJerk);

  /**
   * Applies a target value to the motor based on the specified control mode.
   */
  void applyControlModeValue(ControlMode mode, double value);

  /**
   * Checks if a specific motor has reached its target value within a specified
   * tolerance.
   * * @param motorName The name of the motor
   * 
   * @param allowedError The allowable tolerance
   * @return true if the motor is within tolerance, false otherwise
   */
  boolean isReady(double allowedError);

  boolean isRadiansMotor();

  double getTestValue();

  void setTestValue(double testValue);
}