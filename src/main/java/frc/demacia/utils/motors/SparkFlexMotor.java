package frc.demacia.utils.motors;

import com.revrobotics.PersistMode;
import com.revrobotics.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.Faults;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Alert.AlertType;
import frc.demacia.utils.Data;
import frc.demacia.utils.log.Log;
import static edu.wpi.first.units.Units.*;

/**
 * Wrapper class for the REV Spark Flex motor controller.
 * <p>
 * Handles configuration, PID control, logging, and on-the-fly tuning via
 * SmartDashboard.
 * Uses the REV Lib 2025 API.
 * </p>
 */
public class SparkFlexMotor extends BaseMotor {
  SparkFlex motor;
  com.revrobotics.spark.config.SparkFlexConfig cfg;

  private double lastVelocity = 0;
  private double lastAcceleration = 0;
  private double lastTime = 0;

  ClosedLoopSlot closedLoopSlot;

  /**
   * Creates a new TalonFX motor wrapper.
   * 
   * @param config The configuration object for this motor
   */
  public SparkFlexMotor(SparkFlexConfig config) {
    super(config);

    closedLoopSlot = ClosedLoopSlot.kSlot0;
  }

  protected void createMotor() {
    motor = new SparkFlex(config.id, MotorType.kBrushless);
  }

  protected void createMotorConfig() {
    cfg = new com.revrobotics.spark.config.SparkFlexConfig();
  }

  protected void configMaxCurrent(double maxCurrent) {
    cfg.smartCurrentLimit((int) maxCurrent);
  }

  protected void configRampUpTime(double rampUpTime) {
    cfg.openLoopRampRate(rampUpTime);
    cfg.closedLoopRampRate(rampUpTime);
  }

  protected void configIsInverted(boolean isInverted) {
    cfg.inverted(isInverted);
  }

  protected void configNeutralMode(boolean isBrake) {
    cfg.idleMode(isBrake ? IdleMode.kBrake : IdleMode.kCoast);
  }

  protected void configMaxVolt(double maxVolt) {
    cfg.voltageCompensation(config.maxVolt);
  }

  protected void configMinVolt(double minVolt) {

  }

  protected void configMotorRatio(double motorRatio) {
    cfg.encoder.positionConversionFactor(1 / config.motorRatio);
    cfg.encoder.velocityConversionFactor(1 / config.motorRatio);
  }

  protected void configPidFf(CloseLoopParam[] pidFfParams) {
    cfg.closedLoop.pid(config.pidFfParams[0].kP(), config.pidFfParams[0].kI(), config.pidFfParams[0].kD(),
        ClosedLoopSlot.kSlot0);
    cfg.closedLoop.feedForward.kV(config.pidFfParams[0].kV(), ClosedLoopSlot.kSlot0)
        .kA(config.pidFfParams[0].kA(), ClosedLoopSlot.kSlot0)
        .kS(config.pidFfParams[0].kS(), ClosedLoopSlot.kSlot0)
        .kG(config.pidFfParams[0].kG(), ClosedLoopSlot.kSlot0);

    cfg.closedLoop.pid(config.pidFfParams[1].kP(), config.pidFfParams[1].kI(), config.pidFfParams[1].kD(),
        ClosedLoopSlot.kSlot1);
    cfg.closedLoop.feedForward.kV(config.pidFfParams[1].kV(), ClosedLoopSlot.kSlot1)
        .kA(config.pidFfParams[1].kA(), ClosedLoopSlot.kSlot1)
        .kS(config.pidFfParams[1].kS(), ClosedLoopSlot.kSlot1)
        .kG(config.pidFfParams[1].kG(), ClosedLoopSlot.kSlot1);

    cfg.closedLoop.pid(config.pidFfParams[2].kP(), config.pidFfParams[2].kI(), config.pidFfParams[2].kD(),
        ClosedLoopSlot.kSlot2);
    cfg.closedLoop.feedForward.kV(config.pidFfParams[2].kV(), ClosedLoopSlot.kSlot2)
        .kA(config.pidFfParams[2].kA(), ClosedLoopSlot.kSlot2)
        .kS(config.pidFfParams[2].kS(), ClosedLoopSlot.kSlot2)
        .kG(config.pidFfParams[2].kG(), ClosedLoopSlot.kSlot2);

    cfg.closedLoop.pid(config.pidFfParams[3].kP(), config.pidFfParams[3].kI(), config.pidFfParams[3].kD(),
        ClosedLoopSlot.kSlot3);
    cfg.closedLoop.feedForward.kV(config.pidFfParams[3].kV(), ClosedLoopSlot.kSlot3)
        .kA(config.pidFfParams[3].kA(), ClosedLoopSlot.kSlot3)
        .kS(config.pidFfParams[3].kS(), ClosedLoopSlot.kSlot3)
        .kG(config.pidFfParams[3].kG(), ClosedLoopSlot.kSlot3);
  }

