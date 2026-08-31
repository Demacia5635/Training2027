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
  public static final int FRsteerID = 1;
  public static final TalonFXConfig FRSTEER_CONFIG = new TalonFXConfig(FRsteerID, Canbus.Rio, "FR steer");
  public static final int FLsteerID = 2;
  public static final TalonFXConfig FLSTEER_CONFIG = new TalonFXConfig(FLsteerID, Canbus.Rio, "FL steer");
  public static final int BRsteerID = 3;
  public static final TalonFXConfig BRSTEER_CONFIG = new TalonFXConfig(BRsteerID, Canbus.Rio, "BR steer");
  public static final int BLsteerID = 4;
  public static final TalonFXConfig BLSTEER_CONFIG = new TalonFXConfig(BLsteerID, Canbus.Rio, "BL steer");

  public static final int FRdriveID = 5;
  public static final TalonFXConfig FRDRIVE_CONFIG = new TalonFXConfig(FRdriveID, Canbus.Rio, "FR drive");
  public static final int FLdriveID = 6;
  public static final TalonFXConfig FLDRIVE_CONFIG = new TalonFXConfig(FLdriveID, Canbus.Rio, "FL drive");
  public static final int BRdriveID = 7;
  public static final TalonFXConfig BRDRIVE_CONFIG = new TalonFXConfig(BRdriveID, Canbus.Rio, "BR drive");
  public static final int BLdriveID = 8;
  public static final TalonFXConfig BLDRIVE_CONFIG = new TalonFXConfig(BLdriveID, Canbus.Rio, "BL drive");

  public static final int FRCancoderID = 9;
  public static final CancoderConfig FRCANCODER_CONFIG = new CancoderConfig(FRCancoderID, Canbus.Rio, "FR cancoder");
  public static final int FLCancoderID = 10;
  public static final CancoderConfig FLCANCODER_CONFIG = new CancoderConfig(FLCancoderID, Canbus.Rio, "FL cancoder");
  public static final int BRCancoderID = 11;
  public static final CancoderConfig BRCANCODER_CONFIG = new CancoderConfig(BRCancoderID, Canbus.Rio, "BR cancoder");
  public static final int BLCancoderID = 12;
  public static final CancoderConfig BLCANCODER_CONFIG = new CancoderConfig(BLCancoderID, Canbus.Rio, "BL cancoder");


  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;

  }

}