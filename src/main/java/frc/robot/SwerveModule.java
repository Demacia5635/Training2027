package frc.robot;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXConfig;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.demacia.utils.sensors.Cancoder;
import frc.demacia.utils.sensors.CancoderConfig;

public class SwerveModule {
    private final TalonFXMotor steerMotor;
    private final TalonFXMotor driveMotor;
    private final Cancoder cancoder;

    public SwerveModule(TalonFXConfig steer, TalonFXConfig drive, CancoderConfig cancoder ,double offset ) {
        steerMotor = new TalonFXMotor(steer);
        driveMotor = new TalonFXMotor(drive);
        this.cancoder = new Cancoder(cancoder);
        steerMotor.setEncoderPosition(getAbsoluteAngle()- offset);
    }

    public void setDrivePower(double power) {
        driveMotor.setDuty(power);
    }

    public void setSteerPower(double power) {
        steerMotor.setDuty(power);
    }

    public double getAbsoluteAngle() {
        return cancoder.getAbsolutePosition().getValueAsDouble() * 2 * Math.PI;
    }

    public void setVelocityDrive(double velocity) {
        driveMotor.setVelocity(velocity);
        // this.targetVelocityDrive = velocity;
    }

    public void setSteerPosition(double position) {
        steerMotor.setPositionVoltage(position);
        // this.targetAngleSteer = position;
    }

    public void setVelocitySteer(double velocity) {
        steerMotor.setVelocity(velocity);
    }

    public double getSteerAngle() {
        return steerMotor.getCurrentPosition();
    }

    public double getSteerVelocity() {
        return steerMotor.getCurrentVelocity();
    }

    public double getDriveVelocity() {
        return driveMotor.getCurrentVelocity();
    }

    public Rotation2d getSteerRotation() {
        return new Rotation2d(getSteerAngle());
    }

    public SwerveModuleState getState() {
        return new SwerveModuleState(driveMotor.getCurrentVelocity(), getSteerRotation());
    }

    public SwerveModulePosition getPosition() {
        return new SwerveModulePosition(driveMotor.getCurrentPosition(), getSteerRotation());
    }

    public void resetEncoder() {
        steerMotor.setEncoderPosition(0);
    }

    public double getAbsoluteAngleDeg() {
        return Math.toDegrees(getAbsoluteAngle());
    }

    public double getSteerAngleDeg() {
        return Math.toDegrees(getSteerAngle());
    }

}