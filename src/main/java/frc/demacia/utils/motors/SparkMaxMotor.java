package frc.demacia.utils.motors;

import java.util.function.Supplier;

import com.revrobotics.REVLibError;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.demacia.utils.log.LogManager;
import frc.demacia.utils.log.LogEntryBuilder.LogLevel;

/**
 * Wrapper class for the REV Spark Max motor controller.
 * <p>
 * Handles configuration, PID control, logging, and on-the-fly tuning via SmartDashboard.
 * Uses the REV Lib 2025 API.
 * </p>
 */
public class SparkMaxMotor extends SparkMax implements MotorInterface {

  private frc.demacia.utils.motors.SparkMaxConfig config;
  private String name;
  private SparkMaxConfig cfg;
  private ClosedLoopSlot closedLoopSlot = ClosedLoopSlot.kSlot0;
  private ControlType controlType = ControlType.kDutyCycle;

  private ControlMode controlMode = ControlMode.DISABLE;
  
  // Variables for manual velocity/acceleration calculation
  private double lastVelocity;
  private double lastAcceleration;
  private double setPoint = 0;
  private double lastTime = 0;

  // Motor Stalling
  private final Timer stallTimer = new Timer();
  private boolean conditionActive = false;
  private boolean IsDone = false;
  private boolean isStalled = false;

  /**
   * Creates a new Spark Max motor wrapper.
   * @param config The configuration object
   */
  public SparkMaxMotor(frc.demacia.utils.motors.SparkMaxConfig config) {
    super(config.id, SparkLowLevel.MotorType.kBrushless);
    this.config = config;
    name = config.name;
    configMotor();
    addLog();
    setName(name);
    // SmartDashboard.putData(name, this);
    LogManager.log(name + " motor initialized");
  }

