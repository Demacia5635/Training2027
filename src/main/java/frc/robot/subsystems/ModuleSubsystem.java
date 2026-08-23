package frc.robot.subsystems;

import frc.demacia.utils.motors.TalonFXMotor;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

public class ModuleSubsystem extends SubsystemBase{
    private final TalonFXMotor steerMotor;
    private double powerSteer;
    private final TalonFXMotor driveMotor;
    private double powerDrive;

    public ModuleSubsystem() {
        super();
        steerMotor = new TalonFXMotor(Constants.CONFIG_STEER);
        driveMotor = new TalonFXMotor(Constants.CONFIG_DRIVE);
        SmartDashboard.putData(this);
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

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("get Velocity drive", this::getCurrentVelocityDrive, this::setVelocityDrive);
    }

    @Override
    public void periodic() {
        // double angleFromMotor = SmartDashboard.getAngle(), -1);
        SmartDashboard.putNumber("meter drive", getMeterDrive());
        SmartDashboard.putNumber("PID Drive Velocity", getCurrentVelocityDrive());
        SmartDashboard.putNumber("Ks Calculate drive", calculateKsDrive());
        SmartDashboard.putNumber("Kv Calculate drive", calculateKvDrive());
    }

}
