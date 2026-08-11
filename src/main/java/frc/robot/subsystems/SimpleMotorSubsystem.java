package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorSubsystem extends SubsystemBase {

    private final TalonFX motor1; // DRIVE
    private final TalonFX motor2; // STEER

    public SimpleMotorSubsystem() {

        super();

        motor1 = new TalonFX(
            Constants.SimpleMotorConstants.Motor1ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );

        motor2 = new TalonFX(
            Constants.SimpleMotorConstants.Motor2ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );
    }


    public void setDrivePower(double power) {
        motor1.set(power);
    }

    public double getDrivePosition() {
        return motor1.getPosition().getValueAsDouble();
    }

    public double getDriveVelocity() {
        return motor1.getVelocity().getValueAsDouble();
    }

    public void setSteerPower(double power) {
        motor2.set(power);
    }

    public double getSteerPosition() {
        return motor2.getPosition().getValueAsDouble();
    }

    public double getSteerVelocity() {
        return motor2.getVelocity().getValueAsDouble();
    }


    public void stopDrive() {
        motor1.set(0);
    }

    public void stopSteer() {
        motor2.set(0);
    }

    public void stop() {
        stopDrive();
        stopSteer();
    }

    @Override
    public void periodic() {

        SmartDashboard.putNumber(
            "Drive Position",
            getDrivePosition()
        );

        SmartDashboard.putNumber(
            "Drive Velocity",
            getDriveVelocity()
        );

        // STEER
        SmartDashboard.putNumber(
            "Steer Position",
            getSteerPosition()
        );

        SmartDashboard.putNumber(
            "Steer Velocity",
            getSteerVelocity()
        );
    }
}