package frc.demacia.utils.motors;

import java.util.function.Supplier;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.demacia.utils.Data;
import frc.demacia.utils.elastic.ElasticGenerator;
import frc.demacia.utils.log.Log;
import frc.demacia.utils.log.Log.LogLevel;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.sysid.Sysid;

public abstract class BaseMotor implements MotorInterface {
  protected BaseMotorConfig<?> config;
  private String name;

  private int slot = 0;

  protected Data<?> positionSignal;
  protected Data<?> velocitySignal;
  protected Data<?> accelerationSignal;
  protected Data<?> voltageSignal;
  protected Data<?> currentSignal;
  protected Data<?> closedLoopSPSignal;
  protected Data<?> closedLoopErrorSignal;

  private double wantedValue;
  private double testValue;

  private ControlMode notDutyControlMode = ControlMode.DISABLE;
  private ControlMode controlMode = ControlMode.DISABLE;

  private ControlMode valueControlMode = ControlMode.DUTYCYCLE;
  private SendableChooser<ControlMode> valueControlModeChooser = new SendableChooser<>();

  private boolean[] kFlags = { true, true, true, false, false, false };

  protected static final double MAX_SIM_VEL = 50;

  /**
   * Creates a new TalonFX motor wrapper.
   * 
   * @param config The configuration object for this motor
   */
  public BaseMotor(BaseMotorConfig<?> config) {
    this.config = config;
    name = config.name;
    createMotor();
    configMotor();
    setSignals();
    addLog();
    setName(name);
    SmartDashboard.putData("motors/" + name, this);
    Log.log(name + " motor initialized");
    ElasticGenerator.getInstance().registerMotor(this);
    Sysid.registerMotor(this);
  }

  public BaseMotorConfig<?> getConfig() {
    return config;
  }

  // /**
  // * Applies the initial configuration to the motor.
  // * Sets limits, ramps, PIDFF, and Motion Magic parameters.
  // */
  private void configMotor() {
    createMotorConfig();
    configMaxCurrent(config.maxCurrent);
    configRampUpTime(config.rampUpTime);
    configIsInverted(config.inverted);
    configNeutralMode(config.brake);
    configMaxVolt(config.maxVolt);
    configMinVolt(config.minVolt);
    configMotorRatio(config.motorRatio);
    configPidFf(config.pidFfParams);
    configMotionMagic(config.maxAcceleration, config.maxVelocity, config.maxJerk);

    applyConfigs();
  }

  public boolean isRio() {
    return config.canbus.equals(Canbus.Rio);
  }

