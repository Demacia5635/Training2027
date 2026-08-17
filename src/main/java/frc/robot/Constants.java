package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

public final class Constants {

    public static final class SimpleMotorConstants {

        public static final int Motor1ID = 1;
        public static final int Motor2ID = 2;

        public static final String MotorCANbus = "rio";
        public static final int DRIVE_KP = 0;
        public static final int DRIVE_KI = 0;
        public static final int DRIVE_KD = 0;
        public static final int DRIVE_KS = 0;
        public static final int DRIVE_KV = 0;
        public static final int DRIVE_KA = 0;
        public static final int DRIVE_KG = 0;
        public static final int STEER_KP = 0;
        public static final int STEER_KI = 0;
        public static final int STEER_KD = 0;
        public static final int STEER_KS = 0;
        public static final int STEER_KV = 0;
        public static final int STEER_KA = 0;
        public static final int STEER_KG = 0;
        public static final double GEAR_RATIO = 150d / 7d;
        public static final double GEAR_RATIO2 = 8.14;
        public static final double DIAMETER = 0.1016;
        public static final Canbus CAN_BUS = Canbus.Rio;
        public static final int DRIVE_ID = 1;
        public static final int STEER_ID = 2;
        public static final String NAME_DRIVE = "DRIVE";
        public static final String NAME_STEER = "'STEER'";
        public static final TalonFXConfig DIRVE_CONFIG = new TalonFXConfig(1, CAN_BUS, NAME_DRIVE)
                .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG)
                .withMeterMotor(GEAR_RATIO2, DIAMETER);
        public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(2, CAN_BUS, NAME_STEER)
                .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS, STEER_KV, STEER_KA, STEER_KG)
                .withRadiansMotor(GEAR_RATIO);

    }

    public static final class DriverConstants {

        public static final int DriverControllerPort = 0;
    }

}