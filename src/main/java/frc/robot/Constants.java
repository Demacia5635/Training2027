// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import frc.demacia.utils.chassis.SwerveModuleConfig;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.sensors.CancoderConfig;
import frc.demacia.utils.sensors.PigeonConfig;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or booleanx
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

        public static class ChassisConstants {
                public static final String STEER_NAME = "Steer";
                public static final String DRIVE_NAME = "Drive";

                public static final int STEER_FRONT_LEFT_ID = 2;
                public static final int DRIVE_FRONT_LEFT_ID = 1;
                public static final int CANCODER_FRONT_LEFT_ID = 3;

                public static final int STEER_FRONT_RIGHT_ID = 5;
                public static final int DRIVE_FRONT_RIGHT_ID = 4;
                public static final int CANCODER_FRONT_RIGHT_ID = 6;

                public static final int STEER_BACK_LEFT_ID = 8;
                public static final int DRIVE_BACK_LEFT_ID = 7;
                public static final int CANCODER_BACK_LEFT_ID = 9;

                public static final int STEER_BACK_RIGHT_ID = 11;
                public static final int DRIVE_BACK_RIGHT_ID = 10;
                public static final int CANCODER_BACK_RIGHT_ID = 12;

                public static final double STEER_KP = 2;
                public static final double STEER_KI = 0;
                public static final double STEER_KD = 0;
                public static final double STEER_KS = 0.034773;
                public static final double STEER_KV = 0.36748;
                public static final double STEER_KA = 0.03267;

                public static final double DRIVE_KP = 1;
                public static final double DRIVE_KI = 0;
                public static final double DRIVE_KD = 0;
                public static final double DRIVE_KS = 0.03203;
                public static final double DRIVE_KV = 0.09435;
                public static final double DRIVE_KA = 0.00679;

                public static final double MAX_SPEED_METERS_PER_SEC = 4;
                public static final double MAX_SPEED_RPM = 4;

                public static final double STEER_GEAR_RATIO = 150 / 7;
                public static final double DRIVE_GEAR_RATIO = 8.14;

                public static final Canbus CANBUS = Canbus.CANIvore;
                public static final String CANCODER_NAME = "Cancoder";

                // FRONT RIGHT
                public static final Translation2d FRONT_LEFT_POSITION = new Translation2d(0.295, -0.395);

                public static final TalonFXConfig STEER_FRONT_RIGHT_CONFIG = new TalonFXConfig(STEER_FRONT_RIGHT_ID,
                                CANBUS,
                                STEER_NAME)
                                .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, 0, 0, 0)
                                .withRadiansMotor(STEER_GEAR_RATIO)
                                .withInvert(true)
                                .withBrake(false);

                public static final TalonFXConfig DRIVE_FRONT_RIGHT_CONFIG = new TalonFXConfig(DRIVE_FRONT_RIGHT_ID,
                                CANBUS,
                                DRIVE_NAME)
                                .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, 0, 0, 0)
                                .withMeterMotor(DRIVE_GEAR_RATIO, 4 * Math.PI * 0.254);

                public static final CancoderConfig CANCODER_FRONT_RIGHT_CONFIG = new CancoderConfig(
                                CANCODER_FRONT_RIGHT_ID,
                                CANBUS,
                                CANCODER_NAME); // cancoder

                public static final String FRONT_RIGHT_NAME = "Front Right Module";
                public static final SwerveModuleConfig FRONT_RIGHT_CONFIG = new SwerveModuleConfig(FRONT_RIGHT_NAME,
                                STEER_FRONT_RIGHT_CONFIG, DRIVE_FRONT_RIGHT_CONFIG,
                                CANCODER_FRONT_RIGHT_CONFIG);

                // FRONT LEFT

                public static final Translation2d FRONT_RIGHT_POSITION = new Translation2d(0.295, 0.395);
                public static final TalonFXConfig STEER_FRONT_LEFT_CONFIG = new TalonFXConfig(STEER_FRONT_LEFT_ID,
                                CANBUS,
                                STEER_NAME)
                                .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, 0, 0, 0)
                                .withRadiansMotor(STEER_GEAR_RATIO)
                                .withInvert(true)
                                .withBrake(false);

                public static final TalonFXConfig DRIVE_FRONT_LEFT_CONFIG = new TalonFXConfig(DRIVE_FRONT_LEFT_ID,
                                CANBUS,
                                DRIVE_NAME)
                                .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, 0, 0, 0)
                                .withMeterMotor(DRIVE_GEAR_RATIO, 4 * Math.PI * 0.254);

                public static final CancoderConfig CANCODER_FRONT_LEFT_CONFIG = new CancoderConfig(
                                CANCODER_FRONT_LEFT_ID,
                                CANBUS,
                                CANCODER_NAME); // cancoder

                public static final String FRONT_LEFT_NAME = "Front Right Module";
                public static final SwerveModuleConfig FRONT_LEFT_CONFIG = new SwerveModuleConfig(FRONT_LEFT_NAME,
                                STEER_FRONT_LEFT_CONFIG, DRIVE_FRONT_LEFT_CONFIG,
                                CANCODER_FRONT_LEFT_CONFIG);

                // BACK LEFT

                public static final Translation2d BACK_LEFT_POSITION = new Translation2d(-0.295, 0.395);
                public static final TalonFXConfig STEER_BACK_LEFT_CONFIG = new TalonFXConfig(STEER_BACK_LEFT_ID, CANBUS,
                                STEER_NAME)
                                .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, 0, 0, 0)
                                .withRadiansMotor(STEER_GEAR_RATIO)
                                .withInvert(true)
                                .withBrake(false);

                public static final TalonFXConfig DRIVE_BACK_LEFT_CONFIG = new TalonFXConfig(DRIVE_BACK_LEFT_ID, CANBUS,
                                DRIVE_NAME)
                                .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, 0, 0, 0)
                                .withMeterMotor(DRIVE_GEAR_RATIO, 4 * Math.PI * 0.254);

                public static final CancoderConfig CANCODER_BACK_LEFT_CONFIG = new CancoderConfig(CANCODER_BACK_LEFT_ID,
                                CANBUS,
                                CANCODER_NAME); // cancoder

                public static final String BACK_LEFT_NAME = "Front Right Module";
                public static final SwerveModuleConfig BACK_LEFT_CONFIG = new SwerveModuleConfig(BACK_LEFT_NAME,
                                STEER_BACK_LEFT_CONFIG, DRIVE_BACK_LEFT_CONFIG,
                                CANCODER_BACK_LEFT_CONFIG);

                // BACK RIGHT

                public static final Translation2d BACK_RIGHT_POSITION = new Translation2d(-0.295, -0.395);
                public static final TalonFXConfig STEER_BACK_RIGHT_CONFIG = new TalonFXConfig(STEER_BACK_RIGHT_ID,
                                CANBUS,
                                STEER_NAME)
                                .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, 0, 0, 0)
                                .withRadiansMotor(STEER_GEAR_RATIO)
                                .withInvert(true)
                                .withBrake(false);

                public static final TalonFXConfig DRIVE_BACK_RIGHT_CONFIG = new TalonFXConfig(DRIVE_BACK_RIGHT_ID,
                                CANBUS,
                                DRIVE_NAME)
                                .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, 0, 0, 0)
                                .withMeterMotor(DRIVE_GEAR_RATIO, 4 * Math.PI * 0.254);

                public static final CancoderConfig CANCODER_BACK_RIGHT_CONFIG = new CancoderConfig(
                                CANCODER_BACK_RIGHT_ID,
                                CANBUS,
                                CANCODER_NAME); // cancoder

                public static final String BACK_RIGHT_NAME = "Front Right Module";
                public static final SwerveModuleConfig BACK_RIGHT_CONFIG = new SwerveModuleConfig(BACK_RIGHT_NAME,
                                STEER_BACK_RIGHT_CONFIG, DRIVE_BACK_RIGHT_CONFIG,
                                CANCODER_BACK_RIGHT_CONFIG);

        }

        public static class GyroConstants {
                public static final String PIGEON_NAME = "Gyro";
                public static final int PIGEON_ID = 14;
                public static final Canbus PIGEON_CANBUS = Canbus.Rio;
                public static final PigeonConfig PIGEON_CONFIG = new PigeonConfig(PIGEON_ID, PIGEON_CANBUS,
                                PIGEON_NAME);
        }

        public static class ControllerConstants {
                public static final int CONTROLLER_ID = 1;
        }

}