  @Override
  public void setName(String name) {
    MotorInterface.super.setName(name);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @SuppressWarnings({ "unchecked" })
  private void addLog() {
    Log.putData(name + ": Position, Velocity, Acceleration, Voltage, Current, CloseLoopError, CloseLoopSP",
        new Data[] {
            positionSignal,
            velocitySignal,
            accelerationSignal,
            voltageSignal,
            closedLoopErrorSignal,
            closedLoopSPSignal },
        LogLevel.LOG_ONLY, "motors", false);

    Log.putData("motors/" + name + "/wanted value", this::getWantedValue);
    Log.putData("motors/" + name + "/current value", this::getCurrentValue);
    Log.putData("motors/" + name + "/is Connected", this::isConnected);

    SmartDashboard.putData("motors/" + getName() + "/test value command",
        new RunCommand(() -> applyControlModeValue(valueControlMode, testValue))
            .finallyDo(interrupted -> stop()));

    valueControlModeChooser.setDefaultOption(ControlMode.DUTYCYCLE.name(), ControlMode.DUTYCYCLE);
    for (ControlMode mode : ControlMode.class.getEnumConstants()) {
      if (mode == ControlMode.DISABLE)
        continue;
      valueControlModeChooser.addOption(mode.name(), mode);
    }
    valueControlModeChooser.onChange(mode -> this.valueControlMode = mode);
    SmartDashboard.putData("motors/" + getName() + "/Value Control Mode Chooser", valueControlModeChooser);

    configPidFf(0);
    configMotionMagic();
  }

  @Override
  public void setSlot(int slot) {
    if (slot < 0 || slot > 2) {
      Log.log("Slot must be between 0 and 2", AlertType.kError);
      return;
    }
    this.slot = slot;
    changeMotorSlot(slot);
    configPidFf(slot);
  }

  @Override
  public int getSlot() {
    return slot;
  }

  @Override
  public void setNeutralMode(boolean isBrake) {
    config.brake = isBrake;
    configNeutralMode(isBrake);
    applyNeutralModeConfigs();
  }

  @Override
  public double getWantedValue() {
    return wantedValue;
  }

  @Override
  public void stop() {
    stopMotor();
    wantedValue = 0;
    if (controlMode != ControlMode.DISABLE && controlMode != ControlMode.DUTYCYCLE) {
      notDutyControlMode = controlMode;
    }
    controlMode = ControlMode.DISABLE;
  }

  @Override
  public void setDuty(double power) {
    setMotorDuty(power);
    wantedValue = power;
    if (power == 0) {
      if (controlMode != ControlMode.DISABLE && controlMode != ControlMode.DUTYCYCLE) {
        notDutyControlMode = controlMode;
      }
      controlMode = ControlMode.DISABLE;
    } else {
      if (controlMode != ControlMode.DISABLE && controlMode != ControlMode.DUTYCYCLE) {
        notDutyControlMode = controlMode;
      }
      controlMode = ControlMode.DUTYCYCLE;
    }
  }

  @Override
  public void setVoltage(double voltage) {
    setMotorVoltage(voltage);
    wantedValue = voltage;
    controlMode = ControlMode.VOLTAGE;
  }

  @Override
  public void setVelocity(double velocity, double feedForward) {
    setMotorVelocity(velocity, feedForward + velocityFeedForward(velocity));
    wantedValue = velocity;
    controlMode = ControlMode.VELOCITY;
  }

  @Override
  public void setVelocity(double velocity) {
    setVelocity(velocity, 0);
  }

  @Override
  public void setVelocityWithAcceleration(double velocity, Supplier<Double> wantedAccelerationSupplier) {
    setVelocity(velocity, wantedAccelerationSupplier.get() * config.pidFfParams[slot].kA());
  }

  @Override
  public void setPositionVoltage(double position, double feedForward) {
    setMotorPositionVoltage(position, feedForward + positionFeedForward(position));
    wantedValue = position;
    controlMode = ControlMode.POSITION_VOLTAGE;
  }

  @Override
  public void setPositionVoltage(double position) {
    setPositionVoltage(position, 0);
  }

  @Override
  public void setMotion(double position, double feedForward) {
    setMotorMotionMagic(position, feedForward + positionFeedForward(position));
    wantedValue = position;
    controlMode = ControlMode.MAGIC_MOTION;
  }

  @Override
  public void setMotion(double position) {
    setMotion(position, 0.0);
  }

  @Override
  public void setAngle(double angle, double feedForward) {
    if (config.isRadiansMotor) {
      setMotion(getCurrentPosition() + MathUtil.angleModulus(angle - getCurrentAngle()), feedForward);
      wantedValue = MathUtil.angleModulus(angle);
      controlMode = ControlMode.ANGLE;
    } else {
      setMotion(angle, feedForward);
      Log.log(name + " cant use setAngle without being in Radians");
    }
  }

  @Override
  public void setAngle(double angle) {
    setAngle(angle, 0);
  }

  private double velocityFeedForward(double velocity) {
    return velocity * velocity * Math.signum(velocity) * config.pidFfParams[slot].kV2();
  }

  private double positionFeedForward(double position) {
    return Math.cos(position * config.posToRad) * config.pidFfParams[slot].kCos();
  }

  @Override
  public int getCurrentControlModeInteger() {
    return controlMode.ordinal();
  }

  @Override
  public ControlMode getCurrentControlMode() {
    return controlMode;
  }

  @Override
  public ControlMode getLastControlMode() {
    return notDutyControlMode;
  }

  @Override
  public double getCurrentPosition() {
    return positionSignal.getDouble();
  }

  @Override
  public double getCurrentVelocity() {
    return velocitySignal.getDouble();
  }

  @Override
  public double getCurrentAcceleration() {
    return accelerationSignal.getDouble();
  }

  @Override
  public double getCurrentAngle() {
    if (config.isRadiansMotor) {
      return MathUtil.angleModulus(getCurrentPosition());
    }
    return 0;
  }

  @Override
  public double getCurrentVoltage() {
    return voltageSignal.getDouble();
  }

  @Override
  public double getCurrentCurrent() {
    return currentSignal.getDouble();
  }

  public double getCurrentValue() {
    ControlMode mode = getCurrentControlMode();

    if (mode == ControlMode.DISABLE || mode == ControlMode.DUTYCYCLE) {
      mode = getLastControlMode();
    }

    switch (mode) {
      case VOLTAGE:
        return getCurrentVoltage();
      case VELOCITY:
        return getCurrentVelocity();
      case MAGIC_MOTION, POSITION_VOLTAGE:
        return getCurrentPosition();
      case ANGLE:
        return getCurrentAngle();
      default:
        return 0.0;
    }
  }

  @Override
  public double getCurrentClosedLoopSP() {
    return closedLoopSPSignal.getDouble();
  }

  /**
   * Calculates the software-based closed-loop error.
   */
  protected double getCalculatedError() {
    ControlMode mode = getCurrentControlMode();

    if (mode == ControlMode.VOLTAGE || mode == ControlMode.DUTYCYCLE || mode == ControlMode.DISABLE) {
      return 0.0;
    }

    double error = getWantedValue() - getCurrentValue();

    if (mode == ControlMode.ANGLE) {
      return MathUtil.angleModulus(error);
    }

    return error;
  }

  @Override
  public double getCurrentClosedLoopError() {
    if (getCurrentControlMode() == ControlMode.ANGLE) {
      return getCalculatedError();
    }

    double hardwareError = closedLoopErrorSignal.getDouble();

    return hardwareError;
  }

  @Override
  public boolean[] getSysidFlags() {
    return kFlags;
  }

  @Override
  public void setConfigPidFf(CloseLoopParam newParams, int Slot) {
    config.pidFfParams[slot] = newParams;
    configPidFf(config.pidFfParams);
    applyPidFfConfigs(slot);
  }

  @Override
  public void setConfigMotionParam(double maxVelocity, double maxAcceleration, double maxJerk) {
    config.maxVelocity = maxVelocity;
    config.maxAcceleration = maxAcceleration;
    config.maxJerk = maxJerk;
    applyMotionMagicConfigs();
  }

  /**
   * Creates a command to configure PIDFF and FeedForward parameters via the
   * Dashboard.
   * 
   * @param slot The slot index to tune
   */
  private void configPidFf(int slot) {
    Command configPidFfCmd = new InstantCommand(() -> {
      applyPidFfConfigs(slot);
    }).ignoringDisable(true);

    SmartDashboard.putData("motors/" + getName() + "/PID+FF config slot " + slot, new Sendable() {
      @Override
      public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("PID+FF Config");
        boolean[] flags = getSysidFlags();

        builder.addDoubleProperty("KP", () -> config.pidFfParams[slot].kP(), (v) -> config.pidFfParams[slot].setKP(v));
        builder.addDoubleProperty("KI", () -> config.pidFfParams[slot].kI(), (v) -> config.pidFfParams[slot].setKI(v));
        builder.addDoubleProperty("KD", () -> config.pidFfParams[slot].kD(), (v) -> config.pidFfParams[slot].setKD(v));
        builder.addBooleanProperty("USE_KS", () -> flags[0], (v) -> flags[0] = v);
        builder.addDoubleProperty("KS", () -> config.pidFfParams[slot].kS(), (v) -> config.pidFfParams[slot].setKS(v));
        builder.addBooleanProperty("USE_KV", () -> flags[1], (v) -> flags[1] = v);
        builder.addDoubleProperty("KV", () -> config.pidFfParams[slot].kV(), (v) -> config.pidFfParams[slot].setKV(v));
        builder.addBooleanProperty("USE_KA", () -> flags[2], (v) -> flags[2] = v);
        builder.addDoubleProperty("KA", () -> config.pidFfParams[slot].kA(), (v) -> config.pidFfParams[slot].setKA(v));
        builder.addBooleanProperty("USE_KG", () -> flags[3], (v) -> flags[3] = v);
        builder.addDoubleProperty("KG", () -> config.pidFfParams[slot].kG(), (v) -> config.pidFfParams[slot].setKG(v));
        builder.addBooleanProperty("USE_KCOS", () -> flags[4], (v) -> flags[4] = v);
        builder.addDoubleProperty("KCOS", () -> config.pidFfParams[slot].kCos(), (v) -> config.pidFfParams[slot].setKCos(v));
        builder.addBooleanProperty("USE_KV2", () -> flags[5], (v) -> flags[5] = v);
        builder.addDoubleProperty("KV2", () -> config.pidFfParams[slot].kV2(), (v) -> config.pidFfParams[slot].setKV2(v));

        builder.addBooleanProperty("Update", () -> configPidFfCmd.isScheduled(),
            value -> {
              if (value && !configPidFfCmd.isScheduled()) {
                CommandScheduler.getInstance().schedule(configPidFfCmd);
              } else if (!value && configPidFfCmd.isScheduled()) {
                configPidFfCmd.cancel();
              }
            });
      }
    });
  }

