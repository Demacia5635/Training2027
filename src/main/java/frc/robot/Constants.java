// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.sensors.CancoderConfig;

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
    public static class OperatorConstants {
        public static final int CONTROLLER_PORT = 0;
    }

    public static final String STEER_MOTOR_NAME_FRONT_LEFT = "steerMotorFrontLeft";
    public static final String DRIVE_MOTOR_NAME_FRONT_LEFT = "driveMotorFrontLeft";
    public static final String CANCODER_NAME_FRONT_LEFT = "cancoderFrontLeft";

    public static final String STEER_MOTOR_NAME_FRONT_RIGHT = "steerMotorFrontRight";
    public static final String DRIVE_MOTOR_NAME_FRONT_RIGHT = "driveMotorFrontRight";
    public static final String CANCODER_NAME_FRONT_RIGHT = "cancoderFrontRight";

    public static final String STEER_MOTOR_NAME_BACK_LEFT = "steerMotorBackLeft";
    public static final String DRIVE_MOTOR_NAME_BACK_LEFT = "driveMotorBackLeft";
    public static final String CANCODER_NAME_BACK_LEFT = "cancoderBackLeft";

    public static final String STEER_MOTOR_NAME_BACK_RIGHT = "steerMotorBackRight";
    public static final String DRIVE_MOTOR_NAME_BACK_RIGHT = "driveMotorBackRight";
    public static final String CANCODER_NAME_BACK_RIGHT = "cancoderBackRight";

    public static final int STEER_MOTOR_ID_FRONT_LEFT = 2;// this is not the real ID
    public static final int DRIVE_MOTOR_ID_FRONT_LEFT = 1;// this is not the real ID
    public static final int CANCODER_ID_FRONT_LEFT = 3;// this is not the real ID

    public static final int STEER_MOTOR_ID_FRONT_RIGHT = 2;// this is not the real ID
    public static final int DRIVE_MOTOR_ID_FRONT_RIGHT = 1;// this is not the real ID
    public static final int CANCODER_ID_FRONT_RIGHT = 3;// this is not the real ID

    public static final int STEER_MOTOR_ID_BACK_LEFT = 2;// this is not the real ID
    public static final int DRIVE_MOTOR_ID_BACK_LEFT = 1;// this is not the real ID
    public static final int CANCODER_ID_BACK_LEFT = 3;// this is not the real ID

    public static final int STEER_MOTOR_ID_BACK_RIGHT = 2;// this is not the real ID
    public static final int DRIVE_MOTOR_ID_BACK_RIGHT = 1;// this is not the real ID
    public static final int CANCODER_ID_BACK_RIGHT = 3;// this is not the real ID

    public static final int GYRO_ID = 4;// this is not the real ID

    public static final edu.wpi.first.math.geometry.Translation2d FRONT_LEFT_POSITION = new edu.wpi.first.math.geometry.Translation2d(
            0.3, 0.3);
    public static final edu.wpi.first.math.geometry.Translation2d FRONT_RIGHT_POSITION = new edu.wpi.first.math.geometry.Translation2d(
            0.3, -0.3);
    public static final edu.wpi.first.math.geometry.Translation2d BACK_LEFT_POSITION = new edu.wpi.first.math.geometry.Translation2d(
            -0.3, 0.3);
    public static final edu.wpi.first.math.geometry.Translation2d BACK_RIGHT_POSITION = new edu.wpi.first.math.geometry.Translation2d(
            -0.3, -0.3);

    public static final Canbus CANBUS = Canbus.Rio;
    public static final double SCOPE = 4 * 0.0254 * Math.PI;
    public static final double GEAR_RATIO_DRIVE = 8.14;
    public static final double GEAR_RATIO_STEER = 150.0 / 7.0;

    public static final double KP_Drive = 0;
    public static final double KI_Drive = 0;
    public static final double KD_Drive = 0;
    public static final double KP_STEER = 5;
    public static final double KI_STEER = 0;
    public static final double KD_STEER = 0;
    public static final double KS_DRIVE = 0;
    public static final double KV_DRIVE = 0;
    public static final double KA_DRIVE = 0;
    public static final double KG_DRIVE = 0;
    public static final double KS_STEER = 0;
    public static final double KV_STEER = 0;
    public static final double KA_STEER = 0;
    public static final double KG_STEER = 0;

    public static final double MAX_DRIVE_SPEED_METERS_PER_SECOND = 4.0;
    public static final double MAX_ANGULAR_SPEED_RADIANS_PER_SECOND = 3.0;

    public static final TalonFXConfig CONFIG_STEER_FRONT_LEFT = new TalonFXConfig(STEER_MOTOR_ID_FRONT_LEFT, CANBUS,
            STEER_MOTOR_NAME_FRONT_LEFT)
            .withPID(KP_STEER, KI_STEER, KD_STEER, KS_STEER, KV_STEER, KA_STEER, KG_STEER)
            .withRadiansMotor(GEAR_RATIO_STEER);
    public static final TalonFXConfig CONFIG_DRIVE_FRONT_LEFT = new TalonFXConfig(DRIVE_MOTOR_ID_FRONT_LEFT, CANBUS,
            DRIVE_MOTOR_NAME_FRONT_LEFT)
            .withPID(KP_Drive, KI_Drive, KD_Drive, KS_DRIVE, KV_DRIVE, KA_DRIVE, KG_DRIVE)
            .withMeterMotor(GEAR_RATIO_DRIVE, SCOPE);
    public static CancoderConfig CONFIG_CANCODER_FRONT_LEFT = new CancoderConfig(CANCODER_ID_FRONT_LEFT, CANBUS,
            CANCODER_NAME_FRONT_LEFT);

    public static final TalonFXConfig CONFIG_STEER_FRONT_RIGHT = new TalonFXConfig(STEER_MOTOR_ID_FRONT_RIGHT, CANBUS,
            STEER_MOTOR_NAME_FRONT_RIGHT)
            .withPID(KP_STEER, KI_STEER, KD_STEER, KS_STEER, KV_STEER, KA_STEER, KG_STEER)
            .withRadiansMotor(GEAR_RATIO_STEER);
    public static final TalonFXConfig CONFIG_DRIVE_FRONT_RIGHT = new TalonFXConfig(DRIVE_MOTOR_ID_FRONT_RIGHT, CANBUS,
            DRIVE_MOTOR_NAME_FRONT_RIGHT)
            .withPID(KP_Drive, KI_Drive, KD_Drive, KS_DRIVE, KV_DRIVE, KA_DRIVE, KG_DRIVE)
            .withMeterMotor(GEAR_RATIO_DRIVE, SCOPE);
    public static CancoderConfig CONFIG_CANCODER_FRONT_RIGHT = new CancoderConfig(CANCODER_ID_FRONT_RIGHT, CANBUS,
            CANCODER_NAME_FRONT_RIGHT);

    public static final TalonFXConfig CONFIG_STEER_BACK_LEFT = new TalonFXConfig(STEER_MOTOR_ID_BACK_LEFT, CANBUS,
            STEER_MOTOR_NAME_BACK_LEFT)
            .withPID(KP_STEER, KI_STEER, KD_STEER, KS_STEER, KV_STEER, KA_STEER, KG_STEER)
            .withRadiansMotor(GEAR_RATIO_STEER);
    public static final TalonFXConfig CONFIG_DRIVE_BACK_LEFT = new TalonFXConfig(DRIVE_MOTOR_ID_BACK_LEFT, CANBUS,
            DRIVE_MOTOR_NAME_BACK_LEFT)
            .withPID(KP_Drive, KI_Drive, KD_Drive, KS_DRIVE, KV_DRIVE, KA_DRIVE, KG_DRIVE)
            .withMeterMotor(GEAR_RATIO_DRIVE, SCOPE);
    public static CancoderConfig CONFIG_CANCODER_BACK_LEFT = new CancoderConfig(CANCODER_ID_BACK_LEFT, CANBUS,
            CANCODER_NAME_BACK_LEFT);

    public static final TalonFXConfig CONFIG_STEER_BACK_RIGHT = new TalonFXConfig(STEER_MOTOR_ID_BACK_RIGHT, CANBUS,
            STEER_MOTOR_NAME_BACK_RIGHT)
            .withPID(KP_STEER, KI_STEER, KD_STEER, KS_STEER, KV_STEER, KA_STEER, KG_STEER)
            .withRadiansMotor(GEAR_RATIO_STEER);
    public static final TalonFXConfig CONFIG_DRIVE_BACK_RIGHT = new TalonFXConfig(DRIVE_MOTOR_ID_BACK_RIGHT, CANBUS,
            DRIVE_MOTOR_NAME_BACK_RIGHT)
            .withPID(KP_Drive, KI_Drive, KD_Drive, KS_DRIVE, KV_DRIVE, KA_DRIVE, KG_DRIVE)
            .withMeterMotor(GEAR_RATIO_DRIVE, SCOPE);
    public static CancoderConfig CONFIG_CANCODER_BACK_RIGHT = new CancoderConfig(CANCODER_ID_BACK_RIGHT, CANBUS,
            CANCODER_NAME_BACK_RIGHT);

}