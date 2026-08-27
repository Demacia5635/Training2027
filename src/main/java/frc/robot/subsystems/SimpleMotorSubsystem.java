package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {

    // Steering motor - CAN ID 2
    private final TalonFX STEERMOTOR;

    // Drive motor - CAN ID 1
    private final TalonFX DRIVEMOTOR;


    // Constructor
    public SimpleMotorSubsystem() {

        STEERMOTOR = new TalonFX(
            Constants.OperatorConstants.STEERMOTOR_ID,
            Constants.OperatorConstants.Canbus
        );

        DRIVEMOTOR = new TalonFX(
            Constants.OperatorConstants.DRIVEMOTOR_ID,
            Constants.OperatorConstants.Canbus
        );
    }


    // Controls only the steering motor
    public void setPower(double power) {
        STEERMOTOR.set(power);
    }


    // Controls both motors at the same time
    public void setTwoPowers(double steerPower, double drivePower) {
        STEERMOTOR.set(steerPower);
        DRIVEMOTOR.set(drivePower);
    }


    // Stop both motors
    public void stop() {
        STEERMOTOR.set(0);
        DRIVEMOTOR.set(0);
    }


    // -------------------------
    // STEER MOTOR DATA
    // -------------------------

    // Position in rotations
    public double getSteerPosition() {
        return STEERMOTOR.getPosition().getValueAsDouble();
    }


    public double getSteerPositionRadians() {
    return getSteerPosition()
        * (7.0 / 150.0)
        * 2.0
        * Math.PI;
}
// gear ratio 150(engine) : 7(steer)
    
    // Velocity in rotations per second
    public double getSteerVelocity() {
        return STEERMOTOR.getVelocity().getValueAsDouble();
    }
    
public double getSteerVelocityRadians() {
    return getSteerVelocity()
        * (7.0 / 150.0)
        * 2.0
        * Math.PI;
}
    

    // Position in rotations
    public double getDrivePosition() {
        return DRIVEMOTOR.getPosition().getValueAsDouble();
    }


    // Velocity in rotations per second
    public double getDriveVelocity() {
        return DRIVEMOTOR.getVelocity().getValueAsDouble();
    }


    // Runs automatically every 0.02 secs
    @Override
    public void periodic() {

        // Steering motor
        SmartDashboard.putNumber(
            "Steer Position",
            getSteerPosition()
        );

        SmartDashboard.putNumber(
            "Steer Position Radians",
            getSteerPositionRadians()
        );

        SmartDashboard.putNumber(
            "Steer Velocity Radians",
            getSteerVelocityRadians()
        );


        // Drive motor
        SmartDashboard.putNumber(
            "Drive Position",
            getDrivePosition()
        );

        SmartDashboard.putNumber(
            "Drive Velocity",
            getDriveVelocity()
        );
    }
}