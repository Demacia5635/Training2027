package frc.demacia.utils.motors;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.StaticFeedforwardSignValue;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Alert.AlertType;
import frc.demacia.utils.Data;
import frc.demacia.utils.log.Log;

/**
 * Wrapper class for the TalonFX motor controller using Phoenix 6.
 * <p>
 * Handles configuration, control requests (Voltage, Velocity, MotionMagic),
 * and integrates with the logging system and SmartDashboard.
 * </p>
 */
public class TalonFXMotor extends BaseMotor {
  private TalonFX motor;
  private TalonFXConfiguration cfg;

  // Phoenix 6 Control Requests
  private DutyCycleOut dutyCycle;
  private VoltageOut voltageOut;
  private VelocityVoltage velocityVoltage;
  private MotionMagicVoltage motionMagicVoltage;
  private PositionVoltage positionVoltage;

  /**
   * Creates a new TalonFX motor wrapper.
   * 
   * @param config The configuration object for this motor
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public TalonFXMotor(TalonFXConfig config) {
    super(config);

    dutyCycle = new DutyCycleOut(0);
    voltageOut = new VoltageOut(0);
    velocityVoltage = new VelocityVoltage(0).withSlot(getSlot());
    motionMagicVoltage = new MotionMagicVoltage(0).withSlot(getSlot());
    positionVoltage = new PositionVoltage(0).withSlot(getSlot());

    if (RobotBase.isSimulation()) {
      motor.getSimState().setSupplyVoltage(12);
      
      new Data(() -> {
        double vel = getCurrentVelocity();
        double pos = getCurrentPosition();
    
        double newPos = pos + vel * 0.02;
        double newPosRot = newPos * config.motorRatio * (config.inverted ? -1 : 1);

        motor.getSimState().setRawRotorPosition(newPosRot);
        return 0;
      });
    }
  }

  protected void createMotor() {
    motor = new TalonFX(config.id, config.canbus.canbus);
  }

  protected void createMotorConfig() {
    cfg = new TalonFXConfiguration();
  }

  protected void configMaxCurrent(double maxCurrent) {
    cfg.CurrentLimits.SupplyCurrentLimit = maxCurrent;
    cfg.CurrentLimits.SupplyCurrentLowerLimit = maxCurrent;
    cfg.CurrentLimits.SupplyCurrentLowerTime = 0.1;
  }

  protected void configRampUpTime(double rampUpTime) {
    cfg.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = rampUpTime;
    cfg.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = rampUpTime;
    cfg.ClosedLoopRamps.VoltageClosedLoopRampPeriod = rampUpTime;
    cfg.OpenLoopRamps.VoltageOpenLoopRampPeriod = rampUpTime;
  }

  protected void configIsInverted(boolean isInverted) {
    cfg.MotorOutput.Inverted = isInverted ? InvertedValue.Clockwise_Positive
        : InvertedValue.CounterClockwise_Positive;
  }

  protected void configNeutralMode(boolean isBrake) {
    cfg.MotorOutput.NeutralMode = isBrake ? NeutralModeValue.Brake : NeutralModeValue.Coast;
  }

  protected void configMaxVolt(double maxVolt) {
    cfg.MotorOutput.PeakForwardDutyCycle = maxVolt / 12.0;
    cfg.Voltage.PeakForwardVoltage = maxVolt;
  }

  protected void configMinVolt(double minVolt) {
    cfg.MotorOutput.PeakReverseDutyCycle = minVolt / 12.0;
    cfg.Voltage.PeakReverseVoltage = minVolt;
  }

  protected void configMotorRatio(double motorRatio) {
    cfg.Feedback.SensorToMechanismRatio = motorRatio;
  }

  protected void configPidFf(CloseLoopParam[] pidFfParams) {
    cfg.Slot0.kP = pidFfParams[0].kP();
    cfg.Slot0.kI = pidFfParams[0].kI();
    cfg.Slot0.kD = pidFfParams[0].kD();
    cfg.Slot0.kS = pidFfParams[0].kS();
    cfg.Slot0.kV = pidFfParams[0].kV();
    cfg.Slot0.kA = pidFfParams[0].kA();
    cfg.Slot0.kG = pidFfParams[0].kG();
    cfg.Slot0.StaticFeedforwardSign = StaticFeedforwardSignValue.UseClosedLoopSign;

    cfg.Slot1.kP = pidFfParams[1].kP();
    cfg.Slot1.kI = pidFfParams[1].kI();
    cfg.Slot1.kD = pidFfParams[1].kD();
    cfg.Slot1.kS = pidFfParams[1].kS();
    cfg.Slot1.kV = pidFfParams[1].kV();
    cfg.Slot1.kA = pidFfParams[1].kA();
    cfg.Slot1.kG = pidFfParams[1].kG();
    cfg.Slot1.StaticFeedforwardSign = StaticFeedforwardSignValue.UseClosedLoopSign;

    cfg.Slot2.kP = pidFfParams[2].kP();
    cfg.Slot2.kI = pidFfParams[2].kI();
    cfg.Slot2.kD = pidFfParams[2].kD();
    cfg.Slot2.kS = pidFfParams[2].kS();
    cfg.Slot2.kV = pidFfParams[2].kV();
    cfg.Slot2.kA = pidFfParams[2].kA();
    cfg.Slot2.kG = pidFfParams[2].kG();
    cfg.Slot2.StaticFeedforwardSign = StaticFeedforwardSignValue.UseClosedLoopSign;
  }

  protected void configMotionMagic(double maxVelocity, double maxAcceleration, double maxJerk) {
    cfg.MotionMagic.MotionMagicAcceleration = maxAcceleration;
    cfg.MotionMagic.MotionMagicCruiseVelocity = maxVelocity;
    cfg.MotionMagic.MotionMagicJerk = maxJerk;
  }

  protected void applyConfigs() {
    motor.getConfigurator().apply(cfg);
  }

  protected void applyPidFfConfigs(int slot) {
    motor.getConfigurator().apply(cfg.Slot0);
    motor.getConfigurator().apply(cfg.Slot1);
    motor.getConfigurator().apply(cfg.Slot2);
  }

  protected void applyMotionMagicConfigs() {
    motor.getConfigurator().apply(cfg.MotionMagic);
  }

  protected void applyNeutralModeConfigs() {
    motor.getConfigurator().apply(cfg.MotorOutput);
  }

  protected void setSignals() {
    positionSignal = new Data<>(motor.getPosition(), isRio());
    velocitySignal = new Data<>(motor.getVelocity(), isRio());
    accelerationSignal = new Data<>(motor.getAcceleration(), isRio());
    voltageSignal = new Data<>(motor.getMotorVoltage(), isRio());
    currentSignal = new Data<>(motor.getStatorCurrent(), isRio());
    closedLoopSPSignal = new Data<>(motor.getClosedLoopReference(), isRio());
    closedLoopErrorSignal = new Data<>(motor.getClosedLoopError(), isRio());
  }

  protected void changeMotorSlot(int slot) {
    velocityVoltage.withSlot(slot);
    motionMagicVoltage.withSlot(slot);
    positionVoltage.withSlot(slot);
  }

  protected void stopMotor() {
    motor.stopMotor();

    if (RobotBase.isSimulation()) {
      motor.getSimState().setRotorVelocity(0);
    }
  }

  protected void setMotorDuty(double power) {
    motor.setControl(dutyCycle.withOutput(power));

    if (RobotBase.isSimulation()) {
      motor.getSimState().setRotorVelocity(power * MAX_SIM_VEL * config.motorRatio * (config.inverted ? -1 : 1));
    }
  }

  protected void setMotorVoltage(double voltage) {
    motor.setControl(voltageOut.withOutput(voltage));

    if (RobotBase.isSimulation()) {
      double power = voltage / 12.0;
      motor.getSimState().setRotorVelocity(power * MAX_SIM_VEL * config.motorRatio * (config.inverted ? -1 : 1));
    }
  }

  protected void setMotorVelocity(double velocity, double feedForward) {
    motor.setControl(velocityVoltage.withVelocity(velocity).withFeedForward(feedForward));

    if (RobotBase.isSimulation()) {
      motor.getSimState().setRotorVelocity(velocity * config.motorRatio * (config.inverted ? -1 : 1));
    }
  }

  protected void setMotorPositionVoltage(double position, double feedForward) {
    motor.setControl(positionVoltage.withPosition(position).withFeedForward(feedForward));

    if (RobotBase.isSimulation()) {
      motor.getSimState().setRawRotorPosition(position * config.motorRatio * (config.inverted ? -1 : 1));
    }
  }

  protected void setMotorMotionMagic(double position, double feedForward) {
    motor.setControl(motionMagicVoltage.withPosition(position).withFeedForward(feedForward));

    if (RobotBase.isSimulation()) {
      motor.getSimState().setRawRotorPosition(position * config.motorRatio * (config.inverted ? -1 : 1));
    }
  }

  @Override
  public void checkElectronics() {
    int fault = motor.getFaultField().getValue();
    if (fault != 0) {
      Log.log(getName() + " has fault num: " + fault, AlertType.kError);
    }
  }

  @Override
  public boolean isConnected() {
    return motor.isConnected();
  }

  @Override
  public void setEncoderPosition(double position) {
    motor.setPosition(position);
  }

public void set(double power) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'set'");
}

public double moudulestats() {
	// TODO Auto-generated method stub
	throw new UnsupportedOperationException("Unimplemented method 'moudulestats'");
}
}