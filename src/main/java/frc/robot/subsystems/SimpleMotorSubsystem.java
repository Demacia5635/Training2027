package frc.robot.subsystems;

import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;
import frc.robot.Constants.SimpleMotorConstants;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorSubsystem extends SubsystemBase {

    // Motor 1 = DRIVE
    private final TalonFXMotor driveMotor;

    // Motor 2 = STEER
    private final TalonFXMotor steerMotor;


    public SimpleMotorSubsystem() {

        super();

        driveMotor = new TalonFXMotor(SimpleMotorConstants.CONFIG);

        steerMotor = new TalonFX(
            Constants.SimpleMotorConstants.Motor2ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );
    }



    public void setDrivePower(double power) {
        driveMotor.set(power);
    }

    public double getDrivePosition() {
        return driveMotor.getPosition().getValueAsDouble();
    }

    public double getDriveVelocity() {
        return driveMotor.getVelocity().getValueAsDouble();
    }

    public void stopDrive() {
        driveMotor.set(0);
    }

    public void setSteerPower(double power) {
        steerMotor.set(power);
    }

    public double getSteerPosition() {
        return steerMotor.getPosition().getValueAsDouble();
    }

    public double getSteerVelocity() {
        return steerMotor.getVelocity().getValueAsDouble();
    }

    public double getSteerAngle() {
        return getSteerPosition() * 360.0;
    }

    public void stopSteer() {
        steerMotor.set(0);
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


        SmartDashboard.putNumber(
            "Steer Position",
            getSteerPosition()
        );

        SmartDashboard.putNumber(
            "Steer Velocity",
            getSteerVelocity()
        );

        SmartDashboard.putNumber(
            "Steer Angle",
            getSteerAngle()
        );
    }
}