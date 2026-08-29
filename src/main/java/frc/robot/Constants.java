// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.CAN;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.sensors.CancoderConfig;
import frc.demacia.utils.sensors.PigeonConfig;
import frc.demacia.utils.chassis.SwerveModuleConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static class SwerveModuleConstants {

     public static final String STEER_NAME = "Steer";
    public static final String DRIVE_NAME = "Drive";
    public static final String CANCODER_NAME = "Cancoder";
    public static final int FR_DRIVE_ID = 1;
    public static final int FR_STEER_ID = 2;
    public static final int FR_CANCODER_ID = 3;
    public static final int FL_DRIVE_ID = 4;
    public static final int FL_STEER_ID = 5;
    public static final int FL_CANCODER_ID = 6;
    public static final int BR_DRIVE_ID = 7;
    public static final int BR_STEER_ID = 8;
    public static final int BR_CANCODER_ID = 9;
    public static final int BL_DRIVE_ID = 10;
    public static final int BL_STEER_ID = 11;
    public static final int BL_CANCODER_ID = 12;
    public static final Canbus CANBUS = Canbus.Rio;
    public static final double DRIVE_DIAMETER = 4 * 0.0254;
    public static final double DRIVE_GEAR_RATIO = 8.14;
    public static final double STEER_GEAR_RATIO = 150d / 7d;

    public static final Translation2d FR_POSITION = new Translation2d(1, 1);
    public static final Translation2d FL_POSITION = new Translation2d(1, 1);
    public static final Translation2d BR_POSITION = new Translation2d(1, 1);
    public static final Translation2d BL_POSITION = new Translation2d(1, 1);

    public static final TalonFXConfig FR_STEER_CONFIG = new TalonFXConfig(FR_STEER_ID, CANBUS,
        STEER_NAME)
        // .withPID()
        .withRadiansMotor(STEER_GEAR_RATIO)
        .withBrake(false);

    public static final TalonFXConfig FR_DRIVE_CONFIG = new TalonFXConfig(FR_DRIVE_ID, CANBUS, 
        DRIVE_NAME)
        // .withPID()
        .withMeterMotor(DRIVE_GEAR_RATIO, DRIVE_DIAMETER);

    
        public static final CancoderConfig FR_CANCODER_CONFIG = new CancoderConfig(FR_CANCODER_ID, CANBUS,
        CANCODER_NAME);

    public static final String FR_MODULE_NAME = "front right module";
    public static final SwerveModuleConfig FR_MODULE_CONFIG = new SwerveModuleConfig(FR_MODULE_NAME,
        FR_STEER_CONFIG, FR_DRIVE_CONFIG,
       FR_CANCODER_CONFIG).withSteerOffset(0.0);

       public static final TalonFXConfig BR_STEER_CONFIG = new TalonFXConfig(BR_STEER_ID, CANBUS,
        STEER_NAME)
        // .withPID()
        .withRadiansMotor(STEER_GEAR_RATIO)
        .withBrake(false);

    public static final TalonFXConfig BR_DRIVE_CONFIG = new TalonFXConfig(BR_DRIVE_ID, CANBUS, 
        DRIVE_NAME)
        // .withPID()
        .withMeterMotor(DRIVE_GEAR_RATIO, DRIVE_DIAMETER);

    
        public static final CancoderConfig BR_CANCODER_CONFIG = new CancoderConfig(BR_CANCODER_ID, CANBUS,
        CANCODER_NAME);

    public static final String BR_MODULE_NAME = "back right module";
    public static final SwerveModuleConfig BR_MODULE_CONFIG = new SwerveModuleConfig(BR_MODULE_NAME,
        BR_STEER_CONFIG, BR_DRIVE_CONFIG,
       BR_CANCODER_CONFIG).withSteerOffset(0.0);

       public static final TalonFXConfig FL_STEER_CONFIG = new TalonFXConfig(FL_STEER_ID, CANBUS,
        STEER_NAME)
        // .withPID()
        .withRadiansMotor(STEER_GEAR_RATIO)
        .withBrake(false);

    public static final TalonFXConfig FL_DRIVE_CONFIG = new TalonFXConfig(FL_DRIVE_ID, CANBUS, 
        DRIVE_NAME)
        // .withPID()
        .withMeterMotor(DRIVE_GEAR_RATIO, DRIVE_DIAMETER);

    
        public static final CancoderConfig FL_CANCODER_CONFIG = new CancoderConfig(FL_CANCODER_ID, CANBUS,
        CANCODER_NAME);

    public static final String FL_MODULE_NAME = "front left module";
    public static final SwerveModuleConfig FL_MODULE_CONFIG = new SwerveModuleConfig(FL_MODULE_NAME,
        FL_STEER_CONFIG, FL_DRIVE_CONFIG,
       FL_CANCODER_CONFIG).withSteerOffset(0.0);

        public static final TalonFXConfig BL_STEER_CONFIG = new TalonFXConfig(BL_STEER_ID, CANBUS,
          STEER_NAME)
          // .withPID()
          .withRadiansMotor(STEER_GEAR_RATIO)
          .withBrake(false);
          
    public static final TalonFXConfig BL_DRIVE_CONFIG = new TalonFXConfig(BL_DRIVE_ID, CANBUS, 
        DRIVE_NAME)
        // .withPID()
        .withMeterMotor(DRIVE_GEAR_RATIO, DRIVE_DIAMETER);

    
        public static final CancoderConfig BL_CANCODER_CONFIG = new CancoderConfig(BL_CANCODER_ID, CANBUS,
        CANCODER_NAME);

    public static final String BL_MODULE_NAME = "back left module";
    public static final SwerveModuleConfig BL_MODULE_CONFIG = new SwerveModuleConfig(BL_MODULE_NAME,
        BL_STEER_CONFIG, BL_DRIVE_CONFIG,
       BL_CANCODER_CONFIG).withSteerOffset(0.0);

  }

    public static class GyroConstants {
    public static final String PIGEON_NAME = "gyro";
    public static final int PIGEON_ID = -1;
    public static final Canbus PIGEON_CANBUS = Canbus.Rio;
    public static final PigeonConfig PIGEON_CONFIG = new PigeonConfig(PIGEON_ID, PIGEON_CANBUS, PIGEON_NAME);
  }
}