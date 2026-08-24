package frc.robot.subsystems;

import frc.demacia.utils.motors.TalonFXMotor;

import com.ctre.phoenix6.hardware.CANcoder;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ModuleSubsystem extends SubsystemBase {
    private final TalonFXMotor steerMotor;
    private final TalonFXMotor driveMotor;
    private final CANcoder cancoder;
    private double powerSteer;
    private double powerDrive;
    private double targetVelocityDrive;
    private double targetAngleSteer;

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
        this.targetVelocityDrive = velocity;
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
    public void setSteerAngle(double position) {
        steerMotor.setPositionVoltage(position);
        this.targetAngleSteer = position;

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
        builder.addDoubleProperty("(initSendable) (set) Velocity steer", this::getCurrentVelocitySteer,
                this::setVelocitySteer);
        builder.addDoubleProperty("(initSendable) (set) Drive Velocity", this::getCurrentVelocityDrive,
                this::setVelocityDrive);
        builder.addDoubleProperty("(initSendable) (get) Absolute Steer Angle", this::getAbsoluteAngleDeg, null);
        builder.addDoubleProperty("(initSendable) (get) Target Setpoint velocity drive (target Velocity)",() -> Math.toDegrees(targetVelocityDrive), null);
        builder.addDoubleProperty("(initSendable) (get) Target Setpoint angle steer (target angle)",() -> Math.toDegrees(targetAngleSteer), null);
    }

    @Override
    public void periodic() {
        // double angleFromMotor = SmartDashboard.getAngle(), -1);
        SmartDashboard.putNumber("(Dashbord) meter drive", getMeterDrive());
        SmartDashboard.putNumber("(Dashbord) Drive Velocity", getCurrentVelocityDrive());
        // SmartDashboard.putNumber("(Dashbord) Ks Calculate drive",
        // calculateKsDrive());
        // SmartDashboard.putNumber("(Dashbord) Kv Calculate drive",
        // calculateKvDrive());
        SmartDashboard.putNumber("(Dashbord) angle steer", getAngleSteerDeg());
        SmartDashboard.putNumber("(Dashbord) position steer", Math.toDegrees(getCurrentPositionSteer()));
        SmartDashboard.putNumber("(Dashbord) velocity steer", getCurrentVelocitySteer());
        // SmartDashboard.putNumber("(Dashbord) Ks Calculate steer",
        // calculateKsSteer());
        SmartDashboard.putNumber("(Dashbord) power steer", getPowerSteer());
        // SmartDashboard.putNumber("(Dashbord) Kv Calculate steer",
        // calculateKvSteer());
        SmartDashboard.putNumber("(Dashbord) Drive Velocity", getCurrentVelocityDrive());
        SmartDashboard.putNumber("(Dashbord) Absolute steer angel", getAbsoluteAngleDeg());
        SmartDashboard.putNumber("(Dasshbord) power drive", getPowerDrive());
    }

}
