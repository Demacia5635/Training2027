package frc.robot;

import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.motors.TalonFXConfig;

public final class Constants {

  public static final class SimpleMotorConstants {

    // No physical test stand.
    public static final int DRIVE_MOTOR_ID = -1;
    public static final int STEER_MOTOR_ID = -1;

    public static final Canbus CAN_BUS = Canbus.Rio;

    public static final double POWER_LIMIT = 0.30;

    public static final double DRIVE_GEAR_RATIO = 1.0;

    public static final double DRIVE_WHEEL_DIAMETER_METERS = 1.0;

    public static final double STEER_GEAR_RATIO = 1.0;

    public static final double STEER_KP = 0.01;
    public static final double STEER_KI = 0.0;
    public static final double STEER_KD = 0.0;

    public static final double DRIVE_KP = 0.10;
    public static final double DRIVE_KI = 0.0;
    public static final double DRIVE_KD = 0.0;

    public static final TalonFXConfig DRIVE_CONFIG = new TalonFXConfig(
        DRIVE_MOTOR_ID,
        CAN_BUS,
        "Drive Motor")
        .withMeterMotor(
            DRIVE_GEAR_RATIO,
            DRIVE_WHEEL_DIAMETER_METERS);

    public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(
        STEER_MOTOR_ID,
        CAN_BUS,
        "Steer Motor")
        .withRadiansMotor(
            STEER_GEAR_RATIO);
  }
}