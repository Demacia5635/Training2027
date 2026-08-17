package frc.robot;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

public final class Constants {

    public static final class SimpleMotorConstants {

        public static final int Motor1ID = 1;
        public static final int Motor2ID = 2;

        public static final String MotorCANbus = "rio";
        public static final int KP = 0;
        public static final int KI = 0;
        public static final int KD = 0;
        public static final int KS = ;
        public static final int KV = ;
        public static final int KA = 0;
        public static final int KG = 0 ;
        public static final Canbus CAN_BUS = Canbus.Rio;
        public static final int DRIVE_ID = 1;
        public static final int STEER_ID = 2;
        public static final String NAME_DRIVE = "DRIVE" ;
        public static final String NAME_STEER = "'STEER'" ;
        public static final TalonFXConfig DIRVE_CONFIG = new TalonFXConfig(1 ,CAN_BUS , NAME_DRIVE)
        public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(2 ,CAN_BUS ,NAME_STEER )
        .withPID(KP, KI, KD, KS, KV, KA, KG);

        

    }

    public static final class DriverConstants {

        public static final int DriverControllerPort = 0;
    }


}