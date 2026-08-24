package frc.robot;

import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.motors.TalonFXConfig;

public final class Constants {

  public static final class SimpleMotorConstants {

    // No physical test stand.
    public static final int DRIVE_MOTOR_ID = -1;
    public static final int STEER_MOTOR_ID = -1;
    public static final int encoderID = -1;

    public static final int DRIVE_MOTOR_ID2 = -1;
    public static final int STEER_MOTOR_ID2= -1;
    public static final int encoderID2 = -1;

    public static final int DRIVE_MOTOR_ID3 = -1;
    public static final int STEER_MOTOR_ID3 = -1;
    public static final int encoderID3 = -1;

    public static final int DRIVE_MOTOR_ID4 = -1;
    public static final int STEER_MOTOR_ID4 = -1;
    public static final int encoderID4 = -1;

    public static final Canbus CANIVOR_CANBUS = Canbus.CANIvore;

    public static final double POWER_LIMIT = 0.30;

    public static final double STEER_KP = 0.01;
    public static final double STEER_KI = 0.0;
    public static final double STEER_KD = 0.0;
    public static final double STEER_KS = 0.0;
    public static final double STEER_KV = 0.0;
    public static final double STEER_KA = 0.0;  
    public static final double STEER_KG = 0.0;

    public static final double DRIVE_KP = 0.10;
    public static final double DRIVE_KI = 0.0;
    public static final double DRIVE_KD = 0.0;
    public static final double DRIVE_KS = 0.0;
    public static final double DRIVE_KV = 0.0;
    public static final double DRIVE_KA = 0.0;
    public static final double DRIVE_KG = 0.0;

    public static final TalonFXConfig DRIVE_CONFIG = new TalonFXConfig(
        DRIVE_MOTOR_ID,
        CANIVOR_CANBUS,
        "Drive Motor")
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG);

    public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(
        STEER_MOTOR_ID,
        CANIVOR_CANBUS,
        "Steer Motor")
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG);

    public static final CANDER_CONFIG CANDER_CONFIG = new CANDER_CONFIG(
        encoderID,
        CANIVOR_CANBUS,
        "CANDER Encoder");

    public static final TalonFXConfig DRIVE_CONFIG2 = new TalonFXConfig(
        DRIVE_MOTOR_ID,
        CANIVOR_CANBUS,
        "Drive Motor")
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG);

    public static final TalonFXConfig STEER_CONFIG2 = new TalonFXConfig(
        STEER_MOTOR_ID,
        CANIVOR_CANBUS,
        "Steer Motor")
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG);



    public static final TalonFXConfig DRIVE_CONFIG3 = new TalonFXConfig(
        DRIVE_MOTOR_ID,
        CANIVOR_CANBUS,
        "Drive Motor")
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG);

    public static final TalonFXConfig STEER_CONFIG3 = new TalonFXConfig(
        STEER_MOTOR_ID,
        CANIVOR_CANBUS,
        "Steer Motor")
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG);



    public static final TalonFXConfig DRIVE_CONFIG4 = new TalonFXConfig(
        DRIVE_MOTOR_ID,
        CANIVOR_CANBUS,
        "Drive Motor")
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG);

    public static final TalonFXConfig STEER_CONFIG4 = new TalonFXConfig(
        STEER_MOTOR_ID,
        CANIVOR_CANBUS,
        "Steer Motor")
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG);

  }
}