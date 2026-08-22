package frc.demacia.utils.motors;

import java.util.function.Supplier;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.Timer;
import frc.demacia.utils.log.LogManager;

/**
 * Wrapper class for the CTRE Talon SRX motor controller using Phoenix 5.
 * <p>
 * Implements the MotorInterface for standard control.
 * <b>Note:</b> Many advanced control methods (Velocity, Motion Magic) are
 * currently
 * unimplemented in this wrapper and will log an error if called.
 * </p>
 */
public class TalonSRXMotor extends TalonSRX implements MotorInterface {
    TalonSRXConfig config;
    String name;

    int slot = 0;

    ControlMode controlMode = ControlMode.DISABLE;
    // Motor Stalling
    private final Timer stallTimer = new Timer();
    private boolean conditionActive = false;
    private boolean IsDone = false;
    private boolean isStalled = false;

    /**
     * Creates a new Talon SRX motor wrapper.
     * 
     * @param config The configuration object
     */
    public TalonSRXMotor(TalonSRXConfig config) {
        super(config.id);
        this.config = config;
        name = config.name;
        configMotor();
        addLog();
        setName(name);
        // SmartDashboard.putData(name, this);
        LogManager.log(name + " motor initialized");
    }

    /**
     * Applies the configuration to the motor using Phoenix 5 API.
     * Sets limits, ramps, inversion, and neutral mode.
     */
    private void configMotor() {
        configFactoryDefault();
        configContinuousCurrentLimit((int) config.maxCurrent);
        configPeakCurrentLimit((int) config.maxCurrent);
        configPeakCurrentDuration(100);
        enableCurrentLimit(true);
        configClosedloopRamp(config.rampUpTime);
        configOpenloopRamp(config.rampUpTime);
        setInverted(config.inverted);
        setNeutralMode(config.brake ? NeutralMode.Brake : NeutralMode.Coast);
        configPeakOutputForward(config.maxVolt / 12.0);
        configPeakOutputReverse(config.minVolt / 12.0);
        configVoltageCompSaturation(config.maxVolt);
        enableVoltageCompensation(true);
    }

    @Override
    public void setName(String name) {
        MotorInterface.super.setName(name);
        this.name = name;
    }

    /** Configures the logging entries for this motor */
    private void addLog() {
        // LogManager.addEntry(name + ": position, Velocity, Acceleration, Voltage,
        // Current, CloseLoopError, CloseLoopSP",
        // () -> getCurrentPosition(),
        // () -> getCurrentVelocity(),
        // () -> getCurrentAcceleration(),
        // () -> getCurrentVoltage(),
        // () -> getCurrentCurrent(),
        // () -> getCurrentClosedLoopError(),
        // () -> getCurrentClosedLoopSP()
        // ).withLogLevel(LogLevel.LOG_ONLY_NOT_IN_COMP)
        // .withIsMotor().build();
    }

    @Override
    public void checkElectronics() {
        com.ctre.phoenix.motorcontrol.Faults faults = new com.ctre.phoenix.motorcontrol.Faults();
        getFaults(faults);
        if (faults.hasAnyFault()) {
            LogManager.log(name + " have fault num: " + faults.toString(), AlertType.kError);
        }
    }

    @Override
    public void changeSlot(int slot) {
        if (slot < 0 || slot > 2) {
            LogManager.log("slot is not between 0 and 2", AlertType.kError);
            return;
        }
        this.slot = slot;
    }

    @Override
    public void setNeutralMode(boolean isBrake) {
        setNeutralMode(isBrake ? NeutralMode.Brake : NeutralMode.Coast);
    }

    @Override
    public void setDuty(double power) {
        set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, power);
        if (power == 0) {
            controlMode = ControlMode.DISABLE;
        } else {
            controlMode = ControlMode.DUTYCYCLE;
        }
    }

    @Override
    public void setVoltage(double voltage) {
        set(com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput, voltage / 12.0);
        controlMode = ControlMode.VOLTAGE;
    }

    @Override
    public void setVelocity(double velocity, double feedForward) {
        LogManager.log("there is no Velocity");
    }

    @Override
    public void setVelocity(double velocity) {
        setVelocity(velocity, 0);
    }

    @Override
    public void setVelocityWithAcceleration(double velocity, Supplier<Double> wantedAccelerationSupplier) {
        setVelocity(velocity, wantedAccelerationSupplier.get() * config.pid[slot].kA());
    }

    @Override
    public void setMotion(double position, double feedForward) {
        LogManager.log("there is no motion");
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

    @Override
    public void setPositionVoltage(double position, double feedForward) {
        LogManager.log("there is no PositionVoltage");
    }

    @Override
    public void setPositionVoltage(double position) {
        setPositionVoltage(position, 0);
    }

    @SuppressWarnings("unused")
    private double velocityFeedForward(double velocity) {
        return velocity * velocity * Math.signum(velocity) * config.kv2;
    }

    @SuppressWarnings("unused")
    private double positionFeedForward(double position) {
        return Math.cos(position * config.posToRad) * config.kSin;
    }

    @Override
    public int getCurrentControlMode() {
        return controlMode.ordinal();
    }

    @Override
    public double getCurrentClosedLoopSP() {
        return getClosedLoopTarget(0) / config.motorRatio;
    }

    @Override
    public double getCurrentClosedLoopError() {
        return getClosedLoopError(0) / config.motorRatio;
    }

    @Override
    public double getCurrentPosition() {
        return getSelectedSensorPosition() / config.motorRatio;
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
        return (getSelectedSensorVelocity() * 10.0) / config.motorRatio;
    }

    @Override
    public double getCurrentAcceleration() {
        return 0; // Phoenix 5 SRX doesn't have direct acceleration
    }

    @Override
    public double getCurrentVoltage() {
        return getMotorOutputVoltage();
    }

    @Override
    public double getCurrentCurrent() {
        return getStatorCurrent();
    }

    @Override
    public void setEncoderPosition(double position) {
        setSelectedSensorPosition(position * config.motorRatio);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Talon SRX Motor");
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

    @Override
    public String getName() {
        return name;
    }

    public double gearRatio() {
        return config.motorRatio;
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

    public void stop() {
        setDuty(0);
    }
}