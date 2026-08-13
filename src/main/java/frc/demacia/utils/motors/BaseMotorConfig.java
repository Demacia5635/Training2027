package frc.demacia.utils.motors;

import java.util.function.Consumer;
import com.ctre.phoenix6.CANBus;

/**
 * Abstract base class for motor configurations using the Builder pattern.
 * <p>
 * Allows constructing complex motor configurations (PID, Limits, Ramps, etc.)
 * in a readable, chained manner.
 * </p>
 * 
 * @param <T> The concrete type of the configuration class (self-reference for
 *            builder chaining)
 */
public abstract class BaseMotorConfig<T extends BaseMotorConfig<T>> {

    /** Supported CAN bus types */
    public static enum Canbus {
        Rio("rio"),
        CANIvore("canivore");

        public final CANBus canbus;

        private Canbus(String name) {
            this.canbus = new CANBus(name);
        }
    }

    /** Supported Motor Controller types with factory methods */
    public static enum MotorControllerType {
        TalonFX {
            @Override
            public MotorInterface create(BaseMotorConfig<?> config) {
                return new TalonFXMotor((TalonFXConfig) config);
            }
        },
        TalonSRX {
            @Override
            public MotorInterface create(BaseMotorConfig<?> config) {
                return new TalonSRXMotor((TalonSRXConfig) config);
            }
        },
        SparkMax {
            @Override
            public MotorInterface create(BaseMotorConfig<?> config) {
                return new SparkMaxMotor((SparkMaxConfig) config);
            }
        },
        SparkFlex {
            @Override
            public MotorInterface create(BaseMotorConfig<?> config) {
                return new SparkFlexMotor((SparkFlexConfig) config);
            }
        };

        public abstract MotorInterface create(BaseMotorConfig<?> config);
    }

    public int id;
    public Canbus canbus = Canbus.Rio;
    public MotorControllerType motorClass = MotorControllerType.TalonFX;
    public String name;

    public double maxVolt = 12;
    public double minVolt = -12;
    public double maxCurrent = 40;
    public double rampUpTime = 0.3;

    public boolean brake = true;
    public double motorRatio = 1;
    public boolean inverted = false;

    public double maxVelocity = 0;
    public double maxAcceleration = 0;
    public double maxJerk = 0;

    public CloseLoopParam[] pidFfParams = { new CloseLoopParam(), new CloseLoopParam(), new CloseLoopParam(),
            new CloseLoopParam() };

    public boolean isMeterMotor = false;
    public boolean isRadiansMotor = false;

    public double posToRad = 0;

    public double highCurrentThreshold = 0;
    public double lowVelocityThreshold = 0;
    public double secondsThreshold = 0;
    public Consumer<T> conditionIsTrue;

