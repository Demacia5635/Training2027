package frc.robot.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.Data;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class SimpleMotorSubsystem extends SubsystemBase {
    private final TalonFXMotor driveMotor;
    private final TalonFXMotor steerMotor;

    public SimpleMotorSubsystem() {
        driveMotor =
            new TalonFXMotor(
                Constants.SimpleMotorConstants.DRIVE_CONFIG
            );

        steerMotor =
            new TalonFXMotor(
                Constants.SimpleMotorConstants.STEER_CONFIG
            );

        // Lets Elastic show the motor telemetry sendables.
        SmartDashboard.putData("Simple Motors/Drive", driveMotor);
        SmartDashboard.putData("Simple Motors/Steer", steerMotor);
    }


    public void setDrivePower(double power) {
        driveMotor.setDuty(MathUtil.clamp(power, -1.0, 1.0));
    }

    public double getDrivePosition() {
        return driveMotor.getCurrentPosition();
    }

    public double getDriveVelocity() {
        return driveMotor.getCurrentVelocity();
    }

    public double getDriveCurrent() {
        return driveMotor.getCurrentCurrent();
    }

    // Steer motor

    public void setSteerPower(double power) {
        steerMotor.setDuty(MathUtil.clamp(power, -1.0, 1.0));
    }

    public double getSteerPosition() {
        return steerMotor.getCurrentPosition();
    }

    public double getSteerVelocity() {
        return steerMotor.getCurrentVelocity();
    }

    public double getSteerCurrent() {
        return steerMotor.getCurrentCurrent();
    }

    // Compatibility with your existing SimpleMotorCommand 

    public void setMotor1Power(double power) {
        setDrivePower(power);
    }

    public double getMotor1Pos() {
        return getDrivePosition();
    }

    public void setMotor2Power(double power) {
        setSteerPower(power);
    }

    public double getMotor2Pos() {
        return getSteerPosition();
    }

    // Demacia Data access

    public Data<Angle> getDrivePositionData() {
        return driveMotor.getPositionSignal();
    }

    public Data<AngularVelocity> getDriveVelocityData() {
        return driveMotor.getVelocitySignal();
    }

    public Data<Current> getDriveCurrentData() {
        return driveMotor.getCurrentSignal();
    }

    public Data<Angle> getSteerPositionData() {
        return steerMotor.getPositionSignal();
    }

    public Data<AngularVelocity> getSteerVelocityData() {
        return steerMotor.getVelocitySignal();
    }

    public Data<Current> getSteerCurrentData() {
        return steerMotor.getCurrentSignal();
    }

    public void stop() {
        driveMotor.stop();
        steerMotor.stop();
    }

    public void checkElectronics() {
        driveMotor.checkElectronics();
        steerMotor.checkElectronics();
    }

    @Override
    public void periodic() {
        // Data.refreshAll() is already called by Demacia's LogManager.
        // These getters therefore use the refreshed cached Data values.
        SmartDashboard.putNumber("Motor 1 Position", getDrivePosition());
        SmartDashboard.putNumber("Motor 1 Velocity", getDriveVelocity());
        SmartDashboard.putNumber("Motor 1 Current", getDriveCurrent());

        SmartDashboard.putNumber("Motor 2 Position", getSteerPosition());
        SmartDashboard.putNumber("Motor 2 Velocity", getSteerVelocity());
        SmartDashboard.putNumber("Motor 2 Current", getSteerCurrent());
    }
}