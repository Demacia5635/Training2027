// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public final class SimpleMotorSubsystemConstants {
    public final static int motorID = 2;
    public final static String motorCANBUS = "rio";
  }

  public final class HomeworkMotorSubsystemConstants {
    public final static int DRIVE_ID = 1;
    public final static String DRIVE_CANBUS = "rio";
    public final static int STEER_ID = 2;
    public final static String STEER_CANBUS = "rio";
    public final static double STEER_GEAR_RATIO = 6.12;
    public final static double DRIVE_GEAR_RATIO = 0.25;
    public final static double DRIVE_CIRCUMFERENCE = 4 * 0.0254 * Math.PI;
    public final static double motorPower = 0.7;
    public final static double steerTolerance = 0.015;
    public final static double driveTolerance = 0.001;

  }

  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public final class PIDMotorSubsystemConstants {
    public final static int ADRIVE_ID = 1;
    public final static Canbus ADRIVE_CANBUS = Canbus.Rio;
    public final static String ADRIVE_NAME = "driveMotor";
    public final static int STEERID = 2;
    public final static Canbus STEERCANBUS = Canbus.Rio;
    public final static String STEER_NAME = "steerMotor";
    public final static double DRIVE_KP = 0.3;
    public final static double DRIVE_KI = 0;
    public final static double DRIVE_KD = 0;
    public final static double DRIVE_KS = 0;
    public final static double DRIVE_KV = 0;
    public final static double DRIVE_KA = 0;
    public final static double DRIVE_KG = 0;
    public final static double STEER_KP = 0.5;
    public final static double STEER_KI = 0;
    public final static double STEER_KD = 0;
    public final static double STEER_KS = 0;
    public final static double STEER_KV = 0;
    public final static double STEER_KA = 0;
    public final static double STEER_KG = 0;
    public final static double DRIVEGEAR_RATIO = 8.14;
    public final static double STEERGEAR_RATIO = 150d / 7d;
    public final static double DRIVE_CIRCUMFERENCE = 4 * 0.0254 * Math.PI;

    public static final TalonFXConfig CONFIG = new TalonFXConfig(ADRIVE_ID, ADRIVE_CANBUS, ADRIVE_NAME)
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG)
        .withMeterMotor(DRIVEGEAR_RATIO, DRIVE_CIRCUMFERENCE);

    public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(STEERID, STEERCANBUS, STEER_NAME)
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG)
        .withRadiansMotor(STEERGEAR_RATIO);
  }

  public final class FDMotorConstants {
    public final static int DRIVE_ID2 = 1;
        public final static Canbus DRIVE_CANBUS2 = Canbus.Rio;
        public final static String DRIVE_NAME2 = "drive motor";
        public final static double DRIVE_KP2 = 2;
        public final static double DRIVE_KI2 = 0;
        public final static double DRIVE_KD2 = 0;
        public final static double DRIVE_KS2 = 0.02103;
        // 0.11671;
        public final static double DRIVE_KV2 = 0.93436;
        // 0.01106
        public final static double DRIVE_KA2 = 0;
        public final static double DRIVE_KG2 = 0;
        public final static double DRIVEGEAR_RATIO2 = 8.14;
        public final static double DRIVE_CIRCUMFERENCE2 = 4 * 0.0254 * Math.PI;
        public final static double STEERGEAR_RATIO2 = 150d / 7d;
        public final static double STEER_KP2 = 8;
        public final static double STEER_KI2 = 0.005;
        public final static double STEER_KD2 = 0;
        public final static double STEER_KS2 = 0.03034;
        public final static double STEER_KV2 = 0.37645;
        public final static double STEER_KA2 = 0;
        public final static double STEER_KG2 = 0;

        public final static int STEER_ID2 = 2;
        public final static Canbus STEER_CANBUS2 = Canbus.Rio;
        public final static String STEER_NAME = "steer motor";
    
        public static final TalonFXConfig DRIVECONFIG = new TalonFXConfig(DRIVE_ID2 ,DRIVE_CANBUS2 ,DRIVE_NAME2)
        .withMeterMotor(DRIVEGEAR_RATIO2, DRIVE_CIRCUMFERENCE2)
        .withPID(DRIVE_KP2, DRIVE_KI2, DRIVE_KD2, DRIVE_KS2, DRIVE_KV2, DRIVE_KA2, DRIVE_KG2)
        .withBrake(true);
        
        
        public static final TalonFXConfig STEERCONFIG = new TalonFXConfig(STEER_ID2, STEER_CANBUS2, STEER_NAME)
        .withRadiansMotor(STEERGEAR_RATIO2)
        .withPID(STEER_KP2, STEER_KI2, STEER_KD2, STEER_KS2, STEER_KV2, STEER_KA2, STEER_KG2)
        .withBrake(true);
  }
}
