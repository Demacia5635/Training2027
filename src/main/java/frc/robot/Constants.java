package frc.robot;

public final class Constants {
    public static final class ModuleConstants {
        public static final int DRIVE_ID = 1;
        public static final int STEER_ID = 2;
        public static final int CANCODER_ID = 3;

        public static final double WHEEL_DIAMETER_METERS = 0; 
        public static final double DRIVE_GEAR_RATIO = ; 
        public static final double STEER_GEAR_RATIO = .0 / .0; 

        public static final double DRIVE_ROTATIONS_TO_METERS = (WHEEL_DIAMETER_METERS * Math.PI) / DRIVE_GEAR_RATIO;
        public static final double DRIVE_RPM_TO_METERS_PER_SEC = DRIVE_ROTATIONS_TO_METERS / 60.0;
        
        public static final double STEER_ROTATIONS_TO_DEGREES = 360.0 / STEER_GEAR_RATIO;

        public static final double CANCODER_OFFSET_DEGREES = 0.0; 

        public static final double DRIVE_kS = 0.1; 
        public static final double DRIVE_kV = 2.2; 
        public static final double DRIVE_kP = 0.1;
        public static final double DRIVE_kI = 0.0;
        public static final double DRIVE_kD = 0.0;

        public static final double STEER_kP = 0.5;
        public static final double STEER_kI = 0.0;
        public static final double STEER_kD = 0.0;
    }
}
