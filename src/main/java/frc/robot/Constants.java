// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.CANBus;

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
  public static class OperatorConstants {
    public static final int CONTROLLER_PORT = 0;
  }

  public static final String STEER_MOTOR_NAME = "steerMotor";
  public static final String DRIVE_MOTOR_NAME = "driveMotor";
  public static final Canbus CANBUS = Canbus.Rio;
  public static final int STEER_MOTOR_ID = 2;
  public static final int DRIVE_MOTOR_ID = 1;
  public static final double SCOPE = 4 * 0.0254 * Math.PI;
  public static final double GEAR_RATIO_DRIVE = 8.14;
  public static final double GEAR_RATIO_STEER = 150.0 / 7.0;
  public static final double KP = 1;
  public static final double KI = 0;
  public static final double KD = 0;
  public static final double KS = 0;
  public static final double KV = 0;
  public static final double KA = 0;
  public static final double KG = 0;
  public static final TalonFXConfig CONFIG_STEER = new TalonFXConfig(STEER_MOTOR_ID, CANBUS, STEER_MOTOR_NAME)
      .withPID(KP, KI, KD, KS, KV, KA, KG,0,0)
      .withRadiansMotor(GEAR_RATIO_STEER);
  public static final TalonFXConfig CONFIG_DRIVE = new TalonFXConfig(DRIVE_MOTOR_ID, CANBUS, DRIVE_MOTOR_NAME)
      .withPID(0, 0, 0, 0, 0, 0, 0,0,0)
      .withMeterMotor(GEAR_RATIO_DRIVE,SCOPE);

}
