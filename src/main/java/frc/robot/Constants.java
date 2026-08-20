package frc.robot;

import java.security.PublicKey;

import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.BaseMotorConfig.Canbus;

public final class Constants {
    
    public static final class SimpleMotorConstants {
        public static final int MOTOR_DRIVE = 1;
        public static final int MOTOR_STEER = 2;
        public static final Canbus CAN_BUS = Canbus.Rio;
        public static final double DRIVE_KP = 0.0;
        public static final double DRIVE_KI = 0.0;
        public static final double DRIVE_KD = 0.0;
        public static final double DRIVE_KS = 0.0;
        public static final double DRIVE_KV = 0.0;
        public static final double DRIVE_KA = 0.0;
        public static final double DRIVE_KG = 0.0;

        public static final double STEER_KP = 0.0;
        public static final double STEER_KI = 0.0;
        public static final double STEER_KD = 0.0;
        public static final double STEER_KS = 0.0;
        public static final double STEER_KV = 0.0;
        public static final double STEER_KA = 0.0;
        public static final double STEER_KG = 0.0;



        public static final TalonFXConfig DRIVE_CONFIG = new TalonFXConfig(MOTOR_DRIVE, CAN_BUS, "Drive Motor")
        .withPID(DRIVE_KP, DRIVE_KI, DRIVE_KD, DRIVE_KS);
        public static final TalonFXConfig STEER_CONFIG = new TalonFXConfig(MOTOR_STEER, CAN_BUS, "Steer Motor")
        .withPID(STEER_KP, STEER_KI, STEER_KD, STEER_KS);
    

    }
}