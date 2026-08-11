// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
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
}
