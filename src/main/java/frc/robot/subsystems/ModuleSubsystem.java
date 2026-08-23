package frc.robot.subsystems;

import frc.demacia.utils.motors.TalonFXMotor;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class ModuleSubsystem extends SubsystemBase {
    private final TalonFXMotor steerMotor;
    private final TalonFXMotor driveMotor;
    private final CANcoder cancoder;
    private double powerSteer;
    private double powerDrive;

    public ModuleSubsystem() {
        super();
        steerMotor = new TalonFXMotor(Constants.CONFIG_STEER);
        driveMotor = new TalonFXMotor(Constants.CONFIG_DRIVE);
        cancoder = new CANcoder(Constants.CANCODER_ID);
        SmartDashboard.putData(this);
    }

    // cancoder:
    public double getAbsoluteAngle() {
        return cancoder.getAbsolutePosition().getValueAsDouble() * 2 * Math.PI;
    }

    // drive:
    public void setPositionDrive(double position) {
        driveMotor.setPositionVoltage(position);
    }

    public void setVelocityDrive(double velocity) {
        driveMotor.setVelocity(velocity);
    }

    public double getCurrentVelocityDrive() {
        return driveMotor.getCurrentVelocity();
    }

    public void setVoltageDrive(double voltage) {
        driveMotor.setVoltage(voltage);
    }

    public double getPowerDrive() {
        return powerDrive;
    }

    public void setPowerDrive(double powerDrive) {
        // this.powerDrive = powerDrive;
        driveMotor.setDuty(powerDrive);
    }

    public void stop() {
        setPowerDrive(0);
        setPowerSteer(0);

    }

    public double getMeterDrive() {
        return (driveMotor.getCurrentPosition());
    }

    public double calculateKsDrive() {
        return Math.signum(getCurrentVelocityDrive()) * Constants.KS_STEER;
    }

    public double calculateKvDrive() {
        return Constants.KV_STEER * getCurrentVelocityDrive();
    }

    // steer:
    public void setPositionSteer(double position) {
        steerMotor.setPositionVoltage(position);
    }

    public double getCurrentPositionSteer() {
        return steerMotor.getCurrentPosition();
    }

    public void setVelocitySteer(double velocity) {
        steerMotor.setVelocity(velocity);
    }

    public double getCurrentVelocitySteer() {
        return steerMotor.getCurrentVelocity();
    }

    public void setPowerSteer(double powerSteer) {
        this.powerSteer = powerSteer;
        steerMotor.setDuty(powerSteer);
    }

    public double getPowerSteer() {
        return powerSteer;

    }

    public double getAngleSteer() {
        return (steerMotor.getCurrentPosition());
    }

    public double getAngleSteerDeg() {
        return getAngleSteer() * 180 / Math.PI;
    }

    public double calculateKsSteer() {
        return Math.signum(getCurrentVelocitySteer()) * Constants.KS_STEER;
    }

    public double calculateKvSteer() {
        return Constants.KV_STEER * getCurrentVelocitySteer();
    }

    public void setVoltageSteer(double voltage) {
        steerMotor.setVoltage(voltage);
    }
    public double getAbsoluteAngleDeg() {
        return Math.toDegrees(getAbsoluteAngle());
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("(set) Velocity steer", this::getCurrentVelocitySteer, this::setVelocitySteer);
        builder.addDoubleProperty("(set) Drive Velocity", this::getCurrentVelocityDrive, this::setVelocityDrive);
        builder.addDoubleProperty("Absolute Steer Angle", this::getAbsoluteAngleDeg, null);
    }

    @Override
    public void periodic() {
        // double angleFromMotor = SmartDashboard.getAngle(), -1);
        SmartDashboard.putNumber("meter drive", getMeterDrive());
        SmartDashboard.putNumber(" Drive Velocity", getCurrentVelocityDrive());
        // SmartDashboard.putNumber("Ks Calculate drive", calculateKsDrive());
        // SmartDashboard.putNumber("Kv Calculate drive", calculateKvDrive());
        SmartDashboard.putNumber("angle steer", getAngleSteerDeg());
        SmartDashboard.putNumber(" position steer", Math.toDegrees(getCurrentPositionSteer()));
        SmartDashboard.putNumber(" velocity steer", getCurrentVelocitySteer());
        //SmartDashboard.putNumber("Ks Calculate steer", calculateKsSteer());
        SmartDashboard.putNumber("power steer", getPowerSteer());
        //SmartDashboard.putNumber("Kv Calculate steer", calculateKvSteer());
    }

}
