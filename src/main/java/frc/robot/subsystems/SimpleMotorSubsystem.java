// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;


import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFXMotor drive;
    private final TalonFXMotor steer;

    public SimpleMotorSubsystem() {
        super();
        drive = new TalonFXMotor(Constants.MotorConstants.DRIVE_CONFIG);
        steer = new TalonFXMotor(Constants.MotorConstants.STEER_CONFIG);
        SmartDashboard.putData("this", this);
        // SmartDashboard.putData("Wanted Motor Angle command", new
        // SimpleMotorCommand(this, 5, 5, 5));

    }

    // Set drive motor power
    public void setDrivePower(double power) {
        drive.setDuty(power);
    }

    // Set steeer motor power
    public void setSteerPower(double power) {
        steer.setDuty(power);
    }

    // setters:
    public void setSteerPositionRadians(double targetRadians) {
        steer.setPositionVoltage(targetRadians);
    }

    public void setDrivePositionMeters(double targetMeters) {
        drive.setPositionVoltage(targetMeters);
    }   

    public void setDriveVelocityRPM(double targetVelocity) {
        drive.setVelocity(targetVelocity);
    }

    // getters:
    public double getSteerPositionRadians() {
        return steer.getCurrentPosition(); // convert rotations to degrees
    }

    public double getDrivePositionMeters() {
        return drive.getCurrentPosition();
    }

    public double getSteerVelocity() {
        return steer.getCurrentVelocity(); // convert degrees to rotations
    }

    public double getDriveVel() {
        return drive.getCurrentVelocity();
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
    public void initSendable(SendableBuilder builder) {
        // TODO Auto-generated method stub
        super.initSendable(builder);
        builder.addDoubleProperty("Target Angle", ()->getSteerPositionRadians(),(targetAngle)-> setSteerPositionRadians(targetAngle));
        builder.addDoubleProperty("Target velocity", this::getDriveVel, this::setDriveVelocityRPM);
        builder.addDoubleProperty("", null, null);

    }


    @Override
    public void periodic() {
        SmartDashboard.putNumber("Steer Position (Rotations)", getSteerPositionRadians()); // steer position
        SmartDashboard.putNumber("Steer Velocity (RPM)", getSteerVelocity()); // steer velocity
        SmartDashboard.putNumber("Drive Position (Meters)", getDrivePositionMeters()); // drive position
        SmartDashboard.putNumber("Drive Velocity (RPM)", getDriveVel()); // drive velocity


        // position control
        // SmartDashboard.putNumber("KP", Constants.MotorConstants.STEER_KP);
        // SmartDashboard.getNumber("KI", Constants.MotorConstants.STEER_KI);
        // SmartDashboard.getNumber("KD", Constants.MotorConstants.STEER_KD);
        // SmartDashboard.putNumber("Graph", getSteerPositionRadians());


        // velocity/speed control
        // setDriveVelocityRPM(SmartDashboard.getNumber("Target Velocity", 0));

    }

}