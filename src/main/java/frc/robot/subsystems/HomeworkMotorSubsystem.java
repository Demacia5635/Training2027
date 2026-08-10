package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class HomeworkMotorSubsystem extends SubsystemBase {
    TalonFX driveMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.DRIVER_ID,
            Constants.HomeworkMotorSubsystemConstants.DRIVER_CANBUS);
    TalonFX steerMotor = new TalonFX(Constants.HomeworkMotorSubsystemConstants.STEER_ID,
            Constants.HomeworkMotorSubsystemConstants.STEER_CANBUS);

    public HomeworkMotorSubsystem() {
        super();
        SmartDashboard.putData(this);

    }

    public void driveSetPower(double power) {
        driveMotor.set(power);
    }

    public void steerSetPower(double power) {
        steerMotor.set(power);
    }

    public double getSteerPos() {
        return 2*Math.PI * (steerMotor.getPosition().getValueAsDouble());
    }

    public double getDrivePos() {
        return 2*Math.PI * (driveMotor.getPosition().getValueAsDouble());
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
        return (180/Math.PI) * getSteerPos();
    }

    public double getDrivePosAsDegrees() {
        return (180/Math.PI) * getDrivePos();
    }
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Steer Position", this::getSteerPosAsDegrees, null);
        builder.addDoubleProperty("Drive Position", this::getDrivePosAsDegrees, null);
        builder.addDoubleProperty("Steer Speed ", this::getSteerSpeed, null);
        builder.addDoubleProperty("Drive Speed ", this::getDriveSpeed, null);

    }
}
