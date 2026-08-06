package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFX drive; // backwards and forwards
    private final TalonFX steer; // spin


    public SimpleMotorSubsystem() {
        super();
        drive = new TalonFX(Constants.MotorConstants.DRIVE_ID,Constants.MotorConstants.MOTOR_CANBUS);
        steer = new TalonFX(Constants.MotorConstants.STEER_ID, Constants.MotorConstants.MOTOR_CANBUS);
    }



// Set drive motor power
    public void setDrivePower(double power) {
        drive.set(power);
    }
// Set steeer motor power
    public void setSteerPower(double power) {
        steer.set(power);
    }


    
    // stop the drive motor

    public void driveStop() {
        setDrivePower(0);
    }
    public void steerStop() {
        setSteerPower(0);
    }
}