  protected void configMotionMagic(double maxVelocity, double maxAcceleration, double maxJerk) {
    cfg.closedLoop.maxMotion.cruiseVelocity(config.maxVelocity)
        .maxAcceleration(config.maxAcceleration);
  }

  protected void applyConfigs() {
    motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }

  protected void applyPidFfConfigs(int slot) {
    motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }

  protected void applyMotionMagicConfigs() {
    motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }

  protected void applyNeutralModeConfigs() {
    motor.configure(cfg, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }

  protected void setSignals() {
    positionSignal = new Data<>(() -> Rotations.of(motor.getEncoder().getPosition()));
    velocitySignal = new Data<>(() -> RPM.of(motor.getEncoder().getVelocity()));
    accelerationSignal = new Data<>(() -> {
      double currentTimestamp = Timer.getFPGATimestamp();
      double dt = currentTimestamp - lastTime;

      if (dt < 0.001) {
        return RotationsPerSecondPerSecond.of(lastAcceleration);
      }

      double currentVelocity = getCurrentVelocity();

      lastAcceleration = (currentVelocity - lastVelocity) / dt;
      lastVelocity = currentVelocity;
      lastTime = currentTimestamp;

      return RotationsPerSecondPerSecond.of(lastAcceleration);
    });
    voltageSignal = new Data<>(() -> Volts.of(motor.getAppliedOutput() * 12.0));
    currentSignal = new Data<>(() -> Amps.of(motor.getOutputCurrent()));
    closedLoopSPSignal = new Data<>(() -> getCurrentClosedLoopSP());
    closedLoopErrorSignal = new Data<>(() -> getCurrentClosedLoopError());
  }

  protected void changeMotorSlot(int slot) {
    closedLoopSlot = slot == 0 ? ClosedLoopSlot.kSlot0
        : slot == 1 ? ClosedLoopSlot.kSlot1 : slot == 2 ? ClosedLoopSlot.kSlot2 : ClosedLoopSlot.kSlot3;
  }

  protected void stopMotor() {
    motor.stopMotor();
  }

  protected void setMotorDuty(double power) {
    motor.set(power);
  }

  protected void setMotorVoltage(double voltage) {
    motor.setVoltage(voltage);
  }

  protected void setMotorVelocity(double velocity, double feedForward) {
    motor.getClosedLoopController().setSetpoint(velocity, ControlType.kMAXMotionVelocityControl, closedLoopSlot,
        feedForward);
  }

  protected void setMotorPositionVoltage(double position, double feedForward) {
    motor.getClosedLoopController().setSetpoint(position, ControlType.kPosition, closedLoopSlot, feedForward);
  }

  protected void setMotorMotionMagic(double position, double feedForward) {
    motor.getClosedLoopController().setSetpoint(position, ControlType.kMAXMotionPositionControl, closedLoopSlot,
        feedForward);
  }

  @Override
  public void checkElectronics() {
    Faults faults = motor.getFaults();
    boolean hasFault = faults.other || faults.motorType || faults.sensor ||
        faults.can || faults.temperature;

    if (hasFault) {
      Log.log(getName() + " Fault Detected: " + faults.toString(), AlertType.kError);
    }
  }

  @Override
  public boolean isConnected() {
    return motor.getFirmwareVersion() != 0;
  }

  @Override
  public void setEncoderPosition(double position) {
    motor.getEncoder().setPosition(position);
  }
}