    /**
     * Base constructor.
     * 
     * @param id   The CAN ID
     * @param name The name of the motor
     */
    public BaseMotorConfig(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Base constructor with CAN bus.
     * 
     * @param id     The CAN ID
     * @param name   The name of the motor
     * @param canbus The CAN bus instance
     */
    public BaseMotorConfig(int id, String name, Canbus canbus) {
        this(id, name);
        this.canbus = canbus;
    }

    public MotorControllerType getMotorClass() {
        return motorClass;
    }

    /**
     * Sets the type of the motor controller.
     * 
     * @param motorClass The controller type
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withMotorClass(MotorControllerType motorClass) {
        this.motorClass = motorClass;
        return (T) this;
    }

    /**
     * Sets the voltage limits (symmetrical).
     * 
     * @param maxVolt The maximum forward voltage (reverse will be -maxVolt)
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withVolts(double maxVolt) {
        this.maxVolt = maxVolt;
        this.minVolt = -maxVolt;
        return (T) this;
    }

    /**
     * Sets the neutral mode.
     * 
     * @param brake true for Brake, false for Coast
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withBrake(boolean brake) {
        this.brake = brake;
        return (T) this;
    }

    /**
     * Sets whether the motor is inverted.
     * 
     * @param invert true to invert
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withInvert(boolean invert) {
        this.inverted = invert;
        return (T) this;
    }

    /**
     * Sets the open/closed loop ramp time.
     * 
     * @param rampTime Time in seconds to ramp from 0 to full output
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withRampTime(double rampTime) {
        this.rampUpTime = rampTime;
        return (T) this;
    }

    /**
     * Configures the motor for linear motion (Meters).
     * Calculates the sensor-to-mechanism ratio automatically.
     * 
     * @param gearRatio The gear ratio (Input / Output)
     * @param diameter  The diameter of the wheel/pulley in meters
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withMeterMotor(double gearRatio, double diameter) {
        motorRatio = gearRatio / (diameter * Math.PI);
        posToRad = 2 / diameter;
        isMeterMotor = true;
        isRadiansMotor = false;
        return (T) this;
    }

    /**
     * Configures the motor for angular motion (Radians).
     * Calculates the sensor-to-mechanism ratio automatically.
     * 
     * @param gearRatio The gear ratio (Input / Output)
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withRadiansMotor(double gearRatio) {
        motorRatio = gearRatio / (Math.PI * 2);
        posToRad = 1;
        isMeterMotor = false;
        isRadiansMotor = true;
        return (T) this;
    }

    /**
     * Sets the supply current limit.
     * 
     * @param maxCurrent Maximum current in Amps
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withCurrent(double maxCurrent) {
        this.maxCurrent = maxCurrent;
        return (T) this;
    }

    /**
     * Sets Motion Magic parameters.
     * 
     * @param maxVelocity     Maximum cruise velocity
     * @param maxAcceleration Maximum acceleration
     * @param maxJerk         Maximum jerk
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withMotionParam(double maxVelocity, double maxAcceleration, double maxJerk) {
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleration;
        this.maxJerk = maxJerk;
        return (T) this;
    }

    /**
     * Sets the PID parameters for Slot 0.
     * 
     * @param kP Proportional gain
     * @param kI Integral gain
     * @param kD Derivative gain
     * @param kS Static friction feedforward
     * @param kV Velocity feedforward
     * @param kA Acceleration feedforward
     * @param kG Gravity feedforward
     * @return this configuration for chaining
     */
    public T withPID(double kP, double kI, double kD, double kS, double kV, double kA, double kG, double kSin,
            double kV2) {
        return withPID(0, kP, kI, kD, kS, kV, kA, kG, kSin, kV2);
    }

    /**
     * Sets the PID parameters for a specific slot.
     * 
     * @param slot The PID slot index
     * @param kP   Proportional gain
     * @param kI   Integral gain
     * @param kD   Derivative gain
     * @param kS   Static friction feedforward
     * @param kV   Velocity feedforward
     * @param kA   Acceleration feedforward
     * @param kG   Gravity feedforward
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withPID(int slot, double kP, double kI, double kD, double kS, double kV, double kA, double kG, double kSin,
            double kV2) {
        if (slot >= 0 && slot < pidFfParams.length) {
            pidFfParams[slot] = new CloseLoopParam(kP, kI, kD, kS, kV, kA, kG, kSin, kV2);
        }
        return (T) this;
    }

    /**
     * Sets the CAN bus for the motor.
     * 
     * @param canbus The CAN bus enum
     * @return this configuration for chaining
     */
    @SuppressWarnings("unchecked")
    public T withCanbus(Canbus canbus) {
        this.canbus = canbus;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T withDetectStallInMotor(double current, double velocity, double seconds, Consumer<T> conditionIsTrue) {
        this.highCurrentThreshold = current;
        this.lowVelocityThreshold = velocity;
        this.secondsThreshold = seconds;
        this.conditionIsTrue = conditionIsTrue;
        return (T) this;
    }

    /**
     * Helper method to copy fields from another configuration object.
     * 
     * @param other The config to copy from
     */
    protected void copyBaseFields(BaseMotorConfig<?> other) {
        this.canbus = other.canbus;
        this.maxVolt = other.maxVolt;
        this.minVolt = other.minVolt;
        this.maxCurrent = other.maxCurrent;
        this.rampUpTime = other.rampUpTime;
        this.brake = other.brake;
        this.motorRatio = other.motorRatio;
        this.inverted = other.inverted;
        this.posToRad = other.posToRad;
        this.maxAcceleration = other.maxAcceleration;
        this.maxVelocity = other.maxVelocity;
        this.maxJerk = other.maxJerk;
        this.pidFfParams[0] = (other.pidFfParams[0]);
        this.pidFfParams[1] = (other.pidFfParams[1]);
        this.pidFfParams[2] = (other.pidFfParams[2]);
        this.pidFfParams[3] = (other.pidFfParams[3]);
        this.isMeterMotor = other.isMeterMotor;
        this.isRadiansMotor = other.isRadiansMotor;
    }
}