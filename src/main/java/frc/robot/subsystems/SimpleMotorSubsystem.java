// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFX drive; // backwards and forwards
    private final TalonFX steer; // spin
    private final PositionVoltage positionVoltage = new PositionVoltage(0);

    public SimpleMotorSubsystem() {
        super();
        drive = new TalonFX(Constants.MotorConstants.DRIVE_ID, Constants.MotorConstants.MOTOR_CANBUS);
        steer = new TalonFX(Constants.MotorConstants.STEER_ID, Constants.MotorConstants.MOTOR_CANBUS);

        // SmartDashboard.putData("Wanted Motor Angle command", new
        // SimpleMotorCommand(this, 5, 5, 5));

    }

    // Set drive motor power
    public void setDrivePower(double power) {
        drive.set(power);
    }

    // Set steeer motor power
    public void setSteerPower(double power) {
        steer.set(power);
    }

    // setters:
    public void setSteerPositionDegrees(double targetDegrees) {
        double targetRotations = targetDegrees * Constants.ConvertionConstants.RADIANS_TO_ROTATIONS; // convert degrees to rotations
        steer.setControl(positionVoltage.withPosition(targetRotations));
    }

    public void setDrivePositionMeters(double targetMeters) {
        drive.setControl(positionVoltage.withPosition(targetMeters));
    }

    // getters:
    public double getSteerPositionDegrees() {
        return steer.getPosition().getValueAsDouble() * Constants.ConvertionConstants.ROTATIONS_TO_RADIANS; // convert rotations to degrees
    }

    public double getDrivePositionMeters() {
        return drive.getPosition().getValueAsDouble();
    }

    public double getSteerVelocityDegreesPerSec() {
        return steer.getVelocity().getValueAsDouble() * Constants.ConvertionConstants.RADIANS_TO_ROTATIONS; // convert degrees to rotations
    }

    public double getDriveRPM() {
        return drive.getVelocity().getValueAsDouble() * 60.0;
    }

    // stopping the motors:

    public void driveStop() {
        setDrivePower(0);
    }

    public void steerStop() {
        setSteerPower(0);
    }

    public void stopAll() {
        setSteerPower(0);
        setDrivePower(0);
    }

    // public void setAngle() {
    // double wantedAngle = SmartDashboard.getNumber("Motor Position", 0);
    // steer.setPosition(wantedAngle);

    // }

    // public double getMotorPosition() {
    // return steer.getPosition().getValueAsDouble();
    // }

    @Override
    public void periodic() {
        // SmartDashboard.putNumber("Motor Position", getMotorPosition());
        SmartDashboard.putNumber("Steer Position (Rotations)", getSteerPositionDegrees()); // steer position
        SmartDashboard.putNumber("Steer Velocity (RPM)", getSteerVelocityDegreesPerSec()); // steer velocity
        SmartDashboard.putNumber("Drive Position (Meters)", getDrivePositionMeters()); // drive position
        SmartDashboard.putNumber("Drive Velocity (RPM)", getDriveRPM()); // drive velocity

    }

}
