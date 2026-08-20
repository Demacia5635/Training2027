package frc.robot.subsystems;

import frc.demacia.utils.motors.TalonFXMotor;
import frc.robot.Constants;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SimpleMotorSubsystem extends SubsystemBase {

    // Motor 1 = DRIVE
    private final TalonFXMotor motor1;

    // Motor 2 = STEER
    private final TalonFXMotor motor2;

    public SimpleMotorSubsystem() {

        super();

        TalonFXMotor_drive = new TalonFXMotor(Constants.CONFIG

        );

        motor2 = new TalonFXMotor(
            Constants.SimpleMotorConstants.Motor2ID,
            Constants.SimpleMotorConstants.MotorCANbus
        );
    }


    // =========================
    // MOTOR 1 - DRIVE
    // =========================

    public void setMotor1Power(double power) {

        motor1.set(power);
    }


    public double getMotor1Pos() {

        return motor1
            .getPosition()
            .getValueAsDouble();
    }


    // =========================
    // MOTOR 2 - STEER
    // =========================

    public void setMotor2Power(double power) {

        motor2.set(power);
    }


    public double getMotor2Pos() {

        return motor2
            .getPosition()
            .getValueAsDouble();
    }


    // =========================
    // STOP BOTH MOTORS
    // =========================

    public void stop() {

        motor1.set(0);
        motor2.set(0);
    }


    // =========================
    // ELASTIC
    // =========================

    @Override
    public void periodic() {

        SmartDashboard.putNumber(
            "Motor 1 Position",
            getMotor1Pos()
        );

        SmartDashboard.putNumber(
            "Motor 2 Position",
            getMotor2Pos()
        );
    }
}