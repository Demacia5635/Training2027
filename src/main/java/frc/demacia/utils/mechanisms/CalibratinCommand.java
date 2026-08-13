// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.demacia.utils.mechanisms;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.demacia.utils.motors.MotorInterface;

/**
 * A command used to calibrate a mechanism's motor position.
 * <p>
 * This command drives a motor at a specific power until a stop condition is met
 * (e.g., hitting a limit switch or hard stop), then resets the encoder position.
 * Supports an optional initial delay where a different power is applied.
 * </p>
 */
public class CalibratinCommand extends Command {
  BaseMechanism mechanism;
  MotorInterface motor;
  double power;
  BooleanSupplier stopSupplier;
  double resetPos;
  double startPower;
  double sec;
  Timer timer;

  /** * Creates a new CalibratinCommand with a starting delay.
   * * @param mechanism The mechanism to calibrate
   * @param motorName The name of the motor to drive
   * @param power The power to apply during calibration
   * @param stopSupplier Condition to finish calibration (e.g., limit switch)
   * @param resetPos The position value to set the encoder to upon completion
   * @param startPower Initial power to apply before calibration starts
   * @param sec Duration in seconds to apply startPower
   */
  public CalibratinCommand(BaseMechanism mechanism, String motorName, double power, BooleanSupplier stopSupplier, double resetPos, double startPower, double sec) {
    this.mechanism = mechanism;
    motor = mechanism.getMotor(motorName);
    this.power = power;
    this.stopSupplier = stopSupplier;
    this.resetPos = resetPos;
    this.startPower = startPower;
    this.sec = sec;
    timer = new Timer();
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(mechanism);
  }

  /** * Creates a new CalibratinCommand without a starting delay.
   * * @param mechanism The mechanism to calibrate
   * @param motorName The name of the motor to drive
   * @param power The power to apply during calibration
   * @param stopSupplier Condition to finish calibration
   * @param resetPos The position value to set the encoder to upon completion
   */
  public CalibratinCommand(BaseMechanism mechanism, String motorName, double power, BooleanSupplier stopSupplier, double resetPos) {
    this(mechanism, motorName, power, stopSupplier, resetPos, 0, 0);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timer.start();
  }

  /**
   * Applies startPower until 'sec' has elapsed, then applies calibration power.
   */
  @Override
  public void execute() {
    if (timer.hasElapsed(sec)) {
      motor.setDuty(power);
    }
    else {
      motor.setDuty(startPower);
    }
  }

  /**
   * Stops the motor, resets the encoder to resetPos, and marks the mechanism as calibrated.
   */
  @Override
  public void end(boolean interrupted) {
    timer.stop();
    timer.reset();
    motor.stop();
    motor.setEncoderPosition(resetPos);
    mechanism.setCalibration(true);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return stopSupplier.getAsBoolean();
  }
}
