package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class SwerveModule {
    private final TalonFXMotor steerMotor;
    private final TalonFXMotor driveMotor;
    private final CANcoder cancoder;

    public SwerveModule() {
        super();
        steerMotor = new TalonFXMotor(Constants.CONFIG_STEER);
        driveMotor = new TalonFXMotor(Constants.CONFIG_DRIVE);
        cancoder = new CANcoder(Constants.CANCODER_ID);
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
        return new SwerveModuleState(getDriveVelocity(), getSteerRotation());
    }

    public void resetEncoder() {
        steerMotor.setEncoderPosition(0);
    }
}
