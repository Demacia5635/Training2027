package frc.robot;

import com.ctre.phoenix6.hardware.Pigeon2;

public class Chassis {
    private SwerveModule [] modules;
    private Pigeon2 gyro;

    public Chassis() {
        modules = new SwerveModule[] {
            new SwerveModule(Constants.CONFIG_STEER_FRONT_LEFT, Constants.CONFIG_DRIVE_FRONT_LEFT, Constants.CONFIG_CANCODER_FRONT_LEFT),
            new SwerveModule(Constants.CONFIG_STEER_FRONT_RIGHT, Constants.CONFIG_DRIVE_FRONT_RIGHT, Constants.CONFIG_CANCODER_FRONT_RIGHT),
            new SwerveModule(Constants.CONFIG_STEER_BACK_LEFT, Constants.CONFIG_DRIVE_BACK_LEFT, Constants.CONFIG_CANCODER_BACK_LEFT),
            new SwerveModule(Constants.CONFIG_STEER_BACK_RIGHT, Constants.CONFIG_DRIVE_BACK_RIGHT, Constants.CONFIG_CANCODER_BACK_RIGHT)
        };   
        gyro = new Pigeon2(Constants.GYRO_ID);
    }
}
