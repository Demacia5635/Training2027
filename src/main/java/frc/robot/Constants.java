package frc.robot;

import frc.demacia.utils.motors.BaseMotorConfig.Canbus;
import frc.demacia.utils.motors.TalonFXConfig;

public final class Constants {

    public static final class SimpleMotorConstants {

        public static final int MOTOR_DRIVE = 1;
        public static final int MOTOR_STEER = 2;

        public static final int DRIVER_CONTROLLER_PORT = 0;

        public static final Canbus CAN_BUS = Canbus.Rio;

        public static final double POWER_LIMIT = 0.3;
        public static final double PRECISION_POWER_LIMIT = 0.1;

        // TalonFX hardware PID/feedforward constants.
        public static final double DRIVE_KP = 28.2662;
        public static final double DRIVE_KI = 0.0;
        public static final double DRIVE_KD = 0.0;
        public static final double DRIVE_KS = 0.0;
        public static final double DRIVE_KV = 0.0;
        public static final double DRIVE_KA = 0.0;
        public static final double DRIVE_KG = 0.0;

        public static final double STEER_KP = 24.2413;
        public static final double STEER_KI = 0.0;
        public static final double STEER_KD = 0.0;
        public static final double STEER_KS = 0.0;
        public static final double STEER_KV = 0.0;
        public static final double STEER_KA = 0.0;
        public static final double STEER_KG = 0.0;

        // WPILib software PID constants used by the training commands.
        public static final double DRIVE_COMMAND_KP = 0.005;
        public static final double DRIVE_COMMAND_KI = 0.0;
        public static final double DRIVE_COMMAND_KD = 0.0;

        public static final double STEER_COMMAND_KP = 0.005;
        public static final double STEER_COMMAND_KI = 0.0;
        public static final double STEER_COMMAND_KD = 0.0;

        public static final TalonFXConfig DRIVE_CONFIG =
            new TalonFXConfig(MOTOR_DRIVE, CAN_BUS, "Drive Motor")
                .withPID(
                    DRIVE_KP, DRIVE_KI, DRIVE_KD,
                    DRIVE_KS, DRIVE_KV, DRIVE_KA, DRIVE_KG
                );

        public static final TalonFXConfig STEER_CONFIG =
            new TalonFXConfig(MOTOR_STEER, CAN_BUS, "Steer Motor")
                .withPID(
                    STEER_KP, STEER_KI, STEER_KD,
                    STEER_KS, STEER_KV, STEER_KA, STEER_KG
                );
    }
}
