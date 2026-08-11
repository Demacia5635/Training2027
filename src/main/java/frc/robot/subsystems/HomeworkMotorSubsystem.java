package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.HomeworkMotorSubsystemConstants;

public class HomeworkMotorSubsystem extends SubsystemBase {
    TalonFX driveMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.DRIVE_ID,
            Constants.HomeworkMotorSubsystemConstants.DRIVE_CANBUS);
    TalonFX steerMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.STEER_ID,
            Constants.HomeworkMotorSubsystemConstants.STEER_CANBUS);

    public HomeworkMotorSubsystem() {
        super();

        setDrivePos(0);
        setSteerPos(0);

        SmartDashboard.putData("Subsystem", this);
    }

    public void driveSetPower(double power) {
        driveMotor.set(power);
    }

    public void steerSetPower(double power) {
        steerMotor.set(power);
    }

    public double getSteerPos() {
        return 2 * Math.PI
                * ((steerMotor.getPosition().getValueAsDouble()) / HomeworkMotorSubsystemConstants.STEER_GEAR_RATIO);
    }

    public double getDrivePos() {
        return (driveMotor.getPosition().getValueAsDouble()) * HomeworkMotorSubsystemConstants.DRIVE_CIRCUMFERENCE
                / HomeworkMotorSubsystemConstants.DRIVE_GEAR_RATIO;
    }

    public double getSteerSpeed() {
        return steerMotor.getVelocity().getValueAsDouble();
    }

    public double getDriveSpeed() {
        return driveMotor.getVelocity().getValueAsDouble();
    }

    public void driveStop() {
        driveMotor.set(0);
    }

    public void steerStop() {
        steerMotor.set(0);
    }

    public double getSteerPosAsDegrees() {
        return (180 / Math.PI) * getSteerPos();
    }

    public void setSteerPos(double pos) {
        steerMotor.setPosition(pos);
    }

    public void setDrivePos(double pos) {
        driveMotor.setPosition(pos);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Steer Position", this::getSteerPosAsDegrees, null);
        builder.addDoubleProperty("Drive Position", this::getDrivePos, null);
        builder.addDoubleProperty("Steer Speed ", this::getSteerSpeed, null);
        builder.addDoubleProperty("Drive Speed ", this::getDriveSpeed, null);

    }
}