  /**
   * Applies the configuration to the motor.
   * Sets current limits, ramps, inversion, idle mode, and PID slots.
   */
  private void configMotor() {
    cfg = new SparkMaxConfig();
    cfg.smartCurrentLimit((int) config.maxCurrent);
    cfg.openLoopRampRate(config.rampUpTime);
    cfg.closedLoopRampRate(config.rampUpTime);
    cfg.inverted(config.inverted);
    cfg.idleMode(config.brake ? SparkBaseConfig.IdleMode.kBrake : SparkBaseConfig.IdleMode.kCoast);
    cfg.voltageCompensation(config.maxVolt);
    cfg.encoder.positionConversionFactor(1 / config.motorRatio);
    cfg.encoder.velocityConversionFactor(1 / config.motorRatio);
    updatePID(false);
    if (config.maxVelocity != 0) {
      cfg.closedLoop.maxMotion.cruiseVelocity(config.maxVelocity).maxAcceleration(config.maxAcceleration);
    }
    configure(cfg, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
  }

  /**
   * Updates PID constants in the config object.
   * @param apply Whether to apply the config to the motor immediately
   */
  private void updatePID(boolean apply) {
    cfg.closedLoop.pid(config.pid[0].kP(), config.pid[0].kI(), config.pid[0].kD(), 
        ClosedLoopSlot.kSlot0);
    cfg.closedLoop.feedForward.kV(config.pid[0].kV(), ClosedLoopSlot.kSlot0)
      .kA(config.pid[0].kA(), ClosedLoopSlot.kSlot0)
      .kS(config.pid[0].kS(), ClosedLoopSlot.kSlot0)
      .kG(config.pid[0].kG(), ClosedLoopSlot.kSlot0);
    cfg.closedLoop.pid(config.pid[1].kP(), config.pid[1].kI(), config.pid[1].kD(), 
        ClosedLoopSlot.kSlot1);
    cfg.closedLoop.feedForward.kV(config.pid[1].kV(), ClosedLoopSlot.kSlot1)
      .kA(config.pid[1].kA(), ClosedLoopSlot.kSlot1)
      .kS(config.pid[1].kS(), ClosedLoopSlot.kSlot1)
      .kG(config.pid[1].kG(), ClosedLoopSlot.kSlot1);
    cfg.closedLoop.pid(config.pid[2].kP(), config.pid[2].kI(), config.pid[2].kD(), 
        ClosedLoopSlot.kSlot2);
    cfg.closedLoop.feedForward.kV(config.pid[2].kV(), ClosedLoopSlot.kSlot2)
      .kA(config.pid[2].kA(), ClosedLoopSlot.kSlot2)
      .kS(config.pid[2].kS(), ClosedLoopSlot.kSlot2)
      .kG(config.pid[2].kG(), ClosedLoopSlot.kSlot2);
    cfg.closedLoop.pid(config.pid[3].kP(), config.pid[3].kI(), config.pid[3].kD(), 
        ClosedLoopSlot.kSlot3);
    cfg.closedLoop.feedForward.kV(config.pid[3].kV(), ClosedLoopSlot.kSlot3)
      .kA(config.pid[3].kA(), ClosedLoopSlot.kSlot3)
      .kS(config.pid[3].kS(), ClosedLoopSlot.kSlot3)
      .kG(config.pid[3].kG(), ClosedLoopSlot.kSlot3);
    if (apply) {
      configure(cfg, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
    }
  }

  @Override
  public void setName(String name) {
      MotorInterface.super.setName(name);
      this.name = name;
  }

  /** Configures the logging entries for this motor */
  @SuppressWarnings("unchecked")
  private void addLog() {
    LogManager.addEntry(name + ": Position, Velocity, Acceleration, Voltage, Current, CloseLoopError, CloseLoopSP", 
        () -> getCurrentPosition(),
        () -> getCurrentVelocity(),
        () -> getCurrentAcceleration(),
        () -> getCurrentVoltage(),
        () -> getCurrentCurrent(),
        () -> getCurrentClosedLoopError(),
        () -> getCurrentClosedLoopSP(),
        () -> getCurrentControlMode()
      ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP)
      .withIsMotor()
      .build();
  }

  @Override
  public void checkElectronics() {
    Faults faults = getFaults();
    boolean hasFault = faults.other || faults.motorType || faults.sensor || 
      faults.can || faults.temperature;

    if (hasFault) {
        LogManager.log(name + " Fault Detected: " + faults.toString(), AlertType.kError);
    }
  }

  /**
   * Changes the active PID slot for the closed loop controller.
   * @param slot The slot index (0-3)
   */
  public void changeSlot(int slot) {
    if (slot < 0 || slot > 3) {
      LogManager.log("slot is not between 0 and 2", AlertType.kError);
      return;
    }
    this.closedLoopSlot = slot == 0 ? ClosedLoopSlot.kSlot0 : slot == 1 ? ClosedLoopSlot.kSlot1 : ClosedLoopSlot.kSlot2;
  }

  @Override
  public void setNeutralMode(boolean isBrake) {
    cfg.idleMode(isBrake ? SparkBaseConfig.IdleMode.kBrake : SparkBaseConfig.IdleMode.kCoast);
    configure(cfg, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
  }

  @Override
  public REVLibError clearFaults() {
    return super.clearFaults();
  }

  @Override
  public void setDuty(double power) {
    super.set(power);
    controlType = ControlType.kDutyCycle;
    if (power == 0){
      controlMode = ControlMode.DISABLE;
  } else {
      controlMode = ControlMode.DUTYCYCLE;
  }
  }

  @Override
  public void setVoltage(double voltage) {
    super.setVoltage(voltage);
    controlType = ControlType.kVoltage;
    controlMode = ControlMode.VOLTAGE;
  }

  @Override
  public void setVelocity(double velocity, double feedForward) {
    if (config.maxVelocity == 0) {
      LogManager.log(name + ": maxVelocity not configured", AlertType.kError);
      return;
    }
    getClosedLoopController().setSetpoint(velocity, ControlType.kMAXMotionVelocityControl, closedLoopSlot, feedForward + velocityFeedForward(velocity) + config.pid[closedLoopSlot.value].kS()*Math.signum(velocity));
    controlType = ControlType.kMAXMotionVelocityControl;
    controlMode = ControlMode.VELOCITY;
    setPoint = velocity;
  }

  @Override
  public void setVelocity(double velocity) {
    setVelocity(velocity, 0);
  }

  @Override
  public void setVelocityWithAcceleration(double velocity, Supplier<Double> wantedAccelerationSupplier) {
      setVelocity(velocity, wantedAccelerationSupplier.get() * config.pid[closedLoopSlot.value].kA());
  }

  @Override
  public void setPositionVoltage(double position, double feedForward) {
    getClosedLoopController().setSetpoint(position, ControlType.kPosition, closedLoopSlot, feedForward);
    controlType = ControlType.kPosition;
    controlMode = ControlMode.POSITION_VOLTAGE;
    setPoint = position;
  }

  @Override
  public void setPositionVoltage(double position) {
    setPositionVoltage(position, 0);
  }

  @Override
  public void setMotion(double position, double feedForward) {
    if (config.maxVelocity == 0) {
      LogManager.log(name + ": maxVelocity not configured", AlertType.kError);
      return;
    }
    getClosedLoopController().setSetpoint(position, ControlType.kMAXMotionPositionControl, closedLoopSlot, feedForward + config.pid[closedLoopSlot.value].kS() + positionFeedForward(position));
    controlType = ControlType.kMAXMotionPositionControl;
    controlMode = ControlMode.MOTION;
    setPoint = position;
  } 

  @Override
  public void setMotion(double position) {
    setMotion(position, 0);
  }

  @Override
  public void setAngle(double angle, double feedForward) {
    setMotion(getCurrentPosition() + MathUtil.angleModulus(angle - getCurrentAngle()), feedForward);
    controlMode = ControlMode.ANGLE;
  }

  @Override
  public void setAngle(double angle) {
    setAngle(angle, 0);
  }

  private double velocityFeedForward(double velocity) {
    return velocity * velocity * Math.signum(velocity) * config.kv2;
  }

  private double positionFeedForward(double position) {
    return Math.cos(position * config.posToRad) * config.kSin;
  }

  @Override
  public int getCurrentControlMode() {
    return controlMode.ordinal();
  }

  @Override
  public double getCurrentClosedLoopError() {
    switch (controlType) {
      case kPosition, kMAXMotionPositionControl:
        return setPoint - getCurrentPosition();
      case kVelocity, kMAXMotionVelocityControl:
        return setPoint - getCurrentVelocity();
      default:
        return 0;
    }
  }

  @Override
  public double getCurrentClosedLoopSP() {
    return setPoint;
  }

  @Override
  public double getCurrentPosition() {
    return getEncoder().getPosition();
  }

  @Override
  public double getCurrentAngle() {
    if (config.isRadiansMotor) {
      return MathUtil.angleModulus(getCurrentPosition());
    }
    return 0;
  }

  @Override
  public double getCurrentVelocity() {
    double velocity = getEncoder().getVelocity();
    double time = Timer.getFPGATimestamp();
    double dt = time - lastTime;
    if (dt > 0) {
        lastAcceleration = (velocity - lastVelocity) / dt;
        lastTime = time;
        lastVelocity = velocity;
    }
    return velocity;
  }

  @Override
  public double getCurrentAcceleration() {
    return lastAcceleration;
  }

  @Override
  public double getCurrentVoltage() {
    return getAppliedOutput() * 12;
  }
  
  @Override
  public double getCurrentCurrent() {
    return getOutputCurrent();
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("Spark Motor");
    builder.addDoubleProperty("ControlMode", this::getCurrentControlMode, null);
    builder.addDoubleProperty("Position", this::getCurrentPosition, null);
    builder.addDoubleProperty("Velocity", this::getCurrentVelocity, null);
    builder.addDoubleProperty("Voltage", this::getCurrentVoltage, null);
    builder.addDoubleProperty("Current", this::getCurrentCurrent, null);
    builder.addDoubleProperty("CloseLoop Error", this::getCurrentClosedLoopError, null);
    if (config.isRadiansMotor) {
      builder.addDoubleProperty("Angle", this::getCurrentAngle, null);
    }

  }
    
  /**
   * Creates a command to configure PID and FeedForward parameters via the Dashboard.
   * Useful for tuning without redeploying code.
   * @param slot The slot index to tune (0-3)
   */
  public void configPidFf(int slot) {

    Command configPidFf = new InstantCommand(()-> {
      cfg = new SparkMaxConfig();
      closedLoopSlot = slot == 0 ? ClosedLoopSlot.kSlot0 : slot == 1 ? ClosedLoopSlot.kSlot1 : ClosedLoopSlot.kSlot2;
      cfg.closedLoop.pid(config.pid[slot].kP(), config.pid[slot].kI(), config.pid[slot].kD(), 
        closedLoopSlot);
      cfg.closedLoop.feedForward.kV(config.pid[slot].kV(), closedLoopSlot)
        .kA(config.pid[slot].kA(), closedLoopSlot)
        .kS(config.pid[slot].kS(), closedLoopSlot)
        .kG(config.pid[slot].kG(), closedLoopSlot);
      configure(cfg, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
    }).ignoringDisable(true);

    SmartDashboard.putData(name + "/PID+FF config", new Sendable() {
      @Override
      public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("PID+FF Config");
          builder.addDoubleProperty("KP", ()-> config.pid[0].kP(), (double newValue) -> config.pid[0].setKP(newValue));
          builder.addDoubleProperty("KI", ()-> config.pid[0].kI(), (double newValue) -> config.pid[0].setKI(newValue));
          builder.addDoubleProperty("KD", ()-> config.pid[0].kD(), (double newValue) -> config.pid[0].setKD(newValue));
          builder.addDoubleProperty("KS", ()-> config.pid[0].kS(), (double newValue) -> config.pid[0].setKS(newValue));
          builder.addDoubleProperty("KV", ()-> config.pid[0].kV(), (double newValue) -> config.pid[0].setKV(newValue));
          builder.addDoubleProperty("KA", ()-> config.pid[0].kA(), (double newValue) -> config.pid[0].setKA(newValue));
          builder.addDoubleProperty("KG", ()-> config.pid[0].kG(), (double newValue) -> config.pid[0].setKG(newValue));
        
        builder.addBooleanProperty("Update", ()-> configPidFf.isScheduled(), 
          value -> {
            if (value) {
              if (!configPidFf.isScheduled()) {
                CommandScheduler.getInstance().schedule(configPidFf);
              }
            } else {
              if (configPidFf.isScheduled()) {
                configPidFf.cancel();
              }
            }
          }
        );
      }
    });
  }

  /**
   * Creates a command to configure Motion Magic parameters via the Dashboard.
   * Useful for tuning velocity/acceleration constraints live.
   */
  public void configMotionMagic() {
    Command configMotionMagic = new InstantCommand(()-> {
      cfg = new SparkMaxConfig();
      
      cfg.closedLoop.maxMotion.cruiseVelocity(config.maxVelocity).maxAcceleration(config.maxAcceleration);
      
      configure(cfg, com.revrobotics.ResetMode.kNoResetSafeParameters, com.revrobotics.PersistMode.kPersistParameters);
    }).ignoringDisable(true);
    
    SmartDashboard.putData(name + "/Motion Magic Config", new Sendable() {
      @Override
      public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Motion Magic Config");
        
        builder.addDoubleProperty("Vel", ()-> config.maxVelocity, value-> config.maxVelocity = value);
        builder.addDoubleProperty("Acc", ()-> config.maxAcceleration, value-> config.maxAcceleration = value);
        
        builder.addBooleanProperty("Update", ()-> configMotionMagic.isScheduled(), 
        value -> {
          if (value) {
            if (!configMotionMagic.isScheduled()) {
              CommandScheduler.getInstance().schedule(configMotionMagic);
            }
          } else {
            if (configMotionMagic.isScheduled()) {
              configMotionMagic.cancel();
            }
          }
        }
        );
      }
    });
  }
  public void updateStallDetection() {
    if (config.conditionIsTrue == null || config.lowVelocityThreshold == 0)
      return;
    double currentVelocity = Math.abs(getCurrentVelocity());
    double currentCurrent = getCurrentCurrent();
    if (currentCurrent > config.highCurrentThreshold && currentVelocity < config.lowVelocityThreshold) {
      if (!conditionActive) {
        stallTimer.restart();
        conditionActive = true;
        IsDone = false;
        isStalled = true;

      }
      if (stallTimer.hasElapsed(config.secondsThreshold) && !IsDone) {
        config.conditionIsTrue.accept(config);
        IsDone = true;
      }
    } else if (conditionActive) {
      stallTimer.stop();
      stallTimer.reset();
      conditionActive = false;
      IsDone = false;
      isStalled = false;
    }
  }
  public boolean getStallDetection() {
  return isStalled;
}

  public double gearRatio() {
    return config.motorRatio;
  }

  public String getName() {
    return name;
  }

  @Override
  public void setEncoderPosition(double position) {
    getEncoder().setPosition(position);
  }

  public void stop(){
      stopMotor();
      controlMode = ControlMode.DISABLE;
  }
}