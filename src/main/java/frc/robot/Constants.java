// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
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
    public static final int DRIVE_MOTOR_ID = 1;
    public static final int STEER_MOTOR_ID = 2;
    public static final Canbus DRIVE_CANBUS = Canbus.Rio;
    public static final Canbus STEER_CANBUS = Canbus.Rio;
    public static final String DRIVE_MOTOR_NAME = "driveMotor";
    public static final String STEER_MOTOR_NAME = "steerMotor";
    public static final double DRIVE_DIAMETER = 4 * 0.0254;
    public static final double DRIVE_GEAR_RATIO = 8.14;
    public static final double STEER_GEAR_RATIO = 150d / 7d;

    public static final TalonFXConfig DRIVE_MOTOR_CONFIG = new TalonFXConfig(DRIVE_MOTOR_ID, DRIVE_CANBUS, DRIVE_MOTOR_NAME)
    .withBrake(true)
    .withMeterMotor(DRIVE_GEAR_RATIO, DRIVE_DIAMETER);
    public static final TalonFXConfig STEER_MOTOR_CONFIG = new TalonFXConfig(STEER_MOTOR_ID, STEER_CANBUS, STEER_MOTOR_NAME)
    .withBrake(true)
    .withRadiansMotor(STEER_GEAR_RATIO);
  }
}