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
  public static class OperatorConstants {
  }

  public static class MotorConstants {
    public static final String STEER_NAME = "Steer";
    public static final String DRIVE_NAME = "Drive";

    public static final int STEER_ID = 2;
    public static final int DRIVE_ID = 1;


    public static final double STEER_KP = 2; 
    public static final double STEER_KI = 0; 
    public static final double STEER_KD = 0;
    public static final double STEER_KS = 0.;
    public static final double STEER_KV = 0;
    public static final double STEER_KA = 0;
    
    public static final double DRIVE_KP = 1; 
    public static final double DRIVE_KI = 0; 
    public static final double DRIVE_KD = 0; 
    public static final double DRIVE_KS = 0.03203;
    public static final double DRIVE_KV = 0.09435;
    public static final double DRIVE_KA = 0.00679;


    public static final double STEER_GEAR_RATIO = 150/7;
    public static final double DRIVE_GEAR_RATIO = 8.14;

    public static final Canbus MOTOR_CANBUS = Canbus.Rio;

    public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(STEER_ID, MOTOR_CANBUS, STEER_NAME)
    .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, 0, 0, 0)
    .withRadiansMotor(STEER_GEAR_RATIO)
    .withInvert(true)
    .withBrake(false)
    ;
    


    public static final TalonFXConfig DRIVE_CONFIG = new TalonFXConfig(DRIVE_ID, MOTOR_CANBUS, DRIVE_NAME)
    .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS,DRIVE_KV,DRIVE_KA,0,0,0)
    .withMeterMotor(DRIVE_GEAR_RATIO,4 * Math.PI * 0.254)
    ;

  }

  public static class ControllerConstants {
    public static final int CONTROLLER_ID = 0;
  }

  public static class ConvertionConstants {
    public static final double RADIANS_TO_ROTATIONS = 1 / (2 * Math.PI);
    public static final double ROTATIONS_TO_RADIANS = 2 * Math.PI;
    public static final double DEGREES_TO_RADIANS = Math.PI / 180;
    public static final double RADIANS_TO_DEGREES = 180 / Math.PI;

  }

}