  /**
   * Creates a command to configure Motion Magic parameters via the Dashboard.
   */
  private void configMotionMagic() {
    Command configMotionMagicCmd = new InstantCommand(this::applyMotionMagicConfigs).ignoringDisable(true);

    SmartDashboard.putData("motors/" + getName() + "/Motion Magic Config", new Sendable() {
      @Override
      public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Motion Magic Config");

        builder.addDoubleProperty("Vel", () -> config.maxVelocity, (maxVelocity) -> config.maxVelocity = maxVelocity);
        builder.addDoubleProperty("Acc", () -> config.maxAcceleration,
            (maxAcceleration) -> config.maxAcceleration = maxAcceleration);
        builder.addDoubleProperty("Jerk", () -> config.maxJerk, (maxJerk) -> config.maxJerk = maxJerk);
        builder.addBooleanProperty("Update", () -> configMotionMagicCmd.isScheduled(),
            value -> {
              if (value && !configMotionMagicCmd.isScheduled()) {
                CommandScheduler.getInstance().schedule(configMotionMagicCmd);
              } else if (!value && configMotionMagicCmd.isScheduled()) {
                configMotionMagicCmd.cancel();
              }
            });
      }
    });
  }

  public void applyControlModeValue(ControlMode mode, double value) {
    switch (mode) {
      case VOLTAGE:
        setVoltage(value);
        break;
      case VELOCITY:
        setVelocity(value);
        break;
      case POSITION_VOLTAGE:
        setPositionVoltage(value);
        break;
      case MAGIC_MOTION:
        setMotion(value);
        break;
      case ANGLE:
        setAngle(value);
        break;
      case DUTYCYCLE:
      case DISABLE:
      default:
        setDuty(value);
        break;
    }
  }

  public boolean isReady(double allowedError) {
    return Math.abs(getCurrentClosedLoopError()) < allowedError;
  }

  @Override
  public boolean isRadiansMotor() {
    return config.isRadiansMotor;
  }

  @Override
  public double getTestValue() {
    return testValue;
  }

  @Override
  public void setTestValue(double testValue) {
    this.testValue = testValue;
  }

  public double gearRatio() {
    return config.motorRatio;
  }

  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("Motor");
    builder.addBooleanProperty("Is Connected", this::isConnected, null);
    builder.addDoubleProperty("CloseLoopError", this::getCurrentClosedLoopError, null);
    builder.addDoubleProperty("Position", this::getCurrentPosition, null);
    builder.addDoubleProperty("Velocity", this::getCurrentVelocity, null);
    builder.addDoubleProperty("Acceleration", this::getCurrentAcceleration, null);
    builder.addDoubleProperty("Voltage", this::getCurrentVoltage, null);
    builder.addDoubleProperty("Current", this::getCurrentCurrent, null);
    if (isRadiansMotor()) {
      builder.addDoubleProperty("Angle", this::getCurrentAngle, null);
    }
    builder.addDoubleProperty("Value", this::getCurrentValue, null);
    builder.addDoubleProperty("ControlMode", this::getCurrentControlModeInteger, null);
    builder.addDoubleProperty("Wanted Value", this::getWantedValue, null);

    builder.addDoubleProperty("test Value", this::getTestValue, (value) -> setTestValue(value));
  }

  // Raw data Accessors
  public Data<?> getClosedLoopErrorSignal() {
    return closedLoopErrorSignal;
  }

  public Data<?> getClosedLoopSPSignal() {
    return closedLoopSPSignal;
  }

  public Data<?> getPositionSignal() {
    return positionSignal;
  }

  public Data<?> getVelocitySignal() {
    return velocitySignal;
  }

  public Data<?> getAccelerationSignal() {
    return accelerationSignal;
  }

  public Data<?> getVoltageSignal() {
    return voltageSignal;
  }

  public Data<?> getCurrentSignal() {
    return currentSignal;
  }

  protected abstract void createMotor();

  protected abstract void createMotorConfig();

  protected abstract void configMaxCurrent(double maxCurrent);

  protected abstract void configRampUpTime(double rampUpTime);

  protected abstract void configIsInverted(boolean isInverted);

  protected abstract void configNeutralMode(boolean isBrake);

  protected abstract void configMaxVolt(double maxVolt);

  protected abstract void configMinVolt(double minVolt);

  protected abstract void configMotorRatio(double motorRatio);

  protected abstract void configPidFf(CloseLoopParam[] pidFfParams);

  protected abstract void configMotionMagic(double maxVelocity, double maxAcceleration, double maxJerk);

  protected abstract void applyConfigs();

  protected abstract void applyPidFfConfigs(int slot);

  protected abstract void applyMotionMagicConfigs();

  protected abstract void applyNeutralModeConfigs();

  // protected abstract void configSoftwareLimit(double min, double max);

  protected abstract void setSignals();

  protected abstract void changeMotorSlot(int slot);

  protected abstract void stopMotor();

  protected abstract void setMotorDuty(double power);

  protected abstract void setMotorVoltage(double voltage);

  protected abstract void setMotorVelocity(double velocity, double feedForward);

  protected abstract void setMotorPositionVoltage(double position, double feedForward);

  protected abstract void setMotorMotionMagic(double position, double feedForward);
}