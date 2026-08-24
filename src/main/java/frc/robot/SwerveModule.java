package frc.robot;

import com.ctre.phoenix6.hardware.TalonFX;

import frc.demacia.utils.sensors.Cancoder;

public class SwerveModule {
    // Steering motor - CAN ID 2
    private final TalonFX steerMotor;

    // Drive motor - CAN ID 1
    private final TalonFX driveMotor;


    // Constructor
    public SwerveModule() {

        steerMotor = new TalonFX(
            Constants.OperatorConstants.STEERMOTOR_ID,
            Constants.OperatorConstants.Canbus
        );

        driveMotor = new TalonFX(
            Constants.OperatorConstants.DRIVEMOTOR_ID,
            Constants.OperatorConstants.Canbus
        );
    }
}
