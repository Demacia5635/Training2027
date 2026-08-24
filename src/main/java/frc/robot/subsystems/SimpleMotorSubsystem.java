package frc.robot.subsystems;

import java.lang.annotation.Target;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFX SteerMotor;
    private final TalonFX DriveMotor;
    private double targetAngle;
    private double targetDistance;

    public SimpleMotorSubsystem() {
        super();
        this.SteerMotor = new TalonFX(Constants.OperatorConstants.STEER_MOTOR_ID, Constants.OperatorConstants.CANBUS);
        this.DriveMotor = new TalonFX(Constants.OperatorConstants.DRIVE_MOTOR_ID, Constants.OperatorConstants.CANBUS);

    }

    public void setSteerPower(double power) {
        SteerMotor.set(power);

    }

    public double getSteerPower() {
        return SteerMotor.get();
    }

    public double getDrivePower() {
        return DriveMotor.get();
    }

    public void stopSteer() {
        setSteerPower(0);

    }

    public void setDrivePower(double power) {
        SteerMotor.set(power);
        DriveMotor.set(power);

    }

    public void stopDrive() {
        setDrivePower(0);

    }

    public void setTargetAngle(double angle) {
        targetAngle = angle;
    }
    public void setTargetDistance(double distance) {
        targetDistance = distance;
    }
    public void goToTargetAngle(double angle) {
        if (getSteerMotorPosition() < angle) {
            setSteerPower(Constants.OperatorConstants.STEER_MOTOR_SPEED);
        } else if (getSteerMotorPosition() > angle) {
            setSteerPower(-Constants.OperatorConstants.STEER_MOTOR_SPEED);
        } else {
            stopSteer();
        }
    }
    public void driveForward(double distance) {
        if (getDriveMotorPosition() < distance) {
            setDrivePower(Constants.OperatorConstants.DRIVE_MOTOR_SPEED);
        } 
        else if (getDriveMotorPosition() > distance) {
            setDrivePower(-Constants.OperatorConstants.DRIVE_MOTOR_SPEED);
        }
        else {
            stopDrive();
        }
    }
    public double getSteerMotorPosition() {
        return SteerMotor.getPosition().getValueAsDouble();
    }
    public double getDriveMotorPosition() {
        return DriveMotor.getPosition().getValueAsDouble();
    }
    @Override
    public void periodic() {
        SmartDashboard.putNumber("Steer Power", getSteerPower());
        SmartDashboard.putNumber("Drive Power", getDrivePower());
        SmartDashboard.putNumber("Steer Position", getSteerMotorPosition());
        SmartDashboard.putNumber("Drive Position", getDriveMotorPosition());

    }
}